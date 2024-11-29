import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o nome da base de dados: ");
        String database = scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/" + database;

        try (Connection connection = DriverManager.getConnection(url, Autenticacao.USER, Autenticacao.PASSWORD)) {
            System.out.println("Conexão bem-sucedida!");
            Statement statement = connection.createStatement();

            while (true) {
                System.out.print("Digite uma instrução SELECT ou SAIR para terminar: ");
                String sql = scanner.nextLine();

                if (sql.equalsIgnoreCase("SAIR")) {
                    break;
                }

                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    int columnCount = resultSet.getMetaData().getColumnCount();

                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.print(resultSet.getString(i) + "\t");
                        }
                        System.out.println();
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao executar a instrução: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar à base de dados: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}