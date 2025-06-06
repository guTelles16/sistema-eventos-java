package projeto_x.service;

import java.sql.SQLException;
import java.util.Arrays;

import projeto_x.model.Endereco;
import projeto_x.model.Usuario;
import projeto_x.dao.UsuarioDAO;
import projeto_x.util.SenhaUtil;

public class AutenticacaoService {
    private UsuarioDAO usuarioDAO;

    public AutenticacaoService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Realiza o cadastro de um novo usuário, a partir do nome, endereço, e-mail e senha.
     * 
     * @param nome Nome do usuário.
     * @param email Email do usuário.
     * @param endereco Endereço do usuário (foreign key)
     * @param senhaPlana Senha em texto claro do usuário.
     * @return true se o cadastro foi realizado com sucesso, false caso contrário.
     * @throws Exception Se ocorrer algum erro durante o cadastro (falha no banco dados).
     */
    public boolean cadastrarUsuario(String nome, String email, Endereco endereco, char[] senhaPlana) throws Exception {
        if (nome == null || nome.trim().isEmpty() || email == null || email.trim().isEmpty() || endereco == null ||
                senhaPlana == null || senhaPlana.length < 8) {
            throw new IllegalArgumentException("Nome, endereço, email ou senha são inválidos. A senha deve ter pelo menos 8 caracteres!");
        }

        // Verifica se o endereço é válido
        if (enderecoDAO.buscarPorId(endereco == null) || endereco.getID() <= 0) {
            throw new IllegalArgumentException("Endereço inválido! Por favor, cadastre um endereço válido.");
        }

        // Verifica se o email já está em uso
        if (usuarioDAO.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        // Gera o hash da senha
        String hashSenha = SenhaUtil.hashSenha(senhaPlana);

        // Cria o objeto Usuario com o hash
        Usuario novoUsuario = new Usuario(nome, email, endereco, hashSenha);

        // Salvar as informações no banco de dados
        try {
            usuarioDAO.salvar(novoUsuario);
            return true;
        } catch (SQLException e) {
            throw new Exception("Erro ao cadastrar usuário: " + e.getMessage());
        } finally {
            // Zera o array de caracteres da senha em texto claro
            Arrays.fill(senhaPlana, ' ');
        }
    }

    /**
     * Realiza o login de um usuário, a partir do e-mail e senha.
     * 
     * @param email E-mail do usuário.
     * @param senhaPlana Senha em texto claro do usuário.
     * @return O usuário autenticado se o login for bem-sucedido, null caso contrário.
     * @throws Exception Se ocorrer algum erro durante o login (usuário não encontrado ou falha na verificação da senha).
     */
    public Usuario login(String email, char[] senhaPlana) throws Exception {
        // Buscar o usuário pelo e-mail
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado com o e-mail fornecido!");
        }

        // Verifica se a senha em texto claro corresponde ao hash armazenado
        boolean senhaCorreta = SenhaUtil.verificarSenha(senhaPlana, usuario.getHashSenha());

        // Zera o array de caracteres da senha em texto claro
        Arrays.fill(senhaPlana, ' ');

        if (senhaCorreta) {
            return usuario; // Login bem-sucedido
        } else {
            return null; // Senha incorreta
        }
    }
}
