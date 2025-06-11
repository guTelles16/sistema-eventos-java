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
}
