package com.grupo6.projetoptda.Getter;

public record Cliente(int idCliente, String nome) {

    @Override
    public String toString() {
        return nome;
    }
}
