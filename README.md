# commercetools Datacleanup

commercetools Data Cleanup is data reorganisation tool for commercetools. Configure predicates for your resources and commercetools Data Cleanup will periodically delete resources that match the predicate.

## Usage with Spring Boot

### 1. Add dependency

Add our Spring Boot Starter to your gradle or maven file.

```groovy
implementation "io.github.studix:commercetools-datacleanup-spring-boot-starter:x.y.z"
```

(latest version numbers avaible on [Maven Central](https://central.sonatype.com/search?namespace=io.github.studix&name=commercetools-datacleanup-spring-boot-starter))

### 2. Configuration

Use application properties to configure the cleanup predicates for the commercetools resources to cleanup:

```yaml
datacleanup:
    predicates:
        custom-object:
            - "container = 'myContainer' and createdAt > '{{now-6M}}'"
        category:
            - ...
    classes:
        - com.example.myCommand

```

Within the predicate you can provide a datetime pattern enclosed with double curly brackets to get a relative datetime. The basic format is {{now[diff...]}}.

- _diff_ is optional
- There can be multiple _diff's_ and they can be specified in any order
- Whitespaces are allowed before and after each _diff_

Examples:

- {{now}}}
- {{now-3M}}
- {{now+1y+1M}}

If you want full control of the cleanup logic you can configure a class which implements the CleanupCommand interface. The class must be configured by its fully qualified name.

### 3. Create a background cleanup job

Take your background job library of your choice and execute the cleanup commands with the Core API.
Example with Spring Scheduling and [ShedLock](https://github.com/lukas-krecan/ShedLock):

```java
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

...

@AutoWired
public CleanupJob(DataCleanup dataCleanup) {
    this.dataCleanup = dataCleanup;
}

@Scheduled(cron = "0 2 * * *")
@SchedulerLock(name = "cleanup")
public void scheduledTask() {
    LockAssert.assertLocked();
    dataCleanup.execute();
}
```

## Usage with the Core API

### 1. Add dependency

Add the data cleanup core module to your gradle or maven file.

```groovy
implementation "io.github.studix:commercetools-datacleanup-core:x.y.z"
```

(latest version numbers avaible on [Maven Central](https://central.sonatype.com/search?namespace=io.github.studix&name=commercetools-datacleanup-core))

### 2. Configure and execute the cleanup commands

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
