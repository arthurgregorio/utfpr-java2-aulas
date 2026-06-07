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

        for (Field field : clazz.getDeclaredFields()) {
            IO.println(String.format("  %s %s%n", field.getType().getSimpleName(), field.getName()));
        }

        IO.println("== Metodos publicos declarados ==");
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                IO.println("  " + method.getName());
            }
        }

        // 2) INSTANCIAÇÃO DINÂMICA: criar objeto sem usar 'new' diretamente.
        final Constructor<?> constructor = clazz.getConstructor(String.class, double.class);
        final Object object = constructor.newInstance("Maria", 500.0);

        IO.println("\nObjeto criado por reflection: " + object);

        // 3) ACESSAR CAMPO PRIVADO (com setAccessible).
        final Field saldo = clazz.getDeclaredField("saldo");
        saldo.setAccessible(true);

        IO.println("Saldo lido via reflection: " + saldo.get(object));

        saldo.set(object, 999.0); // escrevemos direto no campo privado

        IO.println("Apos alterar campo privado: " + object);

        // 4) INVOCAÇÃO DINÂMICA de metodo.
        final Method creditar = clazz.getMethod("creditar", double.class);
        creditar.invoke(object, 1.0);

        IO.println("Apos invocar creditar(1.0): " + object);
    }
}
