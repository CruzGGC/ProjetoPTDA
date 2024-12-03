package com.grupo6.projetoptda;

import java.util.Scanner;
import java.sql.*;

public class adicionarProduto {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o nome do produto: ");
        String nome = scanner.nextLine();

        System.out.print("Digite o preço do produto: ");
        double preco = scanner.nextDouble();

        System.out.print("Digite a quantidade do produto: ");
        int quantidade = scanner.nextInt();

        String url = "jdbc:mysql://localhost:3306/PTDA24_BD_06";
        String USER = "root";
        String PASSWORD = "password";

        try (Connection connection = DriverManager.getConnection(url, USER, PASSWORD)) {
            String sql = "{call adicionarProduto(?, ?, ?)}";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, nome);
                statement.setDouble(2, preco);
                statement.setInt(3, quantidade);

                statement.execute();
                System.out.println("Produto adicionado com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao adicionar o produto: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}