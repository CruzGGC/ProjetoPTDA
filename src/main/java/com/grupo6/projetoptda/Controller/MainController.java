package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.DateUtils;
import com.grupo6.projetoptda.Utilidades.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label labelHora;
    @FXML
    private Label labelData;

    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        DateUtils.updateTime(labelHora);
    }

    @FXML
    public void onGestaoStockClick() {
        try {
            // Carregar o novo layout (Gerir Stock)
            SceneManager.setScene((Stage) labelHora.getScene().getWindow(), "/com/grupo6/projetoptda/GerirStockPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onFecharCaixaClick() {
        System.out.println("Fechar Caixa clicada!");
    }

    @FXML
    public void onValoresCaixaClick() {
        try {
            // Use labelData to get the current Stage
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/ValoresCaixaPanel.fxml");
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
            // Carregar o novo layout (Nova Venda)
            SceneManager.setScene((Stage) labelHora.getScene().getWindow(), "/com/grupo6/projetoptda/NovaVendaPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}