package com.grupo6.projetoptda.Utilidades;

import javafx.scene.Scene;
import java.util.Objects;

public class CarregarCSS {
    public static void applyCSS(Scene scene) {
        scene.getStylesheets().add(Objects.requireNonNull(CarregarCSS.class.getResource("/com/grupo6/projetoptda/styles.css")).toExternalForm());
    }
}