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
            PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, usuario.getNome());
            
            if (usuario.getEndereco() != null && usuario.getEndereco().getID() > 0) {
                stmt.setInt(2, usuario.getEndereco().getID());
            }

            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getHashSenha());
            stmt.setDouble(5, usuario.getLatitude());
            stmt.setDouble(6, usuario.getLongitude());
        
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
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);

            // Define o e-mail no placeholder (?)
            stmt.setString(1, email);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um novo objeto Usuario com os dados retornados
                    Integer idUsuario = (Integer) rs.getObject("id_usuario");
                    String nomeUsuario = rs.getString("nm_usuario");
                    String emailRecuperado = rs.getString("email");
                    String hashSenha = rs.getString("senha");
                    Double latitudeUsuario = (Double) rs.getObject("latitude_usuario");
                    Double longitudeUsuario = (Double) rs.getObject("longitude_usuario");
                    
                    // Busca o ID da chave estrangeira
                    int enderecoID = rs.getInt("endereco_id");

                    // Busca o objeto Endereco
                    Endereco enderecoUsuario = null;
                    if (enderecoID > 0) {
                        enderecoUsuario = enderecoDAO.buscarPorID(enderecoID);
                        if (enderecoUsuario == null) {
                            throw new PersistenceException("Endereço do evento com ID " + enderecoID + " não encontrado após a busca!");
                        }
                    } else {
                        throw new PersistenceException("ID do Endereço do usuário inválido: " + enderecoID);
                    }

                    // Retorna o objeto Usuario completo
                    return new Usuario(
                        idUsuario,
                        nomeUsuario,
                        enderecoUsuario,
                        emailRecuperado,
                        hashSenha,
                        latitudeUsuario,
                        longitudeUsuario
                    );
                } else {
                    throw new PersistenceException("Usuário não encontrado com o e-mail: " + email);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar usuário por e-mail: " + e.getMessage());
            throw new PersistenceException("Erro ao buscar usuário por e-mail: " + e.getMessage());
        }
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
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);

            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um novo objeto Usuario com os dados retornados
                    Integer idUsuario = (Integer) rs.getObject("id_usuario");
                    String nomeUsuario = rs.getString("nm_usuario");
                    String emailRecuperado = rs.getString("email");
                    String hashSenha = rs.getString("senha");
                    Double latitudeUsuario = (Double) rs.getObject("latitude_usuario");
                    Double longitudeUsuario = (Double) rs.getObject("longitude_usuario");
                    
                    // Busca o ID da chave estrangeira
                    int enderecoID = rs.getInt("endereco_id");

                    // Busca o objeto Endereco
                    Endereco enderecoUsuario = null;
                    if (enderecoID > 0) {
                        enderecoUsuario = enderecoDAO.buscarPorID(enderecoID);
                        if (enderecoUsuario == null) {
                            throw new PersistenceException("Endereço do usuário com ID " + enderecoID + " não encontrado após a busca!");
                        }
                    } else {
                        throw new PersistenceException("ID do Endereço do usuário inválido: " + enderecoID);
                    }

                    // Retorna o objeto Usuario completo
                    return new Usuario(
                        idUsuario,
                        nomeUsuario,
                        enderecoUsuario,
                        emailRecuperado,
                        hashSenha,
                        latitudeUsuario,
                        longitudeUsuario
                    );
                } else {
                    throw new PersistenceException("Usuário não encontrado com o ID: " + id);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar usuário pelo ID: " + e.getMessage());
            throw new PersistenceException("Erro ao buscar usuário por ID: " + e.getMessage());
        }
    }
    // Atualizar
    public boolean atualizar(Usuario usuario) throws PersistenceException {
    String sql = "UPDATE usuarios SET nm_usuario = ?, email_usuario = ?, hash_senha = ?, endereco_id = ?, latitude = ?, longitude = ? WHERE id_usuario = ?";
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        // Defina os parâmetros de acordo com as colunas que você quer atualizar
        stmt.setString(1, usuario.getNome());
        stmt.setString(2, usuario.getEmail());
        stmt.setString(3, usuario.getHashSenha()); // Lembre-se de que a senha deve ser atualizada como hash
     // Para FKs (como endereco_id), verifique se o objeto relacionado e seu ID são válidos
        if (usuario.getEndereco() != null && usuario.getEndereco().getID() > 0) {
            stmt.setInt(4, usuario.getEndereco().getID());
        } else {
            stmt.setNull(4, java.sql.Types.INTEGER); // Ou lance uma exceção se for obrigatório
        }
        
        // Para Double (Latitude/Longitude), trate null
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

        // Condição WHERE: ID do usuário a ser atualizado
        stmt.setInt(7, usuario.getID()); // Assumindo que seu POJO Usuario tem um getID()
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao atualizar usuário: " + e.getMessage(), e);
    }
}
    // Deletar
    public boolean deletar(int id) throws PersistenceException {
    String sql = "DELETE FROM usuarios WHERE id_usuario = ?"; // Adapte a tabela e a coluna ID
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a deleção foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao deletar usuário: " + e.getMessage(), e);
    }
}
}
