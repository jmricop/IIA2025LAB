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
        this.inputDirectory = inputDirectory;
    }

    @Override
    public void action() {
        System.out.println("--- DEBUG CONECTOR ---");
        System.out.println("Ruta configurada: " + inputDirectory);

        File folder = new File(inputDirectory);

        // 1. Verificaciones básicas
        if (!folder.exists()) {
            System.err.println("ERROR: ¡La carpeta NO EXISTE en el disco!");
            return;
        }
        if (!folder.isDirectory()) {
            System.err.println("ERROR: La ruta apunta a un archivo, no a una carpeta.");
            return;
        }

        // 2. Listar contenido
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            System.err.println("ERROR: Fallo de I/O al leer la carpeta (¿Permisos?).");
            return;
        }

        System.out.println("Archivos encontrados en la carpeta: " + listOfFiles.length);

        // 3. Procesar archivos
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            for (File file : listOfFiles) {
                System.out.println(" -> Analizando: " + file.getName());

                if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                    System.out.println("    [OK] Es un XML válido. Procesando...");

                    Document doc = db.parse(file);

                    // UUID aleatorio como ID del mensaje
                    Message msg = new Message(doc, Integer.parseInt(java.util.UUID.randomUUID().toString().replaceAll("\\D", "").substring(0, 5)));

                    port.write(msg);
                    System.out.println("    [EXITO] Mensaje inyectado al puerto.");

                } else {
                    System.out.println("    [IGNORADO] No es un archivo o no acaba en .xml");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("----------------------");
    }
}
