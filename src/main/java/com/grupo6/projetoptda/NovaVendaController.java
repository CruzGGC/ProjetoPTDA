package com.grupo6.projetoptda;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private FlowPane produtosPane;

    @FXML
    public void initialize() {
        carregarProdutos();
    }

    private void carregarProdutos() {
        List<Produto> produtos = buscarProdutos();

        for (Produto produto : produtos) {
            if (produto.getQuantidadeStock() > 0) { // Mostrar apenas produtos em estoque
                Button btnProduto = new Button(produto.getNome() + "\n" + String.format("%.2f€", produto.getPreco()));
                btnProduto.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
                produtosPane.getChildren().add(btnProduto);

                // Adicionar ação para o botão
                btnProduto.setOnAction(e -> {
                    System.out.println("Produto selecionado: " + produto.getNome());
                    // Adicione lógica para adicionar ao carrinho ou outra funcionalidade
                });
            }
        }
    }

    private List<Produto> buscarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String query = "SELECT idProduto, nome, preco, quantidadeStock FROM Produto";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idProduto = rs.getInt("idProduto");
                String nome = rs.getString("nome");
                double preco = rs.getDouble("preco");
                int quantidadeStock = rs.getInt("quantidadeStock");
                produtos.add(new Produto(idProduto, nome, preco, quantidadeStock));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return produtos;
    }
}
