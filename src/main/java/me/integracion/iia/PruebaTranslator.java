package me.integracion.iia;

import Tasks.taskEnum;
import Tasks.transformators.Translator;
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
public class PruebaTranslator {
    public static void main(String[] args) {
        try {
            // --- CREAR SLOTS Y COLAS ---
            ArrayList<Slot> entrySlots = new ArrayList<>();
            ArrayList<Slot> exitSlots = new ArrayList<>();

            Queue<Message> queueEntrada = new LinkedList<>();
            Queue<Message> queueSalida = new LinkedList<>();

            Slot sEntrada = new Slot();
            Slot sSalida = new Slot();

            entrySlots.add(sEntrada);
            exitSlots.add(sSalida);

            // --- CARGAR XML PRINCIPAL ---
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            File xmlFile = new File("src/main/java/resources/cdcatalog.xml");
            Document mainDoc = dBuilder.parse(xmlFile);
            mainDoc.getDocumentElement().normalize();

            // --- CREAR MENSAJE DE ENTRADA ---
            Message msgEntrada = new Message(mainDoc, 1);
            sEntrada.addMessage(msgEntrada);

            // --- RUTA AL XSLT ---
            String rutaXSLT = "src/main/java/resources/transform.xsl";

            // --- CREAR Y EJECUTAR LA TAREA TRANSLATOR ---
            Translator translator = new Translator(
                    taskEnum.TRANSLATOR,
                    entrySlots,
                    exitSlots,
                    rutaXSLT
            );

            translator.action();

            // --- OBTENER Y MOSTRAR RESULTADO ---
            if (!sSalida.isEmpty()) {
                Message msgSalida = sSalida.getFirstMessage();
                System.out.println("\n--- Resultado del Translator ---");
                System.out.println(msgSalida.toString());
            } else {
                System.out.println("No se generó mensaje de salida.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}