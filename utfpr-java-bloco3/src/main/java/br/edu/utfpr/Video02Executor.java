package br.edu.utfpr;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * VÍDEO 2 — Do Thread cru ao ExecutorService
 * Requer Java 25
 */
public class Video02Executor {

    void main() throws Exception {

        // Thread na mão: funciona, mas trabalhoso e sem retorno fácil
        final Thread threadManual = new Thread(() -> IO.println("Ola da thread"));

        threadManual.start();
        threadManual.join();

        // ExecutorService: pool reutilizável + future com retorno
        // try-with-resources: ExecutorService é AutoCloseable (Java 19+)
        // close() faz shutdown e espera as tarefas terminarem
        try (final ExecutorService executor = Executors.newFixedThreadPool(3)) {

            final Callable<Integer> tarefa = () -> {
                Thread.sleep(200);
                return 42;
            };

            final Future<Integer> futuroResultado = executor.submit(tarefa);
            IO.println("Tarefa submetida, fazendo outra coisa...");

            // get() BLOQUEIA até o resultado chegar
            final Integer resultado = futuroResultado.get();
            IO.println("Resultado: " + resultado);
        }
    }
}