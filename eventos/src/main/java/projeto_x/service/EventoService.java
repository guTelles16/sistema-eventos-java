package projeto_x.service;

import java.sql.SQLException;

import projeto_x.dao.EventoDAO;


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
     */
}
