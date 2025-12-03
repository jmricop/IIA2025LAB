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
        this.port = new SolPort(new Slot());
    }

    @Override
    public void action() {
       
        if (this.port.getBuffer().isEmpty()) {
            return;
        }

        
        Message msg = this.port.read(); 
        if (msg == null) return;

        Document doc = msg.getDocument();
        System.out.println("ConectorBaseDatos: Procesando pedido ID " + msg.getIdMsg());

        try {
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            
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
            
            conn = GestorBaseDatos.getInstance().getConnection();

            
            String sqlCheck = "SELECT cantidad FROM stock WHERE producto = ?";
            stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, producto);
            rs = stmtCheck.executeQuery();

            if (rs.next()) {
                int stockActual = rs.getInt("cantidad");
                System.out.println("Stock actual de " + producto + ": " + stockActual);

                if (stockActual >= cantidadSolicitada) {
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