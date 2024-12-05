package com.grupo6.projetoptda;

public class Produto {
    private int idProduto;
    private String nome;
    private double preco;
    private int quantidadeStock;

    public Produto(int idProduto, String nome, double preco, int quantidadeStock) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeStock = quantidadeStock;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeStock() {
        return quantidadeStock;
    }
}
