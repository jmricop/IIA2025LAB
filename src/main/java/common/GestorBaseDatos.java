package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorBaseDatos {

    private static GestorBaseDatos instance;
    private Connection connection;
    private final String url = "jdbc:sqlite:cafeteria.db"; // Nombre de la DB

    private GestorBaseDatos() {
        try {
            connection = DriverManager.getConnection(url);
            inicializarInventario();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static GestorBaseDatos getInstance() {
        if (instance == null) instance = new GestorBaseDatos();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void inicializarInventario() throws SQLException {
        Statement stmt = connection.createStatement();
        
        String sql = "CREATE TABLE IF NOT EXISTS inventario (producto TEXT PRIMARY KEY, cantidad INTEGER)";
        stmt.execute(sql);
        
        
        stmt.execute("DELETE FROM inventario");
        
        
        stmt.execute("INSERT INTO inventario VALUES ('cafe', 1)");       
        stmt.execute("INSERT INTO inventario VALUES ('chocolate', 1)");
        stmt.execute("INSERT INTO inventario VALUES ('te', 1)");         
        stmt.execute("INSERT INTO inventario VALUES ('tila', 1)");
        
        // Bebidas Frías
        stmt.execute("INSERT INTO inventario VALUES ('coca-cola', 1)");  
        stmt.execute("INSERT INTO inventario VALUES ('tonica', 1)");
        stmt.execute("INSERT INTO inventario VALUES ('guarana', 1)");
        stmt.execute("INSERT INTO inventario VALUES ('cerveza', 1)");
        stmt.execute("INSERT INTO inventario VALUES ('fanta', 1)");
        
        System.out.println("--- DB: Tabla 'inventario' inicializada con productos del XML ---");
    }
}