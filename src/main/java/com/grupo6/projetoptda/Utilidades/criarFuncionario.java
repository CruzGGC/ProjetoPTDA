package com.grupo6.projetoptda.Controller;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class criarFuncionario {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PTDA24_BD_06";
    private static final String USER = "root";
    private static final String PASSWORD = "VaiTeFoder123@@";

    public static void main(String[] args) {
        int id = 1;
        String nome = "Nome";
        String password = "password";
        String nivelAcesso = "Gerente ou EmpregadoMesa";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            String sql = "INSERT INTO Funcionario (idFuncionario, fNome, fPassword, nivelAcesso) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, nome);
                stmt.setString(3, hashPassword);
                stmt.setString(4, nivelAcesso);
                stmt.executeUpdate();
                System.out.println("Funcionário criado com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
