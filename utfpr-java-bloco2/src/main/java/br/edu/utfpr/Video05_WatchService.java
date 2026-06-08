package br.edu.utfpr;

import java.nio.file.*;

/**
 * VÍDEO 5 — WatchService: reagindo a mudanças em pastas
 * <p>
 * Requer Java 25 LTS
 * <p>
 * Monitora a pasta de entrada do coletor. Ao cair um arquivo novo, o programa reage automaticamente (hot folder).
 * <p>
 * Para testar: rode o programa e, em outro terminal, crie um arquivo dentro de dados-cotacoes/entrada
 * (ex.: 'touch dados-cotacoes/entrada/x.txt').
 */
public class Video05_WatchService {

    static void main() throws Exception {

        final Path pasta = Path.of("dados-cotacoes", "entrada");
        Files.createDirectories(pasta);

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {

            // Registra a pasta informando os eventos de interesse.
            pasta.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            IO.println("Monitorando: " + pasta.toAbsolutePath());
            IO.println("(Ctrl+C para sair)\n");

            while (true) {
                final WatchKey key = watcher.take(); // bloqueia ate haver evento

                for (var evento : key.pollEvents()) {
                    var tipo = evento.kind();
                    final Path nome = (Path) evento.context();
                    IO.println(tipo.name() + " -> " + nome);
                }

                // Obrigatorio: rearma a key para continuar recebendo eventos.
                boolean valida = key.reset();
                if (!valida) {
                    IO.println("Pasta inacessivel, encerrando.");
                    break;
                }
            }
        }
    }
}