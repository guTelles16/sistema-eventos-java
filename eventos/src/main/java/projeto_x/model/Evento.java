package projeto_x.model;

import java.time.LocalDateTime;

public class Evento {
    private int id;
    private String nome;
    private Endereco endereco;
    private Categoria categoria;
    private LocalDateTime dataHora;
    private String descricao;
    private int capacidade;
    private Usuario criador;

    public Evento(int id, String nome, LocalDateTime dataHora, Endereco endereco, Categoria categoria, String descricao, int capacidade, Usuario criador) {
        setNome(nome);
        setDataHora(dataHora);
        setEndereco(endereco);
        setCategoria(categoria);
        setDescricao(descricao);
        setCapacidade(capacidade);
        setId(id);
        setCriador(criador);;
    }

    public Evento(String nome, LocalDateTime dataHora, Endereco endereco, Categoria categoria, String descricao, int capacidade, Usuario criador) {
        setNome(nome);
        setDataHora(dataHora);
        setEndereco(endereco);
        setCategoria(categoria);
        setDescricao(descricao);
        setCapacidade(capacidade);
        setCriador(criador);
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
    
    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo.");
        }
        this.endereco = endereco;
    }
    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
        if (criador == null) {
            throw new IllegalArgumentException("Criador não pode ser nulo.");
        }
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
