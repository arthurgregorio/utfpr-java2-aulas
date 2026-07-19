package br.edu.utfpr;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * VÍDEO 8 — Date and Time API (java.time)
 * Requer Java 25 LTS
 */
public class Video08DateTime {

    void main() {
        // ---- LocalDate / LocalDateTime: sem fuso ----
        final LocalDate hoje = LocalDate.now();
        final LocalDate natal = LocalDate.of(2025, 12, 25);
        IO.println("Hoje: " + hoje);
        IO.println("Dia da semana do Natal 2025: " + natal.getDayOfWeek());

        // Operações imutáveis (retornam nova instância):
        final LocalDate daquiUmaSemana = hoje.plusWeeks(1);
        IO.println("Daqui a uma semana: " + daquiUmaSemana);

        // ---- Period: diferença em anos/meses/dias ----
        final Period ateNatal = Period.between(hoje, natal);
        IO.println("Ate o Natal: " + ateNatal.getMonths() + " meses e "
                + ateNatal.getDays() + " dias");

        // ---- ChronoUnit: diferença em uma unidade ----
        final long diasAteNatal = ChronoUnit.DAYS.between(hoje, natal);
        IO.println("Dias ate o Natal: " + diasAteNatal);

        // ---- Duration: diferença baseada em tempo ----
        final LocalDateTime inicio = LocalDateTime.of(2025, 1, 1, 9, 0);
        final LocalDateTime fim = LocalDateTime.of(2025, 1, 1, 17, 30);
        final Duration jornada = Duration.between(inicio, fim);
        IO.println("\nJornada: " + jornada.toHours() + "h" + (jornada.toMinutesPart()) + "min");

        // ---- ZonedDateTime: com fuso horario ----
        final ZonedDateTime agoraSaoPaulo = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        final ZonedDateTime agoraToquio = agoraSaoPaulo.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        IO.println("\nSao Paulo: " + agoraSaoPaulo.toLocalTime());
        IO.println("Toquio:    " + agoraToquio.toLocalTime());

        // ---- DateTimeFormatter: formatação e parsing ----
        final DateTimeFormatter formatoBrasil =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        IO.println("\nFormatado: " + inicio.format(formatoBrasil));

        final LocalDate dataParseada = LocalDate.parse("2025-06-15");
        IO.println("Parseada de ISO: " + dataParseada);
    }
}