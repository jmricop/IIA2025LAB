package me.integracion.iia;

import Tasks.enrouters.Distributor;
import Tasks.enrouters.Merger;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.io.File;
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
        //Queue<Message> colaSalida0 = new LinkedList<>();
        //Queue<Message> colaSalida1 = new LinkedList<>();

        ArrayList<Slot> entradas = new ArrayList<>();
        ArrayList<Slot> salidas = new ArrayList<>();
        entradas.add(new Slot());
        salidas.add(new Slot());
        salidas.add(new Slot());

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
 /*
        // --- INICIO PRUEBA SPLITTER ---
        System.out.println("--- INICIANDO PRUEBA DEL SPLITTER ---");

        // --- 1. PREPARACIÓN (Setup) ---
        // Asegúrate de que tu XML está en "resources/students.xml"
        String rutaXmlEntrada = "src/main/java/resources/cdcatalog.xml";
        String xPathReceta = "//students/student"; // La "receta" XPath para cortar

        // Creamos los "buzones" de entrada y salida
        Queue<Message> queueEntrada = new LinkedList<>();
        Slot slotEntrada = new Slot(queueEntrada);
        
        Queue<Message> queueSalida = new LinkedList<>();
        Slot slotSalida = new Slot(queueSalida);

        // Creamos las "paredes de buzones" (ArrayLists) que pide el constructor de Task
        ArrayList<Slot> se = new ArrayList<>();
        se.add(slotEntrada);
        ArrayList<Slot> sl = new ArrayList<>();
        sl.add(slotSalida);

        // Creamos la instancia del Splitter
        // (Asegúrate de que la importación "Tasks.transformer.Splitter" es correcta)
        Splitter splitter = new Splitter(taskEnum.SPLITTER, se, sl);
        
        // Configuramos la receta XPath
        splitter.setXPathExpression(xPathReceta);

        // --- 2. SIMULACIÓN (Simular el EntryPort) ---
        
        // Generamos un ID de lote
        int idLotePadre = IdUnico.getInstance().getNextIdDocument(); // (Esto devolverá 1)
        
        System.out.println("Cargando XML: " + rutaXmlEntrada);
        System.out.println("ID de Lote generado: " + idLotePadre);

        // Cargamos el XML desde el archivo
        Document docPadre = null;
        try {
            File inputFile = new File(rutaXmlEntrada);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            docPadre = dBuilder.parse(inputFile);
            docPadre.getDocumentElement().normalize();
        } catch (Exception e) {
            System.err.println("Error fatal al cargar el XML de prueba: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Creamos el mensaje "Padre" (como haría el EntryPort)
        // (Usamos el constructor Message(Document, int idDocument))
        Message msgPadre = new Message(docPadre, idLotePadre);

        // Ponemos el mensaje "Padre" en el buzón de entrada
        slotEntrada.addMessage(msgPadre);
        System.out.println("Mensaje 'Padre' puesto en el Buzón de Entrada.");

        // --- 3. EJECUCIÓN (Probar el Splitter) ---
        
        System.out.println("\n--- EJECUTANDO splitter.action() ---\n");
        // Aquí se llama a la lógica que hemos estado analizando
        splitter.action();

        // --- 4. VERIFICACIÓN (La Prueba) ---
        
        System.out.println("--- VERIFICANDO RESULTADOS ---");

        // PRUEBA 1: Comprobar el Buzón de Salida
        System.out.println("\n--- 1. Comprobando Buzón de Salida ---");
        System.out.println("Mensajes en Salida: " + slotSalida.nMessages()); // Debería ser 2

        int contadorHijos = 1;
        while (!slotSalida.isEmpty()) {
            Message msgHijo = slotSalida.getFirstMessage();
            System.out.println("\n--- Hijo " + contadorHijos++ + " ---");
            System.out.println("  idDocument (Lote): " + msgHijo.getIdDocument()); // Debería ser 1
            System.out.println("  idSegment (Trozo): " + msgHijo.getIdSegment());  // Debería ser 1, luego 2
            System.out.println("  nSegments (Total): " + msgHijo.getnSegments());  // Debería ser 2
            System.out.println("  Contenido XML:");
            System.out.println(msgHijo.toString()); // Usa el .toString() de tu Message
        }

        // PRUEBA 2: Comprobar el Diccionario (el "Caparazón")
        System.out.println("\n--- 2. Comprobando el Diccionario ---");
        Diccionario dict = Diccionario.getInstance();
        ValoresDiccionario caparazonGuardado = dict.get(idLotePadre); // Pide el Lote 1

        if (caparazonGuardado != null) {
            System.out.println("¡ÉXITO! Se encontró el caparazón del Lote " + idLotePadre);
            System.out.println("Ruta XPath guardada: " + caparazonGuardado.getXPathDondeInsertar()); // Debería ser "//students"
            System.out.println("Contenido del Caparazón guardado:");
            // Usamos el helper "docToString" para imprimir el Document
            System.out.println(docToString(caparazonGuardado.getCaparazon())); // Debería ser <students></students>
        } else {
            System.out.println("¡FALLO! No se encontró el caparazón del Lote " + idLotePadre + " en el Diccionario.");
        }
        
        System.out.println("--- FIN DE LA PRUEBA ---");
        // --- FIN PRUEBA SPLITTER ---
             */
            // --- INICIO PRUEBA MERGER ---
            System.out.println("\n--- INICIANDO PRUEBA DEL MERGER ---");

            // --- 1. PREPARACIÓN (Setup) ---
            // El Merger es lo opuesto: Múltiples entradas, 1 salida.
            // Creamos los 2 buzones de ENTRADA
            Queue<Message> queueEntradaA = new LinkedList<>();
            Slot slotEntradaA = new Slot();

            Queue<Message> queueEntradaB = new LinkedList<>();
            Slot slotEntradaB = new Slot();

            // Creamos el ÚNICO buzón de SALIDA
            Queue<Message> queueSalida = new LinkedList<>();
            Slot slotSalida = new Slot();

            // Creamos la "pared de buzones" de entrada (con 2 buzones)
            ArrayList<Slot> se = new ArrayList<>();
            se.add(slotEntradaA);
            se.add(slotEntradaB);

            // Creamos la "pared de buzones" de salida (con 1 buzón)
            ArrayList<Slot> sl = new ArrayList<>();
            sl.add(slotSalida);

            // Creamos la instancia del Merger
            // (Asegúrate de importar Tasks.transformer.Merger)
            Merger merger = new Merger(taskEnum.MERGER, se, sl);

            // --- 2. SIMULACIÓN (Poner datos en los buzones de entrada) ---
            System.out.println("Poniendo mensajes en los buzones de entrada...");

            // Cargamos un XML de prueba (podemos reusar el de students)
            Document docPrueba = null;
            try {
                // Reutilizamos el 'docToString' de antes si existe, o cargamos de nuevo
                File inputFile = new File("src/main/java/resources/cdcatalog.xml");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                docPrueba = dBuilder.parse(inputFile);
            } catch (Exception e) {
                System.err.println("Error cargando XML para el Merger: " + e.getMessage());
                return;
            }

            // Creamos 3 mensajes de prueba
            // (Usamos el constructor simple Message(doc, id) )
            Message msg1 = new Message(docPrueba, 201);
            Message msg2 = new Message(docPrueba, 202);
            Message msg3 = new Message(docPrueba, 203);

            // Ponemos 2 mensajes en el Buzón A y 1 en el Buzón B
            slotEntradaA.addMessage(msg1);
            slotEntradaA.addMessage(msg2);
            slotEntradaB.addMessage(msg3);

            System.out.println("  Buzón A tiene: " + slotEntradaA.nMessages() + " mensajes");
            System.out.println("  Buzón B tiene: " + slotEntradaB.nMessages() + " mensajes");
            System.out.println("  Buzón Salida tiene: " + slotSalida.nMessages() + " mensajes");

            // --- 3. EJECUCIÓN (Probar el Merger) ---
            System.out.println("\n--- EJECUTANDO merger.action() en bucle ---");

            // Llamamos a action() repetidamente HASTA QUE ambas entradas estén vacías
            int iteraciones = 0;
            while (!slotEntradaA.isEmpty() || !slotEntradaB.isEmpty()) {
                merger.action();
                iteraciones++;
            }
            System.out.println("El Merger 'action()' se ejecutó " + iteraciones + " veces.");

            // --- 4. VERIFICACIÓN (La Prueba) ---
            System.out.println("\n--- VERIFICANDO RESULTADOS DEL MERGER ---");
            System.out.println("  Buzón A tiene: " + slotEntradaA.nMessages() + " mensajes (Debería ser 0)");
            System.out.println("  Buzón B tiene: " + slotEntradaB.nMessages() + " mensajes (Debería ser 0)");
            System.out.println("  Buzón Salida tiene: " + slotSalida.nMessages() + " mensajes (Debería ser 3)");

            System.out.println("\n--- Contenido del Buzón de Salida ---");
            while (!slotSalida.isEmpty()) {
                Message msg = slotSalida.getFirstMessage();
                // Imprimimos el idDocument para ver que están todos
                System.out.println("  Mensaje encontrado con idDocument: " + msg.getIdDocument());
            }

            System.out.println("--- FIN DE LA PRUEBA MERGER ---");
            // --- FIN PRUEBA MERGER ---

            // ... (Este código va DENTRO de tu 'public static void main(String[] args)')
            /*
        // --- INICIO PRUEBA MERGER ---
        // (Tu código de prueba del Merger)
        System.out.println("--- FIN DE LA PRUEBA MERGER ---");
        // --- FIN PRUEBA MERGER ---
             */
            // --- INICIO PRUEBA AGGREGATOR ---
            /*
        System.out.println("\n--- INICIANDO PRUEBA DEL AGGREGATOR ---");

        // --- 1. PREPARACIÓN (Setup) ---
        // 1 entrada (donde llegan los trozos), 1 salida (donde sale el re-ensamblado)
        
        Queue<Message> queueEntradaAgg = new LinkedList<>();
        Slot slotEntradaAgg = new Slot(queueEntradaAgg);
        
        Queue<Message> queueSalidaAgg = new LinkedList<>();
        Slot slotSalidaAgg = new Slot(queueSalidaAgg);

        // Creamos las "paredes de buzones"
        ArrayList<Slot> seAgg = new ArrayList<>();
        seAgg.add(slotEntradaAgg);
        ArrayList<Slot> slAgg = new ArrayList<>();
        slAgg.add(slotSalidaAgg);

        // Creamos la instancia del Aggregator
        Aggregator aggregator = new Aggregator(taskEnum.AGGREGATOR, seAgg, slAgg);
        
        // --- 2. SIMULACIÓN (La parte más importante) ---
        
        // Definimos un ID de Lote para esta prueba
        int idLotePrueba = 301;
        System.out.println("Prueba se ejecutará con ID de Lote: " + idLotePrueba);

        // 2a. Simular que el SPLITTER guardó el "Caparazón"
        System.out.println("Guardando 'Caparazón' en el Diccionario...");
        try {
            // Creamos un caparazón (<students></students>)
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document caparazonDoc = db.newDocument();
            Element raiz = caparazonDoc.createElement("students"); // El nodo padre era <students>
            caparazonDoc.appendChild(raiz);
            
            String xPathInsertar = "//students"; // El XPath que guardó el Splitter
            
            // Creamos la "caja" (ValoresDiccionario)
            ValoresDiccionario vD = new ValoresDiccionario(xPathInsertar, caparazonDoc);
            
            // Guardamos la "caja" en el almacén
            Diccionario.getInstance().put(idLotePrueba, vD);

        } catch (Exception e) {
            System.err.println("Error creando el caparazón de prueba: " + e.getMessage());
            return;
        }
        
        // 2b. Simular la llegada de los mensajes "Hijos"
        System.out.println("Creando y añadiendo mensajes 'Hijos' al buzón de entrada...");
        try {
            // Creamos el Hijo 1 (<student id="1">...)
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document docHijo1 = db.parse(new File("src/main/java/resources/hijo1.xml"));
            
            // Creamos el Hijo 2 (<student id="2">...)
            Document docHijo2 = db.parse(new File("src/main/java/resources/hijo2.xml"));
            
            // Creamos los "sobres" (Message) con los metadatos correctos
            // Message(Document, idDocument, idSegment, nSegments)
            Message msgHijo1 = new Message(docHijo1, idLotePrueba, 1, 2); // Trozo 1 de 2
            Message msgHijo2 = new Message(docHijo2, idLotePrueba, 2, 2); // Trozo 2 de 2

            // Ponemos los hijos en el buzón de entrada (simulando la llegada)
            slotEntradaAgg.addMessage(msgHijo1);
            slotEntradaAgg.addMessage(msgHijo2);

        } catch (Exception e) {
            System.err.println("Error creando los XML de los hijos: " + e.getMessage());
            System.err.println("Asegúrate de que 'hijo1.xml' y 'hijo2.xml' existen en 'src/main/java/resources/'");
            return;
        }
        
        System.out.println("  Buzón Entrada tiene: " + slotEntradaAgg.nMessages() + " mensajes");

        // --- 3. EJECUCIÓN (Probar el Aggregator) ---
        System.out.println("\n--- EJECUTANDO aggregator.action() en bucle ---");
        
        // Llamamos a action() hasta que la entrada esté vacía
        while (!slotEntradaAgg.isEmpty()) {
            aggregator.action();
        }

        // --- 4. VERIFICACIÓN (La Prueba) ---
        System.out.println("\n--- VERIFICANDO RESULTADOS DEL AGGREGATOR ---");
        System.out.println("  Buzón Entrada tiene: " + slotEntradaAgg.nMessages() + " mensajes (Debería ser 0)");
        System.out.println("  Buzón Salida tiene: " + slotSalidaAgg.nMessages() + " mensajes (Debería ser 1)");

        // PRUEBA 1: Comprobar el Buzón de Salida
        if (slotSalidaAgg.nMessages() == 1) {
            Message msgFinal = slotSalidaAgg.getFirstMessage();
            System.out.println("\n--- Mensaje Re-ensamblado ---");
            System.out.println("  idDocument (Lote): " + msgFinal.getIdDocument()); // Debería ser 301
            System.out.println("  idSegment: " + msgFinal.getIdSegment());  // Debería ser -1 (porque es un msg padre)
            System.out.println("  Contenido XML Re-ensamblado:");
            System.out.println(msgFinal.toString()); // Debería mostrar <students><student.../><student.../></students>
        } else {
            System.out.println("¡FALLO! El Aggregator no produjo 1 mensaje de salida.");
        }

        // PRUEBA 2: Comprobar que el Diccionario esté limpio
        System.out.println("\n--- 2. Comprobando el Diccionario ---");
        ValoresDiccionario caparazonRestante = Diccionario.getInstance().get(idLotePrueba);
        
        if (caparazonRestante == null) {
            System.out.println("¡ÉXITO! El caparazón del Lote " + idLotePrueba + " fue borrado del Diccionario.");
        } else {
            System.out.println("¡FALLO! El Aggregator no borró el caparazón del Diccionario.");
        }
        
        System.out.println("--- FIN DE LA PRUEBA AGGREGATOR ---");
        // --- FIN PRUEBA AGGREGATOR ---
             */
        }

        /*
    private static String docToString(Document doc) {
         try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter stringWriter = new StringWriter();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Para que se vea bonito
            transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (TransformerException ex) {
            System.out.println("Error tranformacion en docToString: " + ex.getMessage());
            return "Error en la conversion";
        }
    }*/
    }   
}
