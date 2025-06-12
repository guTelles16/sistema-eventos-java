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
    private UsuarioDAO usuarioDAO;
    private EventoDAO eventoDAO;

    public Confirmacao_PresencaDAO(UsuarioDAO usuarioDAO, EventoDAO eventoDAO) throws PersistenceException {
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
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);

            if (confirmacao.getUsuario() != null && confirmacao.getUsuario().getID() > 0) {            
                throw new PersistenceException("Usuário inválido ou ID do usuário não definido para salvar confirmação!");    
            }

            if (confirmacao.getEvento() != null && confirmacao.getEvento().getID() > 0) {
                throw new PersistenceException("Evento inválido ou ID do evento não definido para salvar confirmação!");
            }

            stmt.setInt(1, confirmacao.getUsuario().getID());
            stmt.setInt(2, confirmacao.getEvento().getID());
            stmt.setTimestamp(3, Timestamp.valueOf(confirmacao.getDataHoraConfirmacao()));
            stmt.setString(4, confirmacao.getStatus());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar confirmação de presença: " + e.getMessage());
        }
    }

    /**
     * Salva uma nova confirmação de presença no banco de dados.
     * 
     * @param idUsuario ID do usuário.
     * @param idEvento ID do evento.
     * @return true se a confirmação foi salva com sucesso, false caso contrário.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public boolean salvarConfirmacao(int idUsuario, int idEvento) throws PersistenceException {
        String sql = "INSERT INTO confirmacoes_presenca (usuario_id, evento_id, dt_hr_confirmacao) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idEvento);
            stmt.setObject(3, LocalDateTime.now());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao salvar confirmação de presença: " + e.getMessage());
        }
    }

    /**
     * Verifica se um usuário já está inscrito em um determinado evento.
     * 
     * @param idUsuario ID do usuário.
     * @param idEvento ID do evento.
     * @return true se o usuário já está inscrito, false caso contrário.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public boolean verificarInscricaoExistente(int idUsuario, int idEvento) throws PersistenceException {
        String sql = "SELECT COUNT(*) FROM confirmacoes_presenca WHERE usuario_id = ? AND evento_id = ?";
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Se a contagem for maior que 0, a inscrição existe
                }
            }
            return false;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao verificar inscrição existente: " + e.getMessage());
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
        String sql = "SELECT dt_hr_confirmacao, st_confirmacao_event " +
                    "FROM confirmacoes_presenca " +
                    "WHERE usuario_id = ? AND evento_id = ?";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
            // Define os IDs nos placeholders (?)
            stmt.setInt(1, usuarioID);
            stmt.setInt(2, eventoID);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Carregar os dados da própria Confirmacao_Presenca
                    LocalDateTime dataHoraConfirmacao = rs.getTimestamp("dt_hr_confirmacao").toLocalDateTime();
                    String statusConfirmacao = rs.getString("status_confirmacao");

                    // Busca os objetos Usuario e Evento
                    Usuario usuarioInscrito = usuarioDAO.buscarPorID(usuarioID);
                    Evento eventoConfirmado = eventoDAO.buscarPorID(eventoID);

                    // Verifica se os objetos foram encontrados
                    if (usuarioInscrito == null) {
                        throw new PersistenceException("Usuário com ID " + usuarioID + " não encontrado para confirmação de presença!");
                    }

                    if (eventoConfirmado == null) {
                        throw new PersistenceException("Evento com ID " + eventoID + "não encontrado para confirmação de presença!");
                    }

                    int totalConfirmacoes = contarConfirmacoesPorEvento(eventoID);

                    return new Confirmacao_Presenca(usuarioInscrito, eventoConfirmado, dataHoraConfirmacao, statusConfirmacao, totalConfirmacoes);
                } else {
                    return null;
                }
            }
        
        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar confirmação de presença: " + e.getMessage());
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
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            // Define o ID do evento no placeholder (?)
            stmt.setInt(1, eventoID);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        
        } catch (SQLException e) {
            System.err.println("Erro SQL ao contar confirmações de presença: " + e.getMessage());
            throw new PersistenceException("Erro ao contar confirmações de presença: " + e.getMessage());
        }
        return total; // Retorna o total de confirmações
    }

    /**
     * Atualiza uma confirmação de presença existente no banco de dados (UPDATE).
     * É baseada nas chaves compostas usuario_id e evento_id.
     * 
     * @param confirmacao Objeto Confirmacao_Presenca.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao atualizar dados (falha no banco de dados).
     */
    public boolean atualizar(Confirmacao_Presenca confirmacao) throws PersistenceException {
        String sql = "UPDATE confirmacoes_presenca SET dt_hr_confirmacao = ?, status = ? WHERE usuario_id = ? AND evento_id = ?";
        
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            // Validação para garantir que os IDs são válidos antes de usar
            if (confirmacao.getUsuario() == null || confirmacao.getUsuario().getID() <= 0) {
                throw new PersistenceException("Usuário inválido ou ID do usuário não definido para atualização.");
            }

            if (confirmacao.getEvento() == null || confirmacao.getEvento().getID() <= 0) {
                throw new PersistenceException("Evento inválido ou ID do evento não definido para atualização.");
            }

            stmt.setTimestamp(1, Timestamp.valueOf(confirmacao.getDataHoraConfirmacao()));
            stmt.setString(2, confirmacao.getStatus());
            
            // Condições WHERE
            stmt.setInt(3, confirmacao.getUsuario().getID());
            stmt.setInt(4, confirmacao.getEvento().getID());
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar presença: " + e.getMessage());
        }
    }

    /**
     * Deleta uma confirmação de presença do banco de dados (DELETE).
     * É baseada nas chaves composta de usuario_id e evento_id.
     * 
     * @param usuarioID ID do usuário (foreign key).
     * @param eventoID ID do evento (foreign key). 
     * @return true se a deleção for bem-sucedida, false caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao deletar os dados (falha no banco de dados).
     */
    public boolean deletar(int usuarioID, int eventoID) throws PersistenceException {
        String sql = "DELETE FROM confirmacoes_presenca WHERE usuario_id = ? AND evento_id = ?";
        
        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioID);
            stmt.setInt(2, eventoID);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a deleção foi bem-sucedida
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao deletar presença: " + e.getMessage());
        }
    }

    /**
     * Deleta uma confirmação de presença do banco de dados (DELETE).
     * É baseada nas chave usuario_id.
     * 
     * @param usuarioID ID do usuário (foreign key).
     * @return true se a deleção for bem-sucedida, false caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao deletar os dados (falha no banco de dados).
     */
    public boolean deletarConfirmacoesPorUsuario(int idUsuario) throws SQLException, PersistenceException {
        String sql = "DELETE FROM confirmacoes_presenca WHERE usuario_id = ?";
        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0 || linhasAfetadas == 0;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao deletar confirmações de presença do usuário: " + e.getMessage());
        }
    }
}
