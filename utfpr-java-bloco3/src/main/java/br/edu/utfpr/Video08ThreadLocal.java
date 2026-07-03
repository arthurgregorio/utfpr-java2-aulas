package br.edu.utfpr;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/**
 * VÍDEO 8 — ThreadLocal (abordagem antiga, usada antes dos Scoped Values / JEP 506)
 * Requer Java 25
 */
public class Video08ThreadLocal {

    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    void main() throws InterruptedException {

        try (final var escopo = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow())) {

            escopo.fork(() -> {
                REQUEST_ID.set("abc-123");
                try {
                    tratarRequisicao();
                } finally {
                    REQUEST_ID.remove(); // precisa limpar manualmente, senão vaza entre reaproveitamentos de thread
                }
                return null;
            });

            escopo.fork(() -> {
                REQUEST_ID.set("xyz-789");
                try {
                    tratarRequisicao();
                } finally {
                    REQUEST_ID.remove();
                }
                return null;
            });

            escopo.join();
        }
    }

    static void tratarRequisicao() throws InterruptedException {

        registrarLog("inicio do tratamento");

        // ThreadLocal não é herdado pelas threads filhas abertas aqui: cada fork roda em uma
        // thread nova, então consultarBanco/chamarServicoExterno enxergam REQUEST_ID vazio.
        try (final var escopo = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow())) {
            escopo.fork(() -> {
                consultarBanco();
                return null;
            });
            escopo.fork(() -> {
                chamarServicoExterno();
                return null;
            });
            escopo.join();
        }

        registrarLog("fim do tratamento");
    }

    static void consultarBanco() {
        registrarLog("consultando banco...");
    }

    static void chamarServicoExterno() {
        registrarLog("chamando servico externo...");
    }

    static void registrarLog(final String mensagem) {
        final String identificadorRequisicao = REQUEST_ID.get() != null ? REQUEST_ID.get() : "<sem-id>";
        IO.println("[" + identificadorRequisicao + "] " + mensagem);
    }
}