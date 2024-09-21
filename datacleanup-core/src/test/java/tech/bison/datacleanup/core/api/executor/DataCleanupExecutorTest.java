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

package tech.bison.datacleanup.core.api.executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.command.CleanupResult;
import tech.bison.datacleanup.core.api.command.ResourceCleanupSummary;

class DataCleanupExecutorTest {

  @Test
  public void execute_allCleanupCommands() {
    Context context = mock(Context.class);
    CleanupCommand command1 = mock(CleanupCommand.class);
    CleanupCommand command2 = mock(CleanupCommand.class);
    ResourceCleanupSummary summary1 = new ResourceCleanupSummary(5);
    ResourceCleanupSummary summary2 = new ResourceCleanupSummary(3);

    when(command1.execute(context)).thenReturn(summary1);
    when(command2.execute(context)).thenReturn(summary2);
    when(command1.getResourceType()).thenReturn(CleanableResourceType.CUSTOMER);
    when(command2.getResourceType()).thenReturn(CleanableResourceType.ORDER);

    DataCleanupExecutor executor = new DataCleanupExecutor();
    List<CleanupCommand> commands = List.of(command1, command2);

    CleanupResult result = executor.execute(context, commands);

    assertEquals(summary1, result.getResourceSummary(CleanableResourceType.CUSTOMER));
    assertEquals(summary2, result.getResourceSummary(CleanableResourceType.ORDER));
  }
}
