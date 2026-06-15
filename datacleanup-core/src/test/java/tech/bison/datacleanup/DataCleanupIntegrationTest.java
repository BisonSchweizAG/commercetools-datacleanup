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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CART;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CATEGORY;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CUSTOM_OBJECT;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.ORDER;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.PRODUCT;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tech.bison.datacleanup.core.DataCleanup;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;
import tech.bison.datacleanup.core.api.configuration.CommercetoolsProperties;
import tech.bison.datacleanup.core.api.configuration.DataCleanupPredicate;

@WireMockTest(httpPort = 8087)
public class DataCleanupIntegrationTest {

  private CommercetoolsProperties commercetoolsProperties;

  @BeforeEach
  void setUp() {
    var mockServerHostAndPort = "http://localhost:8087";
    stubFor(post(urlEqualTo("/auth"))
        .willReturn(jsonResponse("token.json")));

    commercetoolsProperties = new CommercetoolsProperties("test", "test", mockServerHostAndPort, mockServerHostAndPort + "/auth", "integrationtest");
  }

  @Test
  void configureCustomObjectPredicateThenDeleteMatchingResources() {
    stubFor(get(urlPathEqualTo("/integrationtest/custom-objects/myContainer"))
        .willReturn(jsonResponse("query-custom-objects.json")));

    stubFor(delete(urlPathEqualTo("/integrationtest/custom-objects/myContainer/myKey"))
        .willReturn(jsonResponse("delete-custom-object.json")));

    var result = DataCleanup.configure()
        .withClock(Clock.fixed(LocalDateTime.of(2024, 9, 16, 18, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault()))
        .withApiProperties(commercetoolsProperties)
        .withPredicates(Map.of(CUSTOM_OBJECT, new DataCleanupPredicate("myContainer", List.of("creationDate > '{{now-3M}}'"))))
        .load()
        .execute();

    assertThat(result.getResourceSummary(CUSTOM_OBJECT).deleteCount()).isEqualTo(1);
    verify(getRequestedFor(urlPathEqualTo("/integrationtest/custom-objects/myContainer"))
        .withQueryParam("where", equalTo("creationDate > '2024-06-16T18:00:00'")));
  }

  @ParameterizedTest
  @MethodSource("provideInputForCleanup")
  void configurePredicateThenDeleteMatchingResources(CleanableResourceType resourceType, String resourceEndpoint, String predicate) {
    stubFor(get(urlPathEqualTo(String.format("/integrationtest/%s", resourceEndpoint)))
        .willReturn(jsonResponse(String.format("query-%s.json", resourceEndpoint))));

    stubFor(delete(urlPathEqualTo(String.format("/integrationtest/%s/c2f93298-c967-44af-8c2a-d2220bf39eb2", resourceEndpoint)))
        .willReturn(jsonResponse(String.format("delete-%s.json", resourceType.getName()))));

    var result = DataCleanup.configure()
        .withApiProperties(commercetoolsProperties)
        .withPredicates(Map.of(resourceType, new DataCleanupPredicate(null, List.of(predicate))))
        .load()
        .execute();

    assertThat(result.getResourceSummary(resourceType).deleteCount()).isEqualTo(1);
  }

  private static ResponseDefinitionBuilder jsonResponse(String bodyFile) {
    return aResponse().withHeader("Content-Type", "application/json").withBodyFile(bodyFile);
  }

  private static Stream<Arguments> provideInputForCleanup() {
    return Stream.of(
        Arguments.of(CATEGORY, "categories", "name = \"catName\""),
        Arguments.of(PRODUCT, "products", "name = \"productName\""),
        Arguments.of(CART, "carts", "isActive = \"false\""),
        Arguments.of(ORDER, "orders", "creationDate < \"2024-01-04T00:00:00.0000Z\"")
    );
  }
}
