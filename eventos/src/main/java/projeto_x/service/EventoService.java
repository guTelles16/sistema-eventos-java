package projeto_x.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projeto_x.dao.EventoDAO;
import projeto_x.dao.UsuarioDAO;
import projeto_x.dao.EnderecoDAO;
import projeto_x.dao.CategoriaDAO;
import projeto_x.dao.Confirmacao_PresencaDAO;
import projeto_x.model.Categoria;
import projeto_x.model.Endereco;
import projeto_x.model.Evento;
import projeto_x.model.Usuario;
import projeto_x.util.PersistenceException;
import projeto_x.util.ConexaoBanco;
import projeto_x.util.GeolocalizacaoUtil;


public class EventoService {
    private EventoDAO eventoDAO;
    private UsuarioDAO usuarioDAO;
    private EnderecoDAO enderecoDAO;
    private CategoriaDAO categoriaDAO;
    private Confirmacao_PresencaDAO confirmacaoPresencaDAO;

    public EventoService(EventoDAO eventoDAO, UsuarioDAO usuarioDAO, EnderecoDAO enderecoDAO, CategoriaDAO categoriaDAO, Confirmacao_PresencaDAO confirmacao_PresencaDAO) {
        this.eventoDAO = eventoDAO;
        this.usuarioDAO = usuarioDAO;
        this.enderecoDAO = enderecoDAO;
        this.categoriaDAO = categoriaDAO;
        this.confirmacaoPresencaDAO = confirmacao_PresencaDAO;
    }

    /**
     * Realiza o cadastro de um novo evento, a partir do nome, endereço, categoria, descrição, data/hora, capacidade e ID do criador.
     *
     * @param nome Nome do evento.
     * @param endereco Objeto Endereco já preenchido e persistido.
     * @param categoriaID ID da categoria (foreign key).
     * @param descricao Descrição do evento.
     * @param dataHora Data e hora do evento.
     * @param capacidade Limite de vagas do evento.
     * @param latitudeEvento Latitude do evento.
     * @param longitudeEvento Longitude do evento.
     * @param criadorID ID do usuário criador.
     * @throws PersistenceException Se ocorrer algum erro durante o cadastro (falha no banco de dados).
     */
    public boolean cadastrarEvento(String nome, Endereco endereco, int categoriaID, String descricao, LocalDateTime dataHora, Integer capacidade, Double latitudeEvento, Double longitudeEvento, int criadorID)
            throws IllegalArgumentException, PersistenceException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do evento não pode ser vazio!");
        }

        // Validação do Objeto Endereco
        if (endereco == null || endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty() ||
            endereco.getNumeroLogradouro() == null || endereco.getNumeroLogradouro().trim().isEmpty() ||
            endereco.getComplemento() == null || endereco.getComplemento().trim().isEmpty() ||
            endereco.getBairro() == null || endereco.getBairro().trim().isEmpty() ||
            endereco.getCEP() == null || endereco.getCEP().trim().isEmpty()) {
            throw new IllegalArgumentException("Dados de endereço incompletos ou inválidos!");
        }

        Categoria categoria = categoriaDAO.buscarPorID(categoriaID);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria inválida!");
        }

        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A Descrição não pode ser vazia!");
        }

        if (capacidade <= 0) {
            throw new IllegalArgumentException("A Capacidade não pode ser menor ou igual a zero!");
        }

        Usuario criador = usuarioDAO.buscarPorID(criadorID);
        if (criador == null) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (!verificarDataHora(dataHora)) {
            throw new IllegalArgumentException("A data e hora do evento é inválida");
        }

        if (latitudeEvento != null && (latitudeEvento < -90.0 || latitudeEvento > 90.0)) {
            throw new IllegalArgumentException("Latitude inválida: deve estar entre -90 e +180!");
        }

        if (longitudeEvento != null && (longitudeEvento < -180.0 || longitudeEvento > 180.0)) {
            throw new IllegalArgumentException("Longitude inválida: deve estar entre -180 e +180!");
        }

        // Cria o objeto Evento com as informações fornecidas
        Evento novoEvento = new Evento(
            null, nome, endereco, categoria, dataHora, descricao, capacidade,
            latitudeEvento != null ? latitudeEvento : 0.0,
            longitudeEvento != null ? longitudeEvento : 0.0,
            criador
        );

        // Salva o evento no banco de dados
        try {
            eventoDAO.salvar(novoEvento);
            return true;
        } catch (PersistenceException e) {
            throw new PersistenceException("Erro ao cadastrar o evento: " + e.getMessage());
        }
    }

    // Método para verificar se a data do evento é valida
    public boolean verificarDataHora(LocalDateTime dataHora) {
        return dataHora != null && dataHora.isAfter(LocalDateTime.now());
    }

    // Método para cadastrar uma categoria
    public boolean cadastrarCategoria(String nome) throws PersistenceException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria inválido!");
        }

        // Cria o objeto Categoria com as informações fornecidas
        Categoria novaCategoria = new Categoria(null, nome);

        // Salva a categoria no banco de dados
        try {
            categoriaDAO.salvar(novaCategoria);
            return true;
        } catch (PersistenceException e) {
            throw new PersistenceException("Erro ao cadastrar a categoria: " + e.getMessage());
        }
    }

    public List<Evento> buscarEventosProximos(Double latUsuario, Double lonUsuario, Double raioMaximoKm) throws PersistenceException {
        List<Evento> todosEventos = eventoDAO.listarTodosEventos();
        List<Evento> eventosProximosOrdenados = new ArrayList<>();

        // Map para armazenar o evento e sua distância para ordenação
        Map<Evento, Double> eventosComDistancia = new HashMap<>();

        for (Evento evento : todosEventos) {
            Double latEvento = evento.getLatitude();
            Double lonEvento = evento.getLongitude();

            Double distancia = GeolocalizacaoUtil.calcularDistanciaHaversine(latUsuario, lonUsuario, latEvento, lonEvento);

            if (distancia <= raioMaximoKm) {
                eventosComDistancia.put(evento, distancia);
            }
        }

        // Ordenar a lista
        List<Map.Entry<Evento, Double>> listaParaOrdenar = new ArrayList<>(eventosComDistancia.entrySet());

        listaParaOrdenar.sort(Comparator.comparing(Map.Entry::getValue)); // Ordenar pela menor distância

        for (Map.Entry<Evento, Double> entry : listaParaOrdenar) {
            eventosProximosOrdenados.add(entry.getKey());
        }

        return eventosProximosOrdenados;
    }

    /**
     * Lista os eventos criados por um usuário específico.
     * 
     * @param idUsuarioCriador O ID do usuário criador.
     * @return Lista de eventos criados.
     * @throws PersistenceException Se ocorrer algum de erro ao listar todos os eventos (falha no banco de dados).
     */
    public List<Evento> listarEventosCriadosPorUsuario(int idUsuarioCriador) throws PersistenceException {
        List<Evento> eventosCriados = eventoDAO.listarTodosEventos();
        String sql = "SELECT * FROM Evento WHERE usuario_criador_id = ?";

        try (Connection connection = ConexaoBanco.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuarioCriador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento evento = construirEventoDoResultSet(rs);
                    eventosCriados.add(evento);
                }
            }
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar eventos criados pelo usuário: " + e.getMessage());
        }
        return eventosCriados;
    }

    /**
     * Lista os eventos nos quais um usuário específico confirmou presença.
     * 
     * @param idUsuarioInscrito o ID do usuário inscrito.
     * @return Lista de eventos nos quais o usuário confirmou presença.
     * @throws PersistenceException Se ocorrer algum erro ao listar os eventos (falha no banco de dados).
     */
    public List<Evento> listarEventosInscritosPorUsuario(int idUsuarioInscrito) throws PersistenceException {
        List<Evento> eventosInscritos = new ArrayList<>();
        String sql = "SELECT e.* FROM Evento e " +
                     "JOIN Confirmacao_Presenca cp ON e.id_evento = cp.evento_id " +
                     "WHERE cp.usuario_id = ?";

        try (Connection connection = ConexaoBanco.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuarioInscrito);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento evento = construirEventoDoResultSet(rs);
                    eventosInscritos.add(evento);
                }
            }    
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar eventos inscritos pelo usuário: " + e.getMessage());
        
        } catch (PersistenceException e) {
            throw new PersistenceException("Erro ao buscar dados do evento: " + e.getMessage());
        }
        return eventosInscritos;
    }

    /**
     * Lista todos os eventos cadastrados no banco de dados.
     * 
     * @return Lista de objetos Eventos
     * @throws PersistenceException Se ocorrer algum erro ao listar todos os eventos (falha no banco de dados).
     */
    public List<Evento> listarTodos() throws PersistenceException {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos";

        try (Connection connection = ConexaoBanco.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
                
            while (rs.next()) {
                Evento evento = construirEventoDoResultSet(rs);
                eventos.add(evento);
            }
        
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao listar eventos inscritos pelo usuário: " + e.getMessage());
        } 
        return eventos;
    }

    /**
     * Inscreve um usuário em um evento.
     * @param idUsuario O ID do usuário que deseja se inscrever (int primitivo é o mais comum para IDs).
     * @param idEvento O ID do evento no qual o usuário deseja se inscrever.
     * @return true se a inscrição foi bem-sucedida, false caso contrário (ex: evento não existe, vagas esgotadas, já inscrito).
     * @throws PersistenceException Se ocorrer um erro de persistência.
     * @throws IllegalArgumentException Se os IDs forem inválidos.
     */
    public boolean inscreverUsuarioEmEvento(int idUsuario, int idEvento) throws PersistenceException, IllegalArgumentException {
        if (idUsuario <= 0 || idEvento <= 0) {
            throw new IllegalArgumentException("IDs de usuário ou evento inválidos!");
        }

        Evento evento = eventoDAO.buscarPorID(idEvento);
        if (evento == null) {
            System.out.println("Evento não encontrado!");
            return false;
        }

        try {
            // Verificar se o usuário já está inscrito
            if (confirmacaoPresencaDAO.verificarInscricaoExistente(idUsuario, idEvento)) {
                System.out.println("Você já está inscrito neste evento!");
                return false;
            }

            // Verificar se há vagas disponíveis
            int vagasOcupadas = confirmacaoPresencaDAO.contarConfirmacoesPorEvento(idEvento);
            if (vagasOcupadas >= evento.getCapacidade()) {
                System.out.println("Desculpe, as vagas para este evento esgotaram!");
                return false;
            }

            // Salvar a confirmação de presença
            return confirmacaoPresencaDAO.salvarConfirmacao(idUsuario, idEvento);
        } catch (PersistenceException e) {
            throw e;
        }
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
        if (rs.wasNull()) latitudeEvento = null; // Se for NULL no BD, será null no objeto
        Double longitudeEvento = (Double) rs.getObject("longitude_evento");
        if (rs.wasNull()) longitudeEvento = null;

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
}
