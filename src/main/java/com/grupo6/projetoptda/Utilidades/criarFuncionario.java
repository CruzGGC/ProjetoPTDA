package com.grupo6.projetoptda.Utilidades;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

/**
 * A classe criarFuncionario insere um novo funcionário na base de dados.
 */
public class criarFuncionario {

    public static void main(String[] args) {
        // Define os dados do novo funcionário
        int id = 6;
        String nome = "Gabriel";
        String password = "1234";
        String nivelAcesso = "EmpregadoMesa";

        // Tenta estabelecer conexão com a base de dados
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Cria um hash para a password do funcionário
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Insere o novo funcionário na base de dados na tabela do Funcionário
            String sql = "INSERT INTO Funcionario (idFuncionario, fNome, fPassword, nivelAcesso) VALUES (?, ?, ?, ?)";
            // Define os valores dos parametros da SQL
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, nome);
                stmt.setString(3, hashPassword);
                stmt.setString(4, nivelAcesso);
                // Executa a SQL
                stmt.executeUpdate();
                System.out.println("Funcionário criado com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}