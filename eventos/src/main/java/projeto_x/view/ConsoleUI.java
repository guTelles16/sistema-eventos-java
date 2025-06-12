package projeto_x.view;

import projeto_x.service.AutenticacaoService;
import projeto_x.service.EventoService;
import projeto_x.model.Usuario;
import projeto_x.model.Evento;
import projeto_x.dao.CategoriaDAO;
import projeto_x.dao.EnderecoDAO;
import projeto_x.dao.UsuarioDAO;
import projeto_x.model.Categoria;
import projeto_x.util.PersistenceException;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Arrays;

public class ConsoleUI {
    private static Scanner scanner = new Scanner(System.in); 
    private static Usuario usuarioLogado;
    private AutenticacaoService autenticacaoService;
    private EventoService eventoService;

    public static void main(String[] args) {
        try {
            EnderecoDAO enderecoDAO = new EnderecoDAO();
            CategoriaDAO categoriaDAO = new CategoriaDAO();
            UsuarioDAO usuarioDAO = new UsuarioDAO(enderecoDAO);

            usuarioService = new UsuarioService()
    }

    public void iniciar() throws PersistenceException {
        int opcao;
        do {
            System.out.println("\n--- BEM-VINDO AO ORGANIZADOR DE EVENTOS ---");
            exibirMenuPrincipal();

            try{
                switch (opcao) {
                    case 1:
                        fazerLogin();
                        break;
                    case 2:
                    cadastrarNovoUsuario();
                        break;
                    case 0:
                        System.out.println("Saindo do sistema...Até logo!");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Erro de entrada: " + e.getMessage());
                
            } catch (PersistenceException e) {
                System.out.println("Erro de banco de dados: " + e.getMessage());
                System.out.println("Por favor, tente novamente mais tarde!");
            }
        } while (opcao != 0 && usuarioLogado = null);

        if (usuarioLogado != null) {

        }
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1. Entrar (Login)");
        System.out.println("2. Cadastrar-se");
        System.out.println("0. Sair");
        System.out.println("Escolha uma opção: ");
    }

    private void fazerLogin() throws PersistenceException {
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        char[] senhaChars = scanner.nextLine().toCharArray();

        try {
            Usuario usuarioLogado = autenticacaoService.login(email, senhaChars);

            if (usuarioLogado != null) {
                System.out.println("Login bem-sucedido! Bem-vindo, " + usuarioLogado.getNome());
                menuUsuarioLogado();
            } else {
                System.out.println("Email ou senha inválidos. Tente novamente!");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Erro de entrada: " + e.getMessage());
        
        } catch (PersistenceException e) {
            System.err.println("Ocorreu um erro ao tentar fazer login: " + e.getMessage());
        
        } finally {
            Arrays.fill(senhaChars, ' '); // Zerando a senha na memória
        }
    }

    private void cadastrarNovoUsuario() throws PersistenceException {
        System.out.println("\n--- Cadastro de Usuário ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        char[] senhaChars = scanner.nextLine().toCharArray();

        try {
            boolean sucesso = autenticacaoService.cadastrarUsuario(nome, email, null, senhaChars);
            if (sucesso) {
                System.out.println("Usuário cadastrado com sucesso!");
            } else {
                System.out.println("Falha ao cadastrar usuário. Verifique seus dados e tente novamente!");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Erro de entrada: " + e.getMessage());
        
        } catch (PersistenceException e) {
            System.err.println("Erro ao tentar cadastrar usuário: " + e.getMessage());
        
        } finally {
            Arrays.fill(senhaChars, ' '); // Zerando a senha na memória
        }
    }

    private void menuUsuarioLogado() throws PersistenceException {
        boolean isMenuUsuario = true;
        while (isMenuUsuario) {
            System.out.println("\n--- Menu de Usuário ---");
            System.out.println("1. Listar Eventos");
            System.out.println("2. Criar Novo Evento");
            System.out.println("3. Meus Eventos");
            System.out.println("0. Sair da Conta");
            System.out.println("Escolha uma opção: ");

            try {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        eventoService.listarEventos();
                        System.out.println("Listando eventos...");
                        break;
                    case 2:
                        menuCriarEvento();
                        System.out.println("Criando novo evento...");
                        break;
                    case 3:
                        eventoService.listarEventosInscritosPorUsuario(usuarioLogado);
                        System.out.println("Listando meus eventos...");
                        break;
                    case 0:
                        usuarioLogado = null; // Desloga o usuário
                        isMenuUsuario = false;
                        System.out.println("Você foi desconectado com sucesso!");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida! Por favor, digite um número.");
                scanner.nextLine();
            
            } catch (PersistenceException e) {
                System.err.println("Ocorreu um erro ao acessar o menu de usuário: " + e.getMessage());
            }
        }
    }

    private void cadastrarEvento() throws PersistenceException {
        System.out.println("\n--- Cadastro de Evento ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.println("");

        try {
            boolean sucesso = eventoService.cadastrarEvento(nome);
            if (sucesso) {
                System.out.println("Evento cadastrado com sucesso!");
            } else {
                System.out.println("Falha ao cadastrar evento. Verifique os dados e tente novamente!");
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de entrada: " + e.getMessage());

        } catch (PersistenceException e) {
            System.out.println("Erro ao tentar cadastrar evento: " + e.getMessage());
        
        } finally {
            scanner.close();
        }
    }
    
    private void cadastrarCategoria() throws PersistenceException {
        System.out.println("\n--- Cadastro de Categoria ---");
        System.out.println("Nome: ");
        String nome = scanner.nextLine();
        
        try {
            boolean sucesso = eventoService.cadastrarCategoria(nome);
            if (sucesso) {
                System.out.println("Categoria cadastrada com sucesso!");
            } else {
                System.out.println("Falha ao cadastrar categoria. Verifique os dados e tente novamente!");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de entrada: " + e.getMessage());
        
        } catch (PersistenceException e) {
            System.err.println("Erro ao tentar cadastrar categoria: " + e.getMessage());
        
        } finally {
            scanner.close();
        }
    }
}
