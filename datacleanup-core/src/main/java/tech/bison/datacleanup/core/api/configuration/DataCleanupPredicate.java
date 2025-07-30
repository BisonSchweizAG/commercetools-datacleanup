package tech.bison.datacleanup.core.api.configuration;

import java.util.List;

public record DataCleanupPredicate(String container, List<String> whereClauses) {

}
