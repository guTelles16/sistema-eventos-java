package projeto_x.model;

import java.time.LocalDateTime;

public class Evento {
    private Integer id;
    private String nome;
    private Endereco endereco;
    private Categoria categoria;
    private LocalDateTime dataHora;
    private String descricao;
    private Integer capacidade;
    private Double latitude;
    private Double longitude;
    private Usuario criador;

    public Evento(Integer id, String nome, Endereco endereco, Categoria categoria, LocalDateTime dataHora, String descricao, Integer capacidade, 
            Double latitude, Double longitude, Usuario criador) {
        setID(id);
        setNome(nome);
        setEndereco(endereco);
        setCategoria(categoria);
        setDataHora(dataHora);
        setDescricao(descricao);
        setCapacidade(capacidade);
        setLatitude(latitude);
        setLongitude(longitude);
        setCriador(criador);
    }

    public Evento(String nome, Endereco endereco, Categoria categoria, LocalDateTime dataHora, String descricao, Integer capacidade, 
            Double latitude, Double longitude, Usuario criador) {
        setNome(nome);
        setEndereco(endereco);
        setCategoria(categoria);
        setDataHora(dataHora);
        setDescricao(descricao);
        setCapacidade(capacidade);
        setLatitude(latitude);
        setLongitude(longitude);
        setCriador(criador);
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

     public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
                ", local=" + (endereco != null ? endereco.getLogradouro(): "N/A") +
                '}';
    }
}
