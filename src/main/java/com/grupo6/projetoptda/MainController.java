package com.grupo6.projetoptda;

import javafx.fxml.FXML;

public class MainController {
    @FXML
    public void onNovaVendaClick() {
        System.out.println("Nova Venda clicada!");
    }

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
}
