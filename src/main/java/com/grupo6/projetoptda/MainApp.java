package com.grupo6.projetoptda;

import com.grupo6.projetoptda.Utilidades.CarregarCSS;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A classe MainApp é a classe principal da aplicação JavaFX.
 */
public class MainApp extends Application {

    /**
     * Inicia a aplicação JavaFX.
     *
     * @param primaryStage o palco principal da aplicação
     * @throws Exception se ocorrer um erro ao carregar o ficheiro FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo6/projetoptda/InicialPanel.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        CarregarCSS.applyCSS(scene);

        primaryStage.setTitle("DIGIGEST Painel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * O metodo main é o ponto de entrada da aplicação.
     *
     * @param args os argumentos da linha de comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}