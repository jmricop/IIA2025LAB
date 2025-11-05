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
        }
    }
}