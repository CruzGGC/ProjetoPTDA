package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DetalhesFaturaController {

    @FXML
    private Label labelConteudo;

    public void carregarDetalhesFatura(int idFatura) {
        String query = "{CALL mostrarDetalhesFatura(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement stmt = connection.prepareCall(query)) {

            stmt.setInt(1, idFatura);

            ResultSet rs = stmt.executeQuery();
            StringBuilder detalhes = new StringBuilder();

            if (rs.next()) {
                detalhes.append("ID Fatura: ").append(rs.getInt("ID Fatura")).append("\n")
                        .append("Data: ").append(rs.getDate("Data")).append("\n")
                        .append("Hora: ").append(rs.getTime("Hora")).append("\n")
                        .append("Nome do Cliente: ").append(rs.getString("Nome do Cliente")).append("\n")
                        .append("ID Pedido: ").append(rs.getInt("ID Pedido")).append("\n")
                        .append("Produtos: ").append(rs.getString("Produtos")).append("\n")
                        .append("Total Fatura: ").append(rs.getDouble("Total Fatura")).append("\n");
            }

            labelConteudo.setText(detalhes.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void carregarDetalhesFaturaCompra(int idFatura) {
        String query = "{CALL mostrarDetalhesFaturaCompra(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement stmt = connection.prepareCall(query)) {

            stmt.setInt(1, idFatura);

            ResultSet rs = stmt.executeQuery();
            StringBuilder detalhes = new StringBuilder();

            if (rs.next()) {
                detalhes.append("ID Fatura: ").append(rs.getInt("ID Fatura")).append("\n")
                        .append("Data: ").append(rs.getDate("Data")).append("\n")
                        .append("Hora: ").append(rs.getTime("Hora")).append("\n")
                        .append("Nome do Cliente: ").append(rs.getString("Nome do Cliente")).append("\n")
                        .append("ID Compra: ").append(rs.getInt("ID Compra")).append("\n")
                        .append("Produtos: ").append(rs.getString("Produtos")).append("\n")
                        .append("Total Fatura: ").append(rs.getDouble("Total Fatura")).append("\n");
            }

            labelConteudo.setText(detalhes.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}