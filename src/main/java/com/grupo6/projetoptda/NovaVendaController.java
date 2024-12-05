package com.grupo6.projetoptda;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NovaVendaController {

    @FXML
    public void onCategoriaClick() {
        System.out.println("Categoria clicada!");
    }

    @FXML
    public void onProdutoClick() {
        System.out.println("Produto clicado!");
    }

    @FXML
    public void onPagamentoClick() {
        System.out.println("Pagamento iniciado!");
    }

    @FXML
    public void onConsumidorFinalClick() {
        System.out.println("Consumidor final selecionado!");
    }
    @FXML
    public void onVoltarClick() {
        try {
            // Carregar o layout principal
            Parent mainPanelRoot = FXMLLoader.load(getClass().getResource("/com/grupo6/projetoptda/MainPanel.fxml"));

            // Obter a cena atual e substituir pelo layout principal
            Scene scene = ((Stage) Stage.getWindows().get(0)).getScene();
            scene.setRoot(mainPanelRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
