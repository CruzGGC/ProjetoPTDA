package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Getter.Categoria;
import com.grupo6.projetoptda.Getter.Produto;


import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GerirStockController {

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
    private HBox categoriasPane;

    @FXML
    private TableView<Produto> produtosTable;

    @FXML
    private TableColumn<Produto, Integer> colId;

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
    private TableColumn<Produto, Void> colAtualizarStock;

    @FXML
    private StackPane addCategoryPane;

    @FXML
    private TextField nomeCategoriaField;

    @FXML
    private StackPane addProductPane;

    @FXML
    private TextField nomeProdutoField;

    @FXML
    private TextField idCategoriaField;

    @FXML
    private TextField precoProdutoField;

    @FXML
    private TextField quantidadeProdutoField;

    @FXML
    private StackPane modifyProductPane;

    @FXML
    private TextField modifyNomeProdutoField;

    @FXML
    private TextField modifyPrecoProdutoField;

    @FXML
    private TextField modifyQuantidadeProdutoField;

    @FXML
    private StackPane atualizarStockPane;

    @FXML
    private TextField atualizarQuantidadeField;

    private Produto produtoAtual;

    @FXML
    public void mostrarAddProductPane() {
        fecharAddCategoryPane(); // Fechar o painel de adicionar categoria
        addProductPane.setVisible(true);
        addProductPane.setManaged(true);
        addProductPane.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), addProductPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    public void fecharAddProductPane() {
        addProductPane.setVisible(false);
        addProductPane.setManaged(false);
    }

    @FXML
    public void adicionarProduto() {
        String nome = nomeProdutoField.getText();
        int idCategoria = Integer.parseInt(idCategoriaField.getText());
        double preco = Double.parseDouble(precoProdutoField.getText());
        int quantidade = Integer.parseInt(quantidadeProdutoField.getText());

        String query = "{CALL adicionarProduto(?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, nome);
            stmt.setInt(2, idCategoria);
            stmt.setDouble(3, preco);
            stmt.setInt(4, quantidade);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Produto adicionado com sucesso!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao adicionar produto: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void mostrarAddCategoryPane() {
        fecharAddProductPane(); // Fechar o painel de adicionar produto
        addCategoryPane.setVisible(true);
        addCategoryPane.setManaged(true);
        addCategoryPane.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), addCategoryPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    public void fecharAddCategoryPane() {
        addCategoryPane.setVisible(false);
        addCategoryPane.setManaged(false);
    }

    @FXML
    public void mostrarModifyProductPane(Produto produto) {
        fecharAddProductPane(); // Fechar outros paineis
        fecharAddCategoryPane();

        produtoAtual = produto;
        modifyNomeProdutoField.setText(produto.getNome());
        modifyPrecoProdutoField.setText(String.valueOf(produto.getPreco()));
        modifyQuantidadeProdutoField.setText(String.valueOf(produto.getQuantidadeStock()));

        modifyProductPane.setVisible(true);
        modifyProductPane.setManaged(true);
        modifyProductPane.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), modifyProductPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    public void fecharModifyProductPane() {
        modifyProductPane.setVisible(false);
        modifyProductPane.setManaged(false);
    }

    @FXML
    public void salvarModificacoesProduto() {
        String nome = modifyNomeProdutoField.getText();
        double preco = Double.parseDouble(modifyPrecoProdutoField.getText());
        int quantidade = Integer.parseInt(modifyQuantidadeProdutoField.getText());

        String query = "{CALL modificarProduto(?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, produtoAtual.getIdProduto());
            stmt.setString(2, nome);
            stmt.setDouble(3, preco);
            stmt.setInt(4, quantidade);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Produto modificado com sucesso!");
            alert.showAndWait();
            fecharModifyProductPane();
            carregarProdutos(produtoAtual.getIdCategoria());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao modificar produto: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void mostrarAtualizarStockPane(Produto produto) {
        fecharAddProductPane(); // Fechar outros paineis
        fecharAddCategoryPane();
        fecharModifyProductPane();

        produtoAtual = produto;
        atualizarStockPane.setVisible(true);
        atualizarStockPane.setManaged(true);
        atualizarStockPane.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), atualizarStockPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    public void fecharAtualizarStockPane() {
        atualizarStockPane.setVisible(false);
        atualizarStockPane.setManaged(false);
    }

    @FXML
    public void atualizarStock() {
        int quantidade = Integer.parseInt(atualizarQuantidadeField.getText());

        String query = "{CALL atualizarStock(?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, produtoAtual.getIdProduto());
            stmt.setInt(2, quantidade);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Stock atualizado com sucesso!");
            alert.showAndWait();
            fecharAtualizarStockPane();
            carregarProdutos(produtoAtual.getIdCategoria());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao atualizar stock: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void adicionarCategoria() {
        String nome = nomeCategoriaField.getText();

        String query = "{CALL adicionarCategoria(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setString(1, nome);
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Categoria adicionada com sucesso!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao adicionar categoria: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProduto"));
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

        colAtualizarStock.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Atualizar Stock");

            {
                btn.setOnAction(event -> {
                    Produto produto = getTableView().getItems().get(getIndex());
                    mostrarAtualizarStockPane(produto);
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
            Text nomeText = new Text(categoria.getNome() + "\n");
            nomeText.setStyle("-fx-font-size: 16px;");

            Text idText = new Text("ID: " + categoria.getIdCategoria());
            idText.setStyle("-fx-font-size: 12px;");

            TextFlow textFlow = new TextFlow(nomeText, idText);
            Button btnCategoria = new Button();
            btnCategoria.setGraphic(textFlow);
            btnCategoria.setStyle("-fx-padding: 5; -fx-background-color: #4CAF50; -fx-text-fill: white;");
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
        mostrarModifyProductPane(produto);
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
