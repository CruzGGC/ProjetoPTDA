package com.grupo6.projetoptda;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GerirStockController {

    @FXML
    private HBox categoriasPane;

    @FXML
    private TableView<Produto> produtosTable;

    @FXML
    private TableColumn<Produto, String> colNome;

    @FXML
    private TableColumn<Produto, Double> colPreco;

    @FXML
    private TableColumn<Produto, Integer> colQuantidade;

    @FXML
    private TableColumn<Produto, Void> colModificar;

    @FXML
    private TableColumn<Produto, Void> colRemover;

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidadeStock"));

        addButtonToTable();

        carregarCategorias();
    }

    private void addButtonToTable() {
        colModificar.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modificar");

            {
                btn.setOnAction(event -> {
                    Produto produto = getTableView().getItems().get(getIndex());
                    modificarProduto(produto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        colRemover.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Remover");

            {
                btn.setOnAction(event -> {
                    Produto produto = getTableView().getItems().get(getIndex());
                    removerProduto(produto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void carregarCategorias() {
        List<Categoria> categorias = buscarCategorias();

        for (Categoria categoria : categorias) {
            Button btnCategoria = new Button(categoria.getNome());
            btnCategoria.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
            btnCategoria.setOnAction(event -> carregarProdutos(categoria.getIdCategoria()));
            categoriasPane.getChildren().add(btnCategoria);
        }
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

    private void carregarProdutos(int idCategoria) {
        List<Produto> produtos = buscarProdutos(idCategoria);
        ObservableList<Produto> produtosObservableList = FXCollections.observableArrayList(produtos);
        produtosTable.setItems(produtosObservableList);
    }

    private List<Produto> buscarProdutos(int idCategoria) {
        List<Produto> produtos = new ArrayList<>();
        String query = "SELECT * FROM Produto WHERE idCategoria = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCategoria);
            try (ResultSet resultSet = stmt.executeQuery()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return produtos;
    }

    private void modificarProduto(Produto produto) {
        // Call the stored procedure to modify the product
        String query = "{CALL modificarProduto(?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, produto.getIdProduto());
            stmt.setInt(2, produto.getIdCategoria());
            stmt.setString(3, produto.getNome());
            stmt.setDouble(4, produto.getPreco());
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removerProduto(Produto produto) {
        // Call the stored procedure to remove the product
        String query = "{CALL removerProduto(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, produto.getIdProduto());
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
