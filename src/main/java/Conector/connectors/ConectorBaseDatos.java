/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conector.connectors;

import Conector.Conector;
import Port.Port;
import Port.ports.SolPort;
import common.Message;
import common.GestorBaseDatos; // Asumiendo que creaste el Singleton en common
import common.Slot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

/**
 *
 * @author chemi
 */
public class ConectorBaseDatos extends Conector {

    // Constructor: Solo necesita el puerto de donde leerá los mensajes
    public ConectorBaseDatos(Port port) {
        super(port);
        Queue<Message> m = new LinkedList<>();
        this.port = new SolPort(new Slot(m));
    }

    @Override
    public void action() {
        // 1. Verificar si hay mensajes en el puerto
        // (Dependiendo de tu implementación de Port, puede que necesites acceder al buffer directamente)
        if (this.port.getBuffer().isEmpty()) {
            return;
        }

        // 2. Leer el mensaje
        Message msg = this.port.read(); // O this.port.getBuffer().getFirstMessage() si read() no lo saca
        if (msg == null) return;

        Document doc = msg.getDocument();
        System.out.println("ConectorBaseDatos: Procesando pedido ID " + msg.getIdMsg());

        try {
            // 3. Extraer datos del XML usando XPath
            // Ajusta estos XPaths a la estructura real de tu XML (ej: //bebida, //cantidad)
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Asumimos XML tipo: <pedido><bebida>fanta</bebida><cantidad>2</cantidad></pedido>
            String producto = (String) xPath.evaluate("//bebida", doc, XPathConstants.STRING);
            String cantidadStr = (String) xPath.evaluate("//cantidad", doc, XPathConstants.STRING);

            if (producto == null || producto.isEmpty() || cantidadStr == null || cantidadStr.isEmpty()) {
                System.err.println("ConectorBaseDatos: XML incompleto o inválido.");
                return;
            }

            int cantidadSolicitada = Integer.parseInt(cantidadStr);

            // 4. Lógica de Base de Datos (Preguntar y Manejar)
            procesarStock(producto, cantidadSolicitada);

        } catch (Exception e) {
            System.err.println("Error en ConectorBaseDatos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarStock(String producto, int cantidadSolicitada) {
        Connection conn = null;
        PreparedStatement stmtCheck = null;
        PreparedStatement stmtUpdate = null;
        ResultSet rs = null;

        try {
            // Obtenemos conexión del Singleton (GestorBaseDatos)
            // Si no tienes el Singleton, aquí harías: DriverManager.getConnection("jdbc:sqlite:tienda.db");
            conn = GestorBaseDatos.getInstance().getConnection();

            // A. PREGUNTAR (Consultar Stock)
            String sqlCheck = "SELECT cantidad FROM stock WHERE producto = ?";
            stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, producto);
            rs = stmtCheck.executeQuery();

            if (rs.next()) {
                int stockActual = rs.getInt("cantidad");
                System.out.println("Stock actual de " + producto + ": " + stockActual);

                if (stockActual >= cantidadSolicitada) {
                    // B. MANEJAR (Actualizar Stock)
                    String sqlUpdate = "UPDATE stock SET cantidad = cantidad - ? WHERE producto = ?";
                    stmtUpdate = conn.prepareStatement(sqlUpdate);
                    stmtUpdate.setInt(1, cantidadSolicitada);
                    stmtUpdate.setString(2, producto);
                    stmtUpdate.executeUpdate();
                    
                    System.out.println("PEDIDO APROBADO: Se han descontado " + cantidadSolicitada + " unidades.");
                } else {
                    System.out.println("PEDIDO RECHAZADO: Stock insuficiente (Solicitado: " + cantidadSolicitada + ")");
                }
            } else {
                System.out.println("ERROR: El producto '" + producto + "' no existe en la base de datos.");
            }

        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
        } finally {
            // Cerrar recursos específicos de esta consulta (pero NO la conexión si es compartida)
            try {
                if (rs != null) rs.close();
                if (stmtCheck != null) stmtCheck.close();
                if (stmtUpdate != null) stmtUpdate.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}