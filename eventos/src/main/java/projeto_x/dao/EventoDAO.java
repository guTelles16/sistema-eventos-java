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
        String sql = "INSERT INTO eventos (nm_evento, endereco_id, categoria_id, usuario_id, dt_hr_evento, ds_evento, qt_limite_vagas_evento, latitude_evento, longitude_evento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
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

            if (evento.getCriador() != null && evento.getCategoria().getID() > 0) {
                stmt.setInt(4, evento.getCriador().getID());
            } else {
                throw new PersistenceException("Criador do evento é obrigatório e precisa ter um ID válido!");
            }

            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataHora()));
            stmt.setString(6, evento.getDescricao());
            stmt.setInt(7, evento.getCapacidade());
            stmt.setDouble(8, evento.getLatitude());
            stmt.setDouble(9, evento.getLongitude());
            
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
        
        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            
            // Define o ID no placeholder (?)
            stmt.setInt(1, id);

            // Executa a consulta e obtém o resultado
            try (ResultSet rs = stmt.executeQuery()) {
                // Verifica se há resultados
                if (rs.next()) {
                    return construirEventoDoResultSet(rs);
                } else {
                    throw new PersistenceException("Evento não encontrado com o ID: " + id);
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
        String sql = "SELECT* FROM eventos";

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
    * @param idCriador ID do usuário criador.
    * @return Lista de eventos criados pelo criador.
    * @throws PersistenceException Se ocorrer algum erro ao listar todos os eventos de um ID de criador (falha no banco de dados).
    */
    public List<Evento> buscarEventosPorCriador(int idCriador) throws PersistenceException {
        List<Evento> eventosCriados = new ArrayList<>();
        String sql = "SELECT * FROM eventos WHERE usuario_criador_id = ?";

        try (Connection conexao = ConexaoBanco.conectar();
            PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCriador);
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
     * Método auxiliar para evitar duplicação de código ao construir o objeto Evento.
     * 
     * @param rs ResultSet contendo os dados do evento.
     * @return O objeto evento construído a partir dos dados do ResultSet.
     * @throws PersistenceException Se ocorrer algum erro ao buscar os dados do evento (falha no banco de dados).
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
        Endereco enderecoLocalEvento = (enderecoID > 0) ? enderecoDAO.buscarPorID(enderecoID) : null;
        if (enderecoLocalEvento == null) {
            throw new PersistenceException("ID do endereço do evento inválido ou inexistente!");
        }

        int categoriaID = rs.getInt("categoria_id");
        Categoria categoriaEvento = (categoriaID > 0) ? categoriaDAO.buscarPorID(categoriaID) : null;
        if (categoriaEvento == null) {
            throw new PersistenceException("ID da categoria do evento inválido ou inexistente!");
        }

        int criadorID = rs.getInt("usuario_criador_id");
        Usuario criador = (criadorID > 0) ? usuarioDAO.buscarPorID(criadorID) : null;
        if (criador == null) {
            throw new PersistenceException("ID do criador do evento inválido ou inexistente!");
        }

        return new Evento(
            idEvento, nomeEvento, enderecoLocalEvento, categoriaEvento, dataHoraEvento,
            descricaoEvento, capacidadeEvento,
            latitudeEvento != null ? latitudeEvento : 0.0,
            longitudeEvento != null ? longitudeEvento : 0.0,
            criador
        );
    }
    //Atualizar
    public boolean atualizar(Evento evento) throws PersistenceException {
    String sql = "UPDATE usuarios SET nm_evento, endereco_id, categoria_id, usuario_id, dt_hr_evento, ds_evento, qt_limite_vagas_evento, latitude_evento, longitude_evento WHERE id_evento = ?";
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        // Defina os parâmetros de acordo com as colunas que você quer atualizar
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

            if (evento.getCriador() != null && evento.getCategoria().getID() > 0) {
                stmt.setInt(4, evento.getCriador().getID());
            } else {
                throw new PersistenceException("Criador do evento é obrigatório e precisa ter um ID válido!");
            }

            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataHora()));
            stmt.setString(6, evento.getDescricao());
            stmt.setInt(7, evento.getCapacidade());
            stmt.setDouble(8, evento.getLatitude());
            stmt.setDouble(9, evento.getLongitude());
            
            stmt.executeUpdate();
    
        if (evento.getEndereco() != null && evento.getEndereco().getID() > 0) {
            stmt.setInt(4, evento.getEndereco().getID());
        } else {
            stmt.setNull(4, java.sql.Types.INTEGER); // Ou lance uma exceção se for obrigatório
        }
        
        // Para Double (Latitude/Longitude), trate null
        if (evento.getLatitude() != null) {
            stmt.setDouble(5, evento.getLatitude());
        } else {
            stmt.setNull(5, java.sql.Types.DOUBLE);
        }
        if (evento.getLongitude() != null) {
            stmt.setDouble(6, evento.getLongitude());
        } else {
            stmt.setNull(6, java.sql.Types.DOUBLE);
        }

        // Condição WHERE: ID do usuário a ser atualizado
        stmt.setInt(7, evento.getID()); // Assumindo que seu POJO Usuario tem um getID()
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a atualização foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao atualizar evento: " + e.getMessage(), e);
    }
}
    // Deletar
    public boolean deletar(int id) throws PersistenceException {
    String sql = "DELETE FROM usuarios WHERE id_evento = ?"; // Adapte a tabela e a coluna ID
    
    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement stmt = conexao.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        
        int linhasAfetadas = stmt.executeUpdate();
        return linhasAfetadas > 0; // Retorna true se a deleção foi bem-sucedida
        
    } catch (SQLException e) {
        throw new PersistenceException("Erro ao deletar evento: " + e.getMessage(), e);
    }
}
}
