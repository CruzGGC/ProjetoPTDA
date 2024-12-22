package com.grupo6.projetoptda.Utilidades;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * A classe SceneManager gere a troca de scenes no JavaFX.
 */
public class SceneManager {
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    /**
     * Define a cena do palco (stage) com base no caminho do ficheiro FXML fornecido.
     *
     * @param stage o palco onde o scene será definida
     * @param fxmlPath o caminho do ficheiro FXML a ser carregado
     * @throws Exception se ocorrer um erro ao carregar o ficheiro FXML
     */
    public static void setScene(Stage stage, String fxmlPath) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxmlPath), "FXML file not found: " + fxmlPath));
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        CarregarCSS.applyCSS(scene);
        stage.setScene(scene);
        stage.show();
    }
}