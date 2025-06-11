package projeto_x.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import projeto_x.dao.EventoDAO;
import projeto_x.model.Categoria;
import projeto_x.model.Endereco;
import projeto_x.model.Evento;
import projeto_x.util.PersistenceException;


public class EventoService {
    private EventoDAO eventoDAO;

    public EventoService(EventoDAO eventoDAO) {
        this.eventoDAO = eventoDAO;
    }

    /**
     * Realiza o cadastro de um novo evento, a partir do nome, ID do endereço, ID da categoria, ID do usuário,
     * data e hora do evento, descrição e limite de vagas.
     * 
     * @param nome Nome do evento.
     * @param enderecoID ID do endereço (foreign key).
     * @param categoriaID ID da categoria (foreign key).
     * @param usuarioID ID do usuário (foreign key).
     * @param dataHora Data e hora do evento.
     * @param descricao Descrição do evento.
     * @param capacidade Limite de vagas do evento.
     * @return true se o cadastro foi realizado com sucesso, false caso contrário.
     * @throws PersistenceException Se ocorrer algum erro durante o cadastro (falha no banco de dados).
     */
    public void cadastrarEvento (String nome, Endereco endereco, Categoria categoria, String descricao, LocalDateTime dataHora, int capacidade, int usuarioID)
        throws IllegalArgumentException, PersistenceException {
    if (nome == null || nome.trim().isEmpty()) {
        throw new IllegalArgumentException("O nome do evento não pode ser vazio!");
    }

    if (endereco == null || endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty() ||
            endereco.getNumeroLogradouro() == null || endereco.getNumeroLogradouro().trim().isEmpty() ||
            endereco.getComplemento() == null || endereco.getComplemento().trim().isEmpty() ||
            endereco.getBairro() == null || endereco.getBairro().trim().isEmpty() ||
            endereco.getCEP() == null || endereco.getCEP().trim().isEmpty()) {
            throw new IllegalArgumentException("Dados de endereço incompletos ou inválidos!");
        }

    if (categoria == null || categoria.getNome().trim().isEmpty()) {
        throw new IllegalArgumentException("A categoria do evento não pode ser vazio");
    }

    if (descricao == null || descricao.trim().isEmpty()){
        throw new IllegalArgumentException("Descricao não pode estar vazia");
    }

    if (capacidade <= 0) {
        throw new IllegalArgumentException("Capacidade não pode ser 0");
    }

    if (!verificarUsuarioID(usuarioID)) {
        throw new IllegalArgumentException("ID do usuário inválido");
    }

    if (!verificarDataHora(dataHora)) {
        throw new IllegalArgumentException("A data e hora do evento invalida");
    }

    // Método para verificar validade do ID do usuário
    public boolean verificarUsuarioID(int usuarioID) {
        return usuarioID > 0;
    }

    // Método para verificar se a data do evento é valida
    public boolean verificarDataHora(LocalDateTime dataHora) {
        return dataHora != null && dataHora.isAfter(LocalDateTime.now());
    }

    // Método para cadastrar uma categoria
    public void cadastrarCategoria(Categoria categoria) throws PersistenceException {
        if (categoria == null || categoria.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria inválido!");
        }
    }
    
    // Método para listar evento Atraves do EventoDAO
    public EventoService() throws Exception {
        this.conexao = ConexaoBanco.conectar();
    }

    public List<Evento> buscarEventos(int eventoID) throws Exception {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT* FROM eventos";

        try (Connection conexao = ConexaoBanco.conectar()) {
            PreparedStatement stmt = conexao.prepareStatement(sql);
        }
            Evento listarEvento = null;
            if (eventoID > 0) {
                listarEvento = eventoDAO.buscarPorID(eventoID);
                if (listarEvento == null) {
                    throw new PersistenceException("Evento com ID " + eventoID + " não encontrado após a busca!");
                }
            } else {
                throw new PersistenceException("ID di evento inválido: " + eventoID);
            }

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar eventos: " + e.getMessage());
        
        return eventos;
        }
    }
}
