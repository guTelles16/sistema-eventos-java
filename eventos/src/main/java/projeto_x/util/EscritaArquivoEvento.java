package projeto_x.util;

import projeto_x.model.Evento;
import projeto_x.model.Usuario;
import projeto_x.model.Endereco;
import projeto_x.model.Categoria;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class EscritaArquivoEvento {
    private static final String ARQUIVO_EVENTOS = "events_data.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Escreve um evento no arquivo de backup.
     * É chamado após o evento ser salvo com sucesso no banco de dados.
     * 
     * @param evento O objeto a ser escrito.
     * @throws IOException Falha ao salvar evento no arquivo de backup.
     */
    public static void escreverEvento(Evento evento) throws IOException {
        // Se true, adiciona o BufferedWriter no final do arquivo (append)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_EVENTOS, true))) {

            StringBuilder linha = new StringBuilder();

            // Dados do Evento
            linha.append(evento.getId()).append("|");
            linha.append(evento.getNome()).append("|");
            linha.append(evento.getDescricao()).append("|");
            linha.append(evento.getDataHora().format(FORMATTER)).append("|");
            linha.append(evento.getCapacidade()).append("|");

            // Dados do Endereço do Evento
            if (evento.getEndereco() != null) {
                Endereco enderecoEvento = evento.getEndereco();
                linha.append(enderecoEvento.getLogradouro()).append("|");
                linha.append(enderecoEvento.getNumeroLogradouro()).append("|");
                linha.append(enderecoEvento.getComplemento()).append("|");
                linha.append(enderecoEvento.getBairro()).append("|");
                linha.append(enderecoEvento.getCEP()).append("|");
            } else {
                // Se o endereço não existir, preenche com valores vazios
                linha.append("N/A|N/A|N/A|N/A|N/A|");
            }

            // Dados do Criador (Usuário)
            if (evento.getCriador() != null) {
                Usuario criador = evento.getCriador();
                linha.append(criador.getId()).append("|");
                linha.append(criador.getNome()).append("|");
                linha.append(criador.getEmail()).append("|");
            } else {
                // Se o criador não existir, preenche com valores vazios
                linha.append("N/A|N/A|N/A|");
            }

            // Dados da Categoria
            if (evento.getCategoria() != null) {
                Categoria categoria = evento.getCategoria();
                linha.append(categoria.getID()).append("|");
                linha.append(categoria.getNome()).append("|");
            } else {
                linha.append("N/A|N/A|");
            }

            // Remove o ultimo "|" e adiciona uma nova linha
            // Verifica se a linha não está vazia antes de remover o ultimo "|"
            if (linha.length() > 0 && linha.charAt(linha.length() -1) == '|') {
                linha.setLength(linha.length() -1);
            }
            linha.append(System.lineSeparator()); // Quebra de linha

            writer.write(linha.toString());
        } catch (IOException e) {
            System.err.println("Erro ao escrever evento no arquivo de backup: " + e.getMessage());
        }
    }

    /**
     * Método para leitura do arquivo de backup.
     * 
     * @throws IOException Falha ao ler arquivo de backup.
     * @throws FileNotFoundException Se o arquivo de backup não for encontrado.
     */
    public static void lerEventoParaVisualizacao() throws IOException, FileNotFoundException {
        System.out.println("\n--- Conteúdo do Arquivo de Backup de Eventos ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_EVENTOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                System.out.println(linha);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo de backup não encontrado!");
        
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de backup: " + e.getMessage());
        }

        System.out.println("----------------------------------------------");
    }
}
