package projeto_x.view;

import projeto_x.service.AutenticacaoService;
import projeto_x.service.EventoService;
import projeto_x.model.Usuario;
import projeto_x.model.Evento;
import projeto_x.dao.CategoriaDAO;
import projeto_x.dao.Confirmacao_PresencaDAO;
import projeto_x.dao.EnderecoDAO;
import projeto_x.dao.EventoDAO;
import projeto_x.dao.UsuarioDAO;
import projeto_x.model.Endereco;
import projeto_x.model.Categoria;
import projeto_x.util.PersistenceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Scanner;
import java.util.List;
import java.util.Arrays;

public class ConsoleUI {
    private Scanner scanner;
    private Usuario usuarioLogado = null;
    private AutenticacaoService autenticacaoService;
    private EventoService eventoService;
    private EnderecoDAO enderecoDAO;
    private CategoriaDAO categoriaDAO;
    private UsuarioDAO usuarioDAO;
    private EventoDAO eventoDAO;
    private Confirmacao_PresencaDAO confirmacaoPresencaDAO;

    public ConsoleUI() throws PersistenceException {
        this.scanner = new Scanner(System.in);
        this.enderecoDAO = new EnderecoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.usuarioDAO = new UsuarioDAO(this.enderecoDAO);
        this.eventoDAO = new EventoDAO(this.usuarioDAO, this.enderecoDAO, this.categoriaDAO);
        this.confirmacaoPresencaDAO = new Confirmacao_PresencaDAO(this.usuarioDAO, this.eventoDAO);
        
        this.autenticacaoService = new AutenticacaoService(this.usuarioDAO, this.enderecoDAO, this.eventoDAO, this.confirmacaoPresencaDAO);
        this.eventoService = new EventoService(this.eventoDAO, this.usuarioDAO, this.enderecoDAO, this.categoriaDAO, this.confirmacaoPresencaDAO);
    }

    public void menuPrincipal() {
        int opcao;
        do {
            System.out.println("\n--- BEM-VINDO AO ORGANIZADOR DE EVENTOS ---");
            System.out.println("1. Entrar (Login)");
            System.out.println("2. Cadastrar-se");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = lerInteiro();

            try {
                switch (opcao) {
                    case 1:
                        fazerLogin();
                        break;
                    case 2:
                        cadastrarNovoUsuario();
                        break;
                    case 0:
                        System.out.println("Saindo do programa...Até mais!");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Erro de entrada: " + e.getMessage());
            } catch (PersistenceException e) {
                System.out.println("Erro no banco de dados: " + e.getMessage());
                System.out.println("Por favor, tente novamente mais tarde!");
            }
        } while (opcao != 0 && usuarioLogado == null);

        if (usuarioLogado != null) {
            menuLogado();
        }
    }

    private void fazerLogin() throws IllegalArgumentException, PersistenceException {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        char[] senhaArray = senha.toCharArray();

        usuarioLogado = autenticacaoService.login(email, senhaArray);

        if (usuarioLogado != null) {
            System.out.println("Login bem-sucedido! Bem-vindo, " + usuarioLogado.getNome() + "!");
        } else {
            System.out.println("Email ou senha inválidos.");
        }
        Arrays.fill(senhaArray, ' ');
    }

    private void cadastrarNovoUsuario() throws IllegalArgumentException, PersistenceException {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha (mínimo 8 caracteres): ");
        String senha = scanner.nextLine();
        char[] senhaArray = senha.toCharArray();

        System.out.println("--- Endereço ---");
        System.out.print("Logradouro: "); String logradouro = scanner.nextLine();
        System.out.print("Número: "); String numero = scanner.nextLine();
        System.out.print("Complemento (opcional): "); String complemento = scanner.nextLine();
        System.out.print("Bairro: "); String bairro = scanner.nextLine();
        System.out.print("CEP: "); String cep = scanner.nextLine();

        Endereco endereco = new Endereco(0, logradouro, numero, complemento, bairro, cep);
        
        boolean cadastrado = autenticacaoService.cadastrarUsuario(nome, email, endereco, senhaArray);

        if (cadastrado) {
            System.out.println("Usuário cadastrado com sucesso!");
        } else {
            System.out.println("Não foi possível cadastrar o usuário. Tente novamente.");
        }
        Arrays.fill(senhaArray, ' ');
    }

    private void menuLogado() {
        int opcao;
        do {
            System.out.println("\n--- MENU DO USUÁRIO LOGADO (" + usuarioLogado.getNome() + ") ---");
            System.out.println("1. Listar todos os Eventos");
            System.out.println("2. Listar Eventos próximos (por localização)");
            System.out.println("3. Listar Meus Eventos Criados");
            System.out.println("4. Listar Meus Eventos Inscritos");
            System.out.println("5. Cadastrar Novo Evento");
            System.out.println("6. Editar Meus Dados");
            System.out.println("7. Deletar Minha Conta (CUIDADO!)");
            System.out.println("8. Inscrever-se em Evento");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");
            opcao = lerInteiro();

            try {
                switch (opcao) {
                    case 1: listarTodosEventos(); break;
                    case 2: listarEventosProximos(); break;
                    case 3: listarMeusEventosCriados(); break;
                    case 4: listarMeusEventosInscritos(); break;
                    case 5: cadastrarNovoEvento(); break;
                    case 6: editarMeusDados(); break;
                    case 7: deletarMinhaConta(); break;
                    case 8: inscreverEmEvento(); break;
                    case 0:
                        usuarioLogado = null;
                        System.out.println("Logout realizado. Retornando ao menu principal.");
                        break;
                    default: System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Erro de entrada: " + e.getMessage());
            } catch (PersistenceException e) {
                System.out.println("Erro no banco de dados: " + e.getMessage());
                System.out.println("Por favor, tente novamente mais tarde.");
            }
        } while (opcao != 0);
    }

    private void listarTodosEventos() throws PersistenceException {
        System.out.println("\n--- TODOS OS EVENTOS ---");
        List<Evento> eventos = eventoService.listarTodos();
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
        } else {
            for (Evento e : eventos) {
                exibirDetalhesEvento(e);
            }
        }
    }

    private void listarEventosProximos() throws PersistenceException {
        System.out.println("\n--- EVENTOS PRÓXIMOS ---");
        System.out.print("Qual o raio máximo em KM para buscar eventos? ");
        double raio = lerDouble();

        System.out.print("Sua Latitude: "); double latUsuario = lerDouble();
        System.out.print("Sua Longitude: "); double lonUsuario = lerDouble();
        
        List<Evento> eventosProximos = eventoService.buscarEventosProximos(latUsuario, lonUsuario, raio);

        if (eventosProximos.isEmpty()) {
            System.out.println("Nenhum evento encontrado dentro do raio de " + raio + " KM.");
        } else {
            System.out.println("Eventos próximos (ordenados por distância):");
            for (Evento e : eventosProximos) {
                exibirDetalhesEvento(e);
            }
        }
    }

    private void listarMeusEventosCriados() throws PersistenceException {
        System.out.println("\n--- MEUS EVENTOS CRIADOS ---");
        List<Evento> eventosCriados = eventoService.listarEventosCriadosPorUsuario(usuarioLogado.getID());
        if (eventosCriados.isEmpty()) {
            System.out.println("Você ainda não criou nenhum evento.");
        } else {
            for (Evento e : eventosCriados) {
                exibirDetalhesEvento(e);
            }
        }
    }

    private void listarMeusEventosInscritos() throws PersistenceException {
        System.out.println("\n--- MEUS EVENTOS INSCRITOS ---");
        List<Evento> eventosInscritos = eventoService.listarEventosInscritosPorUsuario(usuarioLogado.getID());
        if (eventosInscritos.isEmpty()) {
            System.out.println("Você ainda não se inscreveu em nenhum evento.");
        } else {
            for (Evento e : eventosInscritos) {
                exibirDetalhesEvento(e);
            }
        }
    }

    private void cadastrarNovoEvento() throws IllegalArgumentException, PersistenceException {
        System.out.println("\n--- CADASTRO DE NOVO EVENTO ---");
        System.out.print("Nome do Evento: "); String nome = scanner.nextLine();
        System.out.print("Descrição: "); String descricao = scanner.nextLine();
        System.out.print("Data e Hora (AAAA-MM-DD HH:MM): "); String dataHoraStr = scanner.nextLine();
        LocalDateTime dataHora;
        try {
            dataHora = LocalDateTime.parse(dataHoraStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data e hora inválido! Use AAAA-MM-DD HH:MM.");
        }
        System.out.print("Limite de Vagas: "); int limiteVagas = lerInteiro();
        System.out.print("Latitude do Local: "); double latitude = lerDouble();
        System.out.print("Longitude do Local: "); double longitude = lerDouble();

        System.out.println("--- Endereço do Local do Evento ---");
        System.out.print("Logradouro: "); String logradouro = scanner.nextLine();
        System.out.print("Número: "); String numero = scanner.nextLine();
        System.out.print("Complemento (opcional): "); String complemento = scanner.nextLine();
        System.out.print("Bairro: "); String bairro = scanner.nextLine();
        System.out.print("CEP: "); String cep = scanner.nextLine();
        Endereco enderecoEvento = new Endereco(0, logradouro, numero, complemento, bairro, cep);

        System.out.print("ID da Categoria (Ex: 1 para 'Show', 2 para 'Palestra'): "); int categoriaId = lerInteiro();
        Categoria categoria = new Categoria(categoriaId, "");

        Evento novoEvento = new Evento(0, nome, enderecoEvento, categoria, dataHora, descricao,
                                       limiteVagas, latitude, longitude, usuarioLogado);

        boolean cadastrado = eventoService.cadastrarEvento(
            novoEvento.getNome(),
            novoEvento.getEndereco(),
            novoEvento.getCategoria().getID(),
            novoEvento.getDescricao(),
            novoEvento.getDataHora(),
            novoEvento.getCapacidade(),
            novoEvento.getLatitude(),
            novoEvento.getLongitude(),
            novoEvento.getCriador().getID()
        );

        if (cadastrado) {
            System.out.println("Evento cadastrado com sucesso!");
        } else {
            System.out.println("Não foi possível cadastrar o evento. Verifique os dados.");
        }
    }

    private void editarMeusDados() throws IllegalArgumentException, PersistenceException {
        System.out.println("\n--- EDITAR MEUS DADOS ---");
        System.out.println("Nome atual: " + usuarioLogado.getNome());
        System.out.print("Novo Nome (deixe em branco para manter): ");
        String novoNome = scanner.nextLine();
        if (!novoNome.trim().isEmpty()) {
            usuarioLogado.setNome(novoNome);
        }

        System.out.println("Email atual: " + usuarioLogado.getEmail());
        System.out.print("Novo Email (deixe em branco para manter): ");
        String novoEmail = scanner.nextLine();
        if (!novoEmail.trim().isEmpty()) {
            Usuario usuarioComEmailExistente = autenticacaoService.buscarPorEmail(novoEmail);
            if (usuarioComEmailExistente != null && usuarioComEmailExistente.getID() != usuarioLogado.getID()) {
                throw new IllegalArgumentException("O novo e-mail '" + novoEmail + "' já está em uso por outro usuário!");
            }
            usuarioLogado.setEmail(novoEmail);
        }

        System.out.print("Deseja alterar a senha? (s/n): ");
        String alterarSenhaOpt = scanner.nextLine();
        if (alterarSenhaOpt.equalsIgnoreCase("s")) {
            System.out.print("Senha Antiga: ");
            String senhaAntiga = scanner.nextLine();
            char[] senhaAntigaArray = senhaAntiga.toCharArray();

            System.out.print("Nova Senha (mínimo 8 caracteres): ");
            String novaSenha = scanner.nextLine();
            char[] novaSenhaArray = novaSenha.toCharArray();

            try {
                boolean senhaAlterada = autenticacaoService.alterarSenha(
                    usuarioLogado.getID(), senhaAntigaArray, novaSenhaArray
                );
                if (senhaAlterada) {
                    System.out.println("Senha alterada com sucesso!");
                } else {
                    System.out.println("Não foi possível alterar a senha. Verifique a senha antiga.");
                }
            } finally {
                Arrays.fill(senhaAntigaArray, ' ');
                Arrays.fill(novaSenhaArray, ' ');
            }
        }

        System.out.println("--- Editar Endereço ---");
        Endereco enderecoUsuario = usuarioLogado.getEndereco();
        boolean enderecoModificado = false;

        if (enderecoUsuario != null) {
            System.out.println("Logradouro atual: " + enderecoUsuario.getLogradouro());
            System.out.print("Novo Logradouro (deixe em branco para manter): ");
            String novoLogradouro = scanner.nextLine();
            if (!novoLogradouro.trim().isEmpty()) {
                enderecoUsuario.setLogradouro(novoLogradouro);
                enderecoModificado = true;
            }

            System.out.println("Número atual: " + enderecoUsuario.getNumeroLogradouro());
            System.out.print("Novo Número (deixe em branco para manter): ");
            String novoNumero = scanner.nextLine();
            if (!novoNumero.trim().isEmpty()) {
                enderecoUsuario.setNumeroLogradouro(novoNumero);
                enderecoModificado = true;
            }
            System.out.println("Complemento atual: " + enderecoUsuario.getComplemento());
            System.out.print("Novo Complemento (deixe em branco para manter): ");
            String novoComplemento = scanner.nextLine();
            if (!novoComplemento.trim().isEmpty()) {
                enderecoUsuario.setComplemento(novoComplemento);
                enderecoModificado = true;
            }

            System.out.println("Bairro atual: " + enderecoUsuario.getBairro());
            System.out.print("Novo Bairro (deixe em branco para manter): ");
            String novoBairro = scanner.nextLine();
            if (!novoBairro.trim().isEmpty()) {
                enderecoUsuario.setBairro(novoBairro);
                enderecoModificado = true;
            }

            System.out.println("CEP atual: " + enderecoUsuario.getCEP());
            System.out.print("Novo CEP (deixe em branco para manter): ");
            String novoCep = scanner.nextLine();
            if (!novoCep.trim().isEmpty()) {
                enderecoUsuario.setCEP(novoCep);
                enderecoModificado = true;
            }

            if (enderecoModificado) {
                try {
                    boolean usuarioAtualizado = autenticacaoService.atualizarUsuario(usuarioLogado);
                    if (usuarioAtualizado) {
                        System.out.println("Endereço atualizado localmente!");
                    }
                }  catch (PersistenceException e) {
                 System.out.println("Erro ao atualizar o endereço: " + e.getMessage());
                }
            }

        } else {
            System.out.println("Você não possui um endereço cadastrado. Deseja cadastrar agora? (s/n)");
            String criarEnderecoOpt = scanner.nextLine();
            if (criarEnderecoOpt.equalsIgnoreCase("s")) {
                System.out.println("--- Cadastrar Novo Endereço ---");
                System.out.print("Logradouro: "); String newLogradouro = scanner.nextLine();
                System.out.print("Número: "); String newNumero = scanner.nextLine();
                System.out.print("Complemento (opcional): "); String newComplemento = scanner.nextLine();
                System.out.print("Bairro: "); String newBairro = scanner.nextLine();
                System.out.print("CEP: "); String newCep = scanner.nextLine();
                Endereco novoEndereco = new Endereco(0, newLogradouro, newNumero, newComplemento, newBairro, newCep);
                try {
                    int idNovoEndereco = enderecoDAO.salvar(novoEndereco);
                    novoEndereco.setID(idNovoEndereco);
                    usuarioLogado.setEndereco(novoEndereco);
                    System.out.println("Novo endereço cadastrado e associado!");
                } catch (PersistenceException e) {
                    System.out.println("Erro ao cadastrar novo endereço: " + e.getMessage());
                }
            }
        }

        boolean atualizado = autenticacaoService.atualizarUsuario(usuarioLogado);
        if (atualizado) {
            System.out.println("Seus dados foram atualizados com sucesso!");
        } else {
            System.out.println("Não foi possível atualizar seus dados! Tente novamente.");
        }
    }

    private void deletarMinhaConta() throws PersistenceException {
        System.out.println("\n--- DELETAR MINHA CONTA ---");
        System.out.print("Tem certeza que deseja deletar sua conta? Esta ação é irreversível! (digite 'SIM' para confirmar): ");
        String confirmacao = scanner.nextLine();

        if (confirmacao.equalsIgnoreCase("SIM")) {
            boolean deletado = autenticacaoService.deletarUsuario(usuarioLogado.getID());

            if (deletado) {
                System.out.println("Sua conta foi deletada com sucesso!");
                usuarioLogado = null;
            } else {
                System.out.println("Não foi possível deletar sua conta!");
            }
        } else {
            System.out.println("Deleção de conta cancelada!");
        }
    }

    private void inscreverEmEvento() throws PersistenceException {
        System.out.println("\n--- INSCRIÇÃO EM EVENTO ---");
        System.out.print("Digite o ID do evento que deseja se inscrever: ");
        int idEvento = lerInteiro();

        boolean inscrito = eventoService.inscreverUsuarioEmEvento(usuarioLogado.getID(), idEvento);

        if (inscrito) {
            System.out.println("Inscrição realizada com sucesso!");
        } else {
            System.out.println("Não foi possível realizar a inscrição! Verifique o ID do evento ou se as vagas esgotaram.");
        }
    }

    private int lerInteiro() {
        while (!scanner.hasNextInt()) {
            System.out.print("Entrada inválida! Por favor, digite um número inteiro: ");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    private double lerDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.print("Entrada inválida! Por favor, digite um número decimal: ");
            scanner.next();
        }
        double valor = scanner.nextDouble();
        scanner.nextLine();
        return valor;
    }

    private void exibirDetalhesEvento(Evento e) {
        System.out.println("------------------------------------");
        System.out.println("ID: " + e.getID());
        System.out.println("Nome: " + e.getNome());
        System.out.println("Descrição: " + e.getDescricao());
        System.out.println("Data/Hora: " + e.getDataHora());
        System.out.println("Vagas: " + e.getCapacidade());
        if (e.getEndereco() != null) {
            System.out.println("Local: " + e.getEndereco().getLogradouro() + ", " + e.getEndereco().getNumeroLogradouro() + ", " + e.getEndereco().getBairro() + " - " + e.getEndereco().getCEP());
        }
        if (e.getCategoria() != null) {
            System.out.println("Categoria: " + e.getCategoria().getNome());
        }
        if (e.getCriador() != null) {
            System.out.println("Criador: " + e.getCriador().getNome() + " (" + e.getCriador().getEmail() + ")");
        }
        System.out.println("Latitude: " + e.getLatitude() + ", Longitude: " + e.getLongitude());
        System.out.println("------------------------------------");
    }
}