package projeto_x.model;

import java.time.LocalDateTime;
import projeto_x.model.Usuario;

public class Evento {
    private int id;
    private String nome;
    private LocalDateTime dataHora;
    private String local;
    private String descricao;
    private String tipo;
    private int capacidade;
    private Usuario criador;

    public Evento(String nome, LocalDateTime dataHora, String local, String descricao, String tipo, int capacidade, Usuario criador) {
        setNome(nome);
        setDataHora(dataHora);
        setLocal(local);
        setDescricao(descricao);
        setTIpo(tipo);
        setCapacidade(capacidade);
        this.criador = criador;
    }

    public Evento(int id, String nome, LocalDateTime dataHora, String local, String descricao, String tipo, int capacidade, Usuario criador) {
        setNome(nome);
        setDataHora(dataHora);
        setLocal(local);
        setDescricao(descricao);
        setTIpo(tipo);
        setCapacidade(capacidade);
        setId(id);
        this.criador = criador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    
    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTIpo(String tipo) {
        this.tipo = tipo;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", criador=" + (criador != null ? criador.getNome() : "Desconhecido") +
                '}';
    }
}
