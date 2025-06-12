package projeto_x;

import projeto_x.view.ConsoleUI;
import projeto_x.util.PersistenceException;

public class App {
    public static void main(String[] args) {
        try {
            ConsoleUI ui = new ConsoleUI();
            ui.menuPrincipal();
        } catch (PersistenceException e) {
            System.out.println("Erro fatal de inicialização do sistema: " + e.getMessage());
            System.out.println("Por favor, verifique a conexão com o banco de dados e as configurações.");
        }
    }
}