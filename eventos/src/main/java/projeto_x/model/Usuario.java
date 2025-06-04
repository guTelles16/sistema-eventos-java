package projeto_x.model;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String hashSenha;

    // Construtor completo (criar objeto a partir do banco de dados)
    public Usuario(int id, String nome, String email, String hashSenha) {
        setId(id);
        setNome(nome);
        setEmail(email);
        setHashSenha(hashSenha);
    }

    // Construtor para cadastro (id é gerado automaticamente)
    public Usuario(String nome, String email, String hashSenha) {
        setNome(nome);
        setEmail(email);
        setHashSenha(hashSenha);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido: deve ser maior que zero!");
        } 
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome inválido: não pode ser nulo ou vazio!");
        }
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email inválido: deve conter '@' e '.'!");
        }
        this.email = email;
    }

    // Retorna o hash da senha
    public String getHashSenha() {
        return hashSenha;
    }

    // Set para o hash, quando o banco de dados for carregado
    public void setHashSenha(String hashSenha) {
        this.hashSenha = hashSenha;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
