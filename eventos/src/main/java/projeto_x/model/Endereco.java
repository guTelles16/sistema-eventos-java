package projeto_x.model;

// Classe para representar o Endereço (atributo composto)
public class Endereco {
    private Integer id;
    private String logradouro;
    private String numeroLogradouro;
    private String complemento;
    private String bairro;
    private String cep;

    public Endereco(Integer id, String logradouro, String numeroLogradouro, String complemento, String bairro, String cep) {
        setID(id);
        setLogradouro(logradouro);
        setNumeroLogradouro(numeroLogradouro);
        setComplemento(complemento);
        setBairro(bairro);
        setCEP(cep);
    }

    public Endereco(String logradouro, String numeroLogradouro, String complemento, String bairro, String cep) {
        setLogradouro(logradouro);
        setNumeroLogradouro(numeroLogradouro);
        setComplemento(complemento);
        setBairro(bairro);
        setCEP(cep);
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        if (id == null || id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID inválido: deve ser maior que zero!");
        }
    }
    
    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Logradouro inválido: não pode ser nulo ou vazio!");
        }
        this.logradouro = logradouro.trim();
    }

    public String getNumeroLogradouro() {
        return numeroLogradouro;
    }

    public void setNumeroLogradouro(String numeroLogradouro) {
        if (numeroLogradouro == null || numeroLogradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do logradouro inválido: não pode ser nulo ou vazio!");
        }

        this.numeroLogradouro = numeroLogradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro inválido: não pode ser nulo ou vazio!");
        }
        this.bairro = bairro.trim();
    }

    public String getCEP() {
        return cep;
    }

    public void setCEP(String cep) {    
        if (!isCEPValido(cep)) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos númericos e não pode ser uma sequência repetida!");
        }
        // Armazena o CEP no formato limpo (sem hífen)
        this.cep = cep.replaceAll("[^0-9]", "");
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "id=" + id +
                ", logradouro='" + logradouro + '\'' +
                ", numero_logradouro='" + numeroLogradouro + '\'' +
                ", complemento='" + complemento + '\'' +
                ", bairro='" + bairro + '\'' +
                ", CEP='" + cep + '\'' +
                '}';
    } 

    public static boolean isNumerico(String input) {
        try {
            Long.parseLong(input); // Tenta converter a String para número
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCEPValido(String cep) {
        if (cep == null) {
            return false;
        }

        // Remove tudo que não for digito numérico
        String cepLimpo = cep.replaceAll("[^0-9]", "");

        // Verifica se o resultado tem 8 dígitos
        if (cepLimpo.length() != 8) {
            return false;
        }

        // Verifica se não é uma sequência de dígitos repetidos
        if (cepLimpo.matches("(\\d)\\1{7}")) {
            return false;
        }

        return true;
    }
}