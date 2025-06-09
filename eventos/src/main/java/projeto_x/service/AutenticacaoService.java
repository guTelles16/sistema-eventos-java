package projeto_x.service;

import java.sql.SQLException;
import java.util.Arrays;

import projeto_x.model.Endereco;
import projeto_x.model.Usuario;
import projeto_x.dao.EnderecoDAO;
import projeto_x.dao.UsuarioDAO;
import projeto_x.util.PersistenceException;
import projeto_x.util.SenhaUtil;

public class AutenticacaoService {
    private UsuarioDAO usuarioDAO;
    private EnderecoDAO enderecoDAO;

    public AutenticacaoService(UsuarioDAO usuarioDAO, EnderecoDAO enderecoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.enderecoDAO = enderecoDAO;
    }

    /**
     * Realiza o cadastro de um novo usuário, a partir do nome, endereço, e-mail e senha.
     * 
     * @param nome Nome do usuário.
     * @param email Email do usuário.
     * @param endereco Endereço do usuário (foreign key)
     * @param senhaPlana Senha em texto claro do usuário.
     * @return true se o cadastro foi realizado com sucesso, false caso contrário.
     * @throws PersistenceException Se ocorrer algum erro durante o cadastro (falha no banco dados).
     */
    public boolean cadastrarUsuario(String nome, String email, Endereco endereco, char[] senhaPlana) throws PersistenceException {
        if (nome == null || nome.trim().isEmpty()) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("O Nome do usuário não pode ser vazio!");
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("E-mail inválido!");
        }

        if (senhaPlana == null || senhaPlana.length < 8) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres!");
        }

        // Verifica se o email já está em uso
        if (usuarioDAO.buscarPorEmail(email) != null) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("O e-mail '" + email + "' já cadastrado!");
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
            System.err.println("Erro interno ao salvar usuário " + e.getMessage()); // Log para o dev
            throw new PersistenceException("Não foi possível cadastrar o usuário! Tente novamente mais tarde!");
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
     * @throws PersistenceException Se ocorrer algum erro durante o login (usuário não encontrado ou falha na verificação da senha).
     */
    public Usuario login(String email, char[] senhaPlana) throws PersistenceException {
        if (email == null || !email.contains("@") || email.contains(".")) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("E-mail inválido!");
        }

        if (senhaPlana == null || senhaPlana.length < 8) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("A senha deve conter no mínimo 8 caracteres!");
        }

        Usuario usuario = null;
        
        try {
            // Buscar o usuário pelo e-mail
            usuario = usuarioDAO.buscarPorEmail(email);
        } catch (PersistenceException e) {
            System.err.println("Erro interno ao buscar usuário para login: " + e.getMessage()); // Log para o dev
            throw new RuntimeException("Falha ao tentar fazer login. Tente novamente mais tarde!");
        } finally {
            Arrays.fill(senhaPlana, ' ');
        }

        if (usuario == null) {
            return null; // Não demonstra que o usuário não foi encontrado
        }

        // Verifica se a senha em texto claro corresponde ao hash armazenado
        boolean senhaCorreta = SenhaUtil.verificarSenha(senhaPlana, usuario.getHashSenha());
        if (senhaCorreta) {
            return usuario; // Login bem-sucedido
        } else {
            return null; // Senha incorreta
        }
    }
}
