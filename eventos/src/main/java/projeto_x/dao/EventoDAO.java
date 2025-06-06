package projeto_x.dao;

import projeto_x.model.Evento;
import projeto_x.model.Usuario;
import projeto_x.model.Categoria;
import projeto_x.model.Endereco;
import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class EventoDAO {
    private Connection conexao;
    private EnderecoDAO enderecoDAO;
    private CategoriaDAO categoriaDAO;
    private UsuarioDAO usuarioDAO;

    public EventoDAO(EnderecoDAO enderecoDAO, CategoriaDAO categoriaDAO, UsuarioDAO usuarioDAO) throws PersistenceException {
        this.conexao = ConexaoBanco.conectar();
        this.enderecoDAO = enderecoDAO;
        this.categoriaDAO = categoriaDAO;
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Salva um novo evento no banco de dados (CREATE).
     * 
     * @param evento O evento a ser salvo.
     * @return O ID do evento recém-criado.
     * @throws PersistenceException Se ocorrer algum erro ao salvar o evento (falha no banco de dados).
     */
    public int salvar(Evento evento) throws PersistenceException {
        String sql = "INSERT INTO eventos (nm_evento, endereco_id, categoria_id, usuario_id, dt_hr_evento, ds_evento, qt_limite_vagas_evento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, evento.getNome());
            stmt.setInt(2, evento.getEndereco().getID());
            stmt.setInt(3, evento.getCategoria().getID());
            stmt.setInt(4, evento.getCriador().getId());
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataHora()));
            stmt.setString(6, evento.getDescricao());
            stmt.setInt(7, evento.getCapacidade());
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID gerado
                }
                throw new PersistenceException("Falha ao obter o ID do evento: nenhum ID foi gerado!");
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar evento: " + e.getMessage());
        }
    }

    /**
     * Busca um evento pelo ID no banco de dados (READ).
     * 
     * @param id O ID do evento a ser buscado.
     * @return O evento encontrado se o ID existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o evento (falha no banco de dados).
     */
    public Evento buscarPorID(int id) throws PersistenceException {
        String sql = "SELECT * FROM eventos WHERE id_evento = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (!rs.next()) {
                    throw new PersistenceException("Evento não encontrado com o ID: " + id);
                }

                // Cria um novo objeto Evento com os dados retornados
                int idEvento = rs.getInt("id_evento");
                String nome = rs.getString("nm_evento");
                Endereco endereco = enderecoDAO.buscarPorID(rs.getInt("endereco_id"));
                Timestamp dataHora = rs.getTimestamp("dt_hr_evento");
                String descricao = rs.getString("ds_evento");
                int capacidade = rs.getInt("qt_limite_vagas_evento");
                Categoria categoria = categoriaDAO.buscarPorID(rs.getInt("categoria_id"));
                Usuario criador = usuarioDAO.buscarPorID(rs.getInt("usuario_id"));

                return new Evento(idEvento, nome, endereco, dataHora.toLocalDateTime(), descricao, capacidade, categoria, criador);
            }

        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar evento: " + e.getMessage());
        }   
    }
}
