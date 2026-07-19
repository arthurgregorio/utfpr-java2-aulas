package br.edu.utfpr;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * VÍDEO 4 — Collectors avançados, teeing e Collector customizado
 * Requer Java 25 LTS
 */
public class Video04CollectorsAvancados {

    record Venda(String vendedor, String regiao, double valor) {
    }

    void main() {
        final List<Venda> vendas = List.of(
                new Venda("Ana", "Sul", 1000),
                new Venda("Bruno", "Sul", 1500),
                new Venda("Carla", "Norte", 2000),
                new Venda("Ana", "Norte", 500));

        // ---- groupingBy com downstream: soma por regiao ----
        final Map<String, Double> totalPorRegiao = vendas.stream()
                .collect(Collectors.groupingBy(Venda::regiao, Collectors.summingDouble(Venda::valor)));

        IO.println("Total por regiao: " + totalPorRegiao);

        // ---- groupingBy + mapping: nomes de vendedores por regiao ----
        final Map<String, List<String>> vendedoresPorRegiao = vendas.stream()
                .collect(Collectors.groupingBy(Venda::regiao, Collectors.mapping(Venda::vendedor, Collectors.toList())));

        IO.println("Vendedores por regiao: " + vendedoresPorRegiao);

        // ---- groupingBy com TreeMap (ordenado) + filtering ----
        final Map<String, List<Venda>> grandesVendasPorRegiao = vendas.stream()
                .collect(Collectors.groupingBy(Venda::regiao, TreeMap::new,
                        Collectors.filtering(venda -> venda.valor() >= 1000, Collectors.toList())));

        IO.println("Grandes vendas por regiao (ordenado): " + grandesVendasPorRegiao);

        // ---- teeing: DOIS collectors ao mesmo tempo, combinados ----
        // Calcula media = soma / contagem numa unica passada.
        final double media = vendas.stream()
                .collect(Collectors.teeing(
                        Collectors.summingDouble(Venda::valor),
                        Collectors.counting(),
                        (soma, contagem) -> soma / contagem));

        IO.println("\nTicket medio (via teeing): " + media);

        // ---- Collector CUSTOMIZADO: concatena nomes em uma string ----
        final Collector<Venda, StringBuilder, String> concatenarVendedores =
                Collector.of(
                        StringBuilder::new,                            // supplier
                        (acumulador, venda) -> {    // accumulator
                            if (!acumulador.isEmpty()) {
                                acumulador.append(", ");
                            }
                            acumulador.append(venda.vendedor());
                        },
                        StringBuilder::append,                         // combiner
                        StringBuilder::toString);                      // finisher

        final String todosOsVendedores = vendas.stream().collect(concatenarVendedores);
        IO.println("Vendedores (collector customizado): " + todosOsVendedores);
    }
}