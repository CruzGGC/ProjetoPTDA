package com.grupo6.projetoptda.Getter;

import com.grupo6.projetoptda.Utilidades.DatabaseUtils;

public class Produto {
    private final int idProduto;
    private int idCategoria; // Referência à categoria do produto
    private String nome; // Nome do produto
    private double preco; // Preço do produto
    private int quantidadeStock; // Quantidade em estoque

    public Produto(int idProduto, int idCategoria, String nome, double preco, int quantidade) {
        this.idProduto = idProduto;
        this.idCategoria = idCategoria;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeStock = quantidade;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeStock() {
        return quantidadeStock;
    }

    public Categoria getCategoria() {
        return DatabaseUtils.getCategoriaById(idCategoria);
    }

    public void setCategoria(Categoria categoria) {
        this.idCategoria = categoria.getIdCategoria();
    }

    public int getQuantidade() {
        return quantidadeStock;
    }

    public void setQuantidade(int quantidade) {
        this.quantidadeStock = quantidade;
    }
}