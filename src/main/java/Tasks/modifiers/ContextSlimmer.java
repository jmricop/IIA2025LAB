/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.modifiers;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author apolo
 */

public class ContextSlimmer extends Task {

    // --- Expresiones XPath ---
    private final String xPathToSelectMainNodes;   // Qué nodos del principal se deben analizar/eliminar
    private final String xPathToSelectContextIds;  // Qué nodos/valores del contexto determinan qué se conserva

    private final XPath xpath;

    public ContextSlimmer(ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots,
                          String xPathToSelectMainNodes,
                          String xPathToSelectContextIds,
                          taskEnum t) {

        super(t, entrySlots, exitSlots);
        this.xPathToSelectMainNodes = xPathToSelectMainNodes;
        this.xPathToSelectContextIds = xPathToSelectContextIds;
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    @Override
    public void action() {
        try {
            Message mainMessage = getEntryMessage(0);    // Slot 0 = principal
            Message contextMessage = getEntryMessage(1); // Slot 1 = contexto

            Document mainDoc = mainMessage.getDocument();
            Document contextDoc = contextMessage.getDocument();

            // Obtener los IDs (o valores clave) desde el contexto
            NodeList contextNodes = (NodeList) xpath.evaluate(
                    xPathToSelectContextIds, contextDoc, XPathConstants.NODESET);

            HashSet<String> allowedIds = new HashSet<>();
            for (int i = 0; i < contextNodes.getLength(); i++) {
                Node n = contextNodes.item(i);
                if (n.getTextContent() != null && !n.getTextContent().isBlank()) {
                    allowedIds.add(n.getTextContent().trim());
                }
            }

            if (allowedIds.isEmpty()) {
                Logger.getLogger(ContextSlimmer.class.getName()).log(Level.WARNING,
                        "ContextSlimmer: no se encontraron IDs válidos en el contexto. No se eliminará nada.");
            }

            // Seleccionar los nodos del documento principal
            NodeList mainNodes = (NodeList) xpath.evaluate(
                    xPathToSelectMainNodes, mainDoc, XPathConstants.NODESET);

            //Eliminar los nodos que NO estén permitidos
            for (int i = 0; i < mainNodes.getLength(); i++) {
                Node node = mainNodes.item(i);
                Node idAttr = node.getAttributes() != null ? node.getAttributes().getNamedItem("id") : null;
                String id = (idAttr != null) ? idAttr.getNodeValue() : node.getTextContent().trim();

                if (!allowedIds.contains(id)) {
                    // Eliminar nodo del documento
                    Node parent = node.getParentNode();
                    if (parent != null) {
                        parent.removeChild(node);
                        i--;
                    }
                }
            }

            // 5️⃣ Crear mensaje de salida con el documento adelgazado
            setMensajeSalida(mainMessage, 0);

        } catch (XPathExpressionException e) {
            Logger.getLogger(ContextSlimmer.class.getName())
                    .log(Level.SEVERE, "Error en expresiones XPath del ContextSlimmer", e);
        } catch (Exception e) {
            Logger.getLogger(ContextSlimmer.class.getName())
                    .log(Level.SEVERE, "Error inesperado en ContextSlimmer", e);
        }
    }

    @Override
    public void mock() {
        System.out.println(this.toString());
    }
}
