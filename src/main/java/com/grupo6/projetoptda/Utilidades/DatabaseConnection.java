package com.grupo6.projetoptda.Utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static String URL = "jdbc:mysql://localhost:3306/ptda24_bd_06";
    public static String USER = "root";
    public static String PASSWORD = "VaiTeFoder123@@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
