package com.grupo6.projetoptda.Getter;

public class Pedido {
    private int idPedido;
    private String status;

    public Pedido(int idPedido, String status) {
        this.idPedido = idPedido;
        this.status = status;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public String getStatus() {
        return status;
    }
}