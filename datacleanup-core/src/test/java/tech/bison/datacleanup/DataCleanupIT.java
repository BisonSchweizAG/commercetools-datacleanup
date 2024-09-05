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

package tech.bison.datacleanup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CUSTOM_OBJECT;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tech.bison.datacleanup.core.DataCleanup;
import tech.bison.datacleanup.core.api.configuration.CommercetoolsProperties;

@Testcontainers
public class DataCleanupIT {

  public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName
      .parse("mockserver/mockserver")
      .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

  @Container
  public MockServerContainer mockServer = new MockServerContainer(MOCKSERVER_IMAGE);

  private MockServerClient mockServerClient;

  private CommercetoolsProperties commercetoolsProperties;

  @BeforeEach
  void setUp() throws IOException {
    var mockServerHostAndPort = "http://" + mockServer.getHost() + ":" + mockServer.getServerPort();
    mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    mockServerClient
        .when(request().withPath("/auth"))
        .respond(response().withBody(readResponseFromFile("responses/token.json")));

    commercetoolsProperties = new CommercetoolsProperties("test", "test", mockServerHostAndPort, mockServerHostAndPort + "/auth", "integrationtest");
  }

  @AfterEach
  void tearDown() {
    mockServerClient.reset();
    mockServerClient.close();
  }

  @Test
  void configureCustomObjectPredicateThenDeleteMatchingResources() throws IOException {
    mockServerClient
        .when(request().withPath("/integrationtest/custom-objects"))
        .respond(response().withBody(readResponseFromFile("responses/query-custom-objects.json")));

    mockServerClient
        .when(request().withPath("/integrationtest/custom-objects/myContainer/myKey"))
        .respond(response().withBody(readResponseFromFile("responses/delete-custom-object.json")));

    var result = DataCleanup.configure()
        .withApiProperties(commercetoolsProperties)
        .withPredicates(Map.of(CUSTOM_OBJECT, List.of("container = \"myContainer\"")))
        .load()
        .execute();

    assertThat(result.getResourceSummary(CUSTOM_OBJECT).deleteCount()).isEqualTo(1);
  }

  private String readResponseFromFile(String filename) throws IOException {
    final File file = new File(getClass().getClassLoader().getResource(filename).getFile());
    return Files.readString(file.toPath(), StandardCharsets.UTF_8);
  }
}
