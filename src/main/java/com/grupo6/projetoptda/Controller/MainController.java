package com.grupo6.projetoptda.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    @FXML
    private Label labelHora;
    @FXML
    private Label labelData;

    @FXML
    public void initialize() {
        // Atualizar a data
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        String formattedDate = currentDate.format(dateFormatter);
        labelData.setText(formattedDate);

        // Atualizar a hora a cada segundo
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalTime currentTime = LocalTime.now();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String formattedTime = currentTime.format(timeFormatter);
                    labelHora.setText(formattedTime);
                });
            }
        }, 0, 1000);
    }

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
