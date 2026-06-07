package br.edu.utfpr;

/**
 * VÍDEO 6 — Pattern matching e switch moderno
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Usamos a hierarquia sealed do vídeo 5 para mostrar o ponto alto: pattern matching para switch com record
 * deconstruction, exaustivo
 */
public class Video06_PatternMatching {

    sealed interface Pagamento permits Pix, Boleto, Cartao {
        double valor();
    }

    record Pix(double valor, String chave) implements Pagamento {
    }

    record Boleto(double valor, String codigoBarras) implements Pagamento {
    }

    record Cartao(double valor, String bandeira, int parcelas) implements Pagamento {
    }

    // 1) pattern matching com instanceof - antes: instanceof + cast manual, agora: a variável já vem tipada
    static String descricaoAntiga(Object o) {
        if (o instanceof Pix) {
            final Pix pix = (Pix) o; // cast manual e repetitivo
            return "Pix para " + pix.chave();
        }
        return "desconhecido";
    }

    static String descricaoNova(Object o) {
        if (o instanceof Pix pix) { // 'pix' já é Pix, sem cast
            return "Pix para " + pix.chave();
        }
        return "desconhecido";
    }

    // 2) switch com pattern matching + record deconstruction
    // Como Pagamento é sealed, o compilador SABE que cobrimos todos os casos: não precisamos de 'default' e, se um
    // novo tipo for adicionado ao 'permits', este switch passa a não compilar até ser atualizado
    static String taxa(Pagamento p) {

        return switch (p) {

            case Pix px -> "Pix isento (" + px.valor() + ")";
            case Boleto b -> "Boleto +R$2,00 (" + b.valor() + ")";

            // record deconstruction: extrai os componentes direto no case
            case Cartao(double _, String bandeira, int parcelas) -> "Cartao " + bandeira + " em " + parcelas + "x";
        };
    }

    // 3) guarded patterns (when)
    static String classificarValor(Pagamento p) {
        return switch (p) {
            case Pagamento pg when pg.valor() >= 1000 -> "ALTO valor";
            case Pagamento pg when pg.valor() >= 100 -> "MEDIO valor";
            case Pagamento _ -> "BAIXO valor";
        };
    }

    static void main() {

        final Pagamento[] pagamentos = {
                new Pix(100.0, "joao@pix.com"),
                new Boleto(250.0, "34191..."),
                new Cartao(1500.0, "MASTER", 6)
        };

        IO.println("== instanceof pattern ==");
        IO.println(descricaoNova(pagamentos[0]));

        IO.println("\n== switch exaustivo + deconstruction ==");

        for (final Pagamento p : pagamentos) {
            IO.println(taxa(p));
        }

        IO.println("\n== guarded patterns ==");

        for (final Pagamento p : pagamentos) {
            IO.println(p.valor() + " -> " + classificarValor(p));
        }
    }
}
