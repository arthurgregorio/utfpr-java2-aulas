package br.edu.utfpr;

import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * VÍDEO 5 — Stream Gatherers, parte 1: os prontos (JEP 485, final no JDK 24)
 * Requer Java 25 LTS
 */
public class Video05GatherersProntos {

    void main() {

        final List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7);

        // ---- windowFixed: agrupa em janelas fixas (batches) ----
        final List<List<Integer>> janelasFixas = numeros.stream()
                .gather(Gatherers.windowFixed(3))
                .toList();

        IO.println("windowFixed(3): " + janelasFixas); // [[1,2,3],[4,5,6],[7]]

        // ---- windowSliding: janelas deslizantes (sobrepostas) ----
        final List<List<Integer>> janelasDeslizantes = numeros.stream()
                .gather(Gatherers.windowSliding(3))
                .toList();

        IO.println("windowSliding(3): " + janelasDeslizantes); // [[1,2,3],[2,3,4],...]

        // ---- scan: emite CADA resultado parcial da acumulacao ----
        // (como reduce, mas você vê todos os passos, como operação intermediária)
        final List<Integer> somaAcumulada = numeros.stream()
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .toList();

        IO.println("scan (soma acumulada): " + somaAcumulada); // [1,3,6,10,15,21,28]

        // ---- fold: reduz a UM elemento, como operação intermediária ----
        final List<String> concatenado = numeros.stream()
                .gather(Gatherers.fold(() -> "", (acumulado, elemento) -> acumulado + elemento))
                .toList();

        IO.println("fold (concatena tudo): " + concatenado); // ["1234567"]

        // ---- mapConcurrent: mapeia em paralelo com virtual threads ----
        // Ótimo para etapas I/O-bound; limita a concorrência ao valor informado.
        final List<Integer> processadoConcorrente = Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.mapConcurrent(4, numero -> {
                    dormir(100); // simula I/O
                    return numero * 10;
                }))
                .toList();

        IO.println("mapConcurrent: " + processadoConcorrente); // [10,20,30,40,50]

        // ---- andThen: compor gatherers ----
        final Gatherer<Integer, ?, Integer> somaDasJanelasGatherer = Gatherers.<Integer>windowFixed(2)
                .andThen(Gatherers.scan(() -> 0, (Integer soma, List<Integer> janela)
                        -> soma + janela.stream().mapToInt(Integer::intValue).sum()));

        final List<Integer> somaDasJanelas = numeros.stream()
                .gather(somaDasJanelasGatherer)
                .toList();

        IO.println("windowFixed(2) andThen scan: " + somaDasJanelas);
    }

    static void dormir(final long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (final InterruptedException excecao) {
            Thread.currentThread().interrupt();
        }
    }
}