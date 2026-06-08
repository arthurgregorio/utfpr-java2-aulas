package br.edu.utfpr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * VÍDEO 1 — NIO.2: Path e Files
 * <p>
 * Requer Java 25 LTS
 * <p>
 */
public class Video01_PathFiles {

    static void main() throws IOException {

        // final File file = new File("dados-cotacoes" + File.separator + "entrada" + File.separator + "moedas.txt");

        // 1) Criar Paths sem concatenar strings na mão.
        final Path base = Path.of("dados-cotacoes");
        Path entrada = base.resolve("entrada"); // dados-cotacoes/entrada
        Path arquivo = entrada.resolve("moedas.txt"); // .../entrada/moedas.txt

        IO.println("Caminho absoluto: " + arquivo.toAbsolutePath());
        IO.println("Nome do arquivo:  " + arquivo.getFileName());
        IO.println("Pasta pai:        " + arquivo.getParent());

        // 2) Criar a estrutura de pastas (createDirectories nao falha se ja existir).
        Files.createDirectories(entrada);
        IO.println("\nPasta 'entrada' existe? " + Files.exists(entrada));

        // 3) Escrever um arquivo de exemplo (escrita simples para arquivo pequeno).
        Files.writeString(arquivo, "USD\nEUR\nGBP\n");
        IO.println("Arquivo criado com tamanho: " + Files.size(arquivo) + " bytes");

        // 4) Operacoes de caminho uteis.
        final Path relativo = base.relativize(arquivo); // entrada/moedas.txt
        IO.println("Caminho relativo a base: " + relativo);
        IO.println("Normalizado: " + Path.of("a/./b/../c").normalize());
    }
}