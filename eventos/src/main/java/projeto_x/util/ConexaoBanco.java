package projeto_x.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    // Carrega o .env
    private static Dotenv dotenv = Dotenv.load();
    private static String url = dotenv.get("MYSQL_URL");
    private static String user = dotenv.get("MYSQL_USER");
    private static String password= dotenv.get("MYSQL_PASSWORD");

    public static Connection conectar() throws PersistenceException {

        // Verificação das variáveis de ambiente
        if (url == null || url.trim().isEmpty()) {
            throw new PersistenceException("A variável de ambiente MYSQL_URL não foi definida!");
        }

        if (user == null || user.trim().isEmpty()) {
            throw new PersistenceException("A variável de ambiente MYSQL_USER não foi definida!");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new PersistenceException("A variável de ambiente MYSQL_PASSWORD não foi definida!");
        }

        // Tentativa de conexão com o banco de dados
        try(Connection conexao = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexão estabelecida com sucesso!");
            return conexao;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
