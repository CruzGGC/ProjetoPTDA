package com.grupo6.projetoptda.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grupo6.projetoptda.Getter.Categoria;
import com.grupo6.projetoptda.Getter.Produto;
import com.grupo6.projetoptda.Getter.ProdutoSelecionado;
import com.grupo6.projetoptda.Utilidades.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModificarVendaController {

    @FXML
    private Label labelTotal;

    @FXML
    private Label labelNumArtigos;

    @FXML
    private Label labelData;

    @FXML
    private Label labelIdPedido;

    @FXML
    private FlowPane categoriasPane;

    @FXML
    private FlowPane produtosPane;

    @FXML
    private Button modificarPedidoButton;

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
    private Label labelUtilizador;

    private AppState appState = AppState.getInstance();

    private final ObservableList<ProdutoSelecionado> produtosSelecionados = FXCollections.observableArrayList();

    @FXML
    public void voltarParaGerirPedidos() {
        try {
            SceneManager.setScene((Stage) modificarPedidoButton.getScene().getWindow(), "/com/grupo6/projetoptda/GerirPedidosPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        colunaDescricao.setCellValueFactory(data -> data.getValue().descricaoProperty());
        colunaPVP.setCellValueFactory(data -> data.getValue().precoProperty().asObject());
        colunaQuantidade.setCellValueFactory(data -> data.getValue().quantidadeStockProperty().asObject());
        colunaTotal.setCellValueFactory(data -> data.getValue().totalProperty().asObject());

        modificarPedidoButton.setOnAction(event -> modificarPedido());

        tabelaProdutos.setItems(produtosSelecionados);
        carregarCategorias();
        addButtonToTable();
        DateUtils.updateDate(labelData);
        labelUtilizador.setText(appState.getNomeFuncionario());
    }

    private void modificarPedido() {
        int idPedido = Integer.parseInt(labelIdPedido.getText().replace("Pedido: ", ""));
        String jsonProdutos = criarJsonProdutos();
        int idFuncionario = appState.getFuncionarioId();

        String query = "{CALL personalizarPedido(?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, idPedido);
            stmt.setString(2, jsonProdutos);
            stmt.setInt(3, idFuncionario);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Pedido modificado com sucesso!");
            alert.showAndWait();
            voltarParaGerirPedidos();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao modificar pedido: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private String criarJsonProdutos() {
        JsonArray jsonArray = new JsonArray();

        for (ProdutoSelecionado produto : tabelaProdutos.getItems()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("idProduto", produto.getIdProduto());
            jsonObject.addProperty("quantidade", produto.getQuantidadeStock());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    private void addButtonToTable() {
        TableColumn<ProdutoSelecionado, Void> colBtn = new TableColumn<>("Remover");

        Callback<TableColumn<ProdutoSelecionado, Void>, TableCell<ProdutoSelecionado, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ProdutoSelecionado, Void> call(final TableColumn<ProdutoSelecionado, Void> param) {
                return new TableCell<>() {

                    private final Button btn = new Button("Remover");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ProdutoSelecionado produto = getTableView().getItems().get(getIndex());
                            removerProduto(produto);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        colBtn.setCellFactory(cellFactory);
        tabelaProdutos.getColumns().add(colBtn);
    }

    private void carregarCategorias() {
        List<Categoria> categorias = DatabaseUtils.fetchCategories();

        for (Categoria categoria : categorias) {
            Button btnCategoria = new Button(categoria.getNome());
            btnCategoria.getStyleClass().add("btn-categoria");
            categoriasPane.getChildren().add(btnCategoria);

            btnCategoria.setOnAction(e -> carregarProdutosPorCategoria(categoria.getIdCategoria()));
        }
    }

    private void carregarProdutosPorCategoria(int idCategoria) {
        produtosPane.getChildren().clear();
        List<Produto> produtos = DatabaseUtils.buscarProdutosPorCategoria(idCategoria);

        for (Produto produto : produtos) {
            Button btnProduto = new Button(produto.getNome() + "\n" + String.format("%.2f€", produto.getPreco()));
            btnProduto.getStyleClass().add("btn-item");
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

    @FXML
    private void removerProduto(ProdutoSelecionado produto) {
        produtosSelecionados.remove(produto);
        atualizarTotais();
    }

    public void setPedidoId(int idPedido) {
        labelIdPedido.setText("Pedido: " + idPedido);
        carregarProdutosDoPedido(idPedido);
    }

    private void carregarProdutosDoPedido(int idPedido) {
        List<ProdutoSelecionado> produtos = buscarProdutosDoPedido(idPedido);
        produtosSelecionados.setAll(produtos);
    }

    private List<ProdutoSelecionado> buscarProdutosDoPedido(int idPedido) {
        List<ProdutoSelecionado> produtos = new ArrayList<>();
        String query = "SELECT p.idProduto, p.nome, p.preco, pp.quantidade " +
                "FROM Produto p " +
                "JOIN PedidoProduto pp ON p.idProduto = pp.idProduto " +
                "WHERE pp.idPedido = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idPedido);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    ProdutoSelecionado produto = new ProdutoSelecionado(
                            resultSet.getInt("idProduto"),
                            resultSet.getString("nome"),
                            resultSet.getDouble("preco"),
                            resultSet.getInt("quantidade"),
                            resultSet.getDouble("preco") * resultSet.getInt("quantidade")
                    );
                    produtos.add(produto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return produtos;
    }
}