/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conector.connectors;

import Conector.Conector;
import Port.Port;
import Port.ports.EntryPort;
import common.Message;
import common.Slot;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author apolo
 */
public class ConnectorEntryFile extends Conector {

    private final String inputDirectory;

   
    public ConnectorEntryFile(Port port, String inputDirectory) {
        
        super(port);
        Queue<Message> queue = new LinkedList<>();
        this.port = new EntryPort(new Slot(queue));
        this.inputDirectory = inputDirectory;
    }

    @Override
    public void action() {
        File folder = new File(inputDirectory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) return;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    
                    
                    Document doc = db.parse(file);
                    
                    
                    Message msg = new Message(doc, Integer.parseInt(UUID.randomUUID().toString()));
                    
                    port.write(msg);
                    
                    System.out.println("Conector: Archivo leído e inyectado: " + file.getName());

                    
                }
            }
        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
        }
    }
}
