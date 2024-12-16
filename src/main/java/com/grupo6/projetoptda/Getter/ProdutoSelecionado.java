package com.grupo6.projetoptda.Getter;

import javafx.beans.property.*;

public class ProdutoSelecionado {
    private final IntegerProperty idProduto;
    private final StringProperty descricao;
    private final DoubleProperty preco;
    private final IntegerProperty quantidadeStock;
    private final DoubleProperty total;

    public ProdutoSelecionado(int idProduto, String descricao, double preco, int quantidadeStock, double total) {
        this.idProduto = new SimpleIntegerProperty(idProduto);
        this.descricao = new SimpleStringProperty(descricao);
        this.preco = new SimpleDoubleProperty(preco);
        this.quantidadeStock = new SimpleIntegerProperty(quantidadeStock);
        this.total = new SimpleDoubleProperty(total);
    }

    public int getIdProduto() { return idProduto.get(); }

    public double getPreco() { return preco.get(); }
    public int getQuantidadeStock() { return quantidadeStock.get(); }
    public double getTotal() { return total.get(); }
    public void setQuantidadeStock(int quantidadeStock) { this.quantidadeStock.set(quantidadeStock); }
    public void setTotal(double total) { this.total.set(total); }
    public StringProperty descricaoProperty() { return descricao; }
    public DoubleProperty precoProperty() { return preco; }
    public IntegerProperty quantidadeStockProperty() { return quantidadeStock; }
    public DoubleProperty totalProperty() { return total; }
}
