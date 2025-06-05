package projeto_x.model;

// Classe para representar o Endereço (atributo composto)
public class Endereco {
    private int id;
    private String logradouro;
    private String numeroLogradouro;
    private String complemento;
    private String bairro;
    private String cep;

    public Endereco(int id, String logradouro, String numeroLogradouro, String complemento, String bairro, String cep) {
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

    public int getID() {
        return id;
    }

    public void setID(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido: deve ser maior que zero!");
        }
        this.id = id;
    }
    
    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        if (logradouro == null || logradouro.isEmpty()) {
            throw new IllegalArgumentException("Logradouro inválido: não pode ser nulo ou vazio!");
        }
        this.logradouro = logradouro;
    }

    public String getNumeroLogradouro() {
        return numeroLogradouro;
    }

    public void setNumeroLogradouro(String numeroLogradouro) {
        if (numeroLogradouro == null || numeroLogradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do logradouro inválido: não pode ser nulo ou vazio!");
        }

        if (!isNumerico(numeroLogradouro)) {
            throw new IllegalArgumentException("Número do logradouro inválido: deve ser numérico!");
        }

        this.numeroLogradouro = numeroLogradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        if (complemento == null || complemento.trim().isEmpty()) {
            throw new IllegalArgumentException("Complemento inválido: não pode ser nulo ou vazio!");
        }
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro inválido: não pode ser nulo ou vazio!");
        }
        this.bairro = bairro;
    }

    public String getCEP() {
        return cep;
    }

    public void setCEP(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("CEP inválido: não pode ser nulo ou vazio!");
        }
        
        if (!validarCEP(cep)) {
            throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos númericos e não pode ser uma sequência de dígitos iguais!");
        }
        // Armazena o CEP no formato limpo (sem hífen)
        this.cep = cep.replaceAll("[^0-9]", "");
    }

    public static boolean isNumerico(String input) {
        try {
            Long.parseLong(input); // Tenta converter a String para número
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validarFormatoCEP(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            return false;
        }

        // Remove possíveis hifens e espaços
        String cepLimpo = cep.replaceAll("[^0-9]", "");

        // Verifica se o CEP tem exatamente 8 dígitos
        return cepLimpo.length() == 8 && cep.matches("^\\d{8}$");
    }

    public static boolean validarCEP(String cep) {
        if (!validarFormatoCEP(cep)) {
            return false;
        }

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        // Verifica se o CEP não é uma sequência de dígitos iguais
        if (cepLimpo.matches("(\\d)\\1{7}")) {
            return false;
        }

        if (!isNumerico(cepLimpo)) {
            return false;
        }

        return true;
    }
}