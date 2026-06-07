package br.edu.utfpr;

import java.util.ArrayList;
import java.util.List;

/**
 * VÍDEO 2 — Type erasure: o que o compilador esconde de você
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Continuamos no sistema de pagamentos. Aqui mostramos POR QUE generics às vezes "trollam", o tipo só existe em tempo
 * de compilação.
 */
public class Video02_TypeErasure {

    static class Pix {
        final double valor;

        Pix(double valor) {
            this.valor = valor;
        }
    }

    static class Boleto {
        final double valor;

        Boleto(double valor) {
            this.valor = valor;
        }
    }

    static void main() {

        final List<Pix> pixes = new ArrayList<>(); // dps do erasure > final List<Object>
        final List<Boleto> boletos = new ArrayList<>();

        // PROVA 1: em runtime, as duas listas são a MESMA classe, o <Pix> e o <Boleto> foram "apagados" (erased).
        IO.println("Classe de pixes:   " + pixes.getClass());
        IO.println("Classe de boletos: " + boletos.getClass());
        IO.println("Sao a mesma classe? " + (pixes.getClass() == boletos.getClass()));

        // PROVA 2: por isso não existe "new T[]" nem "Object instanceof List<Pix>":
//        if (pixes instanceof List<Pix>) {
//            IO.println("É uma lista de Pix");
//        }

//        final Object objetosPix = pixes;
//        if (objetosPix instanceof List<Pix>) {
//            IO.println("É uma lista de Pix");
//        }

        // T[] array = new T[10]; // erro: generic array creation

        // PROVA 3: o que o compilador realmente "vê" depois do erasure, List<Pix> vira List<Object> internamente,
        // com casts inseridos internamente
        demonstrarCastImplicito();

        // PROVA 4: heap pollution — o perigo de misturar raw types.
        demonstrarHeapPollution();
    }

    @SuppressWarnings("unchecked")
    static void demonstrarCastImplicito() {
        final List<Pix> lista = new ArrayList<>();
        lista.add(new Pix(100));

        // O compilador insere um cast (Pix) automaticamente aqui:
        final Pix p = lista.getFirst();
        IO.println("Valor recuperado com cast implicito: " + p.valor);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void demonstrarHeapPollution() {

        final List<Pix> pixes = new ArrayList<>();

        final List rawList = pixes; // raw type: o compilador só avisa (warning)
        rawList.add(new Boleto(999)); // inserimos um Boleto numa List<Pix>!
//        rawList.add(new Integer(100));

        IO.println("\nLista 'de Pix' agora contem um Boleto escondido.");

        try {
            final Pix p = pixes.getFirst(); // ClassCastException
            IO.println(p.valor);
        } catch (ClassCastException e) {
            System.err.println("Erro!" + e.getMessage());
        }
    }
}
