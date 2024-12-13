package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.DateUtils;
import com.grupo6.projetoptda.Utilidades.SceneManager;
import javafx.fxml.FXML;
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

    @FXML
    public void initialize() {
        DateUtils.updateDate(labelData);
        DateUtils.updateTime(labelHora);
    }

    @FXML
    private void onMainPanel() {
        String login = utilizadorField.getText();
        String password = passwordField.getText();

        if (autenticarUsuario(login, password)) {
            try {
                SceneManager.setScene((Stage) loginButton.getScene().getWindow(), "/com/grupo6/projetoptda/MainPanel.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Autenticação falhou.");
        }
    }
    private boolean autenticarUsuario(String nome, String passwordInserida) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "CALL autenticar(?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, nome);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String hashSenha = rs.getString("fPassword");
                        return BCrypt.checkpw(passwordInserida, hashSenha);
                    } else {
                        System.out.println("Usuário não encontrado.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}