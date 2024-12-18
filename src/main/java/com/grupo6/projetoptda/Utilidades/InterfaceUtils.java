package com.grupo6.projetoptda.Utilidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class InterfaceUtils {

    public static void recarregarInterface(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(InterfaceUtils.class.getResource(fxmlPath)));
            Scene scene = Stage.getWindows().getFirst().getScene();
            scene.setRoot(root);
            CarregarCSS.applyCSS(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}