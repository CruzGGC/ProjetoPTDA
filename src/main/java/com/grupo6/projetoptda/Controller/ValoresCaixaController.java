package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

/**
 * A classe ValoresCaixaController gere a interface de visualização dos valores de caixa no JavaFX.
 */
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
    private Label labelData;

    @FXML
    private Label labelUtilizador;

    private final AppState appState = AppState.getInstance();

    /**
     * Inicializa o controlador, configurando os botões e atualizando a data e o utilizador.
     */
    @FXML
    public void initialize() {
        btnTurnoAtual.setOnAction(event -> {
            int idTurno = getTurnoAtualId();
            if (idTurno != -1) {
                carregarFaturas(idTurno);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Não foi possível encontrar o turno atual.");
                alert.showAndWait();
            }
        });
        btnHoje.setOnAction(event -> carregarFaturas(LocalDate.now(), LocalDate.now()));
        btnUltimaSemana.setOnAction(event -> carregarFaturas(LocalDate.now().minusWeeks(1), LocalDate.now()));
        btnUltimoMes.setOnAction(event -> carregarFaturas(LocalDate.now().minusMonths(1), LocalDate.now()));
        btnEsteAno.setOnAction(event -> carregarFaturas(LocalDate.now().withDayOfYear(1), LocalDate.now()));
        btnAnoAnterior.setOnAction(event -> carregarFaturas(LocalDate.now().minusYears(1).withDayOfYear(1), LocalDate.now().minusYears(1).withDayOfYear(LocalDate.now().minusYears(1).lengthOfYear())));
        btnSelecionarData.setOnAction(event -> mostrarSelecionarDataPane());
        DateUtils.updateDate(labelData);
        labelUtilizador.setText(appState.getNomeFuncionario());
    }

    /**
     * Obtém o ID do turno atual do funcionário.
     *
     * @return o ID do turno atual, ou -1 se não for encontrado
     */
    private int getTurnoAtualId() {
        int idTurno = -1;
        String query = "SELECT idTurno FROM Turno WHERE idFuncionario = ? AND dataHoraFechamento IS NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appState.getFuncionarioId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idTurno = rs.getInt("idTurno");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idTurno;
    }

    /**
     * Carrega as faturas do turno especificado e exibe-as no painel.
     *
     * @param idTurno o ID do turno
     */
    private void carregarFaturas(int idTurno) {
        faturasPane.getChildren().clear();
        String queryFaturas = "{CALL visualizarRelatorioPorTurno(?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement stmtFaturas = connection.prepareCall(queryFaturas)) {

            stmtFaturas.setInt(1, idTurno);
            ResultSet rsFaturas = stmtFaturas.executeQuery();
            while (rsFaturas.next()) {
                int idFatura = rsFaturas.getInt("idFatura");
                String faturaInfo = String.format("Fatura: %d | Funcionario: %s\nCliente: %s\n%s | %s | %.2f",
                        idFatura,
                        rsFaturas.getString("nomeFuncionario"),
                        rsFaturas.getString("nomeCliente"),
                        rsFaturas.getDate("data").toString(),
                        rsFaturas.getTime("hora").toString(),
                        rsFaturas.getDouble("valorTotal"));

                Button faturaButton = new Button(faturaInfo);
                faturaButton.getStyleClass().add("btn-categoria");
                faturaButton.setOnAction(event -> abrirDetalhesFatura(idFatura));
                faturasPane.getChildren().add(faturaButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega as faturas no intervalo de datas especificado e exibe-as no painel.
     *
     * @param dataInicio a data de início do intervalo
     * @param dataFim a data de fim do intervalo
     */
    private void carregarFaturas(LocalDate dataInicio, LocalDate dataFim) {
        faturasPane.getChildren().clear();
        String queryFaturas = "{CALL visualizarRelatorio(?, ?)}";
        String queryFaturasCompra = "{CALL visualizarRelatorioCompra(?, ?)}";

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement stmtFaturas = connection.prepareCall(queryFaturas);
             CallableStatement stmtFaturasCompra = connection.prepareCall(queryFaturasCompra)) {

            stmtFaturas.setDate(1, Date.valueOf(dataInicio));
            stmtFaturas.setDate(2, Date.valueOf(dataFim));
            ResultSet rsFaturas = stmtFaturas.executeQuery();
            while (rsFaturas.next()) {
                int idFatura = rsFaturas.getInt("idFatura");
                String faturaInfo = String.format("Fatura: %d | Funcionario: %s\nCliente: %s\n%s | %s | %.2f",
                        idFatura,
                        rsFaturas.getString("nomeFuncionario"),
                        rsFaturas.getString("nomeCliente"),
                        rsFaturas.getDate("data").toString(),
                        rsFaturas.getTime("hora").toString(),
                        rsFaturas.getDouble("valorTotal"));

                Button faturaButton = new Button(faturaInfo);
                faturaButton.getStyleClass().add("btn-categoria");
                faturaButton.setOnAction(event -> abrirDetalhesFatura(idFatura));
                faturasPane.getChildren().add(faturaButton);
            }

            stmtFaturasCompra.setDate(1, Date.valueOf(dataInicio));
            stmtFaturasCompra.setDate(2, Date.valueOf(dataFim));
            ResultSet rsFaturasCompra = stmtFaturasCompra.executeQuery();
            while (rsFaturasCompra.next()) {
                int idFatura = rsFaturasCompra.getInt("idFatura");
                String faturaInfo = String.format("Fatura de Compra: %d | Funcionario: %s\nCliente: %s\n%s | %s | %.2f",
                        idFatura,
                        rsFaturasCompra.getString("nomeFuncionario"),
                        rsFaturasCompra.getString("nomeCliente"),
                        rsFaturasCompra.getDate("data").toString(),
                        rsFaturasCompra.getTime("hora").toString(),
                        rsFaturasCompra.getDouble("valorTotal"));

                Button faturaButton = new Button(faturaInfo);
                faturaButton.getStyleClass().add("btn-categoria");
                faturaButton.setOnAction(event -> abrirDetalhesFaturaCompra(idFatura));
                faturasPane.getChildren().add(faturaButton);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre a janela de detalhes da fatura especificada.
     *
     * @param idFatura o ID da fatura
     */
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

    /**
     * Abre a janela de detalhes da fatura de compra especificada.
     *
     * @param idFatura o ID da fatura de compra
     */
    private void abrirDetalhesFaturaCompra(int idFatura) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo6/projetoptda/DetalhesFatura.fxml"));
            Parent root = loader.load();

            DetalhesFaturaController controller = loader.getController();
            controller.carregarDetalhesFaturaCompra(idFatura);

            Stage stage = new Stage();
            stage.setTitle("Detalhes da Fatura da Compra");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra o painel de seleção de data.
     */
    @FXML
    private void mostrarSelecionarDataPane() {
        selecionarDataPane.setVisible(true);
        selecionarDataPane.setManaged(true);
    }

    /**
     * Esconde o painel de seleção de data.
     */
    @FXML
    private void fecharSelecionarDataPane() {
        selecionarDataPane.setVisible(false);
        selecionarDataPane.setManaged(false);
    }

    /**
     * Pesquisa faturas no intervalo de datas selecionado.
     */
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

    /**
     * Volta para o painel principal.
     */
    @FXML
    public void onVoltarClick() {
        try {
            SceneManager.setScene((Stage) selecionarDataPane.getScene().getWindow(), "/com/grupo6/projetoptda/MainPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}