package projeto_x.dao;

import projeto_x.model.Endereco;
import projeto_x.model.Usuario;
import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class UsuarioDAO {
    private EnderecoDAO enderecoDAO;

    public UsuarioDAO(EnderecoDAO enderecoDAO) {
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
        String sql = "INSERT INTO usuarios (nm_usuario, endereco_id, email, senha, latitude_usuario, longitude_usuario) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, usuario.getNome());
            
            if (usuario.getEndereco() != null && usuario.getEndereco().getID() > 0) {
                stmt.setInt(2, usuario.getEndereco().getID());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getHashSenha());
            
            if (usuario.getLatitude() != null) {
                stmt.setDouble(5, usuario.getLatitude());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }

            if (usuario.getLongitude() != null) {
                stmt.setDouble(6, usuario.getLongitude());
            } else {
                stmt.setNull(6, Types.DOUBLE);
            }
        
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
     * @return O método construtor do objeto Usuario, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o usuário (falha no banco de dados).
     */
    public Usuario buscarPorEmail(String email) throws PersistenceException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);

            // Define o e-mail no placeholder (?)
            stmt.setString(1, email);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    return construirUsuarioDoResultSet(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário por e-mail: " + e.getMessage());
        }
    }

    /**
     * Busca um usuário pelo ID no banco de dados (READ).
     * 
     * @param idUsuario O ID do usuário a ser buscado.
     * @return O método construtor do objeto Usuario, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o usuário (falha no banco de dados).
     */
    public Usuario buscarPorID(int idUsuario) throws PersistenceException {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);

            // Define o ID no placeholder (?)
            stmt.setInt(1, idUsuario);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    return construirUsuarioDoResultSet(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar usuário por ID: " + e.getMessage());
        }
    }

    /**
     * Atualiza os dados de Usuario a partir do ID (UPDATE).
     * 
     * @param usuario Objeto Usuario.
     * @return true se a atualização for bem-sucedida.
     * @throws PersistenceException Se ocorrer algum erro ao atualizar dados (falha no banco de dados).
     */
    public boolean atualizar(Usuario usuario) throws PersistenceException {
        String sql = "UPDATE usuarios SET nm_usuario = ?, email = ?, senha = ?, endereco_id = ?, " +
                    "latitude_usuario = ?, longitude_usuario = ? WHERE id_usuario = ?";
    
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getHashSenha()); // Lembre-se de que a senha deve ser atualizada como hash
     
            if (usuario.getEndereco() != null && usuario.getEndereco().getID() > 0) {
                stmt.setInt(4, usuario.getEndereco().getID());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
        
            if (usuario.getLatitude() != null) {
                stmt.setDouble(5, usuario.getLatitude());
            } else {
                stmt.setNull(5, java.sql.Types.DOUBLE);
            }
        
            if (usuario.getLongitude() != null) {
                stmt.setDouble(6, usuario.getLongitude());
            } else {
                stmt.setNull(6, java.sql.Types.DOUBLE);
            }

            // Parâmentro do WHERE
            stmt.setInt(7, usuario.getID()); // Assumindo que seu POJO Usuario tem um getID()
        
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    /**
     * Atualiza o hash da senha de um usuário no banco de dados (UPDATE).
     * 
     * @param usuario O objeto Usuario com o ID e o novo hash de senha.
     * @return true se a senha foi atualizada com sucesso, false caso contrário.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public boolean atualizarSenhaHash(Usuario usuario) throws PersistenceException {
        if (usuario.getID() <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido para atualização de senha.");
        }
        String sql = "UPDATE usuarios SET hash_senha = ? WHERE id_usuario = ?";
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getHashSenha());
            stmt.setInt(2, usuario.getID());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar senha do usuário: " + e.getMessage());
        }
    }

    /**
     * Deleta os dados do objeto Usuario, a partir do ID (DELETE).
     * 
     * @param idUsuario ID do usuário.
     * @return true se a deleção for bem-sucedida.
     * @throws PersistenceException Se ocorrer algum erro ao deletar os dados (falha no banco de dados).
     */
    public boolean deletar(int idUsuario) throws PersistenceException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        
        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a deleção foi bem-sucedida
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao deletar usuário: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para evitar duplicação de código ao construir o objeto Usuario.
     * 
     * @param rs ResultSet contendo os dados do evento.
     * @return O objeto Usuario construído a partir dos dados do ResultSet.
     * @throws SQLException Se ocorrer algum erro ao buscar dados do evento (falha no banco de dados).
     * @throws PersistenceException Se ocorrer alguma outra exceção persistente.
     */
    private Usuario construirUsuarioDoResultSet(ResultSet rs) throws SQLException, PersistenceException {
        Integer idUsuario = (Integer) rs.getObject("id_usuario");
        String nomeUsuario = rs.getString("nm_usuario");
        String emailRecuperado = rs.getString("email");
        String hashSenha = rs.getString("senha");
        Double latitudeUsuario = (Double) rs.getObject("latitude_usuario");
        Double longitudeUsuario = (Double) rs.getObject("longitude_usuario");
                    
        int enderecoID = rs.getInt("endereco_id");
        Endereco enderecoUsuario = (enderecoID > 0) ? enderecoUsuario = enderecoDAO.buscarPorID(enderecoID) : null;
        if (enderecoUsuario == null) {
            throw new PersistenceException("Inconsistência de dados: Endereço com ID " + enderecoID + " não encontrado!");
        }
        
        return new Usuario(
            idUsuario, nomeUsuario, enderecoUsuario, emailRecuperado,
            hashSenha, latitudeUsuario, longitudeUsuario
        );
    }
}
