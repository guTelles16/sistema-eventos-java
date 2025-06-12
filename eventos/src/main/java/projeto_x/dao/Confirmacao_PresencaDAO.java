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
                stmt.setInt(1, confirmacao.getUsuario().getID());
            }

            if (confirmacao.getEvento() != null && confirmacao.getEvento().getID() > 0) {
                stmt.setInt(2, confirmacao.getEvento().getID());
            }

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
        String sql = "SELECT dt_hr_confirmacao, st_confirmacao_evento AS status_confirmacao, qt_total_confirmacoes AS total_confirmacoes " +
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
                    int totalConfirmacoes = rs.getInt("total_confirmacoes");

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

                    return new Confirmacao_Presenca(usuarioInscrito, eventoConfirmado, dataHoraConfirmacao, statusConfirmacao, totalConfirmacoes);
                } else {
                    throw new PersistenceException("Confirmação de presença não encontrada para o usuário com ID: " + usuarioID + " e evento com ID: " + eventoID);
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
            PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
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
            System.err.println("Erro SQL ao contar confirmações de presença: " + e.getMessage());
            throw new PersistenceException("Erro ao contar confirmações de presença: " + e.getMessage());
        }
        return total; // Retorna o total de confirmações
    }
    //Atualizar
    public boolean atualizar(Confirmacao_Presenca confimacao) throws PersistenceException {
    String sql = "UPDATE usuarios SET usuario_id, evento_id, dt_hr_confirmacao, status WHERE id_evento = ? AND id_usuario = ?";
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        // Defina os parâmetros de acordo com as colunas que você quer atualizar
        stmt.setString(1, confirmacao.getStatus());
        stmt.setTimestamp(2, Timestamp.valueOf(confirmacao.getDataHoraConfirmacao()));
        stmt.setString(3, confirmacao.getDataHora());
        stmt.setInt(4, confirmacao.getUsuario().getID());
        stmt.setInt(5, confirmacao.getEvento().getID());
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao atualizar presença: " + e.getMessage(), e);
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
        throw new PersistenceException("Erro ao deletar presença: " + e.getMessage(), e);
    }
}
}
