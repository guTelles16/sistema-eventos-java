package projeto_x.service;

import projeto_x.model.Endereco;
import projeto_x.model.Usuario;
import projeto_x.dao.EnderecoDAO;
import projeto_x.dao.UsuarioDAO;
import projeto_x.dao.EventoDAO;
import projeto_x.dao.Confirmacao_PresencaDAO;
import projeto_x.util.PersistenceException;
import projeto_x.util.SenhaUtil;

import java.sql.SQLException;
import java.util.Arrays;

public class AutenticacaoService {
    private UsuarioDAO usuarioDAO;
    private EnderecoDAO enderecoDAO;
    private EventoDAO eventoDAO;
    private Confirmacao_PresencaDAO confirmacaoPresencaDAO;

    public AutenticacaoService(UsuarioDAO usuarioDAO, EnderecoDAO enderecoDAO, EventoDAO eventoDAO, Confirmacao_PresencaDAO confirmacaoPresencaDAO) {
        this.usuarioDAO = usuarioDAO;
        this.enderecoDAO = enderecoDAO;
        this.eventoDAO = eventoDAO;
        this.confirmacaoPresencaDAO = confirmacaoPresencaDAO;
    }

     /**
     * Busca um usuário por seu endereço de email.
     * 
     * @param email O email do usuário a ser buscado.
     * @return O objeto Usuario encontrado, NULL caso contrário.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public Usuario buscarPorEmail(String email) throws PersistenceException {
        // Delega a chamada para o UsuarioDAO
        return usuarioDAO.buscarPorEmail(email);
    }

    /**
     * Realiza o cadastro de um novo usuário, a partir do nome, endereço, e-mail e senha.
     * 
     * @param nome Nome do usuário.
     * @param email Email do usuário.
     * @param endereco Endereço do usuário (foreign key)
     * @param senhaPlana Senha em texto claro do usuário.
     * @return true se o cadastro foi realizado com sucesso, false caso contrário.
     * @throws IllegalArgumentException Se os dados de entrada forem inválidos.
     * @throws PersistenceException Se ocorrer algum erro durante o cadastro (falha no banco dados).
     */
    public boolean cadastrarUsuario(String nome, String email, Endereco endereco, char[] senhaPlana) 
            throws PersistenceException, IllegalArgumentException {
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

        // Validação do Objeto Endereco
        if (endereco == null || endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty() ||
            endereco.getNumeroLogradouro() == null || endereco.getNumeroLogradouro().trim().isEmpty() ||
            endereco.getBairro() == null || endereco.getBairro().trim().isEmpty() ||
            endereco.getCEP() == null || endereco.getCEP().trim().isEmpty()) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("Dados de endereço incompletos ou inválidos!");
        }

        // Verifica se o email já está em uso
        if (usuarioDAO.buscarPorEmail(email) != null) {
            Arrays.fill(senhaPlana, ' ');
            throw new IllegalArgumentException("O e-mail '" + email + "' já cadastrado!");
        }

        if (endereco.getID() <= 0) { 
            int enderecoIdGerado = enderecoDAO.salvar(endereco);
            endereco.setID(enderecoIdGerado); // Define o ID gerado no objeto Endereco
        }

        // Gera o hash da senha
        String hashSenha = SenhaUtil.hashSenha(senhaPlana);

        // Cria o objeto Usuario com o hash
        Usuario novoUsuario = new Usuario(nome, endereco, email, hashSenha);

        // Salvar as informações no banco de dados
        try {
            usuarioDAO.salvar(novoUsuario);
            return true;
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
        if (email == null || !email.contains("@") || !email.contains(".")) {
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
        
        } catch (PersistenceException e) {
            throw new PersistenceException("Falha ao tentar fazer login. Erro no banco de dados.");
        
        }
    }

    /**
     * Atualiza os dados de um usuário e seu endereço associado.
     * 
     * @param usuario O objeto Usuario com os dados atualizados.
     * @return true se o usuário e/ou seu endereço foram atualizados com sucesso, false caso contrário.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public boolean atualizarUsuario(Usuario usuario) throws PersistenceException {
        try {
            // Lógica para atualizar o endereço primeiro
            if (usuario.getEndereco() != null) {
                if (usuario.getEndereco().getID() > 0) {
                    // O endereço já existe, então atualiza
                    enderecoDAO.atualizar(usuario.getEndereco());
                } else {
                    // O endereço é novo, então salva e associa o ID retornado ao usuário
                    int novoEnderecoId = enderecoDAO.salvar(usuario.getEndereco());
                    usuario.getEndereco().setID(novoEnderecoId);
                }
            }
            // Agora atualiza o usuário
            return usuarioDAO.atualizar(usuario);
        } catch (Exception e) {
            throw new PersistenceException("Erro ao atualizar dados do usuário no serviço: " + e.getMessage());
        }
    }

    /**
     * Altera a senha de um usuário.
     *
     * @param userId O ID do usuário cuja senha será alterada.
     * @param senhaAntigaPlana A senha antiga em texto claro (para verificação).
     * @param novaSenhaPlana A nova senha em texto claro.
     * @return true se a senha foi alterada com sucesso, false caso contrário.
     * @throws IllegalArgumentException Se as senhas forem inválidas ou a senha antiga não corresponder.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public boolean alterarSenha(int userId, char[] senhaAntigaPlana, char[] novaSenhaPlana)
            throws IllegalArgumentException, PersistenceException {

        // Validar as senhas
        if (senhaAntigaPlana == null || senhaAntigaPlana.length == 0) {
            throw new IllegalArgumentException("A senha antiga não pode ser vazia!");
        }
        if (novaSenhaPlana == null || novaSenhaPlana.length < 8) {
            Arrays.fill(senhaAntigaPlana, ' '); // Limpa a senha antiga
            throw new IllegalArgumentException("A nova senha deve conter no mínimo 8 caracteres!");
        }

        Usuario usuario = usuarioDAO.buscarPorID(userId);
        if (usuario == null) {
            Arrays.fill(senhaAntigaPlana, ' ');
            Arrays.fill(novaSenhaPlana, ' ');
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        // Verificar a senha antiga
        if (!SenhaUtil.verificarSenha(senhaAntigaPlana, usuario.getHashSenha())) {
            Arrays.fill(senhaAntigaPlana, ' ');
            Arrays.fill(novaSenhaPlana, ' ');
            throw new IllegalArgumentException("Senha antiga incorreta!");
        }

        // Gerar o hash da nova senha
        String novoHashSenha = SenhaUtil.hashSenha(novaSenhaPlana);

        // Atualizar o hash da senha no objeto usuário
        usuario.setHashSenha(novoHashSenha);

        try {
            // Persistir o novo hash no banco de dados
            return usuarioDAO.atualizarSenhaHash(usuario);
        } finally {
            // Garante que ambos os arrays de senha em texto claro sejam zerados
            Arrays.fill(senhaAntigaPlana, ' ');
            Arrays.fill(novaSenhaPlana, ' ');
        }
    }

    /**
     * Deleta um usuário e todas as informações relacionadas a ele (eventos criados,
     * confirmações de presença, e o endereço associado).
     * 
     * @param idUsuario O ID do usuário a ser deletado.
     * @return true se o usuário e seus dados relacionados foram deletados com sucesso, false caso contrário.
     * @throws PersistenceException Se ocorrer um erro no banco de dados.
     */
    public boolean deletarUsuario(int idUsuario) throws PersistenceException {
        try {
            if (confirmacaoPresencaDAO != null) {
                confirmacaoPresencaDAO.deletarConfirmacoesPorUsuario(idUsuario);
            }

            if (eventoDAO != null) {
                eventoDAO.deletarEventosPorCriador(idUsuario);
            }

            Usuario usuarioParaDeletar = usuarioDAO.buscarPorID(idUsuario);
            boolean usuarioDeletado = usuarioDAO.deletar(idUsuario);

            if (usuarioDeletado && usuarioParaDeletar != null && usuarioParaDeletar.getEndereco() != null && usuarioParaDeletar.getEndereco().getID() > 0) {
                enderecoDAO.deletar(usuarioParaDeletar.getEndereco().getID());
            }
            
            return usuarioDeletado;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao deletar conta do usuário: " + e.getMessage());
        }
    }
}
