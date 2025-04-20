package tarefas;

import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("🌟 Sistema de Gerenciamento de Tarefas 🌟");
        
        DatabaseManager.testarConexao();
        DatabaseManager.criarTabela();
        
        int opcao;
        do {
            exibirMenu();
            opcao = scanner.nextInt();
            scanner.nextLine();
            
            switch(opcao) {
                case 1:
                    adicionarTarefa();
                    break;
                case 2:
                    DatabaseManager.listarTarefas();
                    break;
                case 3:
                    removerTarefa();
                    break;
                case 4:
                    DatabaseManager.executarAnalisePython();
                    break;
                case 5:
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while(opcao != 5);
        
        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n=== MENU ===");
        System.out.println("1. Adicionar Tarefa");
        System.out.println("2. Listar Tarefas");
        System.out.println("3. Remover Tarefa");
        System.out.println("4. Análise em Python");
        System.out.println("5. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void adicionarTarefa() {
        System.out.println("\n📝 Nova Tarefa");
        
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();
        
        System.out.print("Categoria: ");
        String categoria = scanner.nextLine();
        
        System.out.print("Prioridade (1-Urgente, 2-Normal, 3-Baixa): ");
        String prioridade = switch(scanner.nextInt()) {
            case 1 -> "Urgente";
            case 2 -> "Normal";
            case 3 -> "Baixa";
            default -> "Normal";
        };
        scanner.nextLine();
        
        Task novaTarefa = new Task(titulo, descricao, categoria, prioridade);
        DatabaseManager.inserirTarefa(novaTarefa);
    }

    private static void removerTarefa() {
        System.out.println("\n🗑️ Remover Tarefa");
        DatabaseManager.listarTarefas();
        
        System.out.print("\nDigite o ID da tarefa a ser removida: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Por favor, digite um número válido!");
            scanner.next(); 
        }
        int id = scanner.nextInt();
        scanner.nextLine();
        
        DatabaseManager.excluirTarefa(id);
    }
}