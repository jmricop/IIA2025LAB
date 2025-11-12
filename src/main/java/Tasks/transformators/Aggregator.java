/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tasks.transformators;

import Tasks.Task;
import Tasks.taskEnum;

// Importamos las clases de "common"
import common.Message;
import common.Slot;
import common.Diccionario;
import common.ValoresDiccionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator; // Para ordenar los trozos
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 *
 * @author apolo
 */
/**
 * Tarea de tipo Aggregator (Agregador).
 * Esta tarea es la "otra mitad" del Splitter.
 * * 1. Recibe mensajes "hijos" (segmentos) de un solo buzón de entrada.
 * 2. Los guarda en una "sala de espera" interna.
 * 3. Cuando un lote está completo (ej. recibe el 2 de 2), los re-ensambla.
 * 4. Pide el "caparazón" al Diccionario.
 * 5. Pega los hijos (ordenados) en el caparazón.
 * 6. Saca el mensaje "Padre" re-ensamblado por el buzón de salida.
 */
public class Aggregator extends Task {

    // --- Atributos ---
    
    // Esta es la "sala de espera" (stateful).
    // Es un Mapa donde la Clave es el "idDocument" (el lote)
    // y el Valor es la LISTA de mensajes (trozos) que han llegado.
    private Map<Integer, List<Message>> salaDeEspera;
    
    // Herramienta XPath (la creamos una vez por eficiencia)
    private XPath xpath;

    /**
     * Constructor de la tarea Aggregator.
     * @param t El tipo de tarea (ej. taskEnum.AGGREGATOR, si existe)
     * @param se La "Pared de Buzones" de entrada (DEBE ser 1 solo Slot)
     * @param sl La "Pared de Buzones" de salida (DEBE ser 1 solo Slot)
     */
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
            // (Si el número de trozos que tenemos es igual al total que esperábamos)
            if (loteActual.size() == totalEsperado) {
                
                System.out.println("¡Lote " + idLote + " COMPLETO! Ensamblando...");
                
                try {
                    // --- 6. ¡LOTE COMPLETO! A RE-ENSAMBLAR ---
                    
                    // 6a. Pedir el "Caparazón" al almacén (y BORRARLO)
                    ValoresDiccionario vD = Diccionario.getInstance().remove(idLote);
                    
                    if (vD == null) {
                        System.err.println("¡ERROR FATAL! Lote " + idLote + " completo, pero no se encontró caparazón en el Diccionario.");
                        return; // No se puede continuar
                    }
                    
                    Document caparazon = vD.getCaparazon(); // El <factura> o <cafe_order>
                    String xPathInsertar = vD.getXPathDondeInsertar(); // El "//items_pedido" o "//drinks"
                    
                    // 6b. Ordenar los trozos (para que se peguen en orden 1, 2, 3...)
                    // Esto ordena la lista 'loteActual' basándose en el 'idSegment'
                    loteActual.sort(Comparator.comparingInt(Message::getIdSegment));
                    
                    // 6c. Encontrar el nodo "padre" donde pegar los trozos
                    XPathExpression expr = this.xpath.compile(xPathInsertar);
                    Node nodoPadre = (Node) expr.evaluate(caparazon, XPathConstants.NODE);
                    
                    // 6d. Pegar cada trozo (hijo) en el caparazón
                    for (Message trozo : loteActual) {
                        // Saca el XML del trozo (ej. el <student>)
                        Document docHijo = trozo.getDocument();
                        // Coge el nodo raíz del trozo (el <student> mismo)
                        Node nodoHijo = docHijo.getDocumentElement(); 
                        
                        // Importa el nodo al "mundo" del caparazón
                        Node importedNode = caparazon.importNode(nodoHijo, true);
                        
                        // ¡Pega el trozo en el caparazón!
                        nodoPadre.appendChild(importedNode);
                    }
                    
                    // 6e. Crear el mensaje "Padre" final, re-ensamblado
                    // (Usamos el constructor simple, con el mismo ID de lote)
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
            // Si el 'if' (loteActual.size() == totalEsperado) es falso, no se hace nada
            // La 'action' termina y espera a que llegue el siguiente trozo.
        }
    }

    @Override
    public void mock() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
