package app.dbo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/projava";
    private static final String USER = "root"; // Remplace par ton utilisateur MySQL
    private static final String PASSWORD = "root"; // Remplace par ton mot de passe

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

