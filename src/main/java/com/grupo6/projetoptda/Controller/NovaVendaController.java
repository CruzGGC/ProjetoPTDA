package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.*;
import com.grupo6.projetoptda.Getter.Cliente;
import com.grupo6.projetoptda.Getter.ProdutoSelecionado;
import com.grupo6.projetoptda.Getter.Categoria;
import com.grupo6.projetoptda.Getter.Produto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Label;

import static com.grupo6.projetoptda.Utilidades.InterfaceUtils.recarregarInterface;

/**
 * A classe NovaVendaController gere a interface de criação de novas vendas no JavaFX.
 */
public class NovaVendaController {

    @FXML
    public Label labelData;
    @FXML
    private Label labelUtilizador;

    /**
     * Volta para o painel principal.
     */
    @FXML
    public void onVoltarClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/MainPanel.fxml");
        } catch (Exception e) {
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
    private final AppState appState = AppState.getInstance();

    /**
     * Inicializa o controlador, configurando as colunas da tabela e carregando categorias e clientes.
     */
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
        DateUtils.updateDate(labelData);
        labelUtilizador.setText(appState.getNomeFuncionario());
    }

    /**
     * Mostra o painel para adicionar um novo cliente.
     */
    @FXML
    public void mostrarAddClientPane() {
        Panes.showPane(addClientPane);
    }

    /**
     * Seleciona o cliente anónimo.
     */
    @FXML
    public void selecionarClienteAnonimo() {
        Cliente clienteAnonimo = new Cliente(1, "Cliente Anónimo");
        if (!clienteComboBox.getItems().contains(clienteAnonimo)) {
            clienteComboBox.getItems().add(clienteAnonimo);
        }
        clienteComboBox.getSelectionModel().select(clienteAnonimo);
    }

    /**
     * Esconde o painel para adicionar um novo cliente.
     */
    @FXML
    public void fecharAddClientPane() {
        addClientPane.setVisible(false);
        addClientPane.setManaged(false);
    }

    /**
     * Carrega os clientes a partir da base de dados.
     */
    private void carregarClientes() {
        List<Cliente> clientes = buscarClientes();
        clienteComboBox.setItems(FXCollections.observableArrayList(clientes));
    }

    /**
     * Busca os clientes na base de dados.
     *
     * @return uma lista de clientes
     */
    private List<Cliente> buscarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String query = "SELECT * FROM Cliente WHERE idCliente > 2"; // Filtra clientes com id > 2

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

    /**
     * Carrega as categorias a partir da base de dados.
     */
    private void carregarCategorias() {
        List<Categoria> categorias = buscarCategorias();

        for (Categoria categoria : categorias) {
            Button btnCategoria = new Button(categoria.getNome());
            btnCategoria.getStyleClass().add("btn-categoria");
            categoriasPane.getChildren().add(btnCategoria);

            // Adicionar ação para filtrar produtos por categoria
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
        List<Produto> produtos = buscarProdutosPorCategoria(idCategoria);

        for (Produto produto : produtos) {
            Button btnProduto = new Button(produto.getNome() + "\n" + String.format("%.2f€", produto.getPreco()));
            btnProduto.getStyleClass().add("btn-produto");
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
     * Busca as categorias na base de dados.
     *
     * @return uma lista de categorias
     */
    private List<Categoria> buscarCategorias() {
        return DatabaseUtils.fetchCategories();
    }

    /**
     * Busca os produtos de uma categoria específica na base de dados.
     *
     * @param idCategoria o ID da categoria
     * @return uma lista de produtos
     */
    private List<Produto> buscarProdutosPorCategoria(int idCategoria) {
        return DatabaseUtils.buscarProdutosPorCategoria(idCategoria);
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
     * Cria um novo pedido com os produtos selecionados.
     */
    private void criarPedido() {
        Cliente clienteSelecionado = clienteComboBox.getSelectionModel().getSelectedItem();
        if (clienteSelecionado == null) {
            System.out.println("Nenhum cliente selecionado!");
            return;
        }

        int idCliente = clienteSelecionado.idCliente();
        String jsonProdutos = criarJsonProdutos();
        int idFuncionario = appState.getFuncionarioId();

        String query = "{CALL criarPedido(?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, jsonProdutos);
            stmt.setInt(3, idFuncionario);
            boolean hadResults = stmt.execute();

            if (hadResults) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        int idPedido = rs.getInt("idPedido");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Confirmação");
                        alert.setHeaderText(null);
                        alert.setContentText("Pedido criado com sucesso! ID do Pedido: " + idPedido);
                        alert.showAndWait();
                        recarregarInterface("/com/grupo6/projetoptda/NovaVendaPanel.fxml");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao criar pedido: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Adiciona um novo cliente à base de dados.
     */
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
            recarregarInterface("/com/grupo6/projetoptda/NovaVendaPanel.fxml");
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao adicionar cliente: " + e.getMessage());
            alert.showAndWait();
        }
    }
}