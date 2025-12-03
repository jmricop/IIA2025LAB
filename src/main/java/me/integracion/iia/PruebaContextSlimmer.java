package me.integracion.iia;

import Tasks.modifiers.ContextSlimmer;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Usuario
 */
public class PruebaContextSlimmer {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        

        //PRUEBA CONTEXT SLIMMER CORRECTA
        
        try {
            // --- CREAR SLOTS Y COLAS ---
            ArrayList<Slot> entrySlots = new ArrayList<>();
            ArrayList<Slot> exitSlots = new ArrayList<>();

            Queue<Message> queueEntradaPrincipal = new LinkedList<>();
            Queue<Message> queueEntradaContexto = new LinkedList<>();
            Queue<Message> queueSalida = new LinkedList<>();

            Slot slotPrincipal = new Slot();
            Slot slotContexto = new Slot();
            Slot slotSalida = new Slot();

            // --- CARGAR ARCHIVOS XML ---
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            File mainFile = new File("src/main/java/resources/cdcatalog.xml");
            File contextFile = new File("src/main/java/resources/context.xml");

            Document mainDoc = dBuilder.parse(mainFile);
            mainDoc.getDocumentElement().normalize();

            Document contextDoc = dBuilder.parse(contextFile);
            contextDoc.getDocumentElement().normalize();

            // --- CREAR MENSAJES ---
            Message mainMsg = new Message(mainDoc, 1);
            Message contextMsg = new Message(contextDoc, 2);

            // --- AÑADIR MENSAJES A LOS SLOTS ---
            slotPrincipal.addMessage(mainMsg);
            slotContexto.addMessage(contextMsg);
            entrySlots.add(slotPrincipal);
            entrySlots.add(slotContexto);
            exitSlots.add(slotSalida);

            // --- CONFIGURAR XPATHS ---
            // Queremos eliminar estudiantes cuyo id NO esté en el contexto
            String xPathToSelect = "//student/age"; // nodos a revisar en el principal
            String xPathToCompare = "//age";     // valores válidos en el contexto

            // --- CREAR Y EJECUTAR LA TAREA ---
            ContextSlimmer slimmer = new ContextSlimmer(
                    entrySlots,
                    exitSlots,
                    xPathToSelect,
                    xPathToCompare,
                    taskEnum.CONTEXTSLIMMER
            );

            slimmer.action();

            // --- MOSTRAR RESULTADO ---
            System.out.println("\n--- Resultado del ContextSlimmer ---");
            slimmer.mock();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
