package projeto_x.model;

public class Categoria {
    private int id;
    private String nome;

    public Categoria(int id, String nome) {
        setNome(nome);
        setID(id);
    }
    
    public Categoria(String nome) {
        setNome(nome);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
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
