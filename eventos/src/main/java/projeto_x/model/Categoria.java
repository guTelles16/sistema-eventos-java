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
        if (id == null || id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID da categoria inválido: deve ser maior que zero!");
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria inválido: não pode ser nulo ou vazio!");
        }
        this.nome = nome.trim();
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
