import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class criarFuncionario {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PTDA24_BD_06";
    private static final String USER = "root";
    private static final String PASSWORD = "VaiTeFoder123@@";

    public static void main(String[] args) {
        int id = 4;
        String nome = "Manbo Ladrao";
        String password = "1234";
        int nivelAcesso = 1;
        String tipo = "EmpregadoMesa";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            String sql = "INSERT INTO Funcionario (idFuncionario, fNome, fPassword, nivelAcesso, tipo) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.setString(2, nome);
                stmt.setString(3, hashPassword);
                stmt.setInt(4, nivelAcesso);
                stmt.setString(5, tipo);
                stmt.executeUpdate();
                System.out.println("Funcionário criado com sucesso!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
