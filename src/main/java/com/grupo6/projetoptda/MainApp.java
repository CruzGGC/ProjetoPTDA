package com.grupo6.projetoptda;

import com.grupo6.projetoptda.Utilidades.CarregarCSS;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega o arquivo FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo6/projetoptda/InicialPanel.fxml"));
        Parent root = loader.load();

        // Define a cena e aplica o CSS
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        CarregarCSS.applyCSS(scene);

        primaryStage.setTitle("DIGIGEST Painel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setScene(Stage stage, String fxmlFile) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        CarregarCSS.applyCSS(scene);
        stage.setScene(scene);
    }
}