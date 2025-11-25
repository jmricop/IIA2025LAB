package Conector.connectors;

import Conector.Conector;
import Port.Port;
import Port.ports.ExitPort;
import common.Slot;
import common.Message;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ConnectorExitFile extends Conector {

    private final String outputDirectory;
    private final String filePrefix;

    public ConnectorExitFile(Port port, String outputDirectory, String filePrefix) {

        super(port);

        this.outputDirectory = outputDirectory;
        this.filePrefix = filePrefix;
        new File(outputDirectory).mkdirs();

    }

    @Override
    public void action() {

        if (this.port.getBuffer().isEmpty()) {
            return;
        }

        try {

            Message msg = this.port.getBuffer().getFirstMessage();

            if (msg == null || msg.getDocument() == null) {
                return;
            }

            String fileName = filePrefix + System.currentTimeMillis() + ".xml";
            File destFile = new File(outputDirectory, fileName);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(msg.getDocument());
            StreamResult result = new StreamResult(destFile);

            transformer.transform(source, result);

            System.out.println("ConnectorExit: Archivo guardado exitosamente en " + destFile.getAbsolutePath());

        } catch (TransformerException e) {
            Logger.getLogger(ConnectorExitFile.class.getName()).log(Level.SEVERE, "Error guardando XML", e);
        } catch (IllegalArgumentException e) {
            Logger.getLogger(ConnectorExitFile.class.getName()).log(Level.SEVERE, "Error en ConnectorExitFile", e);
        }
    }
}
