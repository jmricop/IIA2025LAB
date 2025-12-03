package Conector.connectors;

import Conector.Conector;
import Port.Port;
import common.GestorBaseDatos;
import common.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConectorBarman extends Conector {

    private Port portSalida;
    private String tipoBarman; // "FRIO" o "CALIENTE"

    // Constructor: Recibe puerto entrada, puerto salida y el tipo
    public ConectorBarman(Port portEntrada, Port portSalida, String tipoBarman) {
        super(portEntrada);
        this.portSalida = portSalida;
        this.tipoBarman = tipoBarman;
    }

    @Override
    public void action() {
        // 1. Verificar si hay mensajes en el puerto de entrada
        if (this.port.getBuffer().isEmpty()) {
            return;
        }

        // 2. Leer mensaje
        Message msg = this.port.read();
        
        try {
            Document doc = msg.getDocument();

            // 3. Extraer nombre de la bebida
            // (Asumimos que el XML llega limpio gracias a la corrección del Translator anterior)
            String nombreBebida = doc.getElementsByTagName("name").item(0).getTextContent();

            // 4. Lógica de Negocio (Consultar BD)
            String estado = consultarStockBD(nombreBebida);

            // 5. Crear XML de respuesta
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document respDoc = dbf.newDocumentBuilder().newDocument();
            Element root = respDoc.createElement("respuesta_barman");
            
            // Usamos el tipoBarman para poner "FRIO: ..." o "CALIENTE: ..."
            root.setTextContent(tipoBarman + ": " + estado);
            respDoc.appendChild(root);

            // 6. Crear mensaje de salida (Manteniendo IDs para el Correlator)
            Message responseMsg = new Message(
                    respDoc,
                    msg.getIdDocument(),
                    msg.getIdSegment(),
                    msg.getnSegments()
            );
            responseMsg.setcorrelatorId(msg.getCorrelatorId());

            // 7. Escribir en el puerto de salida
            this.portSalida.write(responseMsg);
            
            System.out.println("ConnectorBarman (" + tipoBarman + "): Procesado '" + nombreBebida + "' -> " + estado);

        } catch (Exception e) {
            System.err.println("Error en ConnectorBarman: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Método privado para gestión de BD ---
    private String consultarStockBD(String producto) {
        try {
            Connection conn = GestorBaseDatos.getInstance().getConnection();

            // A. Comprobar Stock
            PreparedStatement stmt = conn.prepareStatement("SELECT cantidad FROM inventario WHERE producto = ?");
            stmt.setString(1, producto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int cantidadActual = rs.getInt("cantidad");

                if (cantidadActual > 0) {
                    // B. Si hay stock, RESTAR una unidad
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE inventario SET cantidad = cantidad - 1 WHERE producto = ?");
                    updateStmt.setString(1, producto);
                    updateStmt.executeUpdate();
                    updateStmt.close();
                    
                    rs.close();
                    stmt.close();
                    return "PREPARADA";
                }
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error DB: " + e.getMessage());
        }
        return "AGOTADA";
    }
}
