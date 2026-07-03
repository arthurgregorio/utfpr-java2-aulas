package br.edu.utfpr;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * VÍDEO 7 — Structured Concurrency (API finalizada no JDK 25)
 * Requer Java 25
 */
public class Video07StructuredConcurrency {

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
        dormir(1500);
        return new Cliente("Joao da Silva");
    }

    static List<Item> buscarItens() {
        dormir(200);
        return List.of(new Item("Servico", 500.0));
//        throw new RuntimeException("falha ao buscar itens");
    }

    static Fatura gerarFatura() throws InterruptedException {

        // open + Joiner: política "todas devem ter sucesso, senão falha tudo".
        try (final var escopo = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {

            final Subtask<Emissor> subtarefaEmissor = escopo.fork(Video07StructuredConcurrency::buscarEmissor);
            final Subtask<Cliente> subtarefaCliente = escopo.fork(Video07StructuredConcurrency::buscarCliente);
            final Subtask<List<Item>> subtarefaItens = escopo.fork(Video07StructuredConcurrency::buscarItens);

            escopo.join(); // espera todas e se uma falhar, cancela as demais

            return new Fatura(
                    subtarefaEmissor.get(),
                    subtarefaCliente.get(),
                    subtarefaItens.get());
        }
    }

    void main() throws InterruptedException {

        final long instanteInicio = System.currentTimeMillis();

        try {
            final Fatura fatura = gerarFatura();
            IO.println("Fatura: " + fatura);
        } catch (RuntimeException excecao) {
            System.out.println("Falhou: " + excecao.getMessage());
        } finally {
            final long instanteFim = System.currentTimeMillis();
            IO.println("Tempo: " + (instanteFim - instanteInicio) + " ms");
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