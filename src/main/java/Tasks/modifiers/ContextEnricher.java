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
    private final String xPathToExtract; 
    private final String xPathToInsert;  

    // --- Herramienta XPath (reutilizable) ---
    private final XPath xpath;

    
    public ContextEnricher(ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots,
            String xPathToExtract, String xPathToInsert, taskEnum t) {


        super(t, entrySlots, exitSlots); 


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

           
            Message mainMessage = getEntryMessage(0);    
            Message contextMessage = getEntryMessage(1); 

            // 3. Obtener los documentos de los mensajes
            Document mainDoc = mainMessage.getDocument();
            Document contextDoc = contextMessage.getDocument();

            // 4. EXTRAER (del Contexto)
            NodeList nodesToCopy = (NodeList) xpath.evaluate(
                    xPathToExtract,
                    contextDoc,
                    XPathConstants.NODESET
            );

            if (nodesToCopy.getLength() == 0) {
                
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
            
            Logger.getLogger(ContextEnricher.class.getName()).log(Level.SEVERE, "Error de sintaxis XPath en Enricher", e);
        } catch (Exception e) {
            
            Logger.getLogger(ContextEnricher.class.getName()).log(Level.SEVERE, "Error fatal en la Tarea Enricher", e);
            
        }
    }

    @Override
    public void mock() {
        
        System.out.println(this.toString());
        
    }


   
}
