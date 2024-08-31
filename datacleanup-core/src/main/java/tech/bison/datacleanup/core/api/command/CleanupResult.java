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
package tech.bison.datacleanup.core.api.command;

import java.util.HashMap;
import java.util.Map;

public class CleanupResult {

  private Map<CleanableResourceType, ResourceCleanupSummary> resourceCleanupSummaryMap = new HashMap<>();

  public static CleanupResult empty() {
    return new CleanupResult();
  }

  public void addResult(CleanableResourceType resourceType, ResourceCleanupSummary summary) {
    resourceCleanupSummaryMap.put(resourceType, summary);
  }

  public ResourceCleanupSummary getResourceSummary(CleanableResourceType resourceType) {
    return resourceCleanupSummaryMap.get(resourceType);
  }
}
