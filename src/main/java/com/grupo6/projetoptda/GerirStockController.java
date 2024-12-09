package com.grupo6.projetoptda;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GerirStockController {

    @FXML
    private HBox categoriasPane;
    @FXML
    private HBox produtosPane;

    @FXML
    public void initialize() {
        carregarCategorias();
    }

    private void carregarCategorias() {
        List<Categoria> categorias = buscarCategorias();

        for (Categoria categoria : categorias) {
            Button btnCategoria = new Button(categoria.getNome());
            btnCategoria.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
            categoriasPane.getChildren().add(btnCategoria);

            // Adicionar ação para filtrar produtos por categoria
            btnCategoria.setOnAction(e -> carregarProdutosPorCategoria(categoria.getIdCategoria()));
        }
    }

    private void carregarProdutosPorCategoria(int idCategoria) {
        produtosPane.getChildren().clear();
        List<Produto> produtos = buscarProdutosPorCategoria(idCategoria);

        for (Produto produto : produtos) {
            Button btnProduto = new Button(produto.getNome() + "\n" + String.format("%.2f€", produto.getPreco()));
            btnProduto.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
            produtosPane.getChildren().add(btnProduto);
        }
    }

    private List<Categoria> buscarCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String query = "SELECT * FROM Categoria";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                Categoria categoria = new Categoria(
                        resultSet.getInt("idCategoria"),
                        resultSet.getString("nome")
                );
                categorias.add(categoria);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categorias;
    }

    private List<Produto> buscarProdutosPorCategoria(int idCategoria) {
        List<Produto> produtos = new ArrayList<>();
        String query = "SELECT * FROM Produto WHERE idCategoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, idCategoria);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Produto produto = new Produto(
                        resultSet.getInt("idProduto"),
                        resultSet.getInt("idCategoria"),
                        resultSet.getString("nome"),
                        resultSet.getDouble("preco"),
                        resultSet.getInt("quantidadeStock")
                );
                produtos.add(produto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return produtos;
    }
}
