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
package tech.bison.datacleanup.core.api.executor;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.command.CleanupResult;
import tech.bison.datacleanup.core.api.command.ResourceCleanupSummary;

public class DataCleanupExecutor {

  private final static Logger LOG = LoggerFactory.getLogger(DataCleanupExecutor.class);

  public CleanupResult execute(Context context, List<CleanupCommand> cleanupCommands) {
    CleanupResult cleanupResult = CleanupResult.empty();
    for (var cleanupCommand : cleanupCommands) {
      LOG.info("Running data cleanup for resource '{}'.", cleanupCommand.getResourceType().getName());
      ResourceCleanupSummary resourceCleanupSummary = cleanupCommand.execute(context);
      cleanupResult.addResult(cleanupCommand.getResourceType(), resourceCleanupSummary);
    }
    return cleanupResult;
  }
}
