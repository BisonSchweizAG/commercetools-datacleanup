package tech.bison.datacleanup.core.internal.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CART;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CATEGORY;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CUSTOM_OBJECT;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.ORDER;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.PRODUCT;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.bison.datacleanup.core.api.command.CleanableResourceType;
import tech.bison.datacleanup.core.api.command.CleanupCommand;
import tech.bison.datacleanup.core.api.command.ResourceCleanupSummary;
import tech.bison.datacleanup.core.api.configuration.Configuration;
import tech.bison.datacleanup.core.api.configuration.DataCleanupPredicate;
import tech.bison.datacleanup.core.api.executor.Context;
import tech.bison.datacleanup.core.internal.command.CartCommand;
import tech.bison.datacleanup.core.internal.command.CategoryCommand;
import tech.bison.datacleanup.core.internal.command.CustomObjectCommand;
import tech.bison.datacleanup.core.internal.command.OrderCommand;
import tech.bison.datacleanup.core.internal.command.ProductCommand;

@ExtendWith(MockitoExtension.class)
class CleanupCommandResolverTest {

  @Mock
  private Configuration configuration;

  @Test
  void getCommands_commandClasses_containsCommands() {
    when(configuration.getCustomCommandClasses()).thenReturn(List.of(TestCleanupCommand1.class.getName(), TestCleanupCommand2.class.getName()));
    CleanupCommandResolver cleanupCommandResolver = new CleanupCommandResolver(configuration);

    var commands = cleanupCommandResolver.getCommands();

    assertThat(commands).hasSize(2);
    assertThat(commands.getFirst()).isInstanceOf(TestCleanupCommand1.class);
    assertThat(commands.get(1)).isInstanceOf(TestCleanupCommand2.class);
  }

  @ParameterizedTest
  @MethodSource("provideInputForPredicates")
  void getCommands_predicates_containsCommand(CleanableResourceType resourceType, String container, Class<?> expectedClas) {
    when(configuration.getPredicates()).thenReturn(Map.of(resourceType, new DataCleanupPredicate(container, List.of("predicate"))));
    CleanupCommandResolver cleanupCommandResolver = new CleanupCommandResolver(configuration);

    var commands = cleanupCommandResolver.getCommands();

    assertThat(commands).hasSize(1);
    assertThat(commands.getFirst()).isInstanceOf(expectedClas);
  }

  private static Stream<Arguments> provideInputForPredicates() {
    return Stream.of(
        Arguments.of(CUSTOM_OBJECT, "container", CustomObjectCommand.class),
        Arguments.of(CATEGORY, null, CategoryCommand.class),
        Arguments.of(PRODUCT, null, ProductCommand.class),
        Arguments.of(CART, null, CartCommand.class),
        Arguments.of(ORDER, null, OrderCommand.class)
    );
  }

  public static class TestCleanupCommand1 implements CleanupCommand {

    @Override
    public ResourceCleanupSummary execute(Context context) {
      return null;
    }

    @Override
    public CleanableResourceType getResourceType() {
      return CATEGORY;
    }
  }

  public static class TestCleanupCommand2 implements CleanupCommand {

    @Override
    public ResourceCleanupSummary execute(Context context) {
      return null;
    }

    @Override
    public CleanableResourceType getResourceType() {
      return CATEGORY;
    }
  }
}
