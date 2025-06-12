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

public class EnderecoDAO {
    /**
     * Salva um novo endereço no banco de dados (CREATE).
     * 
     * @param endereco O endereço a ser salvo.
     * @throws PersistenceException Se ocorrer algum erro ao salvar o endereço (falha no banco de dados).
     */
    public int salvar(Endereco endereco) throws PersistenceException {
        String sql = "INSERT INTO enderecos (logradouro, nr_logradouro, complemento, bairro, nr_cep) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, endereco.getLogradouro());
            stmt.setString(2, endereco.getNumeroLogradouro());
            stmt.setString(3, endereco.getComplemento());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCEP());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID gerado 
                }
                throw new PersistenceException("Falha ao obter ID do endereço: nenhum ID foi gerado!");
            }
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar endereço: " + e.getMessage());
        }
    }

    /**
     * Busca um endereço pelo ID no banco de dados (READ).
     * 
     * @param id O ID do endereço a ser buscado.
     * @return O endereço encontrado se o ID existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o endereço (falha no banco de dados).
     */
    public Endereco buscarPorID(int id) throws PersistenceException {
        String sql = "SELECT * FROM enderecos WHERE id_endereco = ?";

        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (!rs.next()) {
                    throw new PersistenceException("Endereço não encontrado com o ID: " + id);
                }
                
                // Cria um novo objeto Endereco com os dados retornados
                int idEndereco = rs.getInt("id_endereco");
                String logradouro = rs.getString("logradouro");
                String numeroLogradouro = rs.getString("nr_logradouro");
                String complemento = rs.getString("complemento");
                String bairro = rs.getString("bairro");
                String cep = rs.getString("nr_cep");

                return new Endereco(idEndereco, logradouro, numeroLogradouro, complemento, bairro, cep);
            }
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar endereço: " + e.getMessage());
        }
    }
    // Atualizar
    public boolean atualizar(Endereco endereco) throws PersistenceException {
    String sql = "UPDATE usuarios SET logradouro, nr_logradouro, complemento, bairro, nr_cep WHERE id_endereco = ?";
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        // Defina os parâmetros de acordo com as colunas que você quer atualizar
        stmt.setString(1, endereco.getLogradouro());
        stmt.setString(2, endereco.getNumeroLogradouro());
        stmt.setString(3, endereco.getComplemento());
        stmt.setString(3, endereco.getBairro());
        stmt.setString(4, endereco.getCEP());
   
        // Condição WHERE: ID do usuário a ser atualizado
        stmt.setInt(7, endereco.getID()); // Assumindo que seu POJO Usuario tem um getID()
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao atualizar endereço: " + e.getMessage(), e);
    }
}
    // Deletar
    public boolean deletar(int id) throws PersistenceException {
    String sql = "DELETE FROM usuarios WHERE id_endereco = ?"; // Adapte a tabela e a coluna ID
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a deleção foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao deletar endereço: " + e.getMessage(), e);
    }
}
}