package projeto_x.model;

import java.time.LocalDateTime;

public class Confirmacao_Presenca {
    private Usuario usuario;
    private Evento evento;
    private LocalDateTime dataHoraConfirmacao;
    private String status;
    private int totalConfirmacoes;

    public Confirmacao_Presenca(Usuario usuario, Evento evento, LocalDateTime dataHoraConfirmacao, String status, int totalConfirmacoes) {
        setUsuario(usuario);
        setEvento(evento);
        setDataHoraConfirmacao(dataHoraConfirmacao);
        setStatus(status);
        setTotalConfirmacoes(totalConfirmacoes);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário inválido: não pode ser nulo!");
        }
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento inválido: não pode ser nulo!");
        }
        this.evento = evento;
    }

    public LocalDateTime getDataHoraConfirmacao() {
        return dataHoraConfirmacao;
    }

    public void setDataHoraConfirmacao(LocalDateTime dataHoraConfirmacao) {
        this.dataHoraConfirmacao = dataHoraConfirmacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status inválido: não pode ser nulo ou vazio!");
        }
        this.status = status;
    }

     public int getTotalConfirmacoes() {
        return totalConfirmacoes;
    }

    public void setTotalConfirmacoes(int totalConfirmacoes) {
        if (totalConfirmacoes < 0) {
            throw new IllegalArgumentException("Total de confirmações inválido: não pode ser menor que zero!");
        }
        this.totalConfirmacoes = totalConfirmacoes;
    }

    @Override
    public String toString() {
        return "Confirmacao_Presenca{" +
                "usuario=" + (usuario != null ? usuario.getNome() : "N/A") + 
                ", evento=" + (evento != null ? evento.getNome() : "N/A") + 
                ", status='" + status + '\'' +
                ", total_confirmacoes='" + totalConfirmacoes + '\'' + 
                '}';
    }
}
