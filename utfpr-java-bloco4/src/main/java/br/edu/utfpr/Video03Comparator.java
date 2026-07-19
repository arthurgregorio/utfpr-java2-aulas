package br.edu.utfpr;

import java.util.Comparator;
import java.util.List;

/**
 * VÍDEO 3 — Comparator em profundidade
 * Requer Java 25 LTS
 */
public class Video03Comparator {

    record Funcionario(String nome, String departamento, int salario) {
    }

    void main() {
        final List<Funcionario> funcionarios = List.of(
                new Funcionario("Ana", "TI", 8000),
                new Funcionario("Bruno", "TI", 6000),
                new Funcionario("Carla", "RH", 8000),
                new Funcionario("Diego", "RH", 5000));

        // ---- comparing simples ----
        final Comparator<Funcionario> porSalario = Comparator.comparingInt(Funcionario::salario);

        IO.println("Por salario (crescente):");
        funcionarios.stream().sorted(porSalario)
                .forEach(funcionario -> IO.println("  " + funcionario));

        // ---- thenComparing: critério de desempate ----
        // Departamento (A-Z), depois salario DECRESCENTE, depois nome.
        final Comparator<Funcionario> composto = Comparator
                .comparing(Funcionario::departamento)
                .thenComparing(Comparator.comparingInt(Funcionario::salario).reversed())
                .thenComparing(Funcionario::nome);

        IO.println("\nDepartamento, salario desc, nome:");
        funcionarios.stream().sorted(composto)
                .forEach(funcionario -> IO.println("  " + funcionario));

        // ---- reversed: inverte a ordem inteira ----
        IO.println("\nSalario decrescente:");
        funcionarios.stream().sorted(porSalario.reversed())
                .forEach(funcionario -> IO.println("  " + funcionario));

        // ---- nullsFirst: lida com valores nulos com seguranca ----
        final Comparator<String> comNulos = Comparator.nullsFirst(Comparator.naturalOrder());
        final List<String> valores = java.util.Arrays.asList("banana", null, "abacaxi");
        valores.sort(comNulos);
        IO.println("\nCom nullsFirst: " + valores);
    }
}