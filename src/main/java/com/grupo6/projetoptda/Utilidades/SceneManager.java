package com.grupo6.projetoptda.Utilidades;

import com.grupo6.projetoptda.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    public static void setScene(Stage stage, String fxmlFile) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        CarregarCSS.applyCSS(scene);
        stage.setScene(scene);
    }
}