package com.grupo6.projetoptda;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MainController {

    @FXML
    public void onGestaoStockClick() {
        System.out.println("Gestão de Stock clicada!");
    }

    @FXML
    public void onGerirMesasClick() {
        System.out.println("Gerir Mesas clicada!");
    }

    @FXML
    public void onFecharCaixaClick() {
        System.out.println("Fechar Caixa clicada!");
    }

    @FXML
    public void onOperacoesCaixaClick() {
        System.out.println("Operações de Caixa clicada!");
    }

    @FXML
    public void onNovaVendaClick() {
        try {
            // Carregar o novo layout (Nova Venda)
            Parent novaVendaRoot = FXMLLoader.load(getClass().getResource("/com/grupo6/projetoptda/NovaVendaPanel.fxml"));

            // Obter a cena atual e substituí-la pelo novo conteúdo
            Scene scene = ((Node) (Stage.getWindows().get(0).getScene().getRoot())).getScene();
            scene.setRoot(novaVendaRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
