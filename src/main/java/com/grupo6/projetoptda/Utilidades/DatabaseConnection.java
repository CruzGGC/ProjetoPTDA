package com.grupo6.projetoptda.Utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A classe DatabaseConnection gere a conexão com a base de dados.
 */
public class DatabaseConnection {
    public static String URL = "jdbc:mysql://localhost:3306/ptda24_bd_06";
    public static String USER = "root";
    public static String PASSWORD = "PASSWORD";

    /**
     * Estabelece uma conexão com a base de dados.
     *
     * @return a conexão estabelecida
     * @throws SQLException se ocorrer um erro ao tentar conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}