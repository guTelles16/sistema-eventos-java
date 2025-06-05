package projeto_x.dao;

import projeto_x.model.Confirmacao_Presenca;
import projeto_x.model.Evento;
import projeto_x.model.Usuario;
import projeto_x.util.ConexaoBanco;
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

    public Confirmacao_PresencaDAO(UsuarioDAO usuarioDAO, EventoDAO eventoDAO) throws Exception {
        Connection conexao = ConexaoBanco.conectar();
        this.conexao = conexao;
        this.usuarioDAO = usuarioDAO;
        this.eventoDAO = eventoDAO;
    }

    /**
     * Salva uma nova confirmação de presença no banco de dados (CREATE).
     * 
     * @param confirmacao A confirmação de presença a ser salva.
     * @throws Exception Se ocorrer algum erro ao salvar a confirmação (falha no banco de dados).
     */
    public void salvar(Confirmacao_Presenca confirmacao) throws Exception {
        String sql = "INSERT INTO confirmacoes_presenca (id_usuario, id_evento, dt_hr_confirmacao, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, confirmacao.getUsuario().getId());
            stmt.setInt(2, confirmacao.getEvento().getId());

            // Converte LocalDateTime para Timestamp
            LocalDateTime dataHoraConfirmacao = confirmacao.getDataHoraConfirmacao();
            Timestamp timestamp = Timestamp.valueOf(dataHoraConfirmacao);
            stmt.setTimestamp(3, timestamp);

            stmt.setString(4, confirmacao.getStatus());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar confirmação de presença: " + e.getMessage());
        }
    }

    /**
     * Buscar a confirmação de presença pelo ID do usuário e ID do evento (READ).
     * 
     * @param idUsuario O ID do usuário.
     * @param idEvento O ID do evento.
     * @return A confirmação de presença encontrada se existir, null caso contrário.
     * @throws Exception Se ocorrer algum erro ao buscar a confirmação (falha no banco de dados).
     */
    public Confirmacao_Presenca buscarPorUsuarioEvento(int idUsuario, int idEvento) throws Exception {
        String sql = "SELECT * FROM confirmacoes_presenca WHERE id_usuario = ? AND id_evento = ?";
        Confirmacao_Presenca confirmacao = null; // Inicializa a confirmação como null
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define os IDs nos placeholders (?)
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idEvento);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um novo objeto Confirmacao_Presenca com os dados retornados
                    Usuario usuario = usuarioDAO.buscarPorId(rs.getInt("id_usuario"));
                    Evento evento = eventoDAO.buscarPorId(rs.getInt("id_evento"));
                    LocalDateTime dataHoraConfirmacao = rs.getTimestamp("dt_hr_confirmacao").toLocalDateTime();
                    String status = rs.getString("status");

                    confirmacao = new Confirmacao_Presenca(usuario, evento, dataHoraConfirmacao, status);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar confirmação de presença: " + e.getMessage());
        }
        return confirmacao;
    }

    /**
     * Conta o número de confirmações de presença para um evento específico (READ).
     * 
     * @param idEvento O ID do evento.
     * @return O número de confirmações de presença para o evento.
     * @throws Exception Se ocorrer algum erro ao contar as confirmações (falha no banco de dados).
     */
    public int contarConfirmacoesPorEvento(int idEvento) throws Exception {
        String sql = "SELECT COUNT(*) AS total FROM confirmacoes_presenca WHERE id_evento = ?";
        int total = 0; // Inicializa o total como 0
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define o ID do evento no placeholder (?)
            stmt.setInt(1, idEvento);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao contar confirmações de presença: " + e.getMessage());
        }
        return total;
    }
}
