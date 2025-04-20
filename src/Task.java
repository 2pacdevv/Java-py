package tarefas;

public class Task {
    private String titulo;
    private String descricao;
    private String categoria;
    private String prioridade;

    public Task(String titulo, String descricao, String categoria, String prioridade) {
        this.titulo = titulo;
        this.descricao = descricao != null ? descricao : "";
        this.categoria = categoria;
        this.prioridade = prioridade;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getCategoria() { return categoria; }
    public String getPrioridade() { return prioridade; }
}