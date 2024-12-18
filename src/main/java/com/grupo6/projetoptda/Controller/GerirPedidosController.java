package com.grupo6.projetoptda.Controller;

import com.grupo6.projetoptda.Utilidades.*;
import com.grupo6.projetoptda.Getter.Pedido;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.grupo6.projetoptda.Utilidades.InterfaceUtils.recarregarInterface;

public class GerirPedidosController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label labelData;

    @FXML
    public void onVoltarClick() {
        try {
            SceneManager.setScene((Stage) labelData.getScene().getWindow(), "/com/grupo6/projetoptda/MainPanel.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private FlowPane pedidosPane;

    @FXML
    private StackPane modifyPedidoPane;

    @FXML
    private StackPane pagamentoPane;

    @FXML
    private TextField pedidoIdField;

    @FXML
    private TextField pedidoStatusField;

    @FXML
    private Button finalizarPedidoButton;

    @FXML
    private Button fazerPagamentoButton;

    @FXML
    private Label labelUtilizador;

    private AppState appState = AppState.getInstance();

    private Pedido pedidoSelecionado;

    @FXML
    public void initialize() {
        finalizarPedidoButton.setDisable(true);
        fazerPagamentoButton.setDisable(true);
        DateUtils.updateDate(labelData);
        labelUtilizador.setText(appState.getNomeFuncionario());
    }

    @FXML
    public void filtrarPedidosEntregues() {
        carregarPedidos("Entregue");
    }

    @FXML
    public void filtrarPedidosPorPagar() {
        carregarPedidos("PorPagar");
    }

    private void carregarPedidos(String status) {
        List<Pedido> pedidos = buscarPedidos(status);
        pedidosPane.getChildren().clear();
        for (Pedido pedido : pedidos) {
            String query = "SELECT c.nome AS nomeCliente, p.dataHora FROM Pedido p JOIN Cliente c ON p.idCliente = c.idCliente WHERE p.idPedido = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, pedido.idPedido());
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        String nomeCliente = resultSet.getString("nomeCliente");
                        Timestamp dataHora = resultSet.getTimestamp("dataHora");
                        String data = dataHora.toLocalDateTime().toLocalDate().toString();
                        String hora = dataHora.toLocalDateTime().toLocalTime().toString();
                        String pedidoInfo = String.format("Fatura: %d\nCliente: %s\n%s | %s",
                                pedido.idPedido(), nomeCliente, data, hora);

                        Button btnPedido = new Button(pedidoInfo);
                        btnPedido.setOnAction(event -> selecionarPedido(pedido));
                        btnPedido.getStyleClass().add("btn-categoria");

                        HBox hbox = new HBox(btnPedido, criarBotoesPedido(pedido));
                        pedidosPane.getChildren().add(hbox);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private VBox criarBotoesPedido(Pedido pedido) {
        Button btnModificar = new Button();
        FontIcon iconModificar = new FontIcon(FontAwesomeSolid.EDIT);
        btnModificar.setGraphic(iconModificar);
        btnModificar.setOnAction(event -> abrirModificarVendaPane(pedido.idPedido())); // Passa o pedido aqui

        Button btnRemover = new Button();
        FontIcon iconRemover = new FontIcon(FontAwesomeSolid.TRASH);
        btnRemover.setGraphic(iconRemover);
        btnRemover.setOnAction(event -> removerPedido(pedido));

        return new VBox(btnModificar, btnRemover);
    }

    private List<Pedido> buscarPedidos(String status) {
        List<Pedido> pedidos = new ArrayList<>();
        String query = "SELECT * FROM Pedido WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Pedido pedido = new Pedido(
                            resultSet.getInt("idPedido"),
                            resultSet.getString("status")
                    );
                    pedidos.add(pedido);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    private void selecionarPedido(Pedido pedido) {
        this.pedidoSelecionado = pedido;
        if ("Entregue".equals(pedido.status())) {
            finalizarPedidoButton.setDisable(false);
            fazerPagamentoButton.setDisable(true);
        } else if ("PorPagar".equals(pedido.status())) {
            finalizarPedidoButton.setDisable(true);
            fazerPagamentoButton.setDisable(false);
        } else {
            finalizarPedidoButton.setDisable(true);
            fazerPagamentoButton.setDisable(true);
        }
    }

    public void abrirModificarVendaPane(int idPedido) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupo6/projetoptda/ModificarVendaPanel.fxml"));
            Parent root = loader.load();

            // Get the controller and set the pedido ID
            ModificarVendaController controller = loader.getController();
            controller.setPedidoId(idPedido);

            // Replace the current scene with the ModificarVenda scene
            Stage stage = (Stage) rootPane.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Scene scene = new Scene(root);
            stage.setWidth(width);
            stage.setHeight(height);
            CarregarCSS.applyCSS(scene);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void removerPedido(Pedido pedido) {
        String query = "{CALL cancelarPedido(?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareCall(query)) {
            stmt.setInt(1, pedido.idPedido());
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Pedido removido com sucesso!");
            alert.showAndWait();
            carregarPedidos(pedido.status());
            recarregarInterface();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao remover pedido: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void finalizarPedido() {
        if (pedidoSelecionado != null && "Entregue".equals(pedidoSelecionado.status())) {
            String query = "{CALL finalizarPedido(?)}";
            try (Connection conn = DatabaseConnection.getConnection();
                 CallableStatement stmt = conn.prepareCall(query)) {
                stmt.setInt(1, pedidoSelecionado.idPedido());
                stmt.execute();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Pedido finalizado com sucesso!");
                alert.showAndWait();
                carregarPedidos("Entregue");
                recarregarInterface();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao finalizar pedido: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void mostrarPagamentoPane() {
        if (pedidoSelecionado != null && "PorPagar".equals(pedidoSelecionado.status())) {
            Panes.showPane(pagamentoPane);
        }
    }

    @FXML
    public void fecharPagamentoPane() {
        pagamentoPane.setVisible(false);
        pagamentoPane.setManaged(false);
    }

    @FXML
    public void fazerPagamento(ActionEvent event) {
        String metodoPagamento = ((Button) event.getSource()).getText();
        String queryPagamento = "{CALL fazerPagamento(?, ?)}";
        String queryEmitirFatura = "{CALL emitirFatura(?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmtPagamento = conn.prepareCall(queryPagamento);
             CallableStatement stmtEmitirFatura = conn.prepareCall(queryEmitirFatura)) {

            // Chamar a stored procedure fazerPagamento
            stmtPagamento.setInt(1, pedidoSelecionado.idPedido());
            stmtPagamento.setString(2, metodoPagamento.equals("Dinheiro Vivo") ? "DinheiroVivo" : metodoPagamento);
            stmtPagamento.execute();

            // Chamar a stored procedure emitirFatura
            stmtEmitirFatura.setInt(1, pedidoSelecionado.idPedido());
            stmtEmitirFatura.execute();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Pagamento realizado e fatura emitida com sucesso!");
            alert.showAndWait();

            carregarPedidos("PorPagar");
            recarregarInterface();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao realizar pagamento e emitir fatura: " + e.getMessage());
            alert.showAndWait();
        }
    }
}