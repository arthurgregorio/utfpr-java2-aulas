package br.edu.utfpr;

import java.util.*;

/**
 * VÍDEO 2 — Collections avançadas: escolhendo a estrutura certa
 * Requer Java 25 LTS
 */
public class Video02CollectionsAvancadas {

    void main() {
        // ---- Deque: fila dupla (pilha OU fila) ----
        final Deque<String> pilha = new ArrayDeque<>();
        pilha.push("primeiro");
        pilha.push("segundo");
        IO.println("Deque como pilha (LIFO): " + pilha.pop()); // segundo

        final Deque<String> fila = new ArrayDeque<>();
        fila.offer("a");
        fila.offer("b");
        IO.println("Deque como fila (FIFO): " + fila.poll()); // a

        // ---- PriorityQueue: sempre remove o "menor" (ou por Comparator) ----
        final PriorityQueue<Integer> prioridades = new PriorityQueue<>();
        prioridades.add(30);
        prioridades.add(10);
        prioridades.add(20);
        IO.println("PriorityQueue remove o menor: " + prioridades.poll()); // 10

        // ---- TreeMap / NavigableMap: mapa ORDENADO com buscas por vizinhança ----
        final NavigableMap<Integer, String> faixasDePreco = new TreeMap<>();
        faixasDePreco.put(100, "barato");
        faixasDePreco.put(500, "medio");
        faixasDePreco.put(1000, "caro");

        // Consultas que só um mapa ordenado oferece:
        IO.println("\nMaior chave <= 700: " + faixasDePreco.floorKey(700));   // 500
        IO.println("Menor chave >= 700: " + faixasDePreco.ceilingKey(700));   // 1000
        IO.println("Primeira entrada: " + faixasDePreco.firstEntry());
        IO.println("Submapa [100,500): " + faixasDePreco.subMap(100, 500));

        // ---- LinkedHashMap: preserva a ordem de INSERÇÃO ----
        final Map<String, Integer> contagem = new LinkedHashMap<>();
        contagem.put("zebra", 1);
        contagem.put("abelha", 2);
        contagem.put("gato", 3);
        IO.println("\nLinkedHashMap mantem ordem de insercao: " + contagem.keySet());
    }
}