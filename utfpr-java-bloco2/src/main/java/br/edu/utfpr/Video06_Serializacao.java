package br.edu.utfpr;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * VÍDEO 6 — Serialização e formatos de dados sem libs
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Persiste cotações de duas formas: serialização binária nativa e texto estruturado (CSV), comparando as abordagens.
 */
public class Video06_Serializacao {

    // record + Serializable: o serialVersionUID fixa a versao do contrato.
    record Cotacao(String moeda, double valor) implements Serializable {
    }

    static void main() throws Exception {

        final List<Cotacao> cotacoes = List.of(
                new Cotacao("BRL", 5.43),
                new Cotacao("EUR", 0.92),
                new Cotacao("GBP", 0.79)
        );

        final Path dir = Path.of("dados-cotacoes", "saida");
        Files.createDirectories(dir);

        // 1) Serialização binária nativa.
        final Path bin = dir.resolve("cotacoes.ser");
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(bin))) {
            out.writeObject(cotacoes);
        }
        IO.println("Serializado (binario): " + Files.size(bin) + " bytes");

        // Desserializacao (cuidado: so faca com dados confiaveis).
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(bin))) {
            @SuppressWarnings("unchecked")
            final List<Cotacao> recuperadas = (List<Cotacao>) in.readObject();
            IO.println("Recuperadas: " + recuperadas);
        }

        // 2) Texto estruturado (CSV) — portavel, sem libs.
        final Path csv = dir.resolve("cotacoes.csv");

        final String conteudo = "moeda,valor\n" + cotacoes.stream()
                .map(c -> c.moeda() + "," + c.valor())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);

        Files.writeString(csv, conteudo);
        IO.println("\nCSV gerado:\n" + Files.readString(csv));
    }
}