package projeto_x.model;

public class Categoria {
    private Integer id;
    private String nome;

    public Categoria(Integer id, String nome) {
        setID(id);
        setNome(nome);
    }
    
    public Categoria(String nome) {
        setNome(nome);
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

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
