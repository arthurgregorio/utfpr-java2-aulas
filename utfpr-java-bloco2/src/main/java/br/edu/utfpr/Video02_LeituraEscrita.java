package br.edu.utfpr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

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

        // 2) Leitura eficiente: stream preguiçoso, linha a linha.
        //    O stream segura o arquivo aberto -> try-with-resources obrigatório.
        IO.println("\nMoedas validas (filtradas via stream):");
        try (Stream<String> linhas = Files.lines(arquivo)) {
            linhas.map(String::trim)
                  .filter(s -> !s.isBlank())
                  .filter(s -> s.length() == 3) // codigo ISO de moeda
                  .forEach(s -> IO.println("  - " + s));
        }

        // 3) Escrita incremental com BufferedWriter (APPEND).
        final Path log = Path.of("dados-cotacoes", "processamento.log");
        try (BufferedWriter w = Files.newBufferedWriter(log,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write("Processado em " + java.time.LocalDateTime.now());
            w.newLine();
        }
        IO.println("\nLog gravado em: " + log.toAbsolutePath());
    }
}