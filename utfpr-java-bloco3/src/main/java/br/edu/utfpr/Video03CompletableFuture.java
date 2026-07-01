package br.edu.utfpr;

import java.util.concurrent.CompletableFuture;

/**
 * VÍDEO 3 — CompletableFuture: revisão e aprofundamento
 * Requer Java 25
 */
public class Video03CompletableFuture {

    void main() throws Exception {

        // supplyAsync: inicia tarefa assíncrona que retorna valor
        final CompletableFuture<Integer> precoBase = CompletableFuture.supplyAsync(() -> {
            dormir(100);
            return 100;
        });

        // thenApply: transforma o resultado (sem bloquear)
        final CompletableFuture<Integer> precoComImposto =
                precoBase.thenApply(preco -> preco + preco * 10 / 100);

        // thenCompose: encadeia OUTRA operação assíncrona (evita future de future)
        final CompletableFuture<String> notaFiscal = precoComImposto
                .thenCompose(valor -> CompletableFuture.supplyAsync(() -> "Nota fiscal: R$ " + valor));

        // exceptionally: trata falha sem quebrar a cadeia
        final CompletableFuture<String> notaSegura =
                notaFiscal.exceptionally(excecao -> "Falhou: " + excecao.getMessage());

        // thenCombine: junta dois futures independentes
        final CompletableFuture<Integer> valorFrete = CompletableFuture.supplyAsync(() -> 20);
        final CompletableFuture<String> resultadoTotal = notaSegura.thenCombine(valorFrete,
                (nota, frete) -> nota + " (+ frete R$ " + frete + ")");

        IO.println(resultadoTotal.get()); // só aqui esperamos o resultado final
    }

    static void dormir(final long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (final InterruptedException excecao) {
            Thread.currentThread().interrupt();
        }
    }
}