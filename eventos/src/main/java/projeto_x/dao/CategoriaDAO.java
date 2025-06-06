package projeto_x.dao;

import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;
import projeto_x.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaDAO {

    private Connection conexao;

    public CategoriaDAO() throws PersistenceException {
        this.conexao = ConexaoBanco.conectar();
    }

    /**
     * Salva uma nova categoria no banco de dados (CREATE).
     * 
     * @param categoria
     * @return O ID da categoria recém-criada.
     * @throws PersistenceException Se ocorrer algum erro ao salvar a categoria (falha no banco de dados).
     */
    public int salvar(Categoria categoria) throws PersistenceException {
        String slq = "INSERT INTO categorias (id_categoria, nm_categoria) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(slq, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, categoria.getID());
            stmt.setString(1, categoria.getNome());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID gerado
                }
                throw new PersistenceException("Falha ao obter ID da categoria: nenhum ID foi gerado!");
            }
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar categoria: " + e.getMessage());
        }
    }

    /**
     * Busca uma categoria pelo nome no banco de dados (READ).
     * 
     * @param nome
     * @return A categoria encontrada se o nome existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar a categoria (falha no banco de dados).
     */
    public Categoria buscarCategoria(String nome) throws PersistenceException {
        String sql = "SELECT * FROM Categoria WHERE nm_categoria = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new PersistenceException("Categoria não encontrada com o nome: " + nome);
                }
                
                int id = rs.getInt("id_categoria");
                String nomeRecuperado = rs.getString("nm_categoria");
                    
                return new Categoria(id, nomeRecuperado);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar categoria: " + e.getMessage());
        }
    }

    /**
     * Busca uma categoria pelo ID no banco de dados (READ).
     * 
     * @param id
     * @return A categoria encontrada se o ID existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar a categoria (falha no banco de dados).
     */
    public Categoria buscarPorID(int id) throws PersistenceException {
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new PersistenceException("Categoria não encontrada com o ID: " + id);
                }
                
                int idCategoria = rs.getInt("id_categoria");
                String nome = rs.getString("nm_categoria");

                return new Categoria(idCategoria, nome);
            }
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar categoria: " + e.getMessage());
        }  
    }
}
