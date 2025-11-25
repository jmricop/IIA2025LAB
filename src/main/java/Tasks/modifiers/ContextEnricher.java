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
import java.util.logging.Level; 
import java.util.logging.Logger; 
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException; 


public class ContextEnricher extends Task {

    // --- Reglas (XPath) ---
    private final String xPathToExtract; // XPath para COPIAR desde el Contexto
    private final String xPathToInsert;  // XPath para PEGAR en el Principal

    // --- Herramienta XPath (reutilizable) ---
    private final XPath xpath;

    
    public ContextEnricher(ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots,
            String xPathToExtract, String xPathToInsert, taskEnum t) {

        // 1. Llamar al constructor padre
        super(t, entrySlots, exitSlots); 


        // 3. Guardar las reglas y crear la herramienta XPath
        this.xPathToExtract = xPathToExtract;
        this.xPathToInsert = xPathToInsert;
        this.xpath = XPathFactory.newInstance().newXPath();
    }

   
    
    @Override
    public void action() {
        
        if (isEmpty(0) || isEmpty(1)) {
            return;
        }
        
        try {

           
            Message mainMessage = getEntryMessage(0);    // Slot 0 = Principal
            Message contextMessage = getEntryMessage(1); // Slot 1 = Contexto

            // 3. Obtener los documentos de los mensajes
            // (Asumiendo que Message tiene getDocument())
            Document mainDoc = mainMessage.getDocument();
            Document contextDoc = contextMessage.getDocument();

            // 4. EXTRAER (del Contexto)
            NodeList nodesToCopy = (NodeList) xpath.evaluate(
                    xPathToExtract,
                    contextDoc,
                    XPathConstants.NODESET
            );

            if (nodesToCopy.getLength() == 0) {
                // No es un error, simplemente no había nada que enriquecer
                // System.out.println("Enricher ADVERTENCIA: No se encontró nada para extraer del contexto con XPath: " + xPathToExtract);

                // El mensaje principal pasa sin cambios.
                setMensajeSalida(mainMessage, 0); // Enviar el mensaje original
                return;
            }

            // 5. ENCONTRAR DESTINO (en el Principal)
            Node insertionParentNode = (Node) xpath.evaluate(
                    xPathToInsert,
                    mainDoc,
                    XPathConstants.NODE
            );

            if (insertionParentNode == null) {
                // Error: El mensaje principal no tiene la estructura esperada
                throw new RuntimeException("Error en el Enricher: No se pudo encontrar el nodo de inserción en el documento principal. XPath: " + xPathToInsert);
            }

            // 6. IMPORTAR y PEGAR
            for (int i = 0; i < nodesToCopy.getLength(); i++) {
                Node node = nodesToCopy.item(i);

                // Creamos una copia del nodo en el contexto del documento principal
                Node importedNode = mainDoc.importNode(node, true);

                // Añadimos la copia como hija del nodo de destino
                insertionParentNode.appendChild(importedNode);
            }

            
            setMensajeSalida(mainMessage, 0);

        } catch (XPathExpressionException e) {
            // Error en la sintaxis de una expresión XPath
            Logger.getLogger(ContextEnricher.class.getName()).log(Level.SEVERE, "Error de sintaxis XPath en Enricher", e);
        } catch (Exception e) {
            // Cualquier otro error (ej. getDocument() es nulo)
            Logger.getLogger(ContextEnricher.class.getName()).log(Level.SEVERE, "Error fatal en la Tarea Enricher", e);
            // Aquí podrías tener lógica para enviar a un slot de error si existiera
        }
    }

    @Override
    public void mock() {
        
        System.out.println(this.toString());
        
    }


   
}
