package br.edu.utfpr;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * VÍDEO 4 — HttpClient assíncrono
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Consulta varias moedas em PARALELO e compara com o tempo do video 3
 */
public class Video04_HttpClientAsync {

    static void main() {

        final List<String> moedas = List.of("BRL", "EUR", "GBP", "JPY", "CAD");

        try (HttpClient client = HttpClient.newHttpClient()) {
            long inicio = System.currentTimeMillis();

            // Dispara TODAS as requisições sem bloquear.
            final List<CompletableFuture<String>> futuros = moedas.stream()
                    .map(m -> consultar(client, m))
                    .toList();

            // Espera todas terminarem.
            CompletableFuture
                    .allOf(futuros.toArray(new CompletableFuture[0]))
                    .join();

            // Coleta os resultados ja prontos.
            futuros.forEach(f -> IO.println(f.join()));

            long fim = System.currentTimeMillis();
            IO.println("\nTempo total: " + (fim - inicio) + " ms " + "(aproxima-se da requisicao mais lenta, nao da soma)");
        }
    }

    static CompletableFuture<String> consultar(HttpClient client, String moeda) {

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.frankfurter.dev/v1/latest?base=USD&symbols=" + moeda))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body) // transforma resposta em corpo
                .thenApply(corpo -> "USD->" + moeda + ": " + corpo)
                .exceptionally(ex -> "USD->" + moeda + ": FALHOU (" + ex.getMessage() + ")");
    }
}