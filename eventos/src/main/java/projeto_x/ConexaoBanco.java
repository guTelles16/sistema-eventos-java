package projeto_x;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    public static Connection conectar() throws Exception {
        String url = System.getenv("MYSQL_URL");
        String user = System.getenv("MYSQL_USER");
        String password = System.getenv("MYSQL_PASSWORD");

        // Verificação das variáveis de ambiente
        if (url == null || url.trim().isEmpty()) {
            throw new Exception("A variável de ambiente MYSQL_URL não foi definida!");
        }

        if (user == null || user.trim().isEmpty()) {
            throw new Exception("A variável de ambiente MYSQL_USER não foi definida!");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new Exception("A variável de ambiente MYSQL_PASSWORD não foi definida!");
        }

        // Tentativa de conexão com o banco de dados
        try(Connection conexao = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conexão estabelecida com sucesso!");
            return conexao;
        } catch (SQLException e) {
            throw new Exception("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
