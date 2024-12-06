package com.grupo6.projetoptda;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.Objects;

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
            Parent mainPanelRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/grupo6/projetoptda/MainPanel.fxml")));

            // Obter a cena atual e substituir pelo layout principal
            Scene scene = Stage.getWindows().getFirst().getScene();
            scene.setRoot(mainPanelRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private HBox categoriasPane; // Painel para botões de categorias
    @FXML
    private HBox produtosPane; // Alterado para HBox

    @FXML
    private TableView<ProdutoSelecionado> tabelaProdutos;
    @FXML
    private TableColumn<ProdutoSelecionado, String> colunaDescricao;
    @FXML
    private TableColumn<ProdutoSelecionado, Double> colunaPVP;
    @FXML
    private TableColumn<ProdutoSelecionado, Integer> colunaQuantidade;
    @FXML
    private TableColumn<ProdutoSelecionado, Double> colunaTotal;
    @FXML
    private Label labelTotal;
    @FXML
    private Label labelNumArtigos;

    private final ObservableList<ProdutoSelecionado> produtosSelecionados = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        colunaDescricao.setCellValueFactory(data -> data.getValue().descricaoProperty());
        colunaPVP.setCellValueFactory(data -> data.getValue().precoProperty().asObject());
        colunaQuantidade.setCellValueFactory(data -> data.getValue().quantidadeStockProperty().asObject());
        colunaTotal.setCellValueFactory(data -> data.getValue().totalProperty().asObject());

        tabelaProdutos.setItems(produtosSelecionados);

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

            btnProduto.setOnAction(e -> adicionarProduto(produto));
        }
    }

    private void adicionarProduto(Produto produto) {
        ProdutoSelecionado existente = produtosSelecionados.stream()
                .filter(p -> p.getIdProduto() == produto.getIdProduto())
                .findFirst()
                .orElse(null);

        if (existente != null) {
            existente.setQuantidadeStock(existente.getQuantidadeStock() + 1);
            existente.setTotal(existente.getQuantidadeStock() * existente.getPreco());
        } else {
            produtosSelecionados.add(new ProdutoSelecionado(produto.getIdProduto(), produto.getNome(), produto.getPreco(), 1, produto.getPreco()));
        }

        atualizarTotais();
    }

    private void atualizarTotais() {
        double total = produtosSelecionados.stream().mapToDouble(ProdutoSelecionado::getTotal).sum();
        int quantidade = produtosSelecionados.stream().mapToInt(ProdutoSelecionado::getQuantidadeStock).sum();

        labelTotal.setText(String.format("Total: %.2f€", total));
        labelNumArtigos.setText("Nº de artigos/quantidade: " + quantidade);
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
