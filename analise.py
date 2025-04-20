import json
import matplotlib.pyplot as plt
import pandas as pd
import os
import sys
from datetime import datetime

def analisar_tarefas(caminho_json):
    try:
        if not os.path.exists(caminho_json):
            raise FileNotFoundError(f"Arquivo {caminho_json} não encontrado")
        
        with open(caminho_json, 'r', encoding='utf-8') as f:
            tarefas = json.load(f)
        
        if not tarefas:
            print("Nenhuma tarefa encontrada para análise.")
            return

        df = pd.DataFrame(tarefas)
        
        plt.style.use('default')  
        fig, axs = plt.subplots(1, 2, figsize=(15, 6))
        
        if 'categoria' in df.columns:
            contagem = df['categoria'].value_counts()
            axs[0].bar(contagem.index, contagem.values, color='#4c72b0')
            axs[0].set_title("Tarefas por Categoria", pad=20)
            axs[0].set_xlabel("Categoria")
            axs[0].set_ylabel("Quantidade")
            axs[0].tick_params(axis='x', rotation=45)
        
        if 'prioridade' in df.columns:
            cores = {'Urgente': '#f70808', 'Normal': '#df7b16', 'Baixa': '#d9df16'}
            prioridades = df['prioridade'].value_counts()
            axs[1].pie(prioridades, 
                      labels=prioridades.index,
                      autopct='%1.1f%%',
                      colors=[cores.get(p, '#999999') for p in prioridades.index])
            axs[1].set_title("Distribuição de Prioridades", pad=20)
        
        plt.tight_layout()
        
        output_file = f'analise_tarefas.png'
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"Análise concluída! Gráfico salvo como '{output_file}'")
        plt.close()
        
    except Exception as e:
        print(f"Erro na análise: {str(e)}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) > 1:
        analisar_tarefas(sys.argv[1])
    else:
        analisar_tarefas("tarefas_exportadas.json")