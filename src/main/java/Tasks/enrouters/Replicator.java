package Tasks.enrouters;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Replicator extends Task {

    private final DocumentBuilder docBuilder;

    public Replicator(ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots, taskEnum t) {
        super(t, entrySlots, exitSlots);

        // 1. Validaciones
        if (entrySlots == null || entrySlots.size() != 1) {
            throw new IllegalArgumentException("Replicator requiere exactamente 1 slot de entrada.");
        }
        if (exitSlots == null || exitSlots.isEmpty()) {
            throw new IllegalArgumentException("Replicator requiere al menos 1 slot de salida.");
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            this.docBuilder = dbf.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException("Error fatal iniciando Replicator: no se puede crear DocumentBuilder", e);
        }
    }

    @Override
    public void action() {
        try {
            if (isEmpty(0)) {
                return;
            }

            Message originalMsg = getEntryMessage(0);
            Document originalDoc = originalMsg.getDocument();
            int numSalidas = getNSalidas();

            for (int i = 0; i < numSalidas; i++) {
                Document copiedDoc = cloneDocument(originalDoc);

                // --- CORRECCIÓN: Generar un ID numérico seguro ---
                // Usamos hashCode() del UUID para obtener un int, o un Random simple
                int nuevoId = java.util.UUID.randomUUID().hashCode();
                // ------------------------------------------------

                // Esto COPIA la info de los segmentos
                Message copyMsg = new Message(
                        copiedDoc,
                        originalMsg.getIdDocument(), // Mismo Lote
                        originalMsg.getIdSegment(), // <--- COPIAR ESTO (ej. 1)
                        originalMsg.getnSegments() // <--- COPIAR ESTO (ej. 3)
                );

                // Copiamos el Correlation ID importante para que luego se puedan unir
                copyMsg.setcorrelatorId(originalMsg.getCorrelatorId());

                setMensajeSalida(copyMsg, i);
            }
        } catch (Exception e) {
            Logger.getLogger(Replicator.class.getName()).log(Level.SEVERE, "Error en Replicator", e);
        }
    }

    @Override
    public void mock() {

        if (!isEmpty(0)) {
            Message msg = getEntryMessage(0);
            for (int i = 0; i < getNSalidas(); i++) {
                setMensajeSalida(msg, i);
            }
        }
    }

    private Document cloneDocument(Document original) {

        Document newDoc = docBuilder.newDocument();

        Node copiedRoot = newDoc.importNode(original.getDocumentElement(), true);

        newDoc.appendChild(copiedRoot);

        return newDoc;
    }
}
