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
        if (id != null && id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID do usuário inválido: deve ser maior que zero!");
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuário inválido: não pode ser nulo ou vazio!");
        }
        this.nome = nome.trim();
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
