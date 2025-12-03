/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.modifiers;

import Tasks.Task;
import Tasks.taskEnum;
import common.Message;
import common.Slot;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult; 
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;


public class Slimmer extends Task {

    
     
    private final Transformer transformer;

    
    public Slimmer(ArrayList<Slot> entrySlots, ArrayList<Slot> exitSlots,
                   String xsltFilePath, taskEnum t) throws Exception {

        
        super(t, entrySlots, exitSlots);


        TransformerFactory factory = TransformerFactory.newInstance();
        Source xsltSource = new StreamSource(new File(xsltFilePath));
        this.transformer = factory.newTransformer(xsltSource);
    }

    
    @Override
    public void action() {
        try {
            

            // 2. Obtener el mensaje de entrada y su documento
            Message inMessage = getEntryMessage(0);
            Document inDoc = inMessage.getDocument();
            if (inDoc == null) {
                // El mensaje no contenía un documento válido
                Logger.getLogger(Slimmer.class.getName()).log(Level.WARNING, "Mensaje de entrada en Slimmer no contenía documento XML.");
                return;
            }

            // 3. Preparar la transformación
            DOMSource source = new DOMSource(inDoc);
            
            // Crearemos un nuevo Documento DOM como resultado
            DOMResult result = new DOMResult(); 

            // 4. Ejecutar la transformación
            // 'transformer' es el objeto XSLT compilado que guardamos
            this.transformer.transform(source, result);

            // 5. Obtener el nuevo documento "adelgazado" del resultado
            Document outDoc = (Document) result.getNode();

            // 6. Crear un nuevo mensaje de salida
            // No modificamos el mensaje original, creamos uno nuevo.
            Message outMessage = new Message(outDoc,Integer.parseInt(UUID.randomUUID().toString()));

            // 7. Poner el nuevo mensaje en el slot de salida
            setMensajeSalida(outMessage, 0);

        } catch (TransformerException e) {
            Logger.getLogger(Slimmer.class.getName()).log(Level.SEVERE, "Error fatal al transformar XML en Slimmer", e);
        } catch (Exception e) {
            Logger.getLogger(Slimmer.class.getName()).log(Level.SEVERE, "Error inesperado en Slimmer", e);
        }
    }

   
    @Override
    public void mock() {
        
        System.out.println(this.toString());
        
    }
}