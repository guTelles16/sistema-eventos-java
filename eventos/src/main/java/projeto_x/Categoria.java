package projeto_x;

public class Categoria {
    private int id;
    private String nome;

    public Categoria(String nome) {
        setNome(nome);
    }

    public Categoria(int id, String nome) {
        setNome(nome);
        setId(id);
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
}
