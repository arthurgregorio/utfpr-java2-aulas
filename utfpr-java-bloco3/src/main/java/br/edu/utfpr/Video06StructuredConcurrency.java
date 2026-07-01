package br.edu.utfpr;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * VÍDEO 6 — Structured Concurrency (API finalizada no JDK 25)
 * Requer Java 25
 */
public class Video06StructuredConcurrency {

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
    }

    static Fatura gerarFatura() throws InterruptedException {

        // open + Joiner: política "todas devem ter sucesso, senão falha tudo".
        try (final var escopo = StructuredTaskScope.open(StructuredTaskScope.Joiner.<Object>allSuccessfulOrThrow())) {

            final Subtask<Emissor> subtarefaEmissor = escopo.fork(Video06StructuredConcurrency::buscarEmissor);
            final Subtask<Cliente> subtarefaCliente = escopo.fork(Video06StructuredConcurrency::buscarCliente);
            final Subtask<List<Item>> subtarefaItens = escopo.fork(Video06StructuredConcurrency::buscarItens);

            escopo.join(); // espera todas e se uma falhar, cancela as demais

            return new Fatura(
                    subtarefaEmissor.get(),
                    subtarefaCliente.get(),
                    subtarefaItens.get());
        }
    }

    void main() throws InterruptedException {

        final long instanteInicio = System.currentTimeMillis();
        final Fatura fatura = gerarFatura();
        final long instanteFim = System.currentTimeMillis();

        IO.println("Fatura: " + fatura);
        IO.println("Tempo: " + (instanteFim - instanteInicio) + " ms (~ da subtarefa mais lenta)");
    }

    static void dormir(final long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (final InterruptedException excecao) {
            Thread.currentThread().interrupt();
        }
    }
}