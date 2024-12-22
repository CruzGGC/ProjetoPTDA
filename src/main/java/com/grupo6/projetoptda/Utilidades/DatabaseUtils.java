package com.grupo6.projetoptda.Utilidades;

import com.grupo6.projetoptda.Getter.Categoria;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.grupo6.projetoptda.Getter.Produto;
import javafx.scene.control.Alert;

/**
 * A classe DatabaseUtils fornece métodos utilitários para interagir com a base de dados.
 */
public class DatabaseUtils {

    /**
     * Procura todas as categorias da base de dados.
     *
     * @return uma lista de objetos Categoria
     */
    public static List<Categoria> fetchCategories() {
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

    /**
     * Procura uma categoria pelo seu ID.
     *
     * @param idCategoria o ID da categoria
     * @return um objeto Categoria ou null se não for encontrado
     */
    public static Categoria getCategoriaById(int idCategoria) {
        String query = "SELECT * FROM Categoria WHERE idCategoria = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCategoria);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Categoria(
                            resultSet.getInt("idCategoria"),
                            resultSet.getString("nome")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adiciona uma nova categoria à base de dados.
     *
     * @param nome o nome da nova categoria
     */
    public static void adicionarCategoria(String nome) {
        if (nome.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("O nome da categoria não pode estar vazio.");
            alert.showAndWait();
            return;
        }

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

    /**
     * Adiciona um novo produto à base de dados.
     *
     * @param nome o nome do produto
     * @param idCategoria o ID da categoria do produto
     * @param preco o preço do produto
     * @param quantidade a quantidade em stock do produto
     */
    public static void adicionarProduto(String nome, int idCategoria, double preco, int quantidade) {
        if (nome.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("O nome do produto não pode estar vazio.");
            alert.showAndWait();
            return;
        }

        if (preco < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("O preço não pode ser negativo.");
            alert.showAndWait();
            return;
        }

        if (quantidade < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("A quantidade não pode ser negativa.");
            alert.showAndWait();
            return;
        }

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

    /**
     * Procura todos os produtos de uma categoria específica.
     *
     * @param idCategoria o ID da categoria
     * @return uma lista de objetos Produto
     */
    public static List<Produto> buscarProdutosPorCategoria(int idCategoria) {
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
}