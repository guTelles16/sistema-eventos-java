package projeto_x.dao;

import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;
import projeto_x.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CategoriaDAO {
    /**
     * Salva uma nova categoria no banco de dados (CREATE).
     * 
     * @param categoria
     * @return O ID da categoria recém-criada.
     * @throws PersistenceException Se ocorrer algum erro ao salvar a categoria (falha no banco de dados).
     */
    public int salvar(Categoria categoria) throws PersistenceException {
        String slq = "INSERT INTO categorias (nm_categoria) VALUES (?)";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(slq, Statement.RETURN_GENERATED_KEYS);
            
            if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
                throw new PersistenceException("Nome da categoria não pode ser nulo ou vazio ao salvar!");
            }

            stmt.setString(1, categoria.getNome());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new PersistenceException("Falha ao salvar categoria: nenhuma linha foi afetada!");
            }

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
        String sql = "SELECT * FROM categorias WHERE nm_categoria = ?";

        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
            if (nome == null || nome.trim().isEmpty()) {
                throw new PersistenceException("Nome para busca da categoria não pode ser nulo ou vazio!");
            }
            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idCategoria = rs.getInt("id_categoria");
                    String nomeRecuperado = rs.getString("nm_categoria");
                    return new Categoria(idCategoria, nomeRecuperado);
                } else {
                    return null;
                }
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
                if (rs.next()) {
                    int idCategoria = rs.getInt("id_categoria");
                    String nome = rs.getString("nm_categoria");
                    return new Categoria(idCategoria, nome);
                } else {
                    return null;
                }
            }
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar categoria: " + e.getMessage());
        }  
    }

    /**
     * Atualiza uma categoria existente no banco de dados (UPDATE).
     *
     * @param categoria A categoria com os dados a serem atualizados.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao atualizar a categoria (falha no banco de dados).
     */
    public boolean atualizar(Categoria categoria) throws PersistenceException {
        String sql = "UPDATE categorias SET nm_categoria = ? WHERE id_categoria = ?";
        
        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            if (categoria.getID() == null || categoria.getID() <= 0) {
                throw new PersistenceException("ID da categoria inválido para atualização.");
            }

            if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
                throw new PersistenceException("Nome da categoria não pode ser nulo ou vazio ao atualizar.");
            }

            stmt.setString(1, categoria.getNome());

            // Condição WHERE
            stmt.setInt(2, categoria.getID());
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar usuário: " + e.getMessage());
    }
}
    /**
     * Deleta uma categoria do banco de dados (DELETE).
     *
     * @param idCategoria O ID da categoria a ser deletada.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao deletar a categoria (falha no banco de dados).
     */
    public boolean deletar(int idCategoria) throws PersistenceException {
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategoria);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a deleção foi bem-sucedida
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao deletar usuário: " + e.getMessage());
        }
    }
}
