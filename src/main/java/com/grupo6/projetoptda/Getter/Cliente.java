package com.grupo6.projetoptda.Getter;

public class Cliente {
    private final int idCliente;
    private final String nome;

    public Cliente(int idCliente, String nome) {
        this.idCliente = idCliente;
        this.nome = nome;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
