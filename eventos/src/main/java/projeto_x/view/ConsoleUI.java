package projeto_x.view;

import projeto_x.service.AutenticacaoService;
import projeto_x.service.EventoService;
import projeto_x.model.Usuario;
import projeto_x.model.Evento;
import projeto_x.model.Categoria;
import projeto_x.util.PersistenceException;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Arrays;

public class ConsoleUI {
    private Scanner scanner = new Scanner(System.in); 
    private AutenticacaoService autenticacaoService;
    private EventoService eventoService;
    private Usuario usuarioLogado;

    public ConsoleUI(AutenticacaoService autenticacaoService, EventoService eventoService) {
        this.autenticacaoService = autenticacaoService;
        this.eventoService = eventoService;
    }

    public void iniciar() throws PersistenceException {
        System.out.println("Bem-vindo ao sistema de gerenciamento de eventos!");
        boolean rodando = true;
        while (rodando) {
            exibirMenuPrincipal();
            try {
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Quebra de linha

                switch (opcao) {
                    case 1:
                        menuLogin();
                        break;
                    case 2:
                        menuCadastro();
                        break;
                    case 3:
                        // Se o usuário já estiver logado, exibe menu de usuário
                        if (usuarioLogado != null) {
                            menuUsuarioLogado();
                        } else {
                            System.out.println("Por favor, faça login para acessar esta opção!");
                        }
                        break;
                    case 0:
                        rodando = false;
                        System.out.println("Saindo do sistema...Até logo!");
                        break;
                    default:
                        System.out.println("Opção inválida! Por favor, escolha uma opção válida.");
                }
            
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida! Por favor, digite um número.");
                scanner.nextLine(); // Limpa o buffer do scanner
            
            } catch (PersistenceException e) {
                System.err.println("Ocorreu um erro inesperado no menu principal: " + e.getMessage());
            }
        }
        scanner.close(); // Fecha o scanner ao sair
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1. Login");
        System.out.println("2. Cadastro");
        System.out.println("3. Acessar Menu de Usuário (necessário estar logado)");
        System.out.println("0. Sair");
        System.out.println("Escolha uma opção: ");
    }

    private void menuLogin() throws PersistenceException {
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

    private void menuCadastro() throws PersistenceException {
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
                        eventoService.listarMeusEventos(usuarioLogado);
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
    public static void main(String[] args) {
        
    }
}
