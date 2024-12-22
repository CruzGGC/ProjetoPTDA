package com.grupo6.projetoptda.Utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A classe DatabaseConnection gere a conexão com a base de dados.
 */
public class DatabaseConnection {
    public static String URL = "jdbc:mysql://estga-dev.ua.pt:3306/";
    public static String SCHEMA = "PTDA24_BD_06";
    public static String USER = "PTDA24_06";
    public static String PASSWORD = "Asnh#235";

    /**
     * Estabelece uma conexão com a base de dados.
     *
     * @return a conexão estabelecida
     * @throws SQLException se ocorrer um erro ao tentar conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + SCHEMA, USER, PASSWORD);
    }
}