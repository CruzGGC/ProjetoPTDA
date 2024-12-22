package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.CarregarCSS;
import com.grupo6.projetoptda.Utilidades.DatabaseConnection;
import com.grupo6.projetoptda.Utilidades.DateUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A classe InicioController gere a interface de login da aplicação JavaFX.
 */
public class InicioController {

    @FXML
    private TextField utilizadorField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label labelHora;
    @FXML
    private Label labelData;

    /**
     * Fecha a aplicação.
     */
    @FXML
    private void onFecharClick() {
        Platform.exit();
    }

    /**
     * Inicializa o controlador, atualizando a data e hora.
     */
    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        DateUtils.updateTime(labelHora);
    }

    /**
     * Navega para o painel principal após autenticação bem-sucedida.
     */
    @FXML
    private void onMainPanel() {
        String login = utilizadorField.getText();
        String password = passwordField.getText();

        int funcionarioId = autenticarUsuario(login, password);
        if (funcionarioId != -1) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo6/projetoptda/MainPanel.fxml"));
                Parent root = loader.load();
                MainController mainController = loader.getController();
                mainController.setFuncionarioId(funcionarioId);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root, 800, 600);
                CarregarCSS.applyCSS(scene);
                stage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Autenticação falhou.");
        }
    }

    /**
     * Autentica o utilizador com base no nome e password fornecidos.
     *
     * @param nome o nome do utilizador
     * @param passwordInserida a password inserida pelo utilizador
     * @return o ID do funcionário se a autenticação for bem-sucedida, -1 caso contrário
     */
    private int autenticarUsuario(String nome, String passwordInserida) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "CALL autenticar(?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, nome);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String hashSenha = rs.getString("fPassword");
                        if (BCrypt.checkpw(passwordInserida, hashSenha)) {
                            return rs.getInt("idFuncionario");
                        } else {
                            System.out.println("Password incorreta.");
                            return -1;
                        }
                    } else {
                        System.out.println("Utilizador não encontrado.");
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}