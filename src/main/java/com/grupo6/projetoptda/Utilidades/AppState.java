// AppState.java
package com.grupo6.projetoptda.Utilidades;

/**
 * A classe AppState gera o estado da aplicação,
 * particularmente relacionado com a sessão do funcionário atual.
 */
public class AppState {

    // Instância singleton de AppState
    private static AppState instance;

    // Variáveis de estado
    private boolean turnoAberto; // Indica se o turno está aberto
    private int funcionarioId; // ID do funcionário atual
    private String nivelAcesso; // Nível de acesso do funcionário atual
    private String nomeFuncionario; // Nome do funcionário atual

    /**
     * Construtor privado para evitar instanciamento.
     */
    private AppState() {}

    /**
     * Metodo para obter a instância de AppState.
     *
     * @return a instância de AppState
     */
    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }

    /**
     * Getter para turnoAberto.
     *
     * @return true se o turno está aberto, false caso contrário
     */
    public boolean isTurnoAberto() {
        return turnoAberto;
    }

    /**
     * Setter para turnoAberto.
     *
     * @param turnoAberto o novo estado de turnoAberto
     */
    public void setTurnoAberto(boolean turnoAberto) {
        this.turnoAberto = turnoAberto;
    }

    /**
     * Getter para funcionarioId.
     *
     * @return o ID do funcionário atual
     */
    public int getFuncionarioId() {
        return funcionarioId;
    }

    /**
     * Setter para funcionarioId.
     *
     * @param funcionarioId o novo ID do funcionário atual
     */
    public void setFuncionarioId(int funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    /**
     * Getter para nivelAcesso.
     *
     * @return o nível de acesso do funcionário atual
     */
    public String getNivelAcesso() {
        return nivelAcesso;
    }

    /**
     * Setter para nivelAcesso.
     *
     * @param nivelAcesso o novo nível de acesso do funcionário atual
     */
    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    /**
     * Getter para nomeFuncionario.
     *
     * @return o nome do funcionário atual
     */
    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    /**
     * Setter para nomeFuncionario.
     *
     * @param nomeFuncionario o novo nome do funcionário atual
     */
    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    /**
     * Metodo para resetar as variaveis de estado para os seus valores padrao.
     */
    public void reset() {
        this.turnoAberto = false;
        this.funcionarioId = 0;
        this.nivelAcesso = null;
        this.nomeFuncionario = null;
    }
}