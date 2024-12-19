package com.grupo6.projetoptda.Utilidades;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class criarFuncionario {

    public static void main(String[] args) {
        int id = 1;
        String nome = "Guilherme";
        String password = "1";
        String nivelAcesso = "Gerente";

        try (Connection connection = DriverManager.getConnection(DatabaseConnection.URL, DatabaseConnection.USER, DatabaseConnection.PASSWORD)) {
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
