package projeto_x.dao;

import projeto_x.model.Usuario;
import projeto_x.util.ConexaoBanco;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    private Connection conexao;

    public UsuarioDAO() throws Exception {
        this.conexao = ConexaoBanco.conectar();
    }

    /**
     * Salva um novo usuário no banco de dados (CREATE).
     * 
     * @param usuario O usuário a ser salvo.
     * @throws Exception Se ocorrer algum erro ao salvar o usuário (falha no banco de dados).
     */
    public void salvar(Usuario usuario) throws Exception {
        String slq = "INSERT INTO usuarios (nm_usuario, email, senha) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(slq)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getHashSenha());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    /**
     * Busca um usuário pelo e-mail no banco de dados (READ).
     * 
     * @param email O e-mail do usuário a ser buscado.
     * @return O usuário encontrado se o e-mail existir, null caso contrário.
     * @throws Exception Se ocorrer algum erro ao buscar o usuário (falha no banco de dados).
     */
    public Usuario buscarPorEmail(String email) throws Exception {
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
                    int id = rs.getInt("id_usuario");
                    String nome = rs.getString("nm_usuario");
                    String emailRecuperado = rs.getString("email");
                    String hashSenha = rs.getString("senha");

                    usuario = new Usuario(id, nome, emailRecuperado, hashSenha);
                }
            }

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar usuário por e-mail: " + e.getMessage());
        }
        return usuario; // Retorna o usuário encontrado ou null
    }

    /**
     * Busca um usuário pelo ID no banco de dados (READ).
     * 
     * @param id O ID do usuário a ser buscado.
     * @return O usuário encontrado se o ID existir, null caso contrário.
     * @throws Exception Se ocorrer algum erro ao buscar o usuário (falha no banco de dados).
     */
    public Usuario buscarPorId(int id) throws Exception {
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
                    String nome = rs.getString("nm_usuario");
                    String email = rs.getString("email");
                    String hashSenha = rs.getString("senha");

                    usuario = new Usuario(id, nome, email, hashSenha);
                }
            }

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return usuario; // Retorna o usuário encontrado ou null
    }
}
