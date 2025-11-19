/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.transformators;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

/**
 *
 * @author apolo
 */
public class Translator extends Task {

    private String xPathQuery;

    public Translator(taskEnum t, ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots, String query) {
        super(t, entrySlots, exitSlots);
        this.xPathQuery = query;
    }

    @Override
    public void action() {

        if (isEmpty(0)) {
            System.out.println("No hay mensaje de entrada para traducir.");
        } else {
            Message mensajeEntrada = getEntryMessage(0);

            try {
                StreamSource xsltSource = new StreamSource(new File(xPathQuery));
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(xsltSource);

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document outputDocument = documentBuilder.newDocument();
                DOMResult domResult = new DOMResult(outputDocument);

                transformer.transform(new DOMSource(mensajeEntrada.getDocument()), domResult);

                Message mensajeSalida = new Message(
                        outputDocument,
                        mensajeEntrada.getIdDocument(),
                        mensajeEntrada.getIdSegment(),
                        mensajeEntrada.getnSegments()
                );

                setMensajeSalida(mensajeSalida, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    
    @Override
    public void mock() {
        
        System.out.println(this.toString());
    
    }
}
