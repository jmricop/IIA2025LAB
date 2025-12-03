/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.transformators; // (El paquete es el que tenías)

import Tasks.Task;
import Tasks.taskEnum;


import common.Message;
import common.Slot;
import common.Diccionario;
import common.IdUnico; 
import common.ValoresDiccionario;


import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 *
 * @author apolo
 */
public class Splitter extends Task {

    private String xPathExpression;

    // --- Atributos de la versión HEAD (la buena) ---
    private DocumentBuilder dBuilder;
    private XPath xpath;
    
    // --- Constructor de la versión HEAD (el bueno) ---
    public Splitter(taskEnum t, ArrayList<Slot> se, ArrayList<Slot> sl) {
        super(t, se, sl);

        if (se.size() != 1 || sl.size() != 1) {
            throw new IllegalArgumentException("Splitter debe tener exactamente 1 buzón de entrada y 1 de salida.");
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            this.dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error fatal: No se pudo inicializar el DocumentBuilder en Splitter", e);
        }
        
        XPathFactory xfactory = XPathFactory.newInstance();
        this.xpath = xfactory.newXPath();
    }

    // --- action() de la versión HEAD, CON LA CORRECCIÓN DE LÓGICA ---
    @Override
    public void action() {
        
        if (!isEmpty(0)) {

            // 1. Coge el mensaje "Padre" PRIMERO
            Message mensaje = getEntryMessage(0); 
            
            // 2. REUTILIZA el ID de lote del padre (ej. 1)
            int idDocumentLote = mensaje.getIdDocument(); 

            // 3. Coge el XML
            Document doc = mensaje.getDocument(); 
            
            try {
                XPathExpression expr = this.xpath.compile(xPathExpression);
                NodeList items = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                int totalSegmentos = items.getLength(); 

                String xp = "//" + items.item(0).getParentNode().getNodeName();
                
                for (int i = 0; i < totalSegmentos; i++) {
                    Node item = items.item(i);
                    item.getParentNode().removeChild(item);
                }

                ValoresDiccionario vD = new ValoresDiccionario(xp, doc);
                
                // Ahora usa el ID correcto (ej. 1) para guardar
                Diccionario.getInstance().put(idDocumentLote, vD);

                for (int i = 0; i < totalSegmentos; i++) {
                    
                    Document newDoc = this.dBuilder.newDocument();
                    Node item = items.item(i);
                    Node importedItem = newDoc.importNode(item, true);
                    newDoc.appendChild(importedItem);
                    
                    int numeroSegmento = i + 1;
                    
                    // Ahora crea los hijos con el ID correcto (ej. 1)
                    Message mensajeAux = new Message(newDoc, idDocumentLote, numeroSegmento, totalSegmentos);

                    setMensajeSalida(mensajeAux, 0);
                }

            } catch (Exception e) {
                System.out.println("Error fatal al ejecutar el splitter: " + e.getMessage());
                e.printStackTrace();
            }
        } 
    }

    public void setXPathExpression(String xPathExpression) {
        this.xPathExpression = xPathExpression;
    }

    @Override
    public void mock() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
  
}