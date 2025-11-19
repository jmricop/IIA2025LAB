package me.integracion.iia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public class PruebaBaseDatos {

    public static void main(String[] args) {
        // Nombre del archivo de base de datos. 
        // Al no poner ruta, se creará en la raíz del proyecto (al nivel del pom.xml)
        String url = "jdbc:sqlite:prueba_conexion.db";

        System.out.println("--- Iniciando prueba de conexión ---");

        // Usamos try-with-resources para que la conexión se cierre sola al terminar
        try (Connection conn = DriverManager.getConnection(url)) {
            
            if (conn != null) {
                // Si entramos aquí, la conexión fue exitosa
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("¡ÉXITO! Conexión establecida.");
                System.out.println("El driver es: " + meta.getDriverName());
                System.out.println("Se ha creado/conectado el archivo: prueba_conexion.db");
            }

        } catch (SQLException e) {
            // Si algo falla (ej: falta la librería), caerá aquí
            System.err.println("¡ERROR! No se pudo conectar.");
            System.out.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}