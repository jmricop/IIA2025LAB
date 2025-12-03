package me.integracion.iia;

import Tasks.modifiers.CorrelationIdSetter;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.xml.parsers.*;
import org.w3c.dom.Document;

/**
 *
 * @author Usuario
 */
public class PruebaCorrelatorIdSetter {

    public static void main(String[] args) {
        try {
            // --- Crear slots y colas ---
            ArrayList<Slot> entradas = new ArrayList<>();
            ArrayList<Slot> salidas = new ArrayList<>();

            Queue<Message> queueEntrada = new LinkedList<>();
            Queue<Message> queueSalida = new LinkedList<>();

            Slot slotEntrada = new Slot();
            Slot slotSalida = new Slot();

            entradas.add(slotEntrada);
            salidas.add(slotSalida);

            // --- Cargar documento de prueba ---
            File inputFile = new File("src/main/java/resources/cdcatalog.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // --- Crear y meter el mensaje ---
            Message msg = new Message(doc, 1);
            slotEntrada.addMessage(msg);

            // --- Crear tarea y ejecutarla ---
            CorrelationIdSetter correlatorIdSetter = new CorrelationIdSetter(taskEnum.CORRELATORIDSETTER, entradas, salidas);
            correlatorIdSetter.action();

            // --- Mostrar resultado ---
            System.out.println("--- Resultado del CorrelatorIdSetter ---");
            correlatorIdSetter.mock();

            // Sacar mensaje de salida y mostrar su idDocument (como correlator)
            Message salida = slotSalida.getFirstMessage();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
