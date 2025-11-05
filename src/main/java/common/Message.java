/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author apolo
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.StringWriter;
import java.util.UUID;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author israe
 */
public class Message {

    private String idMsg;
    private int idDocument;
    private int idSegment;
    private int nSegments;
    private Document document;

    public Message(Document msj, int idDocument) { //Constructor para el puerto
        this.document = msj;
        this.idMsg = UUID.randomUUID().toString();
        this.idDocument = idDocument;
        this.idSegment = -1;
        this.nSegments = 0;
        System.out.println("Generating message --> id " + this.idMsg + " idDocument = " + this.idDocument + " idSegment = " + this.idSegment + " nSegments = " + this.nSegments);

    }

    public Message(Document msj, int idDocument, int idSegment, int nSegmets) {//Constructor para splitter
        this.document = msj;
        this.idMsg = UUID.randomUUID().toString();
        this.idDocument = idDocument;
        this.idSegment = idSegment;
        this.nSegments = nSegmets;
        System.out.println("Generado mensaje con id = " + this.idMsg + " idDocument = " + this.idDocument + " idSegment = " + this.idSegment + " nSegments = " + this.nSegments);
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public String getIdMsg() {
        return idMsg;
    }

    public void setIdMsg(String idMsg) {
        this.idMsg = idMsg;
    }

    public int getIdSegment() {
        return idSegment;
    }

    public void setIdSegment(int idSegment) {
        this.idSegment = idSegment;
    }

    public int getnSegments() {
        return nSegments;
    }

    public void setnSegments(int nSegments) {
        this.nSegments = nSegments;
    }

    public Document getDocument() {
        return document;
    }

    public void setMensaje(Document document) {
        this.document = document;
    }

    @Override
    public String toString() {

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter stringWriter = new StringWriter();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(this.document), new StreamResult(stringWriter));
            String result = stringWriter.toString();
            return result;
        } catch (TransformerException ex) {
            System.out.println("Error tranformacion en toString: " + ex.getMessage());
            System.exit(1);
            return "";
        }
    }

}