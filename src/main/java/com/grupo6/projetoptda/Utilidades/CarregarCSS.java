package com.grupo6.projetoptda.Utilidades;

import javafx.scene.Scene;
import java.util.Objects;

/**
 * A classe CarregarCSS aplica um ficheiro CSS a um scene do JavaFX.
 */
public class CarregarCSS {

    /**
     * Aplica o ficheiro CSS ao scene fornecida.
     *
     * @param scene o scene à qual o CSS será aplicado
     */
    public static void applyCSS(Scene scene) {
        scene.getStylesheets().add(Objects.requireNonNull(CarregarCSS.class.getResource("/com/grupo6/projetoptda/styles.css")).toExternalForm());
    }
}