package tech.bison.datacleanup.core.internal.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CATEGORY;
import static tech.bison.datacleanup.core.api.command.CleanableResourceType.CUSTOM_OBJECT;

import java.util.List;
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
import tech.bison.datacleanup.core.api.executor.Context;
import tech.bison.datacleanup.core.internal.CleanupPredicate;
import tech.bison.datacleanup.core.internal.command.CategoryCommand;
import tech.bison.datacleanup.core.internal.command.CustomObjectCommand;

@ExtendWith(MockitoExtension.class)
class CleanupCommandResolverTest {

  @Mock
  private Configuration configuration;
  @Mock
  private Context context;


  @ParameterizedTest
  @MethodSource("provideInputForPredicates")
  void getCommands_predicates_containsCommand(CleanableResourceType resourceType, Class<?> expectedClas) {
    var cleanupPredicate = new CleanupPredicate(resourceType, "predicate");
    Mockito.when(configuration.getCleanupPredicates()).thenReturn(List.of(cleanupPredicate));
    CleanupCommandResolver cleanupCommandResolver = new CleanupCommandResolver(configuration);

    var commands = cleanupCommandResolver.getCommands(context);

    assertThat(commands).hasSize(1);
    assertThat(commands.getFirst()).isInstanceOf(expectedClas);
  }

  private static Stream<Arguments> provideInputForPredicates() {
    return Stream.of(
        Arguments.of(CUSTOM_OBJECT, CustomObjectCommand.class),
        Arguments.of(CATEGORY, CategoryCommand.class)
    );
  }
}
