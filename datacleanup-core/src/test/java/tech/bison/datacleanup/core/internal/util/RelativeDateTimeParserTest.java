package tech.bison.datacleanup.core.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RelativeDateTimeParserTest {

  private Clock clock;

  @BeforeEach
  void setUp() {
    clock = Clock.fixed(LocalDateTime.of(2024, 9, 15, 10, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault());
  }

  @ParameterizedTest
  @MethodSource("provideInputForParser")
  void parseRelativeDates(String input, LocalDateTime expected) {
    var result = createRelativeDateTimeParser().parse(input);

    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> provideInputForParser() {
    return Stream.of(
        Arguments.of("{{now}}", LocalDateTime.of(2024, 9, 15, 10, 0, 0)),
        Arguments.of("{{now-60s}}", LocalDateTime.of(2024, 9, 15, 9, 59, 0)),
        Arguments.of("{{now+10m}}", LocalDateTime.of(2024, 9, 15, 10, 10, 0)),
        Arguments.of("{{now-1h}}", LocalDateTime.of(2024, 9, 15, 9, 0, 0)),
        Arguments.of("{{now-1d}}", LocalDateTime.of(2024, 9, 14, 10, 0, 0)),
        Arguments.of("{{now-3M}}", LocalDateTime.of(2024, 6, 15, 10, 0, 0)),
        Arguments.of("{{now+2y}}", LocalDateTime.of(2026, 9, 15, 10, 0, 0)),
        Arguments.of("{{now+1y+1M-2h}}", LocalDateTime.of(2025, 10, 15, 8, 0, 0)),
        Arguments.of("{{now+1y +1M -2h}}", LocalDateTime.of(2025, 10, 15, 8, 0, 0))
    );
  }

  private RelativeDateTimeParser createRelativeDateTimeParser() {
    return new RelativeDateTimeParser(clock);
  }
}
