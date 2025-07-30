package tech.bison.springboot.datacleanup.config;

import java.util.List;

public record DataCleanupPredicateConfig(String container, List<String> where) {

}
