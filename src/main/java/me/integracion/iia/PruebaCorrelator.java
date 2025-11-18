/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package me.integracion.iia;

import Tasks.enrouters.Correlator;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 *
 * @author Daniel
 */
public class PruebaCorrelator {
    
    public static void main(String[] args) throws Exception {

        Document doc1 = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        doc1.appendChild(doc1.createElement("msg1"));

        Document doc2 = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        doc2.appendChild(doc2.createElement("msg2"));
        
        Document doc3 = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        doc3.appendChild(doc3.createElement("msg3"));

        Document doc4 = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        doc4.appendChild(doc4.createElement("msg4"));


        // Crear mensajes con correlatorId
        Message m1 = new Message(doc1, 1);
        m1.setcorrelatorId(100);     // entrada slot 0

        Message m2 = new Message(doc2, 2);
        m2.setcorrelatorId(200);     // entrada slot 0

        Message m3 = new Message(doc3, 3);
        m3.setcorrelatorId(100);     // entrada slot 1

        Message m4 = new Message(doc4, 4);
        m4.setcorrelatorId(200);     // entrada slot 1


        // Crear slots de entrada
        Slot entrada1 = new Slot(new LinkedList<>());
        Slot entrada2 = new Slot(new LinkedList<>());

        entrada1.addMessage(m1);
        entrada1.addMessage(m2);

        entrada2.addMessage(m3);
        entrada2.addMessage(m4);


        // Crear slots de salida
        Slot salida1 = new Slot(new LinkedList<>());
        Slot salida2 = new Slot(new LinkedList<>());


        // Preparar listas
        ArrayList<Slot> entradas = new ArrayList<>();
        entradas.add(entrada1);
        entradas.add(entrada2);

        ArrayList<Slot> salidas = new ArrayList<>();
        salidas.add(salida1);
        salidas.add(salida2);


        // Crear correlator
        Correlator correlator = new Correlator(taskEnum.CORRELATOR, entradas, salidas);
        correlator.action();


        // Mostrar salidas
        System.out.println("\n=== RESULTADOS ===");
        
        System.out.println("Salida 0:");
        for (Message m : salida1.getMessages()) {
            System.out.println("Mensaje " + m.getIdDocument() + " corrID=" + m.getCorrelatorId());
        }

        System.out.println("\nSalida 1:");
        for (Message m : salida2.getMessages()) {
            System.out.println("Mensaje " + m.getIdDocument() + " corrID=" + m.getCorrelatorId());
        }

    }
}
