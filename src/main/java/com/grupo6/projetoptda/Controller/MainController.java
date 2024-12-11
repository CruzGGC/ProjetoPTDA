package com.grupo6.projetoptda.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


public class MainController {

    @FXML
    public void onGestaoStockClick() {
        try {
            // Carregar o novo layout (Nova Venda)
            Parent gerirStockRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/grupo6/projetoptda/GerirStockPanel.fxml")));

            // Obter a cena atual e substituí-la pelo novo conteúdo
            Scene scene = Stage.getWindows().getFirst().getScene().getRoot().getScene();
            scene.setRoot(gerirStockRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onFecharCaixaClick() {System.out.println("Fechar Caixa clicada!"); }

    @FXML
    public void onOperacoesCaixaClick() {
        System.out.println("Operações de Caixa clicada!");
    }

    @FXML
    public void onSairClick() {
        Platform.exit();
    }

    @FXML
    public void onNovaVendaClick() {
        try {
            // Carregar o novo layout (Nova Venda)
            Parent novaVendaRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/grupo6/projetoptda/NovaVendaPanel.fxml")));

            // Obter a cena atual e substituí-la pelo novo conteúdo
            Scene scene = Stage.getWindows().getFirst().getScene().getRoot().getScene();
            scene.setRoot(novaVendaRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
