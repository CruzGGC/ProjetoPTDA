package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.CarregarCSS;
import com.grupo6.projetoptda.Utilidades.DatabaseConnection;
import com.grupo6.projetoptda.Utilidades.DateUtils;
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
                            System.out.println("Senha incorreta.");
                            return -1;
                        }
                    } else {
                        System.out.println("Usuário não encontrado.");
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