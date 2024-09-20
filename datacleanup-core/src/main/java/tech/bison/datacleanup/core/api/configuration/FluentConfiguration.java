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
package tech.bison.datacleanup.core.api.configuration;

import com.commercetools.api.client.ProjectApiRoot;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.bison.datacleanup.core.DataCleanup;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;
import tech.bison.datacleanup.core.api.exception.DataCleanupException;


public class FluentConfiguration implements Configuration {

  private CommercetoolsProperties apiProperties;
  private ProjectApiRoot projectApiRoot;
  private final Map<CleanableResourceType, List<String>> predicates = new HashMap<>();
  private final List<String> customCommandClasses = new ArrayList<>();
  private Clock clock;

  /**
   * @return The new fully-configured DataCleanup instance.
   */
  public DataCleanup load() {
    validateConfiguration();
    return new DataCleanup(this);
  }

  private void validateConfiguration() {
    if (projectApiRoot == null && apiProperties == null) {
      throw new DataCleanupException("Missing commercetools import api configuration. Either use withApiProperties() or withApiRoot().");
    }
  }

  /**
   * Configure the commercetools api with properties.
   */
  public FluentConfiguration withApiProperties(CommercetoolsProperties apiProperties) {
    this.apiProperties = apiProperties;
    return this;
  }

  /**
   * Configure the commercetools api with the given api root.
   */
  public FluentConfiguration withApiRoot(ProjectApiRoot projectApiRoot) {
    this.projectApiRoot = projectApiRoot;
    return this;
  }

  /**
   * Configures predicates for the given resource types which should be deleted. Multiple predicates are combined to an or query.
   */
  public FluentConfiguration withPredicates(Map<CleanableResourceType, List<String>> predicates) {
    this.predicates.putAll(predicates);
    return this;
  }

  /**
   * Configures a custom cleanup command.
   */
  public FluentConfiguration withCustomCommands(String... fullQualifiedClassNames) {
    this.customCommandClasses.addAll(Arrays.stream(fullQualifiedClassNames).toList());
    return this;
  }

  public FluentConfiguration withClock(Clock clock) {
    this.clock = clock;
    return this;
  }


  @Override
  public CommercetoolsProperties getApiProperties() {
    return apiProperties;
  }

  @Override
  public ProjectApiRoot getApiRoot() {
    return projectApiRoot;
  }

  @Override
  public Map<CleanableResourceType, List<String>> getPredicates() {
    return predicates;
  }

  @Override
  public Clock getClock() {
    return clock;
  }

  @Override
  public List<String> getCustomCommandClasses() {
    return customCommandClasses;
  }
}
