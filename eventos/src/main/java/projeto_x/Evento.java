package projeto_x;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Evento {
    private int id;
    private String nome;
    private String data;
    private String local;
    private String descricao;
    private String tipo;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public LocalDateTime getDataHora() {
        return LocalDateTime.parse(data);
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.data = dataHora.toString();
    }

    public void setData(LocalDateTime dataHora) {
        this.data = data.toString();
    }

    public int getCapacidade() {
        return 0; //Placcholder for capacity, can be implemented later
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade; //Placcholder for capacity, can be implemented later
    }
}
