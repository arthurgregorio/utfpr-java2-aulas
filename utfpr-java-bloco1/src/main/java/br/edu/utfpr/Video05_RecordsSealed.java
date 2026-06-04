package br.edu.utfpr;

import java.util.List;

/**
 * VÍDEO 5 — Records e sealed classes: modelagem de domínio moderna
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Reescrevemos a hierarquia de pagamentos dos vídeos 1 e 2 com recursos modernos: records (imutáveis, concisos) e
 * sealed (hierarquia fechada e controlada).
 */
public class Video05_RecordsSealed {

    // SEALED INTERFACE
    // "permits" lista EXATAMENTE quem pode implementar. Ninguém mais. Isso habilita o pattern matching exaustivo do
    // vídeo 6
    sealed interface Pagamento permits Pix, Boleto, Cartao {
        double valor();
    }

    // RECORDS
    // Uma linha substitui construtor, getters, equals, hashCode e toString. São imutáveis por padrão.
    record Pix(double valor, String chave) implements Pagamento {
        // Compact constructor: validação no momento da criação.
        Pix {
            if (valor <= 0) throw new IllegalArgumentException("valor deve ser positivo");
            if (chave == null || chave.isBlank()) throw new IllegalArgumentException("chave obrigatoria");
        }
    }

    record Boleto(double valor, String codigoBarras) implements Pagamento {
    }

    record Cartao(double valor, String bandeira, int parcelas) implements Pagamento {
        // Records também podem ter métodos extras.
        double valorParcela() {
            return valor / parcelas;
        }
    }

    static void main() {

        final List<Pagamento> pagamentos = List.of(
                new Pix(100.0, "joao@pix.com"),
                new Boleto(250.0, "34191.79001..."),
                new Cartao(900.0, "VISA", 3)
        );

        // toString, equals e hashCode vêm de graça com records:
        IO.println("toString automatico: " + pagamentos.getFirst());

        final Pix a = new Pix(100.0, "joao@pix.com");
        final Pix b = new Pix(100.0, "joao@pix.com");

        IO.println("equals automatico (a.equals(b)): " + a.equals(b));

        // Acesso aos componentes via métodos gerados (sem 'get'):
        final Cartao c = new Cartao(900.0, "VISA", 3);

        IO.println("Valor da parcela: " + c.valorParcela());

        // Validação do compact constructor em ação:
        try {
            new Pix(-5.0, "x@x.com");
        } catch (IllegalArgumentException e) {
            System.err.println("Erro: " + e.getMessage());
        }

        final double total = pagamentos.stream().mapToDouble(Pagamento::valor).sum();
        IO.println("Total: " + total);
    }
}
