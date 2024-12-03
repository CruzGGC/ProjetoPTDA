import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class Autenticar {
    public static void main(String[] args) {
        final String DB_URL = "jdbc:mysql://localhost:3306/PTDA24_BD_06";
        final String USER = "root";
        final String PASSWORD = "password";

        String nome = "Guilherme Cruz";
        String passwordInserida = "12345";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String sql = "CALL autenticar(?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, nome);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String hashSenha = rs.getString("fPassword");
                        if (BCrypt.checkpw(passwordInserida, hashSenha)) {
                            System.out.println("Autenticação bem-sucedida!");
                        } else {
                            System.out.println("Senha incorreta.");
                        }
                    } else {
                        System.out.println("Usuário não encontrado.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
