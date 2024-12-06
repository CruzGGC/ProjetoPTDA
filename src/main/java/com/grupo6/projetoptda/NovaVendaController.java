package com.grupo6.projetoptda;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

public class NovaVendaController {

    @FXML
    public void onCategoriaClick() {
        System.out.println("Categoria clicada!");
    }

    @FXML
    public void onProdutoClick() {
        System.out.println("Produto clicado!");
    }

    @FXML
    public void onPagamentoClick() {
        System.out.println("Pagamento iniciado!");
    }

    @FXML
    public void onConsumidorFinalClick() {
        System.out.println("Consumidor final selecionado!");
    }
    @FXML
    public void onVoltarClick() {
        try {
            // Carregar o layout principal
            Parent mainPanelRoot = FXMLLoader.load(getClass().getResource("/com/grupo6/projetoptda/MainPanel.fxml"));

            // Obter a cena atual e substituir pelo layout principal
            Scene scene = ((Stage) Stage.getWindows().get(0)).getScene();
            scene.setRoot(mainPanelRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private HBox categoriasPane; // Painel para botões de categorias
    @FXML
    private FlowPane produtosPane; // Painel para botões de produtos

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
        produtosPane.getChildren().clear(); // Limpar os produtos existentes

        List<Produto> produtos = buscarProdutosPorCategoria(idCategoria);

        for (Produto produto : produtos) {
            Button btnProduto = new Button(produto.getNome() + "\n" + String.format("%.2f€", produto.getPreco()));
            btnProduto.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
            produtosPane.getChildren().add(btnProduto);

            // Adicionar ação para o botão (exemplo: adicionar ao carrinho)
            btnProduto.setOnAction(e -> {
                System.out.println("Produto selecionado: " + produto.getNome());
                // Lógica para adicionar ao carrinho
            });
        }
    }

    private List<Categoria> buscarCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String query = "SELECT idCategoria, nome FROM Categoria";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idCategoria = rs.getInt("idCategoria");
                String nome = rs.getString("nome");
                categorias.add(new Categoria(idCategoria, nome));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categorias;
    }

    private List<Produto> buscarProdutosPorCategoria(int idCategoria) {
        List<Produto> produtos = new ArrayList<>();
        String query = "SELECT idProduto, nome, preco, quantidadeStock FROM Produto WHERE idCategoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCategoria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idProduto = rs.getInt("idProduto");
                    String nome = rs.getString("nome");
                    double preco = rs.getDouble("preco");
                    int quantidadeStock = rs.getInt("quantidadeStock");
                    produtos.add(new Produto(idProduto, nome, preco, quantidadeStock));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return produtos;
    }
}
