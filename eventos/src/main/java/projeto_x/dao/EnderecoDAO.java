package projeto_x.dao;

import projeto_x.mdodel.Endereco;
import projeto_x.util.ConexaoBanco;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderecoDAO {
    private Connection conexao;

    public EnderecoDAO() throws Exception {
        this.conexao = ConexaoBanco.conectar();
    }

    /**
     * Salva um novo endereço no banco de dados (CREATE).
     * 
     * @param endereco O endereço a ser salvo.
     * @throws Exception Se ocorrer algum erro ao salvar o endereço (falha no banco de dados).
     */
    public void salvar(projeto_x.model.Endereco endereco) throws Exception {
        String sql = "INSERT INTO enderecos (logradouro, nr_logradouro, complemento, bairro, nr_cep) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, endereco.getLogradouro());
            stmt.setString(2, endereco.getNumeroLogradouro());
            stmt.setString(3, endereco.getComplemento());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCEP());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar endereço: " + e.getMessage());
        }
    }

    /**
     * Busca um endereço pelo ID no banco de dados (READ).
     * 
     * @param id O ID do endereço a ser buscado.
     * @return O endereço encontrado se o ID existir, null caso contrário.
     * @throws Excepiton Se ocorrer algum erro ao buscar o endereço (falha no banco de dados).
     */
    public Endereco buscarPorId(int id) throws Exception {
        String sql = "SELECT * FROM enderecos WHERE id_endereco = ?";
        Endereco endereco = null; // Inicializa o endereço como null

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um novo objeto Endereco com os dados retornados
                    int idEndereco = rs.getInt("id_endereco");
                    String logradouro = rs.getString("logradouro");
                    String numeroLogradouro = rs.getString("nr_logradouro");
                    String complemento = rs.getString("complemento");
                    String bairro = rs.getString("bairro");
                    String cep = rs.getString("nr_cep");

                    endereco = new Endereco(idEndereco, logradouro, numeroLogradouro, complemento, bairro, cep);
                }
            }
        
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar endereço: " + e.getMessage());
        }
        return endereco; // Retorna o endereço encontrado ou null
    }
}
