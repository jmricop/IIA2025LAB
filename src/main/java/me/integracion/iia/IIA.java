package me.integracion.iia;

import Tasks.enrouters.Distributor;
import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class IIA {

    public static void main(String[] args) throws Exception {

        // === Creamos un par de mensajes XML de prueba ===
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();

        // Mensaje 1 -> <mensaje><tipo>ALERTA</tipo></mensaje>
        Document doc1 = builder.newDocument();
        Element root1 = doc1.createElement("mensaje");
        Element tipo1 = doc1.createElement("tipo");
        tipo1.setTextContent("ALERTA");
        root1.appendChild(tipo1);
        doc1.appendChild(root1);

        // Mensaje 2 -> <mensaje><tipo>INFO</tipo></mensaje>
        Document doc2 = builder.newDocument();
        Element root2 = doc2.createElement("mensaje");
        Element tipo2 = doc2.createElement("tipo");
        tipo2.setTextContent("INFO");
        root2.appendChild(tipo2);
        doc2.appendChild(root2);

        Message m1 = new Message(doc1, 1);
        Message m2 = new Message(doc2, 2);

        // ===  Creamos los Slots ===
        Queue<Message> colaEntrada = new LinkedList<>();
        colaEntrada.add(m1);
        colaEntrada.add(m2);

        // Dos salidas posibles
        Queue<Message> colaSalida0 = new LinkedList<>();
        Queue<Message> colaSalida1 = new LinkedList<>();

        ArrayList<Slot> entradas = new ArrayList<>();
        ArrayList<Slot> salidas = new ArrayList<>();
        entradas.add(new Slot(colaEntrada));
        salidas.add(new Slot(colaSalida0));
        salidas.add(new Slot(colaSalida1));

        // ===  Creamos el Distributor ===
        // Distribuye según el contenido del tag <tipo>
        String[] condiciones = {"ALERTA", "INFO"};
        Distributor distributor = new Distributor(taskEnum.DISTRIBUTOR, entradas, salidas, condiciones, condiciones.length, "tipo");

        // ===  Ejecutamos el procesamiento ===
        System.out.println("\n=== Iniciando procesamiento del Distributor ===");
        distributor.procesar();
        System.out.println("=== Procesamiento finalizado ===\n");

        // ===  Mostramos resultados ===
        for (int i = 0; i < salidas.size(); i++) {
            Slot s = salidas.get(i);
            System.out.println("Salida " + i + " contiene " + s.nMessages() + " mensaje(s).");
            for (Message msg : s.getMessages()) {
                System.out.println("Contenido del mensaje:\n" + msg.toString());
            }
            System.out.println("----------------------");
/*
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        /*
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
        }*/

        /*PRUEBA FILTER CORRECTA
        ArrayList<Slot> es = new ArrayList<>();
        Queue<Message> queueEntrada = new LinkedList<>();
        Slot sEntrada = new Slot(queueEntrada);

        ArrayList<Slot> exsl = new ArrayList<>();
        Queue<Message> queueSalida = new LinkedList<>();
        Slot sSalida = new Slot(queueSalida);
        exsl.add(sSalida);
        Document doc = null;
        try {
            File inputFile = new File("src/main/java/resources/cdcatalog.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        sEntrada.addMessage(new Message(doc, 1));
        es.add(sEntrada);

        Filter filtro = new Filter(taskEnum.FILTER, es, exsl, "//age"); // <-- CORREGIDO: XPath

        filtro.action();

        System.out.println("--- Resultado del Filtro ---");
        filtro.mock();
        */
    }
}
