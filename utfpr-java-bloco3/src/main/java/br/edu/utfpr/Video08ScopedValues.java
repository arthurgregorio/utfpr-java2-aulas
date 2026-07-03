package br.edu.utfpr;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

import static java.lang.ScopedValue.where;

/**
 * VÍDEO 8 — Scoped Values (JEP 506, finalizado no JDK 25)
 * Requer Java 25
 */
public class Video08ScopedValues {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    void main() throws InterruptedException {

        try (final var escopo = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow())) {

            escopo.fork(() -> where(REQUEST_ID, "abc-123").call(() -> {
                tratarRequisicao();
                return null;
            }));

            escopo.fork(() -> where(REQUEST_ID, "xyz-789").call(() -> {
                tratarRequisicao();
                return null;
            }));

            escopo.join();
        }
    }

    static void tratarRequisicao() throws InterruptedException {

        registrarLog("inicio do tratamento");

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
        final String identificadorRequisicao = REQUEST_ID.isBound() ? REQUEST_ID.get() : "<sem-id>";
        IO.println("[" + identificadorRequisicao + "] " + mensagem);
    }
}