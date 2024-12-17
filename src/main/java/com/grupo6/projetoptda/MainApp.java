package com.grupo6.projetoptda;

import com.grupo6.projetoptda.Utilidades.CarregarCSS;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo6/projetoptda/MainPanel.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        CarregarCSS.applyCSS(scene);

        primaryStage.setTitle("DIGIGEST Painel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}