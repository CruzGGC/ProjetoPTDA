package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Getter.Categoria;
import com.grupo6.projetoptda.Getter.Produto;
import com.grupo6.projetoptda.Utilidades.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.sql.*;

/**
 * A classe GerirComprasController gere a interface de gestão de compras no JavaFX.
 */
public class GerirComprasController {

    @FXML
    private TableView<Produto> tableView;

    @FXML
    private TableColumn<Produto, String> nomeColumn;

    @FXML
    private TableColumn<Produto, Categoria> categoriaColumn;

    @FXML
    private TableColumn<Produto, Double> precoColumn;

    @FXML
    private TableColumn<Produto, Integer> quantidadeColumn;

    @FXML
    private Button voltarButton;

    @FXML
    private Label labelData;

    @FXML
    private StackPane addCategoryPane;

    @FXML
    private TextField nomeCategoriaField;

    @FXML
    private Label labelUtilizador;

    private final AppState appState = AppState.getInstance();

    private ObservableList<Categoria> categorias;
    private ObservableList<Produto> produtos;

    /**
     * Inicializa o controlador, configurando a tabela e carregando as categorias.
     */
    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        loadCategorias();
        setupTableView();
        produtos = FXCollections.observableArrayList();
        tableView.setItems(produtos);
        labelUtilizador.setText(appState.getNomeFuncionario());
    }

    /**
     * Configura as colunas da TableView para permitir edição.
     */
    private void setupTableView() {
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nomeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nomeColumn.setOnEditCommit(event -> event.getRowValue().setNome(event.getNewValue()));

        categoriaColumn.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        categoriaColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categorias));
        categoriaColumn.setOnEditCommit(event -> event.getRowValue().setCategoria(event.getNewValue()));

        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));
        precoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        precoColumn.setOnEditCommit(event -> event.getRowValue().setPreco(event.getNewValue()));

        quantidadeColumn.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        quantidadeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantidadeColumn.setOnEditCommit(event -> event.getRowValue().setQuantidade(event.getNewValue()));

        tableView.setEditable(true);
    }

    /**
     * Carrega as categorias a partir da base de dados.
     */
    private void loadCategorias() {
        categorias = FXCollections.observableArrayList(DatabaseUtils.fetchCategories());
    }

    /**
     * Adiciona os produtos da tabela à base de dados e emite a compra e a fatura.
     */
    @FXML
    private void adicionarProdutos() {
        ObservableList<Produto> produtos = tableView.getItems();
        if (produtos.isEmpty()) {
            System.out.println("No products in the table.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DatabaseConnection.URL, DatabaseConnection.USER, DatabaseConnection.PASSWORD)) {
            for (Produto produto : produtos) {
                if (!isRowEmpty(produto)) {
                    System.out.println("Processing product: " + produto);

                    // Chama o procedimento armazenado para adicionar ou atualizar o produto
                    String sql = "{CALL adicionarProduto(?, ?, ?, ?)}";
                    try (CallableStatement stmt = connection.prepareCall(sql)) {
                        stmt.setString(1, produto.getNome());
                        stmt.setInt(2, produto.getCategoria().getIdCategoria());
                        stmt.setDouble(3, produto.getPreco());
                        stmt.setInt(4, produto.getQuantidade());
                        stmt.execute();
                    }
                } else {
                    System.out.println("Skipping empty row: " + produto);
                }
            }

            // Converte os produtos para o formato JSON
            StringBuilder jsonBuilder = new StringBuilder("[");
            for (Produto produto : produtos) {
                if (!isRowEmpty(produto)) {
                    jsonBuilder.append("{")
                            .append("\"nome\":\"").append(produto.getNome()).append("\",")
                            .append("\"idCategoria\":").append(produto.getCategoria().getIdCategoria()).append(",")
                            .append("\"quantidade\":").append(produto.getQuantidade()).append(",")
                            .append("\"preco\":").append(produto.getPreco())
                            .append("},");
                }
            }
            if (jsonBuilder.length() > 1) {
                jsonBuilder.setLength(jsonBuilder.length() - 1); // Remove trailing comma
            }
            jsonBuilder.append("]");

            String produtosJson = jsonBuilder.toString();
            System.out.println("Generated JSON: " + produtosJson);

            // Chama o procedimento armazenado para emitir a compra
            String sqlEmitirCompra = "{CALL efetuarCompra(?)}";
            try (CallableStatement stmtCompra = connection.prepareCall(sqlEmitirCompra)) {
                stmtCompra.setString(1, produtosJson);
                stmtCompra.execute(); // Garante que o procedimento armazenado é executado

                // Recupera o idCompra gerado
                ResultSet rs = stmtCompra.getResultSet();
                if (rs.next()) {
                    int idCompra = rs.getInt("idCompra");

                    // Chama o procedimento armazenado para emitir a fatura da compra
                    String sqlEmitirFaturaCompra = "{CALL emitirFaturaCompra(?)}";
                    try (CallableStatement stmtFatura = connection.prepareCall(sqlEmitirFaturaCompra)) {
                        stmtFatura.setInt(1, idCompra);
                        stmtFatura.execute(); // Garante que o procedimento armazenado é executado
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra o painel para adicionar uma nova categoria.
     */
    @FXML
    private void botaoAdicionarCategoria() {
        mostrarAddCategoryPane();
    }

    /**
     * Torna visível o painel para adicionar uma nova categoria.
     */
    @FXML
    private void mostrarAddCategoryPane() {
        addCategoryPane.setVisible(true);
        addCategoryPane.setManaged(true);
    }

    /**
     * Esconde o painel para adicionar uma nova categoria.
     */
    @FXML
    private void fecharAddCategoryPane() {
        addCategoryPane.setVisible(false);
        addCategoryPane.setManaged(false);
    }

    /**
     * Adiciona uma nova categoria à base de dados.
     */
    @FXML
    private void adicionarCategoria() {
        String nome = nomeCategoriaField.getText();
        DatabaseUtils.adicionarCategoria(nome);
        fecharAddCategoryPane();
        loadCategorias();
    }

    /**
     * Volta para o painel principal.
     */
    @FXML
    private void botaoVoltar() {
        try {
            SceneManager.setScene((Stage) voltarButton.getScene().getWindow(), "/com/grupo6/projetoptda/MainPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adiciona uma nova linha à tabela de produtos.
     */
    @FXML
    private void adicionarLinha() {
        Produto novoProduto = new Produto(0, 0, "", 0.0, 0);
        produtos.add(novoProduto);
    }

    /**
     * Verifica se uma linha da tabela está vazia.
     *
     * @param produto o produto a ser verificado
     * @return true se a linha estiver vazia, false caso contrário
     */
    private boolean isRowEmpty(Produto produto) {
        return produto.getNome() == null || produto.getNome().trim().isEmpty() ||
                produto.getCategoria() == null ||
                produto.getPreco() <= 0.0 ||
                produto.getQuantidade() <= 0;
    }
}