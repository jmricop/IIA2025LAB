package me.integracion.iia;

import Conector.connectors.*;
import Port.Port;
import Tasks.Task;
import Tasks.enrouters.*;
import Tasks.modifiers.*;
import Tasks.transformators.*;
import Tasks.taskEnum;
import common.*;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import org.w3c.dom.*;

public class CafeteriaPruebas {

    public static void main(String[] args) {
        System.out.println(">>> INICIANDO SISTEMA DE INTEGRACIÓN CAFETERÍA (Modo Manual) <<<");

        // ===============================================================
        // 1. DEFINICIÓN DE "CABLES" (SLOTS)
        // Siguiendo el diagrama de la página 2 del PDF 'Cafe Implementation'
        // ===============================================================
        // --- Troncal Principal ---
        Slot s0_Entrada = new Slot(new LinkedList<>());
        Slot s1_SplitterOut = new Slot(new LinkedList<>());
        Slot s2_ConId = new Slot(new LinkedList<>());

        // --- Distribución ---
        Slot s3_Fria = new Slot(new LinkedList<>());
        Slot s4_Caliente = new Slot(new LinkedList<>());

        // --- Rama Fría (Pasos d, e) ---
        Slot s5_Fria_Copia = new Slot(new LinkedList<>());      // Copia para guardar
        Slot s6_Fria_ParaBarman = new Slot(new LinkedList<>()); // Copia para enviar
        Slot s7_Fria_Traducida = new Slot(new LinkedList<>());  // Salida del Translator
        Slot s8_Fria_Respuesta = new Slot(new LinkedList<>());  // Salida del Barman (Simulado)
        Slot s9_Fria_Corr_Main = new Slot(new LinkedList<>());  // Salida Correlator (Mensaje Principal)
        Slot s9_Fria_Corr_Ctx = new Slot(new LinkedList<>());   // Salida Correlator (Contexto/Respuesta)
        Slot s10_Fria_Enriched = new Slot(new LinkedList<>());  // Salida Final Rama Fría

        // --- Rama Caliente (Pasos f, g) ---
        Slot s11_Cal_Copia = new Slot(new LinkedList<>());
        Slot s12_Cal_ParaBarman = new Slot(new LinkedList<>());
        Slot s13_Cal_Traducida = new Slot(new LinkedList<>());
        Slot s14_Cal_Respuesta = new Slot(new LinkedList<>());
        Slot s15_Cal_Corr_Main = new Slot(new LinkedList<>());
        Slot s15_Cal_Corr_Ctx = new Slot(new LinkedList<>());
        Slot s16_Cal_Enriched = new Slot(new LinkedList<>());

        // --- Reunificación ---
        Slot s17_MergerOut = new Slot(new LinkedList<>());
        Slot s18_SalidaFinal = new Slot(new LinkedList<>());

        // ===============================================================
        // 2. CONFIGURACIÓN DE TAREAS 
        // ===============================================================
        Port portEntrada = new Port(s0_Entrada) {
            public Message read() {
                return null;
            }

            public void write(Message m) {
                buffer.addMessage(m);
            }
        };

        ConnectorEntryFile conectorEntrada = new ConnectorEntryFile(portEntrada, "src/main/java/resources/inputs");

        ArrayList<Slot> splitIn = new ArrayList<>(List.of(s0_Entrada));
        ArrayList<Slot> splitOut = new ArrayList<>(List.of(s1_SplitterOut));
        Splitter splitter = new Splitter(taskEnum.SPLITTER, splitIn, splitOut);
        splitter.setXPathExpression("//drinks/drink");

        // --- ID SETTER (Para que el Distributor/Correlator funcionen bien) ---
        ArrayList<Slot> idIn = new ArrayList<>(List.of(s1_SplitterOut));
        ArrayList<Slot> idOut = new ArrayList<>(List.of(s2_ConId));
        CorrelationIdSetter idSetter = new CorrelationIdSetter(taskEnum.CORRELATORIDSETTER, idIn, idOut);

        ArrayList<Slot> distIn = new ArrayList<>(List.of(s2_ConId));
        ArrayList<Slot> distOut = new ArrayList<>(List.of(s3_Fria, s4_Caliente));
        String[] condiciones = {"hot", "cold"};
        Distributor distributor = new Distributor(taskEnum.DISTRIBUTOR, distIn, distOut, condiciones, 2, "tipo");

        // ================= RAMA FRÍA =================
        ArrayList<Slot> repFriaIn = new ArrayList<>(List.of(s3_Fria));
        ArrayList<Slot> repFriaOut = new ArrayList<>(List.of(s5_Fria_Copia, s6_Fria_ParaBarman));
        Replicator repFria = new Replicator(repFriaIn, repFriaOut, taskEnum.ROUTER);

        // Nota: Usamos tu Translator. Si no tienes un XSLT específico para esto, 
        // usamos uno genérico o puedes comentar esta tarea si falla por falta de archivo.
        ArrayList<Slot> transFriaIn = new ArrayList<>(List.of(s6_Fria_ParaBarman));
        ArrayList<Slot> transFriaOut = new ArrayList<>(List.of(s7_Fria_Traducida));
        // Usamos tu transform.xsl como ejemplo para que no falle el código
        Translator transFria = new Translator(taskEnum.TRANSLATOR, transFriaIn, transFriaOut, "src/main/java/resources/transform.xsl");

        // Aquí usamos tu truco de crear una Task "al vuelo" para meter la lógica de BD
        ArrayList<Slot> barmanFrioIn = new ArrayList<>(List.of(s7_Fria_Traducida));
        ArrayList<Slot> barmanFrioOut = new ArrayList<>(List.of(s8_Fria_Respuesta));

        Task barmanFrio = new Task(taskEnum.ENRICHER, barmanFrioIn, barmanFrioOut) {
            @Override
            public void action() {
                if (isEmpty(0)) {
                    return;
                }
                Message msg = getEntryMessage(0);
                // Lógica "hardcoded" permitida en este enfoque
                try {
                    Document doc = msg.getDocument();
                    // Ojo: Como pasamos por el Translator, el XML podría haber cambiado.
                    // Si usas el transform.xsl, buscarías tags diferentes. 
                    // Para simplificar, asumimos que el XML sigue teniendo info legible.

                    // Simulación de consulta a BD
                    String estado = consultarStockBD("coca-cola"); // Simplificado para el ejemplo

                    // Crear XML de respuesta
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    Document respDoc = dbf.newDocumentBuilder().newDocument();
                    Element root = respDoc.createElement("respuesta_barman");
                    root.setTextContent("FRIO: " + estado);
                    respDoc.appendChild(root);

                    // IMPORTANTE: Mantener IDs para el Correlator
                    // Usamos el constructor que preserva la info de segmentos
                    Message responseMsg = new Message(
                            respDoc,
                            msg.getIdDocument(),
                            msg.getIdSegment(), // <--- IMPORTANTE
                            msg.getnSegments() // <--- IMPORTANTE
                    );
                    responseMsg.setcorrelatorId(msg.getCorrelatorId());

                    setMensajeSalida(responseMsg, 0);
                    System.out.println("Barman Frío: Bebida procesada.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mock() {
            }
        };

        ArrayList<Slot> corrFriaIn = new ArrayList<>(List.of(s5_Fria_Copia, s8_Fria_Respuesta));
        ArrayList<Slot> corrFriaOut = new ArrayList<>(List.of(s9_Fria_Corr_Main, s9_Fria_Corr_Ctx));
        Correlator corrFria = new Correlator(taskEnum.CORRELATOR, corrFriaIn, corrFriaOut);

        ArrayList<Slot> enrichFriaIn = new ArrayList<>(List.of(s9_Fria_Corr_Main, s9_Fria_Corr_Ctx));
        ArrayList<Slot> enrichFriaOut = new ArrayList<>(List.of(s10_Fria_Enriched));
        ContextEnricher enrichFria = new ContextEnricher(enrichFriaIn, enrichFriaOut, "//respuesta_barman", "//drink", taskEnum.ENRICHER);

        // ================= RAMA CALIENTE =================
        // Repetimos la estructura exacta de arriba (Pasos f, g, h...)
        // Replicator
        ArrayList<Slot> repCalIn = new ArrayList<>(List.of(s4_Caliente));
        ArrayList<Slot> repCalOut = new ArrayList<>(List.of(s11_Cal_Copia, s12_Cal_ParaBarman));
        Replicator repCal = new Replicator(repCalIn, repCalOut, taskEnum.ROUTER);

        // Translator
        ArrayList<Slot> transCalIn = new ArrayList<>(List.of(s12_Cal_ParaBarman));
        ArrayList<Slot> transCalOut = new ArrayList<>(List.of(s13_Cal_Traducida));
        Translator transCal = new Translator(taskEnum.TRANSLATOR, transCalIn, transCalOut, "src/main/java/resources/transform.xsl");

        // Barman Caliente (Anónimo)
        ArrayList<Slot> barmanCalIn = new ArrayList<>(List.of(s13_Cal_Traducida));
        ArrayList<Slot> barmanCalOut = new ArrayList<>(List.of(s14_Cal_Respuesta));
        Task barmanCal = new Task(taskEnum.ENRICHER, barmanCalIn, barmanCalOut) {
            @Override
            public void action() {
                if (isEmpty(0)) {
                    return;
                }
                Message msg = getEntryMessage(0);
                try {
                    String estado = consultarStockBD("cafe"); // Simplificado

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    Document respDoc = dbf.newDocumentBuilder().newDocument();
                    Element root = respDoc.createElement("respuesta_barman");
                    root.setTextContent("CALIENTE: " + estado);
                    respDoc.appendChild(root);

                    // Usamos el constructor que preserva la info de segmentos
                    Message responseMsg = new Message(
                            respDoc,
                            msg.getIdDocument(),
                            msg.getIdSegment(), // <--- IMPORTANTE
                            msg.getnSegments() // <--- IMPORTANTE
                    );
                    responseMsg.setcorrelatorId(msg.getCorrelatorId());
                    setMensajeSalida(responseMsg, 0);
                    System.out.println("Barman Caliente: Bebida procesada.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void mock() {
            }
        };

        // Correlator
        ArrayList<Slot> corrCalIn = new ArrayList<>(List.of(s11_Cal_Copia, s14_Cal_Respuesta));
        ArrayList<Slot> corrCalOut = new ArrayList<>(List.of(s15_Cal_Corr_Main, s15_Cal_Corr_Ctx));
        Correlator corrCal = new Correlator(taskEnum.CORRELATOR, corrCalIn, corrCalOut);

        // Enricher
        ArrayList<Slot> enrichCalIn = new ArrayList<>(List.of(s15_Cal_Corr_Main, s15_Cal_Corr_Ctx));
        ArrayList<Slot> enrichCalOut = new ArrayList<>(List.of(s16_Cal_Enriched));
        ContextEnricher enrichCal = new ContextEnricher(enrichCalIn, enrichCalOut, "//respuesta_barman", "//drink", taskEnum.ENRICHER);

        // ================= REUNIFICACIÓN =================
        ArrayList<Slot> mergIn = new ArrayList<>(List.of(s10_Fria_Enriched, s16_Cal_Enriched));
        ArrayList<Slot> mergOut = new ArrayList<>(List.of(s17_MergerOut));
        Merger merger = new Merger(taskEnum.MERGER, mergIn, mergOut);

        ArrayList<Slot> aggIn = new ArrayList<>(List.of(s17_MergerOut));
        ArrayList<Slot> aggOut = new ArrayList<>(List.of(s18_SalidaFinal));
        Aggregator aggregator = new Aggregator(taskEnum.AGGREGATOR, aggIn, aggOut);

        Port portSalida = new Port(s18_SalidaFinal) {
            public Message read() {
                return buffer.getFirstMessage();
            }

            public void write(Message m) {
            }
        };
        ConnectorExitFile conectorSalida = new ConnectorExitFile(portSalida, "output_entregas", "comanda_final_");

        // ===============================================================
        // 3. MOTOR DE EJECUCIÓN MANUAL (El "While Loop")
        // ===============================================================
        System.out.println(">>> Ejecutando bucles de procesamiento...");

        // Simulamos X ciclos de reloj para procesar todo
        for (int i = 0; i < 20; i++) {
            conectorEntrada.action();

            splitter.action();

            // Si hay algo en la salida del splitter, asignamos ID correlacion
            if (!s1_SplitterOut.isEmpty()) {
                idSetter.action();
            }

            distributor.procesar(); // Ojo: en tu código se llama procesar(), no action()

            // --- RAMA FRIA ---
            if (!s3_Fria.isEmpty()) {
                repFria.action();
            }
            if (!s6_Fria_ParaBarman.isEmpty()) {
                transFria.action();
            }
            if (!s7_Fria_Traducida.isEmpty()) {
                barmanFrio.action();
            }
            // Correlator necesita ambos mensajes, así que ejecutamos si hay en alguno de sus inputs
            if (!s5_Fria_Copia.isEmpty() || !s8_Fria_Respuesta.isEmpty()) {
                corrFria.action();
            }
            if (!s9_Fria_Corr_Main.isEmpty()) {
                enrichFria.action();
            }

            // --- RAMA CALIENTE ---
            if (!s4_Caliente.isEmpty()) {
                repCal.action();
            }
            if (!s12_Cal_ParaBarman.isEmpty()) {
                transCal.action();
            }
            if (!s13_Cal_Traducida.isEmpty()) {
                barmanCal.action();
            }
            if (!s11_Cal_Copia.isEmpty() || !s14_Cal_Respuesta.isEmpty()) {
                corrCal.action();
            }
            if (!s15_Cal_Corr_Main.isEmpty()) {
                enrichCal.action();
            }

            // --- FINAL ---
            if (!s10_Fria_Enriched.isEmpty() || !s16_Cal_Enriched.isEmpty()) {
                merger.action();
            }
            if (!s17_MergerOut.isEmpty()) {
                aggregator.action();
            }

            conectorSalida.action();

            // Pequeña pausa para no saturar la consola en la demo
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }

        System.out.println(">>> Ejecución finalizada.");
    }

    // Método auxiliar para simular la DB sin ensuciar el main
    private static String consultarStockBD(String producto) {
        try {
            Connection conn = GestorBaseDatos.getInstance().getConnection();
            // OJO: Aquí asumimos que tu GestorBaseDatos funciona y la tabla 'inventario' existe
            PreparedStatement stmt = conn.prepareStatement("SELECT cantidad FROM inventario WHERE producto = ?");
            stmt.setString(1, producto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("cantidad") > 0) {
                return "PREPARADA";
            }
        } catch (Exception e) {
            System.out.println("Error DB: " + e.getMessage());
        }
        return "AGOTADA";
    }
}
