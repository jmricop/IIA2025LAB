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
        
        // 1. Crear la tabla que usa tu Main: 'inventario'
        String sql = "CREATE TABLE IF NOT EXISTS inventario (producto TEXT PRIMARY KEY, cantidad INTEGER)";
        stmt.execute(sql);
        
        // 2. Limpiar e insertar datos de prueba (RESET para cada ejecución)
        stmt.execute("DELETE FROM inventario");
        
        // Bebidas Frías (nombres deben coincidir con el XML)
        stmt.execute("INSERT INTO inventario VALUES ('CocaCola', 10)");
        stmt.execute("INSERT INTO inventario VALUES ('Fanta', 5)");
        
        // Bebidas Calientes
        stmt.execute("INSERT INTO inventario VALUES ('CafeSolo', 10)");
        stmt.execute("INSERT INTO inventario VALUES ('TeVerde', 2)");
        
        System.out.println("--- DB: Tabla 'inventario' inicializada ---");
    }
}