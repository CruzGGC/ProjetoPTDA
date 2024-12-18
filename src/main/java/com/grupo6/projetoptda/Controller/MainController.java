package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.AppState;
import com.grupo6.projetoptda.Utilidades.DatabaseConnection;
import com.grupo6.projetoptda.Utilidades.DateUtils;
import com.grupo6.projetoptda.Utilidades.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

    private AppState appState = AppState.getInstance();

    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        DateUtils.updateTime(labelHora);
        restoreState();
    }

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

    public void setFuncionarioId(int funcionarioId) {
        appState.setFuncionarioId(funcionarioId);
        String query = "SELECT nivelAcesso, fNome FROM Funcionario WHERE idFuncionario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, funcionarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                appState.setNivelAcesso(rs.getString("nivelAcesso"));
                appState.setNomeFuncionario(rs.getString("fNome")); // Definir o nome do funcionário
                updateButtonVisibility();
                labelUtilizador.setText(appState.getNomeFuncionario()); // Atualizar o Label com o nome do funcionário
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateButtonVisibility() {
        if ("EmpregadoMesa".equals(appState.getNivelAcesso())) {
            btnValoresCaixa.setVisible(false);
            btnGestaoStock.setVisible(false);
            btnGerirCompras.setVisible(false);
        }
    }

    @FXML
    public void onGestaoStockClick() {
        try {
            SceneManager.setScene((Stage) labelHora.getScene().getWindow(), "/com/grupo6/projetoptda/GerirStockPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCaixaBoolClick() {
        if (!appState.isTurnoAberto()) {
            abrirTurno();
        } else {
            fecharTurno();
        }
    }

    private void abrirTurno() {
        String query = "{CALL registrarAberturaTurno(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, appState.getFuncionarioId());
            stmt.execute();
            appState.setTurnoAberto(true);
            btnCaixa.setText("Fechar Caixa");
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

    private void fecharTurno() {
        String query = "{CALL registrarFechamentoTurno()}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.execute();
            appState.setTurnoAberto(false);
            btnCaixa.setText("Abrir Caixa");
            disableButtonsExceptCaixa();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableButtonsExceptCaixa() {
        btnCaixa.setDisable(false);
        for (Button button : new Button[]{btnValoresCaixa, btnGestaoStock, btnNovaVenda, btnGerirPedidos, btnGerirCompras}) {
            button.setDisable(true);
        }
    }

    private void enableAllButtons() {
        for (Button button : new Button[]{btnValoresCaixa, btnGestaoStock, btnNovaVenda, btnGerirPedidos, btnGerirCompras}) {
            button.setDisable(false);
        }
    }

    @FXML
    public void onValoresCaixaClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/ValoresCaixaPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onGerirPedidosClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/GerirPedidosPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onGerirComprasClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/GerirComprasPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSairClick() {
        Platform.exit();
    }

    @FXML
    public void onNovaVendaClick() {
        try {
            SceneManager.setScene((Stage) labelHora.getScene().getWindow(), "/com/grupo6/projetoptda/NovaVendaPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}