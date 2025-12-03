package me.integracion.iia;

import Conector.connectors.*;
import Port.Port;
import Port.ports.SolPort;
import Tasks.enrouters.*;
import Tasks.modifiers.*;
import Tasks.transformators.*;
import Tasks.taskEnum;
import common.*;
import java.util.*;

public class CafeteriaPruebas {

    public static void main(String[] args) {
        System.out.println(">>> INICIANDO SISTEMA DE INTEGRACIÓN CAFETERÍA (Modo Manual) <<<");

        Slot s0_Entrada = new Slot();
        Slot s1_SplitterOut = new Slot();
        Slot s2_ConId = new Slot();

        // --- Distribución ---
        Slot s3_Fria = new Slot();
        Slot s4_Caliente = new Slot();

        // --- Rama Fría (Pasos d, e) ---
        Slot s5_Fria_Copia = new Slot();      // Copia para guardar
        Slot s6_Fria_ParaBarman = new Slot(); // Copia para enviar
        Slot s7_Fria_Traducida = new Slot();  // Salida del Translator
        Slot s8_Fria_Respuesta = new Slot();  // Salida del Barman (Simulado)
        Slot s9_Fria_Corr_Main = new Slot();  // Salida Correlator (Mensaje Principal)
        Slot s9_Fria_Corr_Ctx = new Slot();   // Salida Correlator (Contexto/Respuesta)
        Slot s10_Fria_Enriched = new Slot();  // Salida Final Rama Fría

        // --- Rama Caliente (Pasos f, g) ---
        Slot s11_Cal_Copia = new Slot();
        Slot s12_Cal_ParaBarman = new Slot();
        Slot s13_Cal_Traducida = new Slot();
        Slot s14_Cal_Respuesta = new Slot();
        Slot s15_Cal_Corr_Main = new Slot();
        Slot s15_Cal_Corr_Ctx = new Slot();
        Slot s16_Cal_Enriched = new Slot();

        // --- Reunificación ---
        Slot s17_MergerOut = new Slot();
        Slot s18_SalidaFinal = new Slot();

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
        String[] condiciones = {"cold", "hot"}; 
        Distributor distributor = new Distributor(taskEnum.DISTRIBUTOR, distIn, distOut, condiciones, 2, "type");
        // ================= RAMA FRÍA =================
        ArrayList<Slot> repFriaIn = new ArrayList<>(List.of(s3_Fria));
        ArrayList<Slot> repFriaOut = new ArrayList<>(List.of(s5_Fria_Copia, s6_Fria_ParaBarman));
        Replicator repFria = new Replicator(repFriaIn, repFriaOut, taskEnum.ROUTER);

        ArrayList<Slot> transFriaIn = new ArrayList<>(List.of(s6_Fria_ParaBarman));
        ArrayList<Slot> transFriaOut = new ArrayList<>(List.of(s7_Fria_Traducida));

        Translator transFria = new Translator(taskEnum.TRANSLATOR, transFriaIn, transFriaOut, "src/main/java/resources/transform.xsl");

        // === CONECTOR BARMAN FRÍO ===
        // Creamos Puertos para envolver los Slots de entrada y salida de esta etapa
        Port portFrioIn = new SolPort(s7_Fria_Traducida);
        Port portFrioOut = new SolPort(s8_Fria_Respuesta);
        
        // Instanciamos el Conector
        ConectorBarman conectorBarmanFrio = new ConectorBarman(portFrioIn, portFrioOut, "FRIO");

        ArrayList<Slot> corrFriaIn = new ArrayList<>(List.of(s5_Fria_Copia, s8_Fria_Respuesta));
        ArrayList<Slot> corrFriaOut = new ArrayList<>(List.of(s9_Fria_Corr_Main, s9_Fria_Corr_Ctx));
        Correlator corrFria = new Correlator(taskEnum.CORRELATOR, corrFriaIn, corrFriaOut);

        ArrayList<Slot> enrichFriaIn = new ArrayList<>(List.of(s9_Fria_Corr_Main, s9_Fria_Corr_Ctx));
        ArrayList<Slot> enrichFriaOut = new ArrayList<>(List.of(s10_Fria_Enriched));
        ContextEnricher enrichFria = new ContextEnricher(enrichFriaIn, enrichFriaOut, "//respuesta_barman", "//drink", taskEnum.ENRICHER);

        // Replicator
        ArrayList<Slot> repCalIn = new ArrayList<>(List.of(s4_Caliente));
        ArrayList<Slot> repCalOut = new ArrayList<>(List.of(s11_Cal_Copia, s12_Cal_ParaBarman));
        Replicator repCal = new Replicator(repCalIn, repCalOut, taskEnum.ROUTER);

        // Translator
        ArrayList<Slot> transCalIn = new ArrayList<>(List.of(s12_Cal_ParaBarman));
        ArrayList<Slot> transCalOut = new ArrayList<>(List.of(s13_Cal_Traducida));
        Translator transCal = new Translator(taskEnum.TRANSLATOR, transCalIn, transCalOut, "src/main/java/resources/transform.xsl");

        // === CONECTOR BARMAN CALIENTE ===
        // Creamos Puertos
        Port portCalIn = new SolPort(s13_Cal_Traducida);
        Port portCalOut = new SolPort(s14_Cal_Respuesta);
        
        // Instanciamos el Conector
        ConectorBarman conectorBarmanCal = new ConectorBarman(portCalIn, portCalOut, "CALIENTE");

        // Correlator
        ArrayList<Slot> corrCalIn = new ArrayList<>(List.of(s11_Cal_Copia, s14_Cal_Respuesta));
        ArrayList<Slot> corrCalOut = new ArrayList<>(List.of(s15_Cal_Corr_Main, s15_Cal_Corr_Ctx));
        Correlator corrCal = new Correlator(taskEnum.CORRELATOR, corrCalIn, corrCalOut);

        // Enricher
        ArrayList<Slot> enrichCalIn = new ArrayList<>(List.of(s15_Cal_Corr_Main, s15_Cal_Corr_Ctx));
        ArrayList<Slot> enrichCalOut = new ArrayList<>(List.of(s16_Cal_Enriched));
        ContextEnricher enrichCal = new ContextEnricher(enrichCalIn, enrichCalOut, "//respuesta_barman", "//drink", taskEnum.ENRICHER);

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

        System.out.println(">>> Ejecutando bucles de procesamiento...");

        // Simulamos X ciclos de reloj para procesar todo
        for (int i = 0; i < 33; i++) {
            conectorEntrada.action();

            splitter.action();

            // Si hay algo en la salida del splitter, asignamos ID correlacion
            if (!s1_SplitterOut.isEmpty()) {
                idSetter.action();
            }

            distributor.procesar();

            // --- RAMA FRIA ---
            if (!s3_Fria.isEmpty()) {
                repFria.action();
            }
            if (!s6_Fria_ParaBarman.isEmpty()) {
                transFria.action();
            }
            
                conectorBarmanFrio.action();
            

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
           
                conectorBarmanCal.action();
            
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

    
}
