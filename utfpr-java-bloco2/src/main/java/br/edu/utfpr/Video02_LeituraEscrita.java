package br.edu.utfpr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * VÍDEO 2 — Lendo e escrevendo arquivos com eficiência
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Reaproveita o arquivo de moedas criado no vídeo 1.
 */
public class Video02_LeituraEscrita {

    static void main() throws IOException {

        final Path arquivo = Path.of("dados-cotacoes", "entrada", "moedas.txt");

        // Garante que o arquivo do video 1 exista (para rodar isolado).
        if (!Files.exists(arquivo)) {
            Files.createDirectories(arquivo.getParent());
            Files.writeString(arquivo, "USD\nEUR\nGBP\n");
        }

        // 1) Leitura conveniente: carrega tudo (ok para arquivo pequeno).
        final List<String> todas = Files.readAllLines(arquivo);
        IO.println("Linhas lidas de uma vez: " + todas);

        // 2) Leitura eficiente: stream lazy, linha a linha.
        //    O stream segura o arquivo aberto -> try-with-resources obrigatório.
        IO.println("\nMoedas validas (filtradas via stream):");
        try (Stream<String> linhas = Files.lines(arquivo)) {
            linhas.map(String::trim)
                  .filter(s -> !s.isBlank())
                  .filter(s -> s.length() == 3) // codigo ISO de moeda
                  .forEach(s -> IO.println("  - " + s));
        }

        // 2b) MESMA operação do item 2, mas com Java NIO + BufferedReader em loop.
        //     Files.newBufferedReader devolve um BufferedReader (java.io) sobre o
        //     canal aberto pela NIO.2. Sem Stream: o controle do laco fica explicito.
        IO.println("\nMoedas validas (NIO + BufferedReader em loop):");
        try (BufferedReader leitor = Files.newBufferedReader(arquivo)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                final String moeda = linha.trim();
                if (!moeda.isBlank() && moeda.length() == 3) {
                    IO.println("  - " + moeda);
                }
            }
        }

        // 2c) MESMA operação do item 2, mas SÓ com a API classica java.io,
        //     na forma mais crua possivel: FileReader puro, sem buffer.
        //     Desvantagem evidente: read() devolve um caractere por vez (int),
        //     entao temos que montar cada linha "na mao" e detectar o \n.
        //     Sem buffer = muitas chamadas de I/O = lento. Compare com 2 e 2b.
        IO.println("\nMoedas validas (FileReader puro, sem buffer):");
        try (FileReader leitor = new FileReader(arquivo.toFile())) {
            final StringBuilder linha = new StringBuilder();
            int xar;
            while ((xar = leitor.read()) != -1) {
                if (xar == '\n') {
                    processarMoeda(linha.toString());
                    linha.setLength(0); // limpa para a proxima linha
                } else {
                    linha.append((char) xar);
                }
            }
            processarMoeda(linha.toString()); // ultima linha (sem \n no fim)
        }

        // 3) Escrita incremental com BufferedWriter (APPEND).
        final Path log = Path.of("dados-cotacoes", "processamento.log");
        try (BufferedWriter writer = Files.newBufferedWriter(log, CREATE, APPEND)) {
            writer.write("Processado em " + java.time.LocalDateTime.now());
            writer.newLine();
        }
        IO.println("\nLog gravado em: " + log.toAbsolutePath());
    }

    // Mesma regra de filtro usada nos itens 2/2b, reutilizada pelo 2c.
    private static void processarMoeda(String linha) {
        final String moeda = linha.trim();
        if (!moeda.isBlank() && moeda.length() == 3) {
            IO.println("  - " + moeda);
        }
    }
}