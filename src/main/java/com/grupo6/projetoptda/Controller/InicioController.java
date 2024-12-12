package com.grupo6.projetoptda.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class InicioController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private void onMainPanel(ActionEvent event) {
        // Handle login button click
        String login = loginField.getText();
        String password = passwordField.getText();

        // Implement your login logic here
        System.out.println("Login: " + login);
        System.out.println("Password: " + password);
    }
}