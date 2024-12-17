

package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Getter.Pedido;
import com.grupo6.projetoptda.Getter.ProdutoSelecionado;
import com.grupo6.projetoptda.Utilidades.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ModificarVendaController {

    @FXML
    private Label labelData;

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

    private final ObservableList<ProdutoSelecionado> produtosSelecionados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colunaDescricao.setCellValueFactory(data -> data.getValue().descricaoProperty());
        colunaPVP.setCellValueFactory(data -> data.getValue().precoProperty().asObject());
        colunaQuantidade.setCellValueFactory(data -> data.getValue().quantidadeStockProperty().asObject());
        colunaTotal.setCellValueFactory(data -> data.getValue().totalProperty().asObject());

        tabelaProdutos.setItems(produtosSelecionados);
    }

    public void setPedido(Pedido pedido) {
        carregarProdutosDoPedido(pedido);
    }

    private void carregarProdutosDoPedido(Pedido pedido) {
        List<ProdutoSelecionado> produtos = buscarProdutosDoPedido(pedido.idPedido());
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

    @FXML
    public void onVoltarClick() {
        Stage stage = (Stage) labelData.getScene().getWindow();
        stage.close();
    }
}