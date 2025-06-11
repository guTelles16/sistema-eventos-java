package projeto_x.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import projeto_x.dao.EventoDAO;
import projeto_x.dao.UsuarioDAO;
import projeto_x.dao.EnderecoDAO;
import projeto_x.dao.CategoriaDAO;
import projeto_x.model.Categoria;
import projeto_x.model.Endereco;
import projeto_x.model.Evento;
import projeto_x.util.PersistenceException;
import projeto_x.util.ConexaoBanco;


public class EventoService {
    private EventoDAO eventoDAO;
    private UsuarioDAO usuarioDAO;
    private EnderecoDAO enderecoDAO;
    private CategoriaDAO categoriaDAO;

    public EventoService(EventoDAO eventoDAO, UsuarioDAO usuarioDAO, EnderecoDAO enderecoDAO, CategoriaDAO categoriaDAO) {
        this.eventoDAO = eventoDAO;
        this.usuarioDAO = usuarioDAO;
        this.enderecoDAO = enderecoDAO;
        this.categoriaDAO = categoriaDAO;
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
    public void cadastrarEvento (String nome, Endereco endereco, String categoria, String descricao, LocalDateTime dataHora, int capacidade, int criadorID)
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
        
        // Verifica se o endereço já está em uso

        if (categoriaDAO.buscarCategoria(categoria) == null) {
            throw new IllegalArgumentException("Categoria inválida!");
        }

        if (descricao == null || descricao.trim().isEmpty()){
            throw new IllegalArgumentException("A Descrição não pode ser vazia!");
        }

        if (capacidade <= 0) {
            throw new IllegalArgumentException("A Capacidade não pode ser menor que zero!");
        }

        if (usuarioDAO.buscarPorID(criadorID) == null && criadorID < 0) {
            throw new IllegalArgumentException("ID do usuário inválido!");
        }

        if (verificarDataHora(dataHora) != true)  {
            throw new IllegalArgumentException("A data e hora do evento invalida");
        }
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
}
