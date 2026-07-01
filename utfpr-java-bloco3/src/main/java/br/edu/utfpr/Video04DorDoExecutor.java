package br.edu.utfpr;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * VÍDEO 4 — A dor do ExecutorService (exemplo da fatura)
 * Requer Java 25
 */
public class Video04DorDoExecutor {

    record Emissor(String nome) {
    }

    record Cliente(String nome) {
    }

    record Item(String descricao, double valor) {
    }

    record Fatura(Emissor emissor, Cliente cliente, List<Item> itens) {
    }

    static Emissor buscarEmissor() {
        dormir(300);
        return new Emissor("Minha Empresa LTDA");
    }

    static Cliente buscarCliente() {
        dormir(1500); // propositalmente lento
        return new Cliente("Joao da Silva");
    }

    static List<Item> buscarItens() {
        dormir(200);
        throw new RuntimeException("falha ao buscar itens"); // falha rapidamente
    }

    static void gerarFatura() throws Exception {
        try (final ExecutorService executor = Executors.newFixedThreadPool(3)) {

            final Future<Emissor> futuroEmissor = executor.submit(Video04DorDoExecutor::buscarEmissor);
            final Future<Cliente> futuroCliente = executor.submit(Video04DorDoExecutor::buscarCliente);
            final Future<List<Item>> futuroItens = executor.submit(Video04DorDoExecutor::buscarItens);

            // get() na ordem fixa, ficamos presos esperando o cliente (~1,5s) mesmo que os itens já tenham
            // falhado (~0,2s)
            new Fatura(futuroEmissor.get(), futuroCliente.get(), futuroItens.get());
        }
    }

    void main() {

        final long instanteInicio = System.currentTimeMillis();

        try {
            gerarFatura();
        } catch (final Exception excecao) {
            final long instanteFim = System.currentTimeMillis();

            IO.println("Falhou em " + (instanteFim - instanteInicio) + " ms");
            IO.println("demorou ~1500ms, mesmo com a falha dos itens tendo ocorrido em ~200ms.");
        }
    }

    static void dormir(final long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (final InterruptedException excecao) {
            Thread.currentThread().interrupt();
        }
    }
}