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
        if (id == null || id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID do evento inválido: deve ser maior que zero!");
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do evento inválido: não pode ser nulo ou vazio!");
        }
        this.nome = nome.trim();
    }
    
    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

     public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new IllegalArgumentException("A data e hora do evento não podem ser nulas!");
        }
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

    public void setCapacidade(Integer capacidade) {
        if (capacidade != null && capacidade > 0) {
            this.capacidade = capacidade;
        } else {
            throw new IllegalArgumentException("Capacidade inválida: deve ser maior que zero!");
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new IllegalArgumentException("Latitude inválida: deve estar entre -90 e +90!");
        }
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new IllegalArgumentException("Longitude inválida: deve estar entre -180 e +180!");
        }
        this.longitude = longitude;
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
                ", local=" + (endereco != null ? endereco.getLogradouro(): "N/A") +
                '}';
    }
}
