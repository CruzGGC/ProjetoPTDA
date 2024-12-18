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

    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        loadCategorias();
        setupTableView();
        produtos = FXCollections.observableArrayList();
        tableView.setItems(produtos);
        labelUtilizador.setText(appState.getNomeFuncionario());
    }

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

    private void loadCategorias() {
        categorias = FXCollections.observableArrayList(DatabaseUtils.fetchCategories());
    }

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

                    // Call the stored procedure to add or update the product
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

            // Convert products to JSON format
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

            // Call the stored procedure to emit the purchase
            String sqlEmitirCompra = "{CALL efetuarCompra(?)}";
            try (CallableStatement stmtCompra = connection.prepareCall(sqlEmitirCompra)) {
                stmtCompra.setString(1, produtosJson);
                stmtCompra.execute(); // Ensure the stored procedure is executed

                // Retrieve the generated idCompra
                ResultSet rs = stmtCompra.getResultSet();
                if (rs.next()) {
                    int idCompra = rs.getInt("idCompra");

                    // Call the stored procedure to emit the invoice for the purchase
                    String sqlEmitirFaturaCompra = "{CALL emitirFaturaCompra(?)}";
                    try (CallableStatement stmtFatura = connection.prepareCall(sqlEmitirFaturaCompra)) {
                        stmtFatura.setInt(1, idCompra);
                        stmtFatura.execute(); // Ensure the stored procedure is executed
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void botaoAdicionarCategoria() {
        mostrarAddCategoryPane();
    }

    @FXML
    private void mostrarAddCategoryPane() {
        addCategoryPane.setVisible(true);
        addCategoryPane.setManaged(true);
    }

    @FXML
    private void fecharAddCategoryPane() {
        addCategoryPane.setVisible(false);
        addCategoryPane.setManaged(false);
    }

    @FXML
    private void adicionarCategoria() {
        String nome = nomeCategoriaField.getText();
        DatabaseUtils.adicionarCategoria(nome);
        fecharAddCategoryPane();
        loadCategorias();
    }

    @FXML
    private void botaoVoltar() {
        try {
            SceneManager.setScene((Stage) voltarButton.getScene().getWindow(), "/com/grupo6/projetoptda/MainPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void adicionarLinha() {
        Produto novoProduto = new Produto(0, 0, "", 0.0, 0);
        produtos.add(novoProduto);
    }

    private boolean isRowEmpty(Produto produto) {
        return produto.getNome() == null || produto.getNome().trim().isEmpty() ||
                produto.getCategoria() == null ||
                produto.getPreco() <= 0.0 ||
                produto.getQuantidade() <= 0;
    }
}