package com.grupo6.projetoptda.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class ValoresCaixaController {

    @FXML
    private FlowPane faturasPane;

    @FXML
    private Button btnTurnoAtual, btnHoje, btnUltimaSemana, btnUltimoMes, btnEsteAno, btnAnoAnterior, btnSelecionarData;

    @FXML
    private StackPane selecionarDataPane;

    @FXML
    private DatePicker dataInicioPicker, dataFimPicker;

    @FXML
    public void initialize() {
        btnTurnoAtual.setOnAction(event -> carregarFaturas(LocalDate.now(), LocalDate.now()));
        btnHoje.setOnAction(event -> carregarFaturas(LocalDate.now(), LocalDate.now()));
        btnUltimaSemana.setOnAction(event -> carregarFaturas(LocalDate.now().minusWeeks(1), LocalDate.now()));
        btnUltimoMes.setOnAction(event -> carregarFaturas(LocalDate.now().minusMonths(1), LocalDate.now()));
        btnEsteAno.setOnAction(event -> carregarFaturas(LocalDate.now().withDayOfYear(1), LocalDate.now()));
        btnAnoAnterior.setOnAction(event -> carregarFaturas(LocalDate.now().minusYears(1).withDayOfYear(1), LocalDate.now().minusYears(1).withDayOfYear(LocalDate.now().minusYears(1).lengthOfYear())));
        btnSelecionarData.setOnAction(event -> mostrarSelecionarDataPane());
    }

    private void carregarFaturas(LocalDate dataInicio, LocalDate dataFim) {
        faturasPane.getChildren().clear();
        String query = "{CALL visualizarRelatorio(?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement stmt = connection.prepareCall(query)) {

            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idFatura = rs.getInt("idFatura");
                String faturaInfo = String.format("Fatura: %d\nCliente: %s\n%s | %s | %.2f",
                        idFatura,
                        rs.getString("nomeCliente"),
                        rs.getDate("data").toString(),
                        rs.getTime("hora").toString(),
                        rs.getDouble("valorTotal"));

                Button faturaButton = new Button(faturaInfo);
                faturaButton.getStyleClass().add("btn-categoria");
                faturaButton.setOnAction(event -> abrirDetalhesFatura(idFatura));
                faturasPane.getChildren().add(faturaButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void abrirDetalhesFatura(int idFatura) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo6/projetoptda/DetalhesFatura.fxml"));
            Parent root = loader.load();

            DetalhesFaturaController controller = loader.getController();
            controller.carregarDetalhesFatura(idFatura);

            Stage stage = new Stage();
            stage.setTitle("Detalhes da Fatura");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarSelecionarDataPane() {
        selecionarDataPane.setVisible(true);
        selecionarDataPane.setManaged(true);
    }

    @FXML
    private void fecharSelecionarDataPane() {
        selecionarDataPane.setVisible(false);
        selecionarDataPane.setManaged(false);
    }

    @FXML
    private void pesquisarPorData() {
        LocalDate dataInicio = dataInicioPicker.getValue();
        LocalDate dataFim = dataFimPicker.getValue();
        if (dataInicio != null && dataFim != null) {
            carregarFaturas(dataInicio, dataFim);
            fecharSelecionarDataPane();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Seleção de Data");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecione ambas as datas: Data Início e Data Fim.");
            alert.showAndWait();
        }
    }
}