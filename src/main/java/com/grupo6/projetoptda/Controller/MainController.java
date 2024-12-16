package com.grupo6.projetoptda.Controller;

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

public class MainController {

    @FXML
    private Label labelHora;
    @FXML
    private Label labelData;
    @FXML
    private Button btnCaixa;

    private boolean turnoAberto = false;
    private int funcionarioId;

    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        DateUtils.updateTime(labelHora);
    }

    public void setFuncionarioId(int funcionarioId) {
        this.funcionarioId = funcionarioId;
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
        if (!turnoAberto) {
            abrirTurno();
        } else {
            fecharTurno();
        }
    }

    private void abrirTurno() {
        String query = "{CALL registrarAberturaTurno(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, funcionarioId);
            stmt.execute();
            turnoAberto = true;
            btnCaixa.setText("Fechar Caixa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fecharTurno() {
        String query = "{CALL registrarFechamentoTurno()}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            stmt.execute();
            turnoAberto = false;
            btnCaixa.setText("Abrir Caixa");
        } catch (Exception e) {
            e.printStackTrace();
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