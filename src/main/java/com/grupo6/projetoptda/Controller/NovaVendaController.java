package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Getter.Cliente;
import com.grupo6.projetoptda.Getter.ProdutoSelecionado;
import com.grupo6.projetoptda.Getter.Categoria;
import com.grupo6.projetoptda.Getter.Produto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class NovaVendaController {

    @FXML
    public Label labelData;

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
    private ComboBox<Cliente> clienteComboBox; // ComboBox para selecionar o cliente
    @FXML
    private FlowPane categoriasPane; // Painel para botões de categorias
    @FXML
    private FlowPane produtosPane; // Painel para botões de produtos
    @FXML
    private Button pedidoButton; // Botão para finalizar o pedido
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
    @FXML
    private StackPane addClientPane;
    @FXML
    private TextField nomeClienteField;

    private final ObservableList<ProdutoSelecionado> produtosSelecionados = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        colunaDescricao.setCellValueFactory(data -> data.getValue().descricaoProperty());
        colunaPVP.setCellValueFactory(data -> data.getValue().precoProperty().asObject());
        colunaQuantidade.setCellValueFactory(data -> data.getValue().quantidadeStockProperty().asObject());
        colunaTotal.setCellValueFactory(data -> data.getValue().totalProperty().asObject());

        tabelaProdutos.setItems(produtosSelecionados);
        pedidoButton.setOnAction(event -> criarPedido());
        carregarCategorias();
        carregarClientes();

        // Formatar a data atual
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        String formattedDate = currentDate.format(formatter);

        // Definir a data no label
        labelData.setText(formattedDate);
    }

    private void recarregarInterface() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/grupo6/projetoptda/NovaVendaPanel.fxml")));
            Scene scene = Stage.getWindows().getFirst().getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarAddClientPane() {
        addClientPane.setVisible(true);
        addClientPane.setManaged(true);
        addClientPane.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), addClientPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    public void fecharAddClientPane() {
        addClientPane.setVisible(false);
        addClientPane.setManaged(false);
    }

    private void carregarClientes() {
        List<Cliente> clientes = buscarClientes();
        clienteComboBox.setItems(FXCollections.observableArrayList(clientes));
    }

    private List<Cliente> buscarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String query = "SELECT * FROM Cliente";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getInt("idCliente"),
                        resultSet.getString("nome")
                );
                clientes.add(cliente);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientes;
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

    private void criarPedido() {
        Cliente clienteSelecionado = clienteComboBox.getSelectionModel().getSelectedItem();
        if (clienteSelecionado == null) {
            // Handle case where no client is selected
            System.out.println("Nenhum cliente selecionado!");
            return;
        }

        int idCliente = clienteSelecionado.getIdCliente();
        String jsonProdutos = criarJsonProdutos();

        String query = "{CALL criarPedido(?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, jsonProdutos);
            boolean hadResults = stmt.execute();

            if (hadResults) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        int idPedido = rs.getInt("idPedido");
                        // Exibir confirmação
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Confirmação");
                        alert.setHeaderText(null);
                        alert.setContentText("Pedido criado com sucesso! ID do Pedido: " + idPedido);
                        alert.showAndWait();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Exibir erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao criar pedido: " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    public void adicionarCliente() {
        String nome = nomeClienteField.getText();

        String query = "{CALL criarCliente(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, nome);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Cliente adicionado com sucesso!");
            alert.showAndWait();
            fecharAddClientPane();
            carregarClientes();
            recarregarInterface();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao adicionar cliente: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
