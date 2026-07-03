package br.edu.utfpr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * VÍDEO 6 — Virtual Threads
 * Requer Java 25
 */
public class Video06VirtualThreads {

    void main() throws InterruptedException {

        // uma virtual thread avulsa
        final Thread virtualThread = Thread.ofVirtual()
                .start(() -> IO.println("Rodando em virtual thread? " + Thread.currentThread().isVirtual()));

        virtualThread.join();

        // muitas tarefas que "bloqueiam" (simulando I/O), cada uma em sua virtual thread
        final int quantidadeTarefas = 50_000;
        final long instanteInicio = System.currentTimeMillis();

        try (final ExecutorService executorVirtual = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, quantidadeTarefas)
                    .forEach(indice ->
                            executorVirtual.submit(() -> {
                                Thread.sleep(1000); // I/O simulada: 1s bloqueado
                                return indice;
                            }));
        }

        final long instanteFim = System.currentTimeMillis();

        IO.println("%d tarefas de 1s cada terminaram em %d ms."
                .formatted(quantidadeTarefas, (instanteFim - instanteInicio)));

        IO.println("Com platform threads, criar 50 mil seria inviável (memoria).");
    }
}