package br.edu.utfpr;

/**
 * VÍDEO 1 — Por que concorrência é difícil
 * Requer Java 25
 */
public class Video01Problemas {

    // race condition
    static final class Contador {

        private int valor = 0;

        void incrementarInseguro() {
            valor++; // NÃO atômico: ler-somar-escrever
        }

        synchronized void incrementarSeguro() {
            valor++; // exclusão mútua garante atomicidade
        }

        int valorAtual() {
            return valor;
        }
    }

    void demonstrarRaceCondition() throws InterruptedException {

        final Contador contador = new Contador();

        final int quantidadeThreads = 8;
        final int incrementosPorThread = 100_000;

        final Thread[] threads = new Thread[quantidadeThreads];

        for (int indice = 0; indice < quantidadeThreads; indice++) {
            threads[indice] = new Thread(() -> {
                for (int repeticao = 0; repeticao < incrementosPorThread; repeticao++) {
                    contador.incrementarInseguro(); // troque por incrementarSeguro() para corrigir
                }
            });
        }

        for (final Thread thread : threads) {
            thread.start();
        }

        for (final Thread thread : threads) {
            thread.join();
        }

        final int totalEsperado = quantidadeThreads * incrementosPorThread;

        IO.println("Esperado: " + totalEsperado + " | Obtido: " + contador.valorAtual());
        IO.println(totalEsperado == contador.valorAtual()
                ? "OK (use incrementarSeguro)"
                : "PERDEMOS incrementos (race condition)!");
    }

    // deadlock
    static final Object RECURSO_PRIMEIRO = new Object();
    static final Object RECURSO_SEGUNDO = new Object();

    void demonstrarDeadlock() {

        final Thread threadA = new Thread(() -> {
            synchronized (RECURSO_PRIMEIRO) {
                dormir(50);
                IO.println("Thread A pegou o primeiro, quer o segundo...");
                synchronized (RECURSO_SEGUNDO) {
                    IO.println("Thread A pegou os dois");
                }
            }
        });

        final Thread threadB = new Thread(() -> {
            synchronized (RECURSO_SEGUNDO) { // ordem INVERTIDA -> deadlock
                dormir(50);
                IO.println("Thread B pegou o segundo, quer o primeiro...");
                synchronized (RECURSO_PRIMEIRO) {
                    IO.println("Thread B pegou os dois");
                }
            }
        });

        threadA.start();
        threadB.start();

        IO.println("(Se travar aqui, e deadlock. Encerre com Ctrl+C.)");
    }

    static void dormir(final long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (final InterruptedException excecao) {
            Thread.currentThread().interrupt();
        }
    }

    void main() throws InterruptedException {

        IO.println("== RACE CONDITION ==");
        demonstrarRaceCondition();

        IO.println("\n== DEADLOCK (vai travar de proposito) ==");
        demonstrarDeadlock();
    }
}