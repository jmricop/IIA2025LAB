package me.integracion.iia;

import Conector.connectors.ConnectorEntryFile;
import Conector.connectors.ConnectorExitFile;
import Port.Port;
import Tasks.Task; // Tu clase base
import Tasks.enrouters.*;
import Tasks.modifiers.*;
import Tasks.transformators.*;
import Tasks.taskEnum;
import common.GestorBaseDatos; // El singleton de la DB
import common.Message;
import common.Slot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CafeteriaPruebas {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO CAFETERÍA (Lógica en Main) ===");
        System.out.println("=== INICIANDO CAFETERÍA (Lógica en Main) ===");

        // --- DEPURACIÓN DE RUTA ---
        // Esto te dirá dónde está buscando Java
        java.io.File carpeta = new java.io.File("src/main/java/resources");
        System.out.println("--> Buscando XMLs en: " + carpeta.getAbsolutePath());
        System.out.println("--> ¿Existe la carpeta?: " + carpeta.exists());
        
        if (carpeta.exists()) {
            String[] archivos = carpeta.list();
            if (archivos == null || archivos.length == 0) {
                System.out.println("--> ¡La carpeta existe pero ESTÁ VACÍA!");
            } else {
                System.out.println("--> Archivos encontrados: " + java.util.Arrays.toString(archivos));
            }
        } else {
            System.out.println("--> ERROR: La ruta no es correcta desde donde se ejecuta el programa.");
            // Intenta mostrar dónde estamos para orientarte
            System.out.println("--> Directorio de ejecución actual: " + new java.io.File(".").getAbsolutePath());
        }

        // --- 1. DEFINICIÓN DE SLOTS (Cables) ---
        // (Igual que antes, definimos la topología)
        Slot s0_Entry = new Slot(new LinkedList<>());
        Slot s1_SplitterOut = new Slot(new LinkedList<>());
        Slot s2_CorrIdOut = new Slot(new LinkedList<>());
        Slot s3_Cold = new Slot(new LinkedList<>());
        Slot s4_Hot = new Slot(new LinkedList<>());
        
        // Rama Fría
        Slot s5_RepCold_Direct = new Slot(new LinkedList<>());
        Slot s6_RepCold_ToBarman = new Slot(new LinkedList<>());
        Slot s7_BarmanColdResponse = new Slot(new LinkedList<>()); // Salida del Barman anónimo
        Slot s8_CorrColdOut_Main = new Slot(new LinkedList<>());
        Slot s8_CorrColdOut_Context = new Slot(new LinkedList<>());
        Slot s9_EnrichedCold = new Slot(new LinkedList<>());
        
        // Rama Caliente
        Slot s10_RepHot_Direct = new Slot(new LinkedList<>());
        Slot s11_RepHot_ToBarman = new Slot(new LinkedList<>());
        Slot s12_BarmanHotResponse = new Slot(new LinkedList<>());
        Slot s13_CorrHotOut_Main = new Slot(new LinkedList<>());
        Slot s13_CorrHotOut_Context = new Slot(new LinkedList<>());
        Slot s14_EnrichedHot = new Slot(new LinkedList<>());
        
        // Final
        Slot s15_MergerOut = new Slot(new LinkedList<>());
        Slot s16_AggOut = new Slot(new LinkedList<>());

        // --- 2. COMPONENTES GENÉRICOS (Tu estructura estándar) ---

        Port pEntry = new Port(s0_Entry) { public Message read() { return null; } public void write(Message m) { buffer.addMessage(m); }};
        ConnectorEntryFile entryFile = new ConnectorEntryFile(pEntry, "src/main/java/resources");
        
        ArrayList<Slot> splitIn = new ArrayList<>(); splitIn.add(s0_Entry);
        ArrayList<Slot> splitOut = new ArrayList<>(); splitOut.add(s1_SplitterOut);
        Splitter splitter = new Splitter(taskEnum.SPLITTER, splitIn, splitOut);
        splitter.setXPathExpression("//lista_bebidas/bebida");

        ArrayList<Slot> idIn = new ArrayList<>(); idIn.add(s1_SplitterOut);
        ArrayList<Slot> idOut = new ArrayList<>(); idOut.add(s2_CorrIdOut);
        CorrelationIdSetter idSetter = new CorrelationIdSetter(taskEnum.CORRELATORIDSETTER, idIn, idOut);

        ArrayList<Slot> distIn = new ArrayList<>(); distIn.add(s2_CorrIdOut);
        ArrayList<Slot> distOut = new ArrayList<>(); distOut.add(s3_Cold); distOut.add(s4_Hot);
        String[] cond = {"fria", "caliente"};
        Distributor distributor = new Distributor(taskEnum.DISTRIBUTOR, distIn, distOut, cond, 2, "tipo");

        // Replicators (Usando tu clase estándar corregida)
        ArrayList<Slot> repColdIn = new ArrayList<>(); repColdIn.add(s3_Cold);
        ArrayList<Slot> repColdOut = new ArrayList<>(); repColdOut.add(s5_RepCold_Direct); repColdOut.add(s6_RepCold_ToBarman);
        Replicator repCold = new Replicator(repColdIn, repColdOut, taskEnum.ROUTER);

        ArrayList<Slot> repHotIn = new ArrayList<>(); repHotIn.add(s4_Hot);
        ArrayList<Slot> repHotOut = new ArrayList<>(); repHotOut.add(s10_RepHot_Direct); repHotOut.add(s11_RepHot_ToBarman);
        Replicator repHot = new Replicator(repHotIn, repHotOut, taskEnum.ROUTER);


        // --- 3. IMPLEMENTACIÓN "AD-HOC" (Aquí está la magia) ---
        
        // Definimos la Tarea del Barman Frío directamente aquí
        ArrayList<Slot> bColdIn = new ArrayList<>(); bColdIn.add(s6_RepCold_ToBarman);
        ArrayList<Slot> bColdOut = new ArrayList<>(); bColdOut.add(s7_BarmanColdResponse);
        
        Task barmanFrioTask = new Task(taskEnum.ENRICHER, bColdIn, bColdOut) {
            @Override
            public void action() {
                if (isEmpty(0)) return;
                Message msg = getEntryMessage(0);
                Document doc = msg.getDocument();
                // LÓGICA ESPECÍFICA DE ESTA PRÁCTICA
                try {
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    String nombre = (String) xPath.evaluate("//nombre", doc, XPathConstants.STRING);
                    
                    Connection conn = GestorBaseDatos.getInstance().getConnection();
                    // Consulta específica para fríos
                    PreparedStatement stmt = conn.prepareStatement("SELECT cantidad FROM inventario WHERE producto = ?");
                    stmt.setString(1, nombre);
                    ResultSet rs = stmt.executeQuery();
                    
                    String estado = "AGOTADA";
                    if (rs.next() && rs.getInt("cantidad") > 0) {
                        conn.createStatement().execute("UPDATE inventario SET cantidad = cantidad - 1 WHERE producto = '" + nombre + "'");
                        estado = "PREPARADA (Frio)";
                    }
                    rs.close(); stmt.close();
                    
                    // Añadimos la respuesta XML
                    Element resp = doc.createElement("respuesta_barman");
                    resp.setTextContent(estado);
                    doc.getDocumentElement().appendChild(resp);
                    
                    setMensajeSalida(msg, 0);
                    System.out.println("🧊 Barman Frío procesó: " + nombre + " -> " + estado);
                } catch (Exception e) { e.printStackTrace(); }
            }
            @Override public void mock() {}
        };

        // Definimos la Tarea del Barman Caliente (similar pero podría tener otra lógica)
        ArrayList<Slot> bHotIn = new ArrayList<>(); bHotIn.add(s11_RepHot_ToBarman);
        ArrayList<Slot> bHotOut = new ArrayList<>(); bHotOut.add(s12_BarmanHotResponse);
        
        Task barmanCalienteTask = new Task(taskEnum.ENRICHER, bHotIn, bHotOut) {
            @Override
            public void action() {
                if (isEmpty(0)) return;
                Message msg = getEntryMessage(0);
                Document doc = msg.getDocument();
                try {
                    XPath xPath = XPathFactory.newInstance().newXPath();
                    String nombre = (String) xPath.evaluate("//nombre", doc, XPathConstants.STRING);
                    
                    Connection conn = GestorBaseDatos.getInstance().getConnection();
                    PreparedStatement stmt = conn.prepareStatement("SELECT cantidad FROM inventario WHERE producto = ?");
                    stmt.setString(1, nombre);
                    ResultSet rs = stmt.executeQuery();
                    
                    String estado = "AGOTADA";
                    if (rs.next() && rs.getInt("cantidad") > 0) {
                        conn.createStatement().execute("UPDATE inventario SET cantidad = cantidad - 1 WHERE producto = '" + nombre + "'");
                        estado = "PREPARADA (Caliente)";
                    }
                    rs.close(); stmt.close();
                    
                    Element resp = doc.createElement("respuesta_barman");
                    resp.setTextContent(estado);
                    doc.getDocumentElement().appendChild(resp);
                    
                    setMensajeSalida(msg, 0);
                    System.out.println("☕ Barman Caliente procesó: " + nombre + " -> " + estado);
                } catch (Exception e) { e.printStackTrace(); }
            }
            @Override public void mock() {}
        };


        // --- 4. CONTINUAMOS CON ESTRUCTURA ESTÁNDAR ---
        
        // Correlators
        ArrayList<Slot> corrColdIn = new ArrayList<>(); corrColdIn.add(s5_RepCold_Direct); corrColdIn.add(s7_BarmanColdResponse);
        ArrayList<Slot> corrColdOut = new ArrayList<>(); corrColdOut.add(s8_CorrColdOut_Main); corrColdOut.add(s8_CorrColdOut_Context);
        Correlator corrCold = new Correlator(taskEnum.CORRELATOR, corrColdIn, corrColdOut);

        ArrayList<Slot> corrHotIn = new ArrayList<>(); corrHotIn.add(s10_RepHot_Direct); corrHotIn.add(s12_BarmanHotResponse);
        ArrayList<Slot> corrHotOut = new ArrayList<>(); corrHotOut.add(s13_CorrHotOut_Main); corrHotOut.add(s13_CorrHotOut_Context);
        Correlator corrHot = new Correlator(taskEnum.CORRELATOR, corrHotIn, corrHotOut);

        // Enrichers (Usan la respuesta generada por las tareas anónimas)
        ArrayList<Slot> enrichColdIn = new ArrayList<>(); enrichColdIn.add(s8_CorrColdOut_Main); enrichColdIn.add(s8_CorrColdOut_Context);
        ArrayList<Slot> enrichColdOut = new ArrayList<>(); enrichColdOut.add(s9_EnrichedCold);
        ContextEnricher enrichCold = new ContextEnricher(enrichColdIn, enrichColdOut, "//respuesta_barman", "//bebida", taskEnum.ENRICHER);

        ArrayList<Slot> enrichHotIn = new ArrayList<>(); enrichHotIn.add(s13_CorrHotOut_Main); enrichHotIn.add(s13_CorrHotOut_Context);
        ArrayList<Slot> enrichHotOut = new ArrayList<>(); enrichHotOut.add(s14_EnrichedHot);
        ContextEnricher enrichHot = new ContextEnricher(enrichHotIn, enrichHotOut, "//respuesta_barman", "//bebida", taskEnum.ENRICHER);

        // Merger & Aggregator
        ArrayList<Slot> mergerIn = new ArrayList<>(); mergerIn.add(s9_EnrichedCold); mergerIn.add(s14_EnrichedHot);
        ArrayList<Slot> mergerOut = new ArrayList<>(); mergerOut.add(s15_MergerOut);
        Merger merger = new Merger(taskEnum.MERGER, mergerIn, mergerOut);

        ArrayList<Slot> aggIn = new ArrayList<>(); aggIn.add(s15_MergerOut);
        ArrayList<Slot> aggOut = new ArrayList<>(); aggOut.add(s16_AggOut);
        Aggregator aggregator = new Aggregator(taskEnum.AGGREGATOR, aggIn, aggOut);

        Port pExit = new Port(s16_AggOut) { public Message read() { return buffer.getFirstMessage(); } public void write(Message m) {} };
        ConnectorExitFile exit = new ConnectorExitFile(pExit, "output_entregas", "final_");


        // --- 5. EJECUCIÓN (Bucle simple) ---
        System.out.println(">>> Ejecutando flujo...");
        
        // Esta lógica de "tick" manual es útil para ver el orden
        entryFile.action();
        splitter.action();
        while(!s1_SplitterOut.isEmpty()) idSetter.action();
        distributor.procesar();
        
        // Ramas paralelas
        while(!s3_Cold.isEmpty()) repCold.action();
        while(!s4_Hot.isEmpty()) repHot.action();
        
        // Aquí ejecutamos nuestras TAREAS ANÓNIMAS
        while(!s6_RepCold_ToBarman.isEmpty()) barmanFrioTask.action();
        while(!s11_RepHot_ToBarman.isEmpty()) barmanCalienteTask.action();
        
        corrCold.action();
        corrHot.action();
        
        enrichCold.action();
        enrichHot.action();
        
        merger.action();
        while(!s15_MergerOut.isEmpty()) aggregator.action(); // Aggregator necesita acumular
        
        exit.action();
        System.out.println(">>> Fin.");
    }
}