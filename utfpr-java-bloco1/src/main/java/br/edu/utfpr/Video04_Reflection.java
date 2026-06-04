package br.edu.utfpr;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * VÍDEO 4 — Reflection API: inspecionar e manipular código em runtime
 * <p>
 * Requer Java 25 LTS
 * <p>
 * É a "mágica" por trás de frameworks como Spring e Hibernate. Continuamos com o sistema de pagamentos.
 */
public class Video04_Reflection {

    static class ContaPagamento {

        private String titular;
        private double saldo;

        public ContaPagamento() {
        } // construtor sem-args, necessário para a maioria dos frameworks

        public ContaPagamento(String titular, double saldo) {
            this.titular = titular;
            this.saldo = saldo;
        }

        public void creditar(double v) {
            this.saldo += v;
        }

        public double getSaldo() {
            return saldo;
        }

        @Override
        public String toString() {
            return "ContaPagamento{titular=" + titular + ", saldo=" + saldo + "}";
        }
    }

    static void main() throws Exception {

        final Class<?> clazz = ContaPagamento.class;

        // 1) INTROSPECÇÃO: descobrir a estrutura da classe em runtime.
        IO.println("== Campos ==");

        for (Field f : clazz.getDeclaredFields()) {
            IO.println(String.format("  %s %s%n", f.getType().getSimpleName(), f.getName()));
        }

        IO.println("== Metodos publicos declarados ==");
        for (Method m : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(m.getModifiers())) {
                IO.println("  " + m.getName());
            }
        }

        // 2) INSTANCIAÇÃO DINÂMICA: criar objeto sem usar 'new' diretamente.
        final Constructor<?> ctor = clazz.getConstructor(String.class, double.class);
        final Object obj = ctor.newInstance("Maria", 500.0);

        IO.println("\nObjeto criado por reflection: " + obj);

        // 3) ACESSAR CAMPO PRIVADO (com setAccessible).
        final Field saldo = clazz.getDeclaredField("saldo");
        saldo.setAccessible(true);

        IO.println("Saldo lido via reflection: " + saldo.get(obj));

        saldo.set(obj, 999.0); // escrevemos direto no campo privado

        IO.println("Apos alterar campo privado: " + obj);

        // 4) INVOCAÇÃO DINÂMICA de método.
        final Method creditar = clazz.getMethod("creditar", double.class);
        creditar.invoke(obj, 1.0);

        IO.println("Apos invocar creditar(1.0): " + obj);
    }
}
