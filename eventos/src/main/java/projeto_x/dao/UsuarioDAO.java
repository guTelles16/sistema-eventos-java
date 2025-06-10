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
        String sql = "SELECT" +
                    "u.id_usuario, u.nm_usuario AS nome_usuario, u.email AS email_usuario, " +
                    "u.endereco_id AS endereco_usuario, " +
                    "addr.logradouro, addr.nr_logradouro, addr.complemento, addr.bairro, addr.nr_cep " +
                    "FROM usuarios u" +
                    "JOIN enderecos addr ON u.endereco_id = addr.id_endereco " +
                    "WHERE email = ?";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);

            // Define o e-mail no placeholder (?)
            stmt.setString(1, email);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um objeto Endereco com as informações necessárias
                    Endereco enderecoUsuario = new Endereco(
                        rs.getInt("endereco_usuario"),
                        rs.getString("logradouro"),
                        rs.getString("nr_logradouro"),
                        rs.getString("complemento"),
                        rs.getString("bairro"),
                        rs.getString("nr_cep")
                    );

                    // Cria um novo objeto Usuario com os dados retornados
                    int idUsuario = rs.getInt("id_usuario");
                    String nome = rs.getString("nm_usuario");
                    String emailRecuperado = rs.getString("email");

                    return new Usuario(
                        idUsuario,
                        nome,
                        enderecoUsuario,
                        emailRecuperado
                    );
                } else {
                    throw new PersistenceException("Usuário não encontrado com o e-mail: " + email);
                }
            }

        } catch (SQLException e) {
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
        String sql = "SELECT" +
                    "u.id_usuario, u.nm_usuario AS nome_usuario, u.email AS email_usuario, " +
                    "u.endereco_id AS endereco_usuario, " +
                    "addr.logradouro, addr.nr_logradouro, addr.complemento, addr.bairro, addr.nr_cep " +
                    "FROM usuarios u" +
                    "JOIN enderecos addr ON u.endereco_id = addr.id_endereco " +
                    "WHERE id_usuario = ?";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);

            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {

                    // Cria um objeto Endereco com as informações necessárias
                    Endereco enderecoUsuario = new Endereco(
                        rs.getInt("endereco_usuario"),
                        rs.getString("logradouro"),
                        rs.getString("nr_logradouro"),
                        rs.getString("complemento"),
                        rs.getString("bairro"),
                        rs.getString("nr_cep")
                    );

                    // Cria um novo objeto Usuario com os dados retornados
                    int idUsuario = rs.getInt("id_usuario");
                    String nome = rs.getString("nm_usuario");
                    String email = rs.getString("email");

                    return new Usuario(
                        idUsuario,
                        nome,
                        enderecoUsuario,
                        email
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
