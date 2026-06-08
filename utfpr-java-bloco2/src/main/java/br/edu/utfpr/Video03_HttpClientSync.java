package br.edu.utfpr;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * VÍDEO 3 — HttpClient síncrono
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Consulta a cotação do dólar (USD) usando a API publica Frankfurter
 */
public class Video03_HttpClientSync {

    static void main() throws Exception {

        // HttpClient e AutoCloseable desde o Java 21 -> try-with-resources.
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build()) {

            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.frankfurter.dev/v1/latest?base=USD&symbols=BRL"))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            // Chamada bloqueante: espera a resposta chegar.
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            IO.println("Status: " + response.statusCode());
            IO.println("Corpo:  " + response.body());

            // Versao HTTP efetivamente usada:
            IO.println("Versao: " + response.version());
        }
    }
}