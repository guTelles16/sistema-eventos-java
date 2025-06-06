package projeto_x.view;

import projeto_x.service.AutenticacaoService;
import projeto_x.model.Usuario;
import java.util.Scanner;
import java.util.Arrays;

public class Console {
    // Simulação de serviço de autenticação (deve ser instanciado corretamente)
    private static AutenticacaoService autenticacaoService = new AutenticacaoService();

    public static void menuCadastro(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        char[] senhaChars = scanner.nextLine().toCharArray();

        try {
            if (autenticacaoService.cadastrarUsuario(nome, email, senhaChars)) {
                System.out.println("Usuário cadastrado com sucesso!");
            } else {
                System.out.println("Falha ao cadastrar usuário. Verifique seus dados!");
            }
        } finally {
            Arrays.fill(senhaChars, ' '); // Zerando a senha na memória
        }
    }

    public static void menuLogin(Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        char[] senhaChars = scanner.nextLine().toCharArray();

        try {
            Usuario usuarioLogado = autenticacaoService.login(email, senhaChars);

            if (usuarioLogado != null) {
                System.out.println("Login bem-sucedido! Bem-vindo, " + usuarioLogado.getNome());
            } else {
                System.out.println("Email ou senha inválidos.");
            }
        } finally {
            Arrays.fill(senhaChars, ' '); // Zerando a senha na memória
        }
    }

    public static void cadastrarEvento(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Id : ");
        int id = scanner.nextInt();

        try  { 
            Categoria cadastrarEvento = eventoService.categoria(id, nome);
                System.out.println("Evento cadastrado com sucesso! ");
        } else {
                System.out.println("Erro ao cadastrar evento ");
        }
        }
    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bem-vindo ao sistema!");

        // Exemplo de chamada dos métodos
        menuCadastro(scanner);
        menuLogin(scanner);
        cadastrarEvento(scanner);

        scanner.close(); // Fechando o scanner apenas no final
    }

