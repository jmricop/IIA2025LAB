package me.integracion.iia;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 *
 * @author Usuario
 */
public class IIA {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        // --- DEFINICIÓN DE RUTAS ---
        // Esta es la ruta relativa correcta desde la raíz del proyecto (IIA/) 
        // hasta tu archivo, basado en tu estructura.
        String archivoXmlPath = "src/main/java/resources/cdcatalog.xml";
        
        // Asumo que 'xslt.txt' está en la misma carpeta
        String archivoXsltPath = "src/main/java/resources/xslt.txt"; 
        
        // El archivo de salida se creará en la raíz del proyecto: IIA/salida.html
        String archivoSalida = "salida.html";

        
        // --- 1. PARSEAR EL XML (UNA SOLA VEZ) ---
        // Parseamos el documento aquí y lo reutilizamos más adelante
        Document doc = null;
        try {
            File inputFile = new File(archivoXmlPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile); // Guardamos el documento en la variable 'doc'
            doc.getDocumentElement().normalize();

            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("cd");

            System.out.println("Iterando sobre " + nodeList.getLength() + " elementos <cd>...");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                // (Tu lógica de iteración comentada va aquí)
            }
            System.out.println("Iteración DOM completada.");

        } catch (Exception e) {
            System.err.println("Error al leer y parsear el XML: " + e.getMessage());
            e.printStackTrace();
            return; // Si no podemos leer el XML, no continuamos
        }

        
        // --- 2. TRANSFORMACIÓN XSLT ---
        try {
            TransformerFactory factory = TransformerFactory.newInstance();

            // Usamos las rutas corregidas
            StreamSource xmlSource = new StreamSource(new File(archivoXmlPath));
            StreamSource xsltSource = new StreamSource(new File(archivoXsltPath));

            Transformer transformer = factory.newTransformer(xsltSource);
            StreamResult result = new StreamResult(new File(archivoSalida));
            transformer.transform(xmlSource, result);

            System.out.println("¡Transformación XSLT completada! Revisa el archivo: " + archivoSalida);

        } catch (Exception e) {
            System.err.println("Error durante la transformación XSLT: " + e.getMessage());
            e.printStackTrace();
        }

        
        // --- 3. CONSULTA XPATH ---
        // ¡No es necesario volver a cargar el archivo!
        // Reutilizamos la variable 'doc' que parseamos en el paso 1.
        if (doc != null) {
            try {
                System.out.println("\nEjecutando consulta XPath...");
                XPath xpath = XPathFactory.newInstance().newXPath();
                XPathExpression exp = xpath.compile("/catalog/cd/title");

                NodeList resultado = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);

                System.out.println("XPath encontró " + resultado.getLength() + " títulos:");
                for (int i = 0; i < resultado.getLength(); i++) {
                    System.out.println(" - " + resultado.item(i).getTextContent());
                }

            } catch (XPathExpressionException e) {
                System.err.println("Error en la expresión XPath: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}