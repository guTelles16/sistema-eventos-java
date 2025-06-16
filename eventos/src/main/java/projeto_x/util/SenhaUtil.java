package projeto_x.util;

import org.mindrot.jbcrypt.BCrypt;

public class SenhaUtil {
    /**
     * Gera o hash (criptografia) a partir de uma senha em texto claro.
     * Torna segura armazenar no banco de dados.
     * 
     * @param senhaPlana Senha em texto claro.
     * @return Hash da senha; inclui o salt e o resultado do hashing.
     */
    public static String hashSenha(char[] senhaPlana) {
        // Converte o char[] para String
        String senhaString = new String(senhaPlana);

        // Gera um 'salt' aleatório
        String salt = BCrypt.gensalt();

        // Aplica o algoritmo de hashing com o salt e a senha
        return BCrypt.hashpw(senhaString, salt);
    }

    /**
     * Verifica se uma senha em texto claro corresponde ao hash armazenado no banco de dados.
     * 
     * @param senhaPlana Senha em texto claro.
     * @param hashSenha O hash da senha armazenado no banco de dados.
     * @return true se corresponder (senha correta com hash armazenado), false caso contrário.
     */
    public static boolean verificarSenha(char[] senhaPlana, String hashSenha) {
        // Converte o char[] para String
        String senhaString = new String(senhaPlana);

        // Verifica se a senha em texto claro corresponde ao hash armazenado
        return BCrypt.checkpw(senhaString, hashSenha);
    }
}
