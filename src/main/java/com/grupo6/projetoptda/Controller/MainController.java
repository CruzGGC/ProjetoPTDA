package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.AppState;
import com.grupo6.projetoptda.Utilidades.DatabaseConnection;
import com.grupo6.projetoptda.Utilidades.DateUtils;
import com.grupo6.projetoptda.Utilidades.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * A classe MainController gere a interface principal da aplicação JavaFX.
 */
public class MainController {

    @FXML
    private Label labelHora;
    @FXML
    private Label labelData;
    @FXML
    private Label labelUtilizador;
    @FXML
    private Button btnCaixa;
    @FXML
    private Button btnValoresCaixa;
    @FXML
    private Button btnGestaoStock;
    @FXML
    private Button btnNovaVenda;
    @FXML
    private Button btnGerirPedidos;
    @FXML
    private Button btnGerirCompras;

    private final AppState appState = AppState.getInstance();

    /**
     * Inicializa o controlador, atualizando a data e hora e restaurando o estado da interface.
     */
    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        DateUtils.updateTime(labelHora);
        restoreState();
    }

    /**
     * Restaura o estado da interface com base no estado da aplicação.
     */
    private void restoreState() {
        if (appState.isTurnoAberto()) {
            btnCaixa.setText("Fechar Caixa");
            if ("EmpregadoMesa".equals(appState.getNivelAcesso())) {
                btnNovaVenda.setVisible(true);
                btnNovaVenda.setDisable(false);
                btnGerirPedidos.setVisible(true);
                btnGerirPedidos.setDisable(false);
                btnValoresCaixa.setVisible(false);
                btnGestaoStock.setVisible(false);
                btnGerirCompras.setVisible(false);
            } else {
                enableAllButtons();
            }
        } else {
            btnCaixa.setText("Abrir Caixa");
            disableButtonsExceptCaixa();
            if ("EmpregadoMesa".equals(appState.getNivelAcesso())) {
                btnNovaVenda.setVisible(false);
                btnGerirPedidos.setVisible(false);
                btnValoresCaixa.setVisible(false);
                btnGestaoStock.setVisible(false);
                btnGerirCompras.setVisible(false);
            }
        }
        labelUtilizador.setText(appState.getNomeFuncionario()); // Atualizar o Label com o nome do funcionário
    }

    /**
     * Define o ID do funcionário e atualiza o estado da aplicação e a interface.
     *
     * @param funcionarioId o ID do funcionário
     */
    public void setFuncionarioId(int funcionarioId) {
        appState.setFuncionarioId(funcionarioId);
        String query = "SELECT nivelAcesso, fNome, servico FROM Funcionario WHERE idFuncionario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, funcionarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                appState.setNivelAcesso(rs.getString("nivelAcesso"));
                appState.setNomeFuncionario(rs.getString("fNome"));
                appState.setTurnoAberto(rs.getBoolean("servico"));
                updateButtonVisibility();
                labelUtilizador.setText(appState.getNomeFuncionario());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Atualizar o estado da interface
        restoreState();
    }

    /**
     * Atualiza a visibilidade dos botões com base no nível de acesso do funcionário.
     */
    private void updateButtonVisibility() {
        if ("EmpregadoMesa".equals(appState.getNivelAcesso())) {
            btnValoresCaixa.setVisible(false);
            btnGestaoStock.setVisible(false);
            btnGerirCompras.setVisible(false);
        }
    }

    /**
     * Navega para a interface de gestão de stock.
     */
    @FXML
    public void onGestaoStockClick() {
        try {
            SceneManager.setScene((Stage) labelHora.getScene().getWindow(), "/com/grupo6/projetoptda/GerirStockPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navega para a interface de nova venda.
     */
    @FXML
    public void onNovaVendaClick() {
        try {
            SceneManager.setScene((Stage) labelHora.getScene().getWindow(), "/com/grupo6/projetoptda/NovaVendaPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Alterna entre abrir e fechar o turno.
     */
    @FXML
    public void onCaixaBoolClick() {
        if (!appState.isTurnoAberto()) {
            abrirTurno();
        } else {
            fecharTurno();
        }
    }

    /**
     * Abre o turno e atualiza a interface.
     */
    private void abrirTurno() {
        String query = "{CALL registrarAberturaTurno(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, appState.getFuncionarioId());
            stmt.execute();
            appState.setTurnoAberto(true);
            btnCaixa.setText("Fechar Caixa");
            btnCaixa.getStyleClass().remove("btn-fecharcaixa");
            btnCaixa.getStyleClass().add("btn-fecharcaixa");
            if ("EmpregadoMesa".equals(appState.getNivelAcesso())) {
                btnNovaVenda.setVisible(true);
                btnNovaVenda.setDisable(false);
                btnGerirPedidos.setVisible(true);
                btnGerirPedidos.setDisable(false);
            } else {
                enableAllButtons();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fecha o turno e atualiza a interface.
     */
    private void fecharTurno() {
        String query = "{CALL registrarFechamentoTurno(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, appState.getFuncionarioId());
            stmt.execute();
            appState.setTurnoAberto(false);
            btnCaixa.setText("Abrir Caixa");
            btnCaixa.getStyleClass().remove("btn-fecharcaixa");
            btnCaixa.getStyleClass().add("btn-abrircaixa");
            disableButtonsExceptCaixa();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Desativa todos os botões exceto o botão de caixa.
     */
    private void disableButtonsExceptCaixa() {
        btnCaixa.setDisable(false);
        for (Button button : new Button[]{btnValoresCaixa, btnGestaoStock, btnNovaVenda, btnGerirPedidos, btnGerirCompras}) {
            button.setDisable(true);
        }
    }

    /**
     * Ativa todos os botões.
     */
    private void enableAllButtons() {
        for (Button button : new Button[]{btnValoresCaixa, btnGestaoStock, btnNovaVenda, btnGerirPedidos, btnGerirCompras}) {
            button.setDisable(false);
        }
    }

    /**
     * Navega para a interface de valores de caixa.
     */
    @FXML
    public void onValoresCaixaClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/ValoresCaixaPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navega para a interface de gestão de pedidos.
     */
    @FXML
    public void onGerirPedidosClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/GerirPedidosPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navega para a interface de gestão de compras.
     */
    @FXML
    public void onGerirComprasClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/GerirComprasPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sai da aplicação e navega para a interface inicial.
     */
    @FXML
    public void onSairClick() {
        try {
            appState.reset(); // Reset the AppState
            SceneManager.setScene((Stage) labelHora.getScene().getWindow(), "/com/grupo6/projetoptda/InicialPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}