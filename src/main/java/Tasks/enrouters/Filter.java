/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.enrouters;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants; // Muy importante para definir qué esperas (un nodo, una lista, un string...)
import javax.xml.xpath.XPathExpressionException; // Para manejar errores en tu expresión
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author apolo
 */
public class Filter extends Task {

    private String xPathQuery;

    public Filter(taskEnum t, ArrayList<Slot> es, ArrayList<Slot> exsl, String query) {
        super(t, es, exsl);
        this.xPathQuery = query;
    }

    @Override
    public void action() {
        try {

            XPath x = XPathFactory.newInstance().newXPath();
            Message m = this.getEntryMessage(0);

            Document doc = m.getDocument();
            XPathExpression exp;

            exp = x.compile(xPathQuery);

            NodeList result = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 2. Crear el nuevo documento vacío
            Document newDoc = builder.newDocument();

            // 3. Crear el elemento raíz para el nuevo documento
            Element newRoot = newDoc.createElement(m.getDocument().getDocumentElement().getNodeName());
            newDoc.appendChild(newRoot);

            for (int i = 0; i < result.getLength(); i++) {
                Node node = result.item(i);

                // 5. IMPORTAR el nodo al contexto del nuevo documento
                // (true = deep copy, copia el nodo y todos sus hijos)
                Node importedNode = newDoc.importNode(node, true);

                // 6. Añadir el nodo importado a la raíz del nuevo documento
                newRoot.appendChild(importedNode);
            }
            
           m.setDocument(newDoc);
           
           this.setMensajeSalida(m, 0);

        } catch (XPathExpressionException | ParserConfigurationException ex) {
            Logger.getLogger(Filter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void mock() {
        
        System.out.println(this.toString());
    
        
    
    }
    
    

}
