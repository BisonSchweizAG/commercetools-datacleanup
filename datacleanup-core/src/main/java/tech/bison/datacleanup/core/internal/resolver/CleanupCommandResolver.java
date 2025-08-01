/*
 * Copyright (C) 2024 Bison Schweiz AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.bison.datacleanup.core.internal.resolver;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.configuration.Configuration;
import tech.bison.datacleanup.core.api.exception.DataCleanupException;
import tech.bison.datacleanup.core.internal.command.CartCommand;
import tech.bison.datacleanup.core.internal.command.CategoryCommand;
import tech.bison.datacleanup.core.internal.command.CustomObjectCommand;
import tech.bison.datacleanup.core.internal.command.OrderCommand;
import tech.bison.datacleanup.core.internal.command.ProductCommand;
import tech.bison.datacleanup.core.internal.util.RelativeDateTimeParser;

public class CleanupCommandResolver {

  private static final Logger LOG = LoggerFactory.getLogger(CleanupCommandResolver.class);
  private static final Pattern predicateReplacePattern = Pattern.compile("\\{\\{(.*)}}");
  private final Configuration configuration;
  private final RelativeDateTimeParser dateTimeParser;


  public CleanupCommandResolver(Configuration configuration) {

    this.configuration = configuration;
    if (configuration.getClock() != null) {
      this.dateTimeParser = new RelativeDateTimeParser(configuration.getClock());
    } else {
      this.dateTimeParser = new RelativeDateTimeParser();
    }
  }

  public List<CleanupCommand> getCommands() {
    List<CleanupCommand> cleanupCommands = new ArrayList<>();
    for (var entry : configuration.getPredicates().entrySet()) {
      var cleanupPredicate = entry.getValue();
      switch (entry.getKey()) {
        case CUSTOM_OBJECT -> cleanupCommands.add(new CustomObjectCommand(cleanupPredicate.container(), parsePredicates(cleanupPredicate.whereClauses())));
        case CATEGORY -> cleanupCommands.add(new CategoryCommand(parsePredicates(cleanupPredicate.whereClauses())));
        case ORDER -> cleanupCommands.add(new OrderCommand(parsePredicates(cleanupPredicate.whereClauses())));
        case CART -> cleanupCommands.add(new CartCommand(parsePredicates(cleanupPredicate.whereClauses())));
        case PRODUCT -> cleanupCommands.add(new ProductCommand(parsePredicates(cleanupPredicate.whereClauses())));
        default -> LOG.warn("Unsupported predicate type '{}'.", entry.getKey());
      }
    }
    configuration.getCustomCommandClasses().forEach(c -> cleanupCommands.add(createCommand(c)));
    return cleanupCommands;
  }

  private CleanupCommand createCommand(String clazz) {
    try {
      return (CleanupCommand) Class.forName(clazz).getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new DataCleanupException("Unable to instantiate class " + clazz + " : " + e.getMessage(), e);
    }
  }

  private List<String> parsePredicates(List<String> predicates) {
    return predicates.stream().map(this::parsePredicate).toList();
  }

  private String parsePredicate(String predicate) {
    var matcher = predicateReplacePattern.matcher(predicate);
    return matcher.replaceAll(match -> dateTimeParser.parse(match.group()).format(DateTimeFormatter.ISO_DATE_TIME));
  }

}
