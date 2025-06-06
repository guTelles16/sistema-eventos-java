package projeto_x.dao;

import projeto_x.model.Confirmacao_Presenca;
import projeto_x.model.Evento;
import projeto_x.model.Usuario;
import projeto_x.util.ConexaoBanco;
import projeto_x.util.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Confirmacao_PresencaDAO {
    private Connection conexao;
    private UsuarioDAO usuarioDAO;
    private EventoDAO eventoDAO;

    public Confirmacao_PresencaDAO(UsuarioDAO usuarioDAO, EventoDAO eventoDAO) throws PersistenceException {
        this.conexao = ConexaoBanco.conectar();
        this.usuarioDAO = usuarioDAO;
        this.eventoDAO = eventoDAO;
    }

    /**
     * Salva uma nova confirmação de presença no banco de dados (CREATE).
     * 
     * @param confirmacao A confirmação de presença a ser salva.
     * @throws PersistenceException Se ocorrer algum erro ao salvar a confirmação (falha no banco de dados).
     */
    public void salvar(Confirmacao_Presenca confirmacao) throws PersistenceException {
        String sql = "INSERT INTO confirmacoes_presenca (usuario_id, evento_id, dt_hr_confirmacao, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, confirmacao.getUsuario().getId());
            stmt.setInt(2, confirmacao.getEvento().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(confirmacao.getDataHoraConfirmacao()));
            stmt.setString(4, confirmacao.getStatus());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar confirmação de presença: " + e.getMessage());
        }
    }

    /**
     * Buscar a confirmação de presença pelo ID do usuário e ID do evento (READ).
     * 
     * @param usuarioID O ID do usuário (foreign key).
     * @param eventoID O ID do evento (foreign key).
     * @return A confirmação de presença encontrada se existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar a confirmação (falha no banco de dados).
     */
    public Confirmacao_Presenca buscarPorUsuarioEvento(int usuarioID, int eventoID) throws PersistenceException {
        String sql = "SELECT * FROM confirmacoes_presenca WHERE usuario_id = ? AND evento_id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define os IDs nos placeholders (?)
            stmt.setInt(1, usuarioID);
            stmt.setInt(2, eventoID);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (!rs.next()) {
                    throw new PersistenceException("Confirmação de presença não encontrada para o ID do usuário: " + usuarioID + " e ID do evento: " + eventoID);
                }

                // Cria um novo objeto Confirmacao_Presenca com os dados retornados
                Usuario usuario = usuarioDAO.buscarPorID(rs.getInt("usuario_id"));
                Evento evento = eventoDAO.buscarPorID(rs.getInt("evento_id"));
                LocalDateTime dataHoraConfirmacao = rs.getTimestamp("dt_hr_confirmacao").toLocalDateTime();
                String status = rs.getString("status");

                return new Confirmacao_Presenca(usuario, evento, dataHoraConfirmacao, status);
            }
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar confirmação de presença: " + e.getMessage());
        }
    }

    /**
     * Conta o número de confirmações de presença para um evento específico (READ).
     * 
     * @param eventoID O ID do evento (foreign key).
     * @return O número de confirmações de presença para o evento.
     * @throws PersistenceException Se ocorrer algum erro ao contar as confirmações (falha no banco de dados).
     */
    public int contarConfirmacoesPorEvento(int eventoID) throws PersistenceException {
        String sql = "SELECT COUNT(*) AS total FROM confirmacoes_presenca WHERE evento_id = ?";
        int total = 0; // Inicializa o total como 0
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Define o ID do evento no placeholder (?)
            stmt.setInt(1, eventoID);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new PersistenceException("Nenhuma confirmação de presença encontrada para o ID do evento: " + eventoID);
                }

                total = rs.getInt("qt_total_confirmacoes");
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao contar confirmações de presença: " + e.getMessage());
        }
        return total; // Retorna o total de confirmações
    }
}
