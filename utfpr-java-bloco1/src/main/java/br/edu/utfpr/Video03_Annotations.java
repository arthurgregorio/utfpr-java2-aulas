package br.edu.utfpr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * VÍDEO 3 — Criando suas próprias annotations
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Ainda no sistema de pagamentos: vamos anotar campos que precisam ser validados. Neste vídeo só definimos as
 * annotations e as usamos; no vídeo 5 vamos lê-las via Reflection para construir um validador
 * <p>
 * Em projeto real cada uma ficaria em seu próprio arquivo .java.
 */
public class Video03_Annotations {

    // @Retention(RUNTIME): a annotation sobrevive até a execução, (sem isso, Reflection não enxerga é o erro
    // numero 1 de iniciantes), @Target(FIELD): só pode ser usada em campos.
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NaoNulo {
        // mensagem customizável, com valor padrão
        String mensagem() default "nao pode ser nulo";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Tamanho {

        int min() default 0;

        int max() default Integer.MAX_VALUE;

        String mensagem() default "tamanho invalido";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Positivo {
        String mensagem() default "deve ser positivo";
    }

    // Classe de domínio anotada, as annotations são apenas metadados, sozinhas não fazem nada. Elas só ganham vida
    // quando alguém as lê (vídeo 5).
    static class DadosPix {

        @NaoNulo(mensagem = "a chave pix e obrigatoria")
        @Tamanho(min = 11, max = 77, mensagem = "chave pix com tamanho fora do padrao")
        String chave;

        @Positivo(mensagem = "o valor do pix deve ser maior que zero")
        double valor;

        DadosPix(String chave, double valor) {
            this.chave = chave;
            this.valor = valor;
        }
    }

    static void main() {

        final DadosPix dados = new DadosPix("joao@pix.com", 150.0);

        try {
            final var campoChave = DadosPix.class.getDeclaredField("chave");
            final NaoNulo naoNulo = campoChave.getAnnotation(NaoNulo.class);
            final Tamanho tamanho = campoChave.getAnnotation(Tamanho.class);

            IO.println("Campo 'chave' tem @NaoNulo? " + (naoNulo != null));

            if (naoNulo != null) {
                IO.println("  mensagem: " + naoNulo.mensagem());
            }

            if (tamanho != null) {
                IO.println("  tamanho: " + tamanho.min() + ".." + tamanho.max());
            }

            IO.println("Dados de exemplo: chave=" + dados.chave + " valor=" + dados.valor);
        } catch (NoSuchFieldException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
