package com.grupo6.projetoptda.Utilidades;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A classe DateUtils fornece métodos utilitários para atualizar a data e a hora em etiquetas (labels) do JavaFX.
 */
public class DateUtils {

    /**
     * Atualiza a etiqueta com a data atual formatada.
     *
     * @param labelData a etiqueta que exibirá a data formatada
     */
    public static void updateDate(Label labelData) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.of("pt", "PT"));
        String formattedDate = currentDate.format(dateFormatter);
        labelData.setText(formattedDate);
    }

    /**
     * Atualiza a etiqueta com a hora atual formatada a cada segundo.
     *
     * @param labelHora a etiqueta que exibirá a hora formatada
     */
    public static void updateTime(Label labelHora) {
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
}