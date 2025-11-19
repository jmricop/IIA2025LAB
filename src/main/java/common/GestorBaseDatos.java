package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GestorBaseDatos {

    private static GestorBaseDatos instance;
    private Connection connection;
    // Asegúrate de que la ruta a la DB sea correcta
    private final String url = "jdbc:sqlite:tienda.db"; 

    private GestorBaseDatos() {
        try {
            connection = DriverManager.getConnection(url);
            crearTablaFrio();
            crearTablaCaliente();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static GestorBaseDatos getInstance() {
        if (instance == null) {
            instance = new GestorBaseDatos();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
    
    private void crearTablaFrio() throws SQLException {
        // Tabla simple para validar tu ejercicio
        String sql = "CREATE TABLE IF NOT EXISTS stock_frio (producto TEXT PRIMARY KEY, cantidad INTEGER)";
        connection.createStatement().execute(sql);
        // Insertamos datos de prueba si está vacía
        connection.createStatement().execute("INSERT OR IGNORE INTO stock (producto, cantidad) VALUES ('coca-cola', 10)");
        connection.createStatement().execute("INSERT OR IGNORE INTO stock (producto, cantidad) VALUES ('fanta', 5)");
    }
    
        private void crearTablaCaliente() throws SQLException {
        // Tabla simple para validar tu ejercicio
        String sql = "CREATE TABLE IF NOT EXISTS stock_caliente (producto TEXT PRIMARY KEY, cantidad INTEGER)";
        connection.createStatement().execute(sql);
        // Insertamos datos de prueba si está vacía
        connection.createStatement().execute("INSERT OR IGNORE INTO stock (producto, cantidad) VALUES ('colacao', 5)");
        connection.createStatement().execute("INSERT OR IGNORE INTO stock (producto, cantidad) VALUES ('cafe', 5)");
    }
}