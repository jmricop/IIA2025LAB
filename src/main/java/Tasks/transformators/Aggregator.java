/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.transformators;

import Tasks.Task;
import Tasks.taskEnum;

import common.Message;
import common.Slot;
import common.Diccionario;
import common.ValoresDiccionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator; 
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


public class Aggregator extends Task {

    
    private Map<Integer, List<Message>> salaDeEspera;
    
    // Herramienta XPath (la creamos una vez por eficiencia)
    private XPath xpath;

    
    public Aggregator(taskEnum t, ArrayList<Slot> se, ArrayList<Slot> sl) {
        
        // 1. Llama al constructor del "Padre" (Task)
        super(t, se, sl);

        // 2. Comprobación de seguridad: 1 entrada (donde el Merger deja todo), 1 salida
        if (se.size() != 1 || sl.size() != 1) {
            throw new IllegalArgumentException("Aggregator debe tener exactamente 1 buzón de entrada y 1 de salida.");
        }
        
        // 3. Inicializa la "sala de espera" (la memoria)
        this.salaDeEspera = new HashMap<>();
        
        // 4. Pre-carga la herramienta XPath
        XPathFactory xfactory = XPathFactory.newInstance();
        this.xpath = xfactory.newXPath();
    }

    @Override
    public void action() {
        
        // 1. Comprobar si hay trabajo (¿El buzón 0 de entrada NO está vacío?)
        if (!isEmpty(0)) {
            
            // 2. Coger el siguiente mensaje "hijo" (trozo) que llega
            Message msgHijo = getEntryMessage(0);
            
            // 3. Extraer sus metadatos
            int idLote = msgHijo.getIdDocument();
            int totalEsperado = msgHijo.getnSegments();
            
            // 4. Guardar el mensaje en la "Sala de Espera"
            
            // 4a. ¿Es el primer mensaje de este lote? Si no, crea la lista.
            if (!this.salaDeEspera.containsKey(idLote)) {
                this.salaDeEspera.put(idLote, new ArrayList<>());
            }
            
            // 4b. Añade el mensaje (el trozo) a la lista de su lote
            List<Message> loteActual = this.salaDeEspera.get(idLote);
            loteActual.add(msgHijo);
            
            System.out.println("Aggregator: Recibido trozo " + msgHijo.getIdSegment() + "/" + totalEsperado + " del Lote " + idLote);

            // 5. Comprobar si el lote está completo
            if (loteActual.size() == totalEsperado) {
                
                System.out.println("¡Lote " + idLote + " COMPLETO! Ensamblando...");
                
                try {
                    
                    
                    // 6a. Pedir el "Caparazón" al almacén (y BORRARLO)
                    ValoresDiccionario vD = Diccionario.getInstance().remove(idLote);
                    
                    if (vD == null) {
                        System.err.println("¡ERROR FATAL! Lote " + idLote + " completo, pero no se encontró caparazón en el Diccionario.");
                        return; // No se puede continuar
                    }
                    
                    Document caparazon = vD.getCaparazon(); 
                    String xPathInsertar = vD.getXPathDondeInsertar(); 
                    
                    // 6b. Ordenar los trozos (para que se peguen en orden 1, 2, 3...)
                    loteActual.sort(Comparator.comparingInt(Message::getIdSegment));
                    
                    // 6c. Encontrar el nodo "padre" donde pegar los trozos
                    XPathExpression expr = this.xpath.compile(xPathInsertar);
                    Node nodoPadre = (Node) expr.evaluate(caparazon, XPathConstants.NODE);
                    
                    // 6d. Pegar cada trozo (hijo) en el caparazón
                    for (Message trozo : loteActual) {
                        
                        Document docHijo = trozo.getDocument();
                        
                        Node nodoHijo = docHijo.getDocumentElement(); 
                        
                        
                        Node importedNode = caparazon.importNode(nodoHijo, true);
                        
                       
                        nodoPadre.appendChild(importedNode);
                    }
                    
                    // 6e. Crear el mensaje "Padre" final, re-ensamblado
                    Message msgFinal = new Message(caparazon, idLote);
                    
                    // 6f. Poner el mensaje final en el buzón de salida
                    setMensajeSalida(msgFinal, 0);
                    
                    // 6g. Limpiar la "sala de espera"
                    this.salaDeEspera.remove(idLote);
                    
                    System.out.println("¡Lote " + idLote + " ensamblado y enviado!");
                    
                } catch (Exception e) {
                    System.err.println("Error fatal al ensamblar el lote " + idLote + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } 
            
        }
    }

    @Override
    public void mock() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
