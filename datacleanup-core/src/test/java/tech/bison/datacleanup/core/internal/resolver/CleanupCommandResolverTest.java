package tech.bison.datacleanup.core.internal.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CART;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CATEGORY;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CUSTOM_OBJECT;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.ORDER;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.PRODUCT;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;
import tech.bison.datacleanup.core.api.configuration.Configuration;
import tech.bison.datacleanup.core.internal.command.CartCommand;
import tech.bison.datacleanup.core.internal.command.CategoryCommand;
import tech.bison.datacleanup.core.internal.command.CustomObjectCommand;
import tech.bison.datacleanup.core.internal.command.OrderCommand;
import tech.bison.datacleanup.core.internal.command.ProductCommand;

@ExtendWith(MockitoExtension.class)
class CleanupCommandResolverTest {

  @Mock
  private Configuration configuration;

  @ParameterizedTest
  @MethodSource("provideInputForPredicates")
  void getCommands_predicates_containsCommand(CleanableResourceType resourceType, Class<?> expectedClas) {
    Mockito.when(configuration.getPredicates()).thenReturn(Map.of(resourceType, List.of("predicate")));
    CleanupCommandResolver cleanupCommandResolver = new CleanupCommandResolver(configuration);

    var commands = cleanupCommandResolver.getCommands();

    assertThat(commands).hasSize(1);
    assertThat(commands.getFirst()).isInstanceOf(expectedClas);
  }

  private static Stream<Arguments> provideInputForPredicates() {
    return Stream.of(
        Arguments.of(CUSTOM_OBJECT, CustomObjectCommand.class),
        Arguments.of(CATEGORY, CategoryCommand.class),
        Arguments.of(PRODUCT, ProductCommand.class),
        Arguments.of(CART, CartCommand.class),
        Arguments.of(ORDER, OrderCommand.class)
    );
  }
}
