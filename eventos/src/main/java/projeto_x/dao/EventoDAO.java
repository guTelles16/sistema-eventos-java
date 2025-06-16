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
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    private UsuarioDAO usuarioDAO;
    private EnderecoDAO enderecoDAO;
    private CategoriaDAO categoriaDAO;
    
    public EventoDAO(UsuarioDAO usuarioDAO, EnderecoDAO enderecoDAO, CategoriaDAO categoriaDAO) {
        this.usuarioDAO = usuarioDAO;
        this.enderecoDAO = enderecoDAO;
        this.categoriaDAO = categoriaDAO;
    }
    /**
     * Salva um novo evento no banco de dados (CREATE).
     * 
     * @param evento O evento a ser salvo.
     * @return O ID do evento recém-criado.
     * @throws PersistenceException Se ocorrer algum erro ao salvar o evento (falha no banco de dados).
     */
    public int salvar(Evento evento) throws PersistenceException {
        String sql = "INSERT INTO eventos (nm_evento, endereco_id, categoria_id, usuario_criador_id, dt_hr_evento, ds_evento, qt_limite_vagas_evento, latitude_evento, longitude_evento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, evento.getNome());

            if (evento.getEndereco() != null && evento.getEndereco().getID() > 0) {
                stmt.setInt(2, evento.getEndereco().getID());
            } else {
                throw new PersistenceException("Endereço do evento é obrigatório e precisa ter um ID válido!");
            }

            if (evento.getCategoria() != null && evento.getCategoria().getID() > 0) {
                stmt.setInt(3, evento.getCategoria().getID());
            } else {
                throw new PersistenceException("Categoria do evento é obrigatória e precisa ter um ID válido!");
            }

            if (evento.getCriador() != null && evento.getCriador().getID() > 0) {
                stmt.setInt(4, evento.getCriador().getID());
            } else {
                throw new PersistenceException("Criador do evento é obrigatório e precisa ter um ID válido!");
            }

            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataHora()));
            stmt.setString(6, evento.getDescricao());
            stmt.setInt(7, evento.getCapacidade());
             
            if (evento.getLatitude() != null) {
                stmt.setDouble(5, evento.getLatitude());
            } else {
                stmt.setNull(5, Types.DOUBLE);
            }

            if (evento.getLongitude() != null) {
                stmt.setDouble(6, evento.getLongitude());
            } else {
                stmt.setNull(6, Types.DOUBLE);
            }
            
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
     * @param idEvento O ID do evento a ser buscado.
     * @return O evento encontrado se o ID existir, null caso contrário.
     * @throws PersistenceException Se ocorrer algum erro ao buscar o evento (falha no banco de dados).
     */
    public Evento buscarPorID(int idEvento) throws PersistenceException {
        String sql = "SELECT * FROM eventos WHERE id_evento = ?";
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
            // Define o ID no placeholder (?)
            stmt.setInt(1, idEvento);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    return construirEventoDoResultSet(rs);
                } else {
                    throw new PersistenceException("Evento não encontrado com o ID: " + idEvento);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar evento por ID: " + e.getMessage());
            throw new PersistenceException("Erro ao buscar evento: " + e.getMessage());
        }
    }

    /**
     * Busca todos os eventos cadastrados no banco de dados (READ).
     * 
     * @return
     * @throws PersistenceException Se ocorrer algum erro ao buscar todos os eventos (falha no banco de dados).
     */
    public List<Evento> listarTodosEventos() throws PersistenceException {
        String sql = "SELECT * FROM eventos";

        List<Evento> eventos = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Evento evento = construirEventoDoResultSet(rs);
                eventos.add(evento); // Adiciona a lista
            }

        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar eventos: " + e.getMessage());
        }
        return eventos;
    }

    /**
    * Busca os eventos criados por um usuário específico.
    *
    * @param criadorID ID do usuário criador.
    * @return Lista de eventos criados pelo criador.
    * @throws PersistenceException Se ocorrer algum erro ao listar todos os eventos de um ID de criador (falha no banco de dados).
    */
    public List<Evento> buscarEventosPorCriador(int criadorID) throws PersistenceException {
        List<Evento> eventosCriados = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE usuario_criador_id = ?";

        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, criadorID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento evento = construirEventoDoResultSet(rs);
                    eventosCriados.add(evento);
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar eventos criados pelo usuário: " + e.getMessage());
        }
        return eventosCriados;
    }

    /**
     * Atualiza os dados do objeto Evento, a partir do ID (UPDATE).
     * 
     * @param evento O objeto Evento.
     * @return true se a atualização for bem sucedida.
     * @throws PersistenceException Se ocorrer algum erro ao atualizar os dados (falha no banco de dados).
    */
    public boolean atualizar(Evento evento) throws PersistenceException {
        String sql = "UPDATE eventos SET nm_evento = ?, endereco_id = ?, categoria_id = ?, dt_hr_evento = ?, " +
                "ds_evento = ?, qt_limite_vagas_evento = ?, latitude_evento = ?, longitude_evento = ? " +
                "WHERE id_evento = ?";
    
        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
            stmt.setString(1, evento.getNome());

            if (evento.getEndereco() != null && evento.getEndereco().getID() > 0) {
                stmt.setInt(2, evento.getEndereco().getID());
            } else {
                throw new PersistenceException("Endereço do evento é obrigatório e precisa ter um ID válido!");
            }

            if (evento.getCategoria() != null && evento.getCategoria().getID() > 0) {
                stmt.setInt(3, evento.getCategoria().getID());
            } else {
                throw new PersistenceException("Categoria do evento é obrigatória e precisa ter um ID válido!");
            }

            stmt.setTimestamp(4, Timestamp.valueOf(evento.getDataHora()));
            stmt.setString(5, evento.getDescricao());
            stmt.setInt(6, evento.getCapacidade());
            stmt.setDouble(7, evento.getLatitude());
            stmt.setDouble(8, evento.getLongitude());

            // Parâmentro do WHERE
            stmt.setInt(9, evento.getID());
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
            
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao atualizar evento: " + e.getMessage());
        }
    }
    
    /**
     * Deleta os dados do objeto Evento, a partir do ID (DELETE).
     * 
     * @param idEvento ID do evento. 
     * @return true se a deleção for bem-sucedida.
     * @throws PersistenceException Se ocorrer algum erro ao deletar os dados (falha no banco de dados).
     */
    public boolean deletar(int idEvento) throws PersistenceException {
        String sql = "DELETE FROM eventos WHERE id_evento = ?";     
        
        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
            stmt.setInt(1, idEvento);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se a deleção foi bem-sucedida
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao deletar evento: " + e.getMessage());
        }
    }

    /**
     * Deleta todos os eventos criados por um determinado usuário.
     * 
     * @param idCriador O ID do usuário criador.
     * @return true se a operação foi bem-sucedida (pode ser true mesmo se nenhum evento foi deletado), false em caso de falha.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public boolean deletarEventosPorCriador(int idCriador) throws PersistenceException {
        // SQL para deletar eventos onde o 'criador_id' corresponde
        String sql = "DELETE FROM eventos WHERE usuario_criador_id = ?";
        
        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, idCriador);
            
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; 
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao deletar eventos criados pelo usuário: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para evitar duplicação de código ao construir o objeto Evento.
     * 
     * @param rs ResultSet contendo os dados do evento.
     * @return O objeto Evento construído a partir dos dados do ResultSet.
     * @throws SQLException Se ocorrer algum erro ao buscar os dados do evento (falha no banco de dados).
     * @throws PersistenceException Se ocorrer alguma outra exceção persistente.
     */
    private Evento construirEventoDoResultSet(ResultSet rs) throws SQLException, PersistenceException {
        Integer idEvento = (Integer) rs.getObject("id_evento");
        String nomeEvento = rs.getString("nm_evento");
        LocalDateTime dataHoraEvento = rs.getTimestamp("dt_hr_evento").toLocalDateTime();
        String descricaoEvento = rs.getString("ds_evento");
        Integer capacidadeEvento = (Integer) rs.getObject("qt_limite_vagas_evento");
        Double latitudeEvento = (Double) rs.getObject("latitude_evento");
        Double longitudeEvento = (Double) rs.getObject("longitude_evento");

        int enderecoID = rs.getInt("endereco_id");
        Endereco enderecoLocalEvento = null;
        if (enderecoID > 0) {
            enderecoLocalEvento = enderecoDAO.buscarPorID(enderecoID);
            // Lança a exceção apenas se o ID existia, mas o endereço não foi encontrado.
            if (enderecoLocalEvento == null) {
                throw new PersistenceException("Inconsistência de dados: Endereço com ID " + enderecoID + " não encontrado!");
            }
        }

        int categoriaID = rs.getInt("categoria_id");
        Categoria categoriaEvento = null;
        if (categoriaID > 0) {
            categoriaEvento = categoriaDAO.buscarPorID(categoriaID);
            if (categoriaEvento == null) {
                throw new PersistenceException("Inconsistência de dados: Categoria com ID " + categoriaID + " não encontrado!");
            }
        }

        int criadorID = rs.getInt("usuario_criador_id");
        Usuario criador = null;
        if (criadorID > 0) {
            criador = usuarioDAO.buscarPorID(criadorID);
            if (criador == null) {
                throw new PersistenceException("Inconsistência de dados: Criador do evento com ID " + criadorID + " não encontrado!");
            }
        }

        return new Evento(
            idEvento, nomeEvento, enderecoLocalEvento, categoriaEvento, dataHoraEvento,
            descricaoEvento, capacidadeEvento, latitudeEvento, longitudeEvento, criador
        );
    }
}
