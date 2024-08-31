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
package tech.bison.datacleanup.core;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.command.CleanupResult;
import tech.bison.datacleanup.core.api.configuration.Configuration;
import tech.bison.datacleanup.core.api.configuration.FluentConfiguration;
import tech.bison.datacleanup.core.api.exception.DataCleanupException;
import tech.bison.datacleanup.core.api.executor.Context;
import tech.bison.datacleanup.core.api.executor.DataCleanupExecutor;
import tech.bison.datacleanup.core.internal.resolver.CleanupCommandResolver;

/**
 * Entry point for a data cleanup run.
 */
public class DataCleanup {

  private static final Logger LOG = LoggerFactory.getLogger(DataCleanup.class);

  private final Configuration configuration;
  private final CleanupCommandResolver commandResolver;
  private final DataCleanupExecutor dataCleanupExecutor;


  public DataCleanup(Configuration configuration) {
    this.configuration = configuration;
    commandResolver = new CleanupCommandResolver(configuration);
    dataCleanupExecutor = new DataCleanupExecutor();
  }

  /**
   * This is your starting point. This creates a configuration which can be customized to your needs before being loaded into a new DataCleanup instance using the load() method.
   * <p>
   * In its simplest form, this is how you configure DataCleanup with all defaults to get started:
   * <pre>DataCleanup dataCleanup = DataCleanup.configure().withApiUrl(..).load();</pre>
   * <p>
   * After that you have a fully-configured DataCleanup instance and you can call migrate()
   *
   * @return A new configuration from which DataCleanup can be loaded.
   */
  public static FluentConfiguration configure() {
    return new FluentConfiguration();
  }

  /**
   * Executes the configured data cleanup commands.
   *
   * @throws tech.bison.datacleanup.core.api.exception.DataCleanupException in case of any exception thrown during execution.
   */
  public CleanupResult execute() {
    try {
      var context = new Context(configuration);
      List<CleanupCommand> cleanupCommands = commandResolver.getCommands(context);
      if (cleanupCommands.isEmpty()) {
        LOG.info("No cleanup commands found.");
        return CleanupResult.empty();
      }
      return dataCleanupExecutor.execute(context, cleanupCommands);
    } catch (Exception ex) {
      throw new DataCleanupException("Error while executing data cleanup.", ex);
    }
  }
}
