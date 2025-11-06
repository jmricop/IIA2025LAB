/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks;

import common.Message;
import common.Slot;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author apolo
 */
public abstract class Task {

    private ArrayList<Slot> entrySlot;
    private ArrayList<Slot> exitSlot;
    private taskEnum type;

    public abstract void action();

    public abstract void mock();

    public Task(taskEnum t, ArrayList<Slot> es, ArrayList<Slot> exsl) {
        type = t;
        entrySlot = es;
        exitSlot = exsl;
    }

    public boolean isEmpty(int numE) {
        return entrySlot.get(numE).isEmpty();
    }

    public Message getEntryMessage(int numE) {
        return entrySlot.get(numE).getFirstMessage();
    }

    public void setMensajeSalida(Message msg, int numS) {
        exitSlot.get(numS).addMessage(msg);

    }

    public int getNSalidas() {
        return exitSlot.size();
    }

    public ArrayList<Slot> getslotsSalida() {
        return exitSlot;
    }

    @Override
    public String toString() {

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 2. Configurar el transformer para "pretty print" (indentación)
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        // Omitir la declaración <?xml ...?> (opcional)
        // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        // 3. Preparar el destino (un StringWriter)
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        // 4. Preparar la fuente (tu Document)
        DOMSource source = new DOMSource(exitSlot.get(0).getFirstMessage().getDocument());

        try {
            // 5. Realizar la transformación
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }

        // 6. Obtener el string resultante
        return writer.getBuffer().toString();

    }

}
