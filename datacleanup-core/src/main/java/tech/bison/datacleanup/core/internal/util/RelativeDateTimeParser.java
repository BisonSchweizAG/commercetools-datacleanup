/*
 * Copyright (C) 2000 - 2024 Bison Schweiz AG
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.bison.datacleanup.core.internal.util;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Parser for relative date time string in the format of {@code {{now[diff...]}}} <br> 'diff' is optional.<br> Whitespaces are allowed before and after each 'diff'.
 * </p>
 * <p>Examples:
 * <ul>
 *   <li>{@code {{now}}}</li>
 *   <li>{@code {{now-60s}}}</li>
 *   <li>{@code {{now-3M}}</li>
 *   <li>{@code {{now+1y+1M-2h}}</li>
 * </ul>
 * </p>
 */
public class RelativeDateTimeParser {

  private static final Pattern datePattern = Pattern.compile("\\{\\{now(\\s*[+-]\\d+[yMdhms])*\\s*}}");
  private static final Pattern diffPattern = Pattern.compile("[+-]\\d+[yMdhms]");
  private final LocalDateTime now;

  public RelativeDateTimeParser() {
    this(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
  }

  public RelativeDateTimeParser(Clock clock) {
    this.now = LocalDateTime.now(clock);
  }

  public LocalDateTime parse(String input) {
    if (input == null || input.isEmpty()) {
      throw new IllegalArgumentException("Input must not be null or empty.");
    }
    Matcher matcher = datePattern.matcher(input);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(String.format("Pattern '%s' has not correct format.", input));
    }
    LocalDateTime result = now;
    Matcher diffMatcher = diffPattern.matcher(input);
    while (diffMatcher.find()) {
      String diff = diffMatcher.group();
      int amountLength = diff.length() - 1;
      TemporalUnit unit = resolveUnit(diff.charAt(amountLength));
      long amount = Long.parseLong(diff.substring(0, amountLength));
      result = result.plus(amount, unit);
    }
    return result;
  }

  private static TemporalUnit resolveUnit(char unit) {
    return switch (unit) {
      case 'y' -> ChronoUnit.YEARS;
      case 'M' -> ChronoUnit.MONTHS;
      case 'd' -> ChronoUnit.DAYS;
      case 'h' -> ChronoUnit.HOURS;
      case 'm' -> ChronoUnit.MINUTES;
      case 's' -> ChronoUnit.SECONDS;
      default -> throw new UnsupportedOperationException(String.format("Invalid time unit '%s'. Supported units are: yMdhms.", unit));
    };
  }

}
