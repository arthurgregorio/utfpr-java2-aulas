package br.edu.utfpr;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * VÍDEO 7 — Streams paralelos e suas armadilhas
 * Requer Java 25 LTS
 */
public class Video07StreamsParalelos {

    void main() {

        // ---- Quando vale: muito dado + operacao cara + independente ----
        final long inicioSequencial = System.currentTimeMillis();

        final long somaSequencial = IntStream.rangeClosed(1, 10_000_000)
                .mapToLong(numero -> (long) numero * numero)
                .sum();

        final long tempoSequencial = System.currentTimeMillis() - inicioSequencial;

        final long inicioParalelo = System.currentTimeMillis();

        final long somaParalela = IntStream.rangeClosed(1, 10_000_000)
                .parallel()
                .mapToLong(numero -> (long) numero * numero)
                .sum();

        final long tempoParalelo = System.currentTimeMillis() - inicioParalelo;

        IO.println("Soma sequencial: " + somaSequencial + " em " + tempoSequencial + " ms");
        IO.println("Soma paralela:   " + somaParalela + " em " + tempoParalelo + " ms");
        IO.println("(resultados iguais; tempo varia conforme nucleos disponiveis)");

        // The N*Q rule - parallel streams
        // N = numero de elementos
        // Q = tempo de processamento no processador por elemento

        // ---- ARMADILHA 1: estado compartilhado mutavel ----
        // NÃO faça isto: ArrayList não é thread-safe -> resultado corrompido/errado.
        final List<Integer> destinoInseguro = new ArrayList<>();

        IntStream.range(0, 1000).parallel().forEach(destinoInseguro::add); // ERRADO

        IO.println("\nArmadilha (add em ArrayList paralelo): tamanho = "
                + destinoInseguro.size() + " (esperado 1000; pode variar/corromper)");

        // Correto: deixe o stream coletar (sem efeito colateral compartilhado).
        final List<Integer> destinoSeguro = IntStream.range(0, 1000).parallel().boxed().toList();
        IO.println("Correto (toList): tamanho = " + destinoSeguro.size());

        // ---- ARMADILHA 2: operacoes dependentes de ordem sao mais caras ----
        // forEachOrdered preserva ordem, mas serializa a emissao (perde ganho).
        // Prefira forEach quando a ordem nao importa.
        IO.println("\nRegra: em paralelo, evite estado mutavel compartilhado e");
        IO.println("operacoes sensiveis a ordem; use reduce/collect apropriados.");
    }
}