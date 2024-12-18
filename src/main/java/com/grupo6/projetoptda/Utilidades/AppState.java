// AppState.java
package com.grupo6.projetoptda.Utilidades;

public class AppState {
    private static AppState instance;
    private boolean turnoAberto;
    private int funcionarioId;
    private String nivelAcesso;
    private String nomeFuncionario; // Novo campo

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    public boolean isTurnoAberto() {
        return turnoAberto;
    }

    public void setTurnoAberto(boolean turnoAberto) {
        this.turnoAberto = turnoAberto;
    }

    public int getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(int funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }
}