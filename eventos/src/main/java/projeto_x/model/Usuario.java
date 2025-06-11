package projeto_x.model;

public class Usuario {
    private Integer id;
    private String nome;
    private Endereco endereco;
    private String email;
    private String hashSenha;
    private Double latitude;
    private Double longitude;

    // Construtor completo (criar objeto a partir do banco de dados)
    public Usuario(Integer id, String nome, Endereco endereco, String email, String hashSenha,
            Double latitude, Double longitude) {
        setID(id);
        setNome(nome);
        setEndereco(endereco);
        setEmail(email);
        setHashSenha(hashSenha);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    // Construtor para cadastro (id é gerado automaticamente)
    public Usuario(String nome, Endereco endereco, String email, String hashSenha,
            Double latitude, Double longitude) {
        setNome(nome);
        setEndereco(endereco);
        setEmail(email);
        setHashSenha(hashSenha);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    // Construtor para AutenticacaoService
    public Usuario(String nome, Endereco endereco, String email, String hashSenha) {
        this.id = null;
        setNome(nome);
        setEndereco(endereco);
        setEmail(email);
        setHashSenha(hashSenha);
        this.latitude = null;
        this.longitude = null;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
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

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço inválido: não pode ser nulo!");
        }
        this.endereco = endereco;
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
    
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                (endereco != null ? ", endereco=" + endereco.getLogradouro() : "") +
                '}';
    }
}
