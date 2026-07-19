package br.edu.utfpr;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

/**
 * VÍDEO 1 — Interfaces funcionais a fundo, composição e Optional
 * Requer Java 25 LTS
 */
public class Video01FuncionaisComposicao {

    record Produto(String nome, double preco, String categoria) {
    }

    void main() {
        final List<Produto> produtos = List.of(
                new Produto("Notebook", 3500.0, "eletronicos"),
                new Produto("Mouse", 80.0, "eletronicos"),
                new Produto("Cadeira", 600.0, "moveis"));

        // ---- As quatro interfaces funcionais centrais ----
        final Function<Produto, String> obterNome = Produto::nome;
        final Predicate<Produto> ehCaro = produto -> produto.preco() > 500;
        final Supplier<Produto> produtoPadrao = () -> new Produto("Desconhecido", 0.0, "n/a");
        final Consumer<Produto> imprimir = produto -> IO.println("  " + produto.nome());

        // ---- COMPOSIÇÃO de Function: andThen e compose ----
        final Function<Produto, Integer> tamanhoDoNome = obterNome.andThen(String::length);
        IO.println("Tamanho do nome do 1o produto: " + tamanhoDoNome.apply(produtos.getFirst()));

        // ---- COMPOSIÇÃO de Predicate: and, or, negate ----
        final Predicate<Produto> ehEletronico = produto -> produto.categoria().equals("eletronicos");
        final Predicate<Produto> caroEEletronico = ehCaro.and(ehEletronico);
        final Predicate<Produto> baratoOuMovel = ehCaro
                .negate()
                .or(produto -> produto.categoria().equals("moveis"));

        IO.println("\nCaros E eletronicos:");
        produtos.stream().filter(caroEEletronico).forEach(imprimir);

        IO.println("Baratos OU moveis:");
        produtos.stream().filter(baratoOuMovel).forEach(imprimir);

        // ---- BiFunction ----
        final BiFunction<Produto, Double, Double> aplicarDesconto =
                (produto, percentual) -> produto.preco() * (1 - percentual / 100);
        IO.println("\nNotebook com 10% off: " + aplicarDesconto.apply(produtos.getFirst(), 10.0));

        // ---- Optional: uso CORRETO (transversal no bloco) ----
        final Optional<Produto> maisCaro = produtos.stream()
                .max(Comparator.comparingDouble(Produto::preco));

        // Bom: map + orElse, sem isPresent()/get()
        final String nomeMaisCaro = maisCaro.map(Produto::nome).orElse("nenhum");
        IO.println("\nMais caro: " + nomeMaisCaro);

        // Bom: fornecer padrao com orElseGet (lazy)
        final Produto escolhido = produtos.stream()
                .filter(produto -> produto.preco() > 10_000)
                .findFirst()
                .orElseGet(produtoPadrao);

        IO.println("Escolhido (com fallback): " + escolhido.nome());

        // ANTIPADRÃO (NÃO faça): if (opt.isPresent()) { opt.get() ... }
        // ANTIPADRÃO: Optional em campos ou parâmetros de metodo.
        // ANTIPADRÃO: optional.get() sem checar -> NoSuchElementException.
    }
}