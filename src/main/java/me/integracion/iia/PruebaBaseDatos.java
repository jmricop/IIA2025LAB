package me.integracion.iia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public class PruebaBaseDatos {

    public static void main(String[] args) {
        
        String url = "jdbc:sqlite:iia.db";

        System.out.println("--- Iniciando prueba de conexión ---");

        
        try (Connection conn = DriverManager.getConnection(url)) {
            
            if (conn != null) {
                
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("¡ÉXITO! Conexión establecida.");
                System.out.println("El driver es: " + meta.getDriverName());
                System.out.println("Se ha creado/conectado el archivo: iia.db");
                
            }

        } catch (SQLException e) {
           
            System.err.println("¡ERROR! No se pudo conectar.");
            System.out.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}