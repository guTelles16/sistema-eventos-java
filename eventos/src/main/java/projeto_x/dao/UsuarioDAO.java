package projeto_x.dao;

import projeto_x.model.Endereco;
import projeto_x.model.Usuario;
import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    private Connection conexao;
    private EnderecoDAO enderecoDAO;

    public UsuarioDAO(EnderecoDAO enderecoDAO) throws Exception {
        this.conexao = ConexaoBanco.conectar();
        this.enderecoDAO = enderecoDAO;
    }

    /**
     * Salva um novo usuário no banco de dados (CREATE).
     * 
     * @param usuario O usuário a ser salvo.
     * @return O ID do usuário recém-criado.
     * @throws PersistenceException Se ocorrer algum erro ao salvar o usuário (falha no banco de dados).
     */
    public int salvar(Usuario usuario) throws PersistenceException {
        String sql = "INSERT INTO usuarios (nm_usuario, endereco_id, email, senha) VALUES (?, ?, ?, ?)";
    
        try (PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setInt(2, usuario.getEndereco().getID());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getHashSenha());
        
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID gerado
                }
                throw new PersistenceException("Falha ao obter o ID do usuário: nenhum ID foi gerado!");
            }
    
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    /**
     * Busca um usuário pelo e-mail no banco de dados (READ).
     * 
     * @param email O e-mail do usuário a ser buscado.
     * @return O usuário encontrado se o e-mail existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o usuário (falha no banco de dados).
     */
    public Usuario buscarPorEmail(String email) throws PersistenceException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        Usuario usuario = null; // Inicializa o usuário como null
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define o e-mail no placeholder (?)
            stmt.setString(1, email);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um novo objeto Usuario com os dados retornados
                    int idUsuario = rs.getInt("id_usuario");
                    String nome = rs.getString("nm_usuario");
                    Endereco endereco = enderecoDAO.buscarPorID(rs.getInt("endereco_id"));
                    String emailRecuperado = rs.getString("email");
                    String hashSenha = rs.getString("senha");

                    usuario = new Usuario(idUsuario, nome, endereco, emailRecuperado, hashSenha);
                }
            }

        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário por e-mail: " + e.getMessage());
        }
        return usuario; // Retorna o usuário encontrado ou null
    }

    /**
     * Busca um usuário pelo ID no banco de dados (READ).
     * 
     * @param id O ID do usuário a ser buscado.
     * @return O usuário encontrado se o ID existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o usuário (falha no banco de dados).
     */
    public Usuario buscarPorID(int id) throws PersistenceException {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        Usuario usuario = null; // Inicializa o usuário como null
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um novo objeto Usuario com os dados retornados
                    int idUsuario = rs.getInt("id_usuario");
                    String nome = rs.getString("nm_usuario");
                    String email = rs.getString("email");
                    Endereco endereco = enderecoDAO.buscarPorID(rs.getInt("endereco_id"));
                    String hashSenha = rs.getString("senha");

                    usuario = new Usuario(idUsuario, nome, endereco, email, hashSenha);
                }
            }

        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return usuario; // Retorna o usuário encontrado ou null
    }
}
