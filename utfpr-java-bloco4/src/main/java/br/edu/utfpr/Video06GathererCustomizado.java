package br.edu.utfpr;

import java.util.List;
import java.util.stream.Gatherer;

/**
 * VÍDEO 6 — Stream Gatherers, parte 2: escrevendo o seu próprio
 * Requer Java 25 LTS
 */
public class Video06GathererCustomizado {

    void main() {
        final List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8);

        // ---- Gatherer 1: STATELESS — emite só os elementos em posição par ----
        // Sem estado: usamos Gatherer.of com um integrator (Void, T, R). A variável de estado não é usada,
        // então recebe o nome '_' (unnamed)
        final Gatherer<Integer, ?, Integer> apenasPares =
                Gatherer.of(
                        (_, elemento, emissor) -> {
                            if (elemento % 2 == 0) {
                                emissor.push(elemento);
                            }
                            return true; // continuar consumindo
                        });

        final List<Integer> pares = numeros.stream()
                .gather(apenasPares)
                .toList();

        IO.println("Gatherer stateless (pares): " + pares);

        // ---- Gatherer 2: STATEFUL — limita a soma acumulada a um teto ----
        // Emite elementos enquanto a soma não ultrapassa o limite; depois para.
        final Gatherer<Integer, ?, Integer> ateSomaAtingir = somarAte(10);

        final List<Integer> ateDez = numeros.stream()
                .gather(ateSomaAtingir)
                .toList();

        IO.println("Gatherer stateful (soma <= 10): " + ateDez); // 1,2,3,4 (soma 10)
    }

    /**
     * Cria um gatherer stateful que emite elementos enquanto a soma acumulada não ultrapassa 'limite'; ao ultrapassar,
     * encerra o fluxo (short-circuit). Usa um array de 1 posição como estado mutável.
     */
    static Gatherer<Integer, ?, Integer> somarAte(final int limite) {
        return Gatherer.ofSequential(
                () -> new int[1],                                                        // initializer: estado
                (estado, elemento, emissor) -> {    // integrator
                    if (estado[0] + elemento > limite) {
                        return false;                                                   // encerra: não consome mais
                    }
                    estado[0] += elemento;
                    emissor.push(elemento);
                    return true;
                });
    }
}