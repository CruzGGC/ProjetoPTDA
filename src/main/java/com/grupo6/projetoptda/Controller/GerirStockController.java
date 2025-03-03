package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Getter.Categoria;
import com.grupo6.projetoptda.Getter.Produto;
import com.grupo6.projetoptda.Utilidades.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.grupo6.projetoptda.Utilidades.InterfaceUtils.recarregarInterface;

/**
 * A classe GerirStockController gere a interface de gestão de stock no JavaFX.
 */
public class GerirStockController {

    /**
     * Volta para o painel principal.
     */
    @FXML
    public void onVoltarClick() {
        try {
            SceneManager.setScene((Stage) categoriasPane.getScene().getWindow(), "/com/grupo6/projetoptda/MainPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private HBox categoriasPane;

    @FXML
    private Label labelData;

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
    private Label labelUtilizador;

    private final AppState appState = AppState.getInstance();

    /**
     * Mostra o painel para adicionar um novo produto.
     */
    @FXML
    public void mostrarAddProductPane() {
        fecharAddCategoryPane(); // Fechar o painel de adicionar categoria
        Panes.showPane(addProductPane);
    }

    /**
     * Esconde o painel para adicionar um novo produto.
     */
    @FXML
    public void fecharAddProductPane() {
        addProductPane.setVisible(false);
        addProductPane.setManaged(false);
    }

    /**
     * Adiciona um novo produto à base de dados.
     */
    @FXML
    public void adicionarProduto() {
        String nome = nomeProdutoField.getText();
        int idCategoria = Integer.parseInt(idCategoriaField.getText());
        double preco = Double.parseDouble(precoProdutoField.getText());
        int quantidade = Integer.parseInt(quantidadeProdutoField.getText());

        DatabaseUtils.adicionarProduto(nome, idCategoria, preco, quantidade);
        recarregarInterface("/com/grupo6/projetoptda/GerirStockPanel.fxml");
    }

    /**
     * Mostra o painel para adicionar uma nova categoria.
     */
    @FXML
    public void mostrarAddCategoryPane() {
        fecharAddProductPane(); // Fechar o painel de adicionar produto
        Panes.showPane(addCategoryPane);
    }

    /**
     * Esconde o painel para adicionar uma nova categoria.
     */
    @FXML
    public void fecharAddCategoryPane() {
        addCategoryPane.setVisible(false);
        addCategoryPane.setManaged(false);
    }

    /**
     * Mostra o painel para modificar um produto existente.
     *
     * @param produto o produto a ser modificado
     */
    @FXML
    public void mostrarModifyProductPane(Produto produto) {
        fecharAddProductPane(); // Fechar outros paineis
        fecharAddCategoryPane();

        produtoAtual = produto;
        modifyNomeProdutoField.setText(produto.getNome());
        modifyPrecoProdutoField.setText(String.valueOf(produto.getPreco()));
        modifyQuantidadeProdutoField.setText(String.valueOf(produto.getQuantidadeStock()));

        Panes.showPane(modifyProductPane);
    }

    /**
     * Esconde o painel para modificar um produto existente.
     */
    @FXML
    public void fecharModifyProductPane() {
        modifyProductPane.setVisible(false);
        modifyProductPane.setManaged(false);
    }

    /**
     * Salva as modificações feitas em um produto existente.
     */
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
            recarregarInterface("/com/grupo6/projetoptda/GerirStockPanel.fxml");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao modificar produto: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Mostra o painel para atualizar o stock de um produto.
     *
     * @param produto o produto cujo stock será atualizado
     */
    @FXML
    public void mostrarAtualizarStockPane(Produto produto) {
        fecharAddProductPane(); // Fechar outros paineis
        fecharAddCategoryPane();
        fecharModifyProductPane();

        produtoAtual = produto;
        Panes.showPane(atualizarStockPane);
    }

    /**
     * Esconde o painel para atualizar o stock de um produto.
     */
    @FXML
    public void fecharAtualizarStockPane() {
        atualizarStockPane.setVisible(false);
        atualizarStockPane.setManaged(false);
    }

    /**
     * Atualiza o stock de um produto na base de dados.
     */
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
            recarregarInterface("/com/grupo6/projetoptda/GerirStockPanel.fxml");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao atualizar stock: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Adiciona uma nova categoria à base de dados.
     */
    @FXML
    private void adicionarCategoria() {
        DatabaseUtils.adicionarCategoria(nomeCategoriaField.getText());
        fecharAddCategoryPane();
    }

    /**
     * Inicializa o controlador, configurando as colunas da tabela e carregando categorias.
     */
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProduto"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidadeStock"));

        addButtonToTable();
        carregarCategorias();
        DateUtils.updateDate(labelData);
        labelUtilizador.setText(appState.getNomeFuncionario());
    }

    /**
     * Adiciona botões de ação à tabela de produtos.
     */
    private void addButtonToTable() {
        colModificar.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();
            {
                btn.setText("✏");
                btn.getStyleClass().add("btn-tables");
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
            private final Button btn = new Button();
            {
                btn.setText("🗑");
                btn.getStyleClass().add("btn-tables");
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

    /**
     * Carrega as categorias a partir da base de dados e as exibe no painel.
     */
    private void carregarCategorias() {
        List<Categoria> categorias = buscarCategorias();

        for (Categoria categoria : categorias) {
            Text nomeText = new Text(categoria.getNome() + "\n");
            nomeText.getStyleClass().add("text-nome");

            Text idText = new Text("ID: " + categoria.getIdCategoria());
            idText.getStyleClass().add("text-id");

            TextFlow textFlow = new TextFlow(nomeText, idText);
            Button btnCategoria = new Button();
            btnCategoria.setGraphic(textFlow);
            btnCategoria.getStyleClass().add("btn-categoria");
            btnCategoria.setOnAction(event -> carregarProdutos(categoria.getIdCategoria()));
            categoriasPane.getChildren().add(btnCategoria);
        }
    }

    /**
     * Procura as categorias na base de dados.
     *
     * @return uma lista de categorias
     */
    private List<Categoria> buscarCategorias() {
        return DatabaseUtils.fetchCategories();
    }

    /**
     * Carrega os produtos de uma categoria específica e os exibe na tabela.
     *
     * @param idCategoria o ID da categoria
     */
    private void carregarProdutos(int idCategoria) {
        List<Produto> produtos = buscarProdutos(idCategoria);
        ObservableList<Produto> produtosObservableList = FXCollections.observableArrayList(produtos);
        produtosTable.setItems(produtosObservableList);
    }

    /**
     * Procura os produtos de uma categoria específica na base de dados.
     *
     * @param idCategoria o ID da categoria
     * @return uma lista de produtos
     */
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

    /**
     * Mostra o painel para modificar um produto.
     *
     * @param produto o produto a ser modificado
     */
    private void modificarProduto(Produto produto) {
        mostrarModifyProductPane(produto);
    }

    /**
     * Remove um produto da base de dados.
     *
     * @param produto o produto a ser removido
     */
    private void removerProduto(Produto produto) {
        // Chama o procedimento armazenado para remover o produto
        String query = "{CALL removerProduto(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, produto.getIdProduto());
            stmt.execute();
            recarregarInterface("/com/grupo6/projetoptda/GerirStockPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}