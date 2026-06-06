package br.edu.utfpr;

import java.util.ArrayList;
import java.util.List;

/**
 * VÍDEO 1 — Generics avançados: wildcards e bounded types
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Fio condutor do bloco: um sistema de pagamentos. Aqui modelamos os tipos de pagamento e mostramos como generics
 * dão segurança de tipo em estruturas reutilizáveis.
 * <p>
 * Compilar: javac Video01_Generics.java
 * Executar: java Video01_Generics
 */
public class Video01_Generics {

    static abstract class Pagamento {

        private final double valor;

        protected Pagamento(double valor) {
            this.valor = valor;
        }

        public double valor() {
            return valor;
        }
    }

    static class Pix extends Pagamento {

        private final String chave;

        public Pix(double valor, String chave) {
            super(valor);
            this.chave = chave;
        }

        public String chave() {
            return chave;
        }
    }

    static class Boleto extends Pagamento {

        private final String codigoBarras;

        public Boleto(double valor, String codigoBarras) {
            super(valor);
            this.codigoBarras = codigoBarras;
        }

        public String codigoBarras() {
            return codigoBarras;
        }
    }

    // Bounded type parameter, <T extends Pagamento> garante que só aceitamos pagamentos e ainda podemos chamar
    // valor() com segurança
    static double somar(List<Pagamento> pagamentos) {

        double total = 0;

        for (Pagamento p : pagamentos) {
            total += p.valor();
        }
        return total;
    }

//    static <T extends Pagamento> double somar(List<T> pagamentos) {
//
//        double total = 0;
//
//        for (T p : pagamentos) {
//            total += p.valor();
//        }
//        return total;
//    }

    // Wildcards - ? extends (PECS - "Producer Extends") apenas lemos da lista, então usamos extends.
    static double somarLeitura(List<? extends Pagamento> pagamentos) {

        double total = 0;

        for (Pagamento p : pagamentos) {
            total += p.valor();
        }

        return total;
    }

    // Wildcards - ? super (PECS - "Consumer Super"), escrevemos na lista, então usamos super
    static void adicionarPix(List<? super Pix> destino) {
        destino.add(new Pix(50.0, "chave@email.com"));
        // destino.add(new Boleto(...)); // NÃO compila, garante segurança
    }

    static void main() {

        final List<Pix> pixes = new ArrayList<>();
        pixes.add(new Pix(100.0, "11999998888"));
        pixes.add(new Pix(250.0, "joao@pix.com"));

        final List<Boleto> boletos = new ArrayList<>();
        boletos.add(new Boleto(80.0, "34191.79001..."));

//        IO.println("Total pix (bounded): " + somar(pixes));
//        IO.println("Total boleto (bounded): " + somar(boletos));

        IO.println("Total pix (extends): " + somarLeitura(pixes));
        IO.println("Total boleto (extends): " + somarLeitura(boletos));

        final List<Pagamento> genericos = new ArrayList<>();
        adicionarPix(genericos);
        IO.println("Apos adicionarPix, tamanho: " + genericos.size());
    }
}
