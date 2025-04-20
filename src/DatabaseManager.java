package tarefas;

import java.sql.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import com.google.gson.Gson;

public class DatabaseManager {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar driver SQLite: " + e.getMessage());
        }
    }

    private static final String DB_URL = "jdbc:sqlite:tarefas.db";
    
    public static void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS tarefas (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "titulo TEXT NOT NULL," +
                     "descricao TEXT," +  
                     "categoria TEXT," +
                     "prioridade TEXT)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            
            boolean descricaoExists = false;
            ResultSet rs = conn.getMetaData().getColumns(null, null, "tarefas", "descricao");
            if (rs.next()) {
                descricaoExists = true;
            }
            
            if (!descricaoExists) {
                stmt.execute("ALTER TABLE tarefas ADD COLUMN descricao TEXT");
                System.out.println("Coluna 'descricao' adicionada à tabela.");
            }
            
            System.out.println("Tabela verificada/atualizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar/atualizar tabela: " + e.getMessage());
        }
    }
    
    public static void inserirTarefa(Task tarefa) {
        String sql = "INSERT INTO tarefas(titulo, descricao, categoria, prioridade) VALUES(?,?,?,?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarefa.getTitulo());
            pstmt.setString(2, tarefa.getDescricao() != null ? tarefa.getDescricao() : "");
            pstmt.setString(3, tarefa.getCategoria());
            pstmt.setString(4, tarefa.getPrioridade());
            pstmt.executeUpdate();
            System.out.println("Tarefa salva no banco de dados!");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir tarefa: " + e.getMessage());
        }
    }
    
    public static List<Task> listarTarefas() {
        List<Task> tarefas = new ArrayList<>();
        String sql = "SELECT id, titulo, descricao, categoria, prioridade FROM tarefas";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\nLista de Tarefas:");
            while (rs.next()) {
                Task task = new Task(
                    rs.getString("titulo"),
                    rs.getString("descricao"),
                    rs.getString("categoria"),
                    rs.getString("prioridade")
                );
                tarefas.add(task);
                
                System.out.println(
                    "ID: " + rs.getInt("id") + "\n" +
                    "Título: " + rs.getString("titulo") + "\n" +
                    "Descrição: " + rs.getString("descricao") + "\n" +
                    "Categoria: " + rs.getString("categoria") + "\n" +
                    "Prioridade: " + rs.getString("prioridade") + "\n" +
                    "-----------------------------"
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas: " + e.getMessage());
        }
        return tarefas;
    }
    
    public static void excluirTarefa(int id) {
        String sql = "DELETE FROM tarefas WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Tarefa excluída com sucesso!");
            } else {
                System.out.println("Nenhuma tarefa encontrada com o ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir tarefa: " + e.getMessage());
        }
    }
    
    public static void testarConexao() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Conexão com SQLite estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println("Falha na conexão: " + e.getMessage());
        }
    }

    public static void exportarParaJSON(String caminho) {
        List<Task> tarefas = new ArrayList<>();
        String sql = "SELECT titulo, descricao, categoria, prioridade FROM tarefas";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                Task task = new Task(
                    rs.getString("titulo"),
                    rs.getString("descricao"),
                    rs.getString("categoria"),
                    rs.getString("prioridade")
                );
                tarefas.add(task);
            }
            
            Gson gson = new Gson();
            String json = gson.toJson(tarefas);
            Files.write(Path.of(caminho), json.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("Dados exportados para: " + caminho);
        } catch (Exception e) {
            System.err.println("Erro ao exportar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void executarAnalisePython() {
        try {
            String jsonPath = "tarefas_exportadas.json";
            exportarParaJSON(jsonPath);
            
            if (!Files.exists(Path.of(jsonPath))) {
                throw new FileNotFoundException("Arquivo JSON não foi gerado");
            }
            
            ProcessBuilder pb = new ProcessBuilder("python", "analise.py")
                .directory(new File(System.getProperty("user.dir")))
                .inheritIO(); 
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("Análise Python concluída com sucesso!");
            } else {
                System.err.println("Script Python falhou com código: " + exitCode);
            }
            
        } catch (Exception e) {
            System.err.println("Erro na análise Python: " + e.getMessage());
            e.printStackTrace();
        }
    }
}