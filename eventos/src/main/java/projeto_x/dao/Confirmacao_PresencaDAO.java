package projeto_x.dao;

import projeto_x.model.Confirmacao_Presenca;
import projeto_x.model.Evento;
import projeto_x.model.Usuario;
import projeto_x.util.ConexaoBanco;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Confirmacao_PresencaDAO {
    private Connection conexao;
    private UsuarioDAO usuarioDAO;

    public Confirmacao_PresencaDAO() throws Exception {
        Connection conexao = ConexaoBanco.conectar();
        this.conexao = conexao;
    }


}
