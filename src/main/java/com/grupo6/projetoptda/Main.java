package com.grupo6.projetoptda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/PTDA24_BD_06";
        String USER = "root";
        String PASSWORD = "VaiTeFoder123@@";
        try (Scanner scanner = new Scanner(System.in); Connection connection = DriverManager.getConnection(url, USER, PASSWORD)) {
            System.out.println("Conexão bem-sucedida!");
            Statement statement = connection.createStatement();

            while (true) {
                System.out.print("Digite uma instrução SELECT ou SAIR para terminar: ");
                String sql = scanner.nextLine();

                if (sql.equalsIgnoreCase("SAIR")) {
                    break;
                }

                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    int columnCount = resultSet.getMetaData().getColumnCount();

                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(resultSet.getString(i) + "\t");
                        }
                        System.out.println();
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao executar a instrução: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar à base de dados: " + e.getMessage());
        }
    }
}