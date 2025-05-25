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

import static org.assertj.core.api.Assertions.assertThat;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CATEGORY;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CUSTOM_OBJECT;

import com.commercetools.api.client.ProjectApiRoot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tech.bison.datacleanup.core.DataCleanup;

@SpringBootTest(classes = {SpringBootDataCleanupAutoConfiguration.class})
@ActiveProfiles("test")
class SpringBootDataCleanupAutoConfigurationTest {

  @MockitoBean
  private ProjectApiRoot projectApiRoot;

  @Autowired
  private DataCleanup dataCleanup;

  @Test
  void startup() {
    assertThat(dataCleanup).isNotNull();
    var configuration = dataCleanup.getConfiguration();
    assertThat(configuration.getPredicates()).hasSize(2);
    assertThat(configuration.getPredicates()).hasEntrySatisfying(CUSTOM_OBJECT, e -> e.contains("container='myContainer'"));
    assertThat(configuration.getPredicates()).hasEntrySatisfying(CATEGORY, e -> e.contains("version=10"));
    assertThat(configuration.getCustomCommandClasses()).hasSize(2);
    assertThat(configuration.getCustomCommandClasses().getFirst()).isEqualTo("com.example.MyCleanupCustomCommand1");
    assertThat(configuration.getCustomCommandClasses().get(1)).isEqualTo("com.example.MyCleanupCustomCommand2");
  }

}
