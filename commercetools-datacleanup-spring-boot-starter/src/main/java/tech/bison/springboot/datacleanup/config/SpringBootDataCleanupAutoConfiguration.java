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

package tech.bison.springboot.datacleanup.config;

import com.commercetools.api.client.ProjectApiRoot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import tech.bison.datacleanup.core.DataCleanup;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;
import tech.bison.datacleanup.core.api.configuration.DataCleanupPredicate;

@AutoConfiguration
@EnableConfigurationProperties(DataCleanupConfig.class)
public class SpringBootDataCleanupAutoConfiguration {

  @Bean
  @ConditionalOnClass(DataCleanup.class)
  public DataCleanup dataCleanup(ProjectApiRoot projectApiRoot, DataCleanupConfig dataCleanupConfig) {
    Map<CleanableResourceType, DataCleanupPredicate> typedDefinitions = new EnumMap<>(CleanableResourceType.class);
    if (dataCleanupConfig.getPredicates() != null) {
      dataCleanupConfig.getPredicates()
          .forEach((key, value) -> addDataCleanupPredicate(typedDefinitions, key, value));
    }
    List<String> classes = new ArrayList<>();
    if (dataCleanupConfig.getClasses() != null) {
      classes.addAll(dataCleanupConfig.getClasses());
    }
    return DataCleanup.configure()
        .withPredicates(typedDefinitions)
        .withCustomCommands(classes.toArray(new String[0]))
        .withApiRoot(projectApiRoot)
        .load();
  }

  private void addDataCleanupPredicate(Map<CleanableResourceType, DataCleanupPredicate> typedDefinitions, String type, DataCleanupPredicateConfig predicateConfig) {
    var cleanableResourceType = Arrays.stream(CleanableResourceType.values()).filter(e -> e.getName().equals(type))
        .findFirst().orElseThrow(() -> new UnsupportedOperationException(String.format("Unsupported resource type %s", type)));
    if (cleanableResourceType == CleanableResourceType.CUSTOM_OBJECT && StringUtils.isBlank(predicateConfig.container())) {
      throw new UnsupportedOperationException("Predicate of type custom-object must define a container.");
    }
    typedDefinitions.put(cleanableResourceType, new DataCleanupPredicate(predicateConfig.container(), predicateConfig.where()));
  }
}
