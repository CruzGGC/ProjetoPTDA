package com.grupo6.projetoptda.Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ptda24_bd_06";
    private static final String USER = "root";
    private static final String PASSWORD = "MUDEM A PASSWORD AQUI MAS NAO DEEM COMMIT A ESTE FICHEIRO";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
