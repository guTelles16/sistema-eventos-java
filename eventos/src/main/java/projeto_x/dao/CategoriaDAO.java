package projeto_x.dao;

import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;
import projeto_x.model.Categoria;
import projeto_x.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaDAO {
    /**
     * Salva uma nova categoria no banco de dados (CREATE).
     * 
     * @param categoria
     * @return O ID da categoria recém-criada.
     * @throws PersistenceException Se ocorrer algum erro ao salvar a categoria (falha no banco de dados).
     */
    public int salvar(Categoria categoria) throws PersistenceException {
        String slq = "INSERT INTO categorias (id_categoria, nm_categoria) VALUES (?, ?)";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(slq, PreparedStatement.RETURN_GENERATED_KEYS);
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

        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new PersistenceException("Categoria não encontrada com o nome: " + nome);
                }
                
                int idCategoria = rs.getInt("id_categoria");
                String nomeRecuperado = rs.getString("nm_categoria");
                    
                return new Categoria(idCategoria, nomeRecuperado);
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

        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
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
    // Atualizar
    public boolean atualizar(Categoria categoria) throws PersistenceException {
    String sql = "UPDATE usuarios SET nm_categoria WHERE id_categoria";
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        // Defina os parâmetros de acordo com as colunas que você quer atualizar
        stmt.setString(1, categoria.getNome());

        // Condição WHERE: ID do usuário a ser atualizado
        stmt.setInt(2, categoria.getID()); // Assumindo que seu POJO Usuario tem um getID()
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao atualizar usuário: " + e.getMessage());
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
