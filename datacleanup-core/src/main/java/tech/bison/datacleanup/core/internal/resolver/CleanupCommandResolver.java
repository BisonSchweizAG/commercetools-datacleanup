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

import java.util.ArrayList;
import java.util.List;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.configuration.Configuration;
import tech.bison.datacleanup.core.internal.command.CategoryCommand;
import tech.bison.datacleanup.core.internal.command.CustomObjectCommand;

public class CleanupCommandResolver {

  private final Configuration configuration;

  public CleanupCommandResolver(Configuration configuration) {
    this.configuration = configuration;
  }

  public List<CleanupCommand> getCommands() {
    List<CleanupCommand> cleanupCommands = new ArrayList<>();
    for (var cleanupPredicate : configuration.getPredicates().entrySet()) {
      switch (cleanupPredicate.getKey()) {
        case CUSTOM_OBJECT -> cleanupCommands.add(new CustomObjectCommand(cleanupPredicate.getValue()));
        case CATEGORY -> cleanupCommands.add(new CategoryCommand(cleanupPredicate.getValue()));
      }
    }
    return cleanupCommands;
  }

}
