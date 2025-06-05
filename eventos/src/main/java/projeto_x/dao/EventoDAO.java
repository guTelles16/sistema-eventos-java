package projeto_x.dao;

import projeto_x.model.Evento;
import projeto_x.util.ConexaoBanco;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Date;


public class EventoDAO {
    private Connection conexao;

    public EventoDAO() throws Exception {
        this.conexao = ConexaoBanco.conectar();
    }

    /**
     * Salva um novo evento no banco de dados (CREATE).
     * 
     * @param evento O evento a ser salvo.
     * @throws Exception Se ocorrer algum erro ao salvar o evento (falha no banco de dados).
     */
    public void salvar(Evento evento) throws Exception {
        String sql = "INSERT INTO eventos (nome, data, local) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, evento.getNome());
            stmt.setDate(2, evento.getDataHora());
            stmt.setString(3, evento.getLocal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar evento: " + e.getMessage());
        }
    }

    // Outros métodos como buscar, atualizar e deletar podem ser implementados aqui
    /**
     * Busca um evento pelo ID no banco de dados (READ).
     * 
     * @param id O ID do evento a ser buscado.
     * @return O evento encontrado se o ID existir, null caso contrário.
     * @throws Exception Se ocorrer algum erro ao buscar o evento (falha no banco de dados).
     */
    public Evento buscarPorId(int id) throws Exception {
        String sql = "SELECT * FROM eventos WHERE id_evento = ?";
        Evento evento = null; // Inicializa o evento como null
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    // Cria um novo objeto Evento com os dados retornados
                    String nome = rs.getString("nome");
                    LocalDateTime data = rs.getTimestamp("data").toLocalDateTime();
                    String local = rs.getString("local");
                    
                    evento = new Evento(id, nome, data, local);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar evento: " + e.getMessage());
        }
        
        return evento; // Retorna o evento encontrado ou null   

    
}
}
