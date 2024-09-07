# commercetools Datacleanup

commercetools Data Cleanup is data reorganisation tool for commercetools. Configure predicates for your resources and commercetools Data Cleanup will periodically delete resources that match the predicate.

## Setup

TODO

## Usage with Spring Boot

### 1. Add dependency

Add our Spring Boot Starter to your gradle or maven file.

### 2. Configuration

Use application properties to configure the cleanup predicates for the commercetools resources to cleanup:

```yaml
datacleanup:
    predicates:
        custom-object:
            - "container = \"myContainer\" and createdAt > ($currentDate - 6M)"
        category:
            - ...
    classes:
        - com.example.myCommand

```

### 3. Create a background cleanup job

Take your background job library of your choice and execute the cleanup commands with the Core API.
Example with Spring and ShedLock:

```java
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

...

@AutoWired
public CleanupJob(DataCleanup dataCleanup) {
    this.dataCleanup = dataCleanup;
}

@Scheduled(...)
@SchedulerLock(name = "cleanup")
public void scheduledTask() {
    // To assert that the lock is held (prevents misconfiguration errors)
    LockAssert.assertLocked();
    dataCleanup.execute();
}
```

## Usage with the Core API

```java
DataCleanup dataCleanup = DataCleanup.configure()
        .withApiProperties(new CommercetoolsProperties("clientId", "clientSecret", "apiUrl", "authUrl", "projectKey"))
        .withPredicate(CUSTOM_OBJECT, "container = \"email\" and createdAt > \"2024 - 08 - 28T08:25:59.157Z\"")
        .load()
        .execute();
```

## Building

There is a possibility to use alternative url to maven central:
create gradle.properties and set for example:
REPO1_URL=https://artifactory.example.com/repo1

## License

commercetools Data Cleanup is published under the Apache License 2.0, see http://www.apache.org/licenses/LICENSE-2.0 for details.
