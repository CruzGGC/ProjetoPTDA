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

/**
 * A classe ModificarVendaController gere a interface de modificação de vendas no JavaFX.
 */
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
    private TableColumn<ProdutoSelecionado, Void> colBtn;

    @FXML
    private Label labelUtilizador;

    private final AppState appState = AppState.getInstance();

    private final ObservableList<ProdutoSelecionado> produtosSelecionados = FXCollections.observableArrayList();

    /**
     * Volta para o painel de gestão de pedidos.
     */
    @FXML
    public void voltarParaGerirPedidos() {
        try {
            SceneManager.setScene((Stage) modificarPedidoButton.getScene().getWindow(), "/com/grupo6/projetoptda/GerirPedidosPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa o controlador, configurando as colunas da tabela e carregando categorias.
     */
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

    /**
     * Modifica o pedido atual com os produtos selecionados.
     */
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

    /**
     * Cria um JSON com os produtos selecionados.
     *
     * @return uma string JSON com os produtos
     */
    private String criarJsonProdutos() {
        JsonArray jsonArray = new JsonArray();

        for (ProdutoSelecionado produto : tabelaProdutos.getItems()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("idProduto", produto.getIdProduto());
            jsonObject.addProperty("quantidade", produto.getQuantidadeStock());
            jsonObject.addProperty("preco", produto.getPreco()); // Adiciona o preço do produto
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    /**
     * Adiciona um botão de remoção à tabela de produtos.
     */
    private void addButtonToTable() {
        Callback<TableColumn<ProdutoSelecionado, Void>, TableCell<ProdutoSelecionado, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ProdutoSelecionado, Void> call(final TableColumn<ProdutoSelecionado, Void> param) {
                return new TableCell<>() {

                    private final Button btn = new Button("Remover");

                    {
                        btn.setText("🗑");
                        btn.getStyleClass().add("btn-tables");
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

        // Verifica se a coluna já foi adicionada para evitar duplicação
        if (!tabelaProdutos.getColumns().contains(colBtn)) {
            tabelaProdutos.getColumns().add(colBtn);
            colBtn.getStyleClass().add("btn-pedido-buttons");
        }

    }

    /**
     * Carrega as categorias a partir da base de dados.
     */
    private void carregarCategorias() {
        List<Categoria> categorias = DatabaseUtils.fetchCategories();

        for (Categoria categoria : categorias) {
            Button btnCategoria = new Button(categoria.getNome());
            btnCategoria.getStyleClass().add("btn-categoria");
            categoriasPane.getChildren().add(btnCategoria);

            btnCategoria.setOnAction(e -> carregarProdutosPorCategoria(categoria.getIdCategoria()));
        }
    }

    /**
     * Carrega os produtos de uma categoria específica.
     *
     * @param idCategoria o ID da categoria
     */
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

    /**
     * Adiciona um produto à lista de produtos selecionados.
     *
     * @param produto o produto a ser adicionado
     */
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

    /**
     * Atualiza os totais de preço e quantidade dos produtos selecionados.
     */
    private void atualizarTotais() {
        double total = produtosSelecionados.stream().mapToDouble(ProdutoSelecionado::getTotal).sum();
        int quantidade = produtosSelecionados.stream().mapToInt(ProdutoSelecionado::getQuantidadeStock).sum();

        labelTotal.setText(String.format("Total: %.2f€", total));
        labelNumArtigos.setText("Nº de artigos/quantidade: " + quantidade);
    }

    /**
     * Remove um produto da lista de produtos selecionados.
     *
     * @param produto o produto a ser removido
     */
    @FXML
    private void removerProduto(ProdutoSelecionado produto) {
        produtosSelecionados.remove(produto);
        atualizarTotais();
    }

    /**
     * Define o ID do pedido a ser modificado.
     *
     * @param idPedido o ID do pedido
     */
    public void setPedidoId(int idPedido) {
        labelIdPedido.setText("Pedido: " + idPedido);
        carregarProdutosDoPedido(idPedido);
    }

    /**
     * Carrega os produtos do pedido especificado.
     *
     * @param idPedido o ID do pedido
     */
    private void carregarProdutosDoPedido(int idPedido) {
        List<ProdutoSelecionado> produtos = buscarProdutosDoPedido(idPedido);
        produtosSelecionados.setAll(produtos);
    }

    /**
     * Busca os produtos do pedido especificado na base de dados.
     *
     * @param idPedido o ID do pedido
     * @return uma lista de produtos selecionados
     */
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