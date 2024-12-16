package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Getter.Categoria;
import com.grupo6.projetoptda.Getter.Produto;
import com.grupo6.projetoptda.Utilidades.DatabaseUtils;
import com.grupo6.projetoptda.Utilidades.DateUtils;
import com.grupo6.projetoptda.Utilidades.SceneManager;
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

    private ObservableList<Categoria> categorias;
    private ObservableList<Produto> produtos;

    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        loadCategorias();
        setupTableView();
        produtos = FXCollections.observableArrayList();
        tableView.setItems(produtos);
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
        for (Produto produto : produtos) {
            if (!isRowEmpty(produto)) {
                DatabaseUtils.adicionarProduto(produto.getNome(), produto.getCategoria().getIdCategoria(), produto.getPreco(), produto.getQuantidade());
            }
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