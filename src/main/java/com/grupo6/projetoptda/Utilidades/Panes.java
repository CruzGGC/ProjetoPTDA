package com.grupo6.projetoptda.Utilidades;

import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * A classe Panes fornece métodos utilitários para manipular StackPanes no JavaFX.
 */
public class Panes {

    /**
     * Exibe um StackPane com uma transição de fade-in.
     *
     * @param pane o StackPane a ser exibido
     */
    public static void showPane(StackPane pane) {
        pane.setVisible(true);
        pane.setManaged(true);
        pane.toFront();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), pane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
}