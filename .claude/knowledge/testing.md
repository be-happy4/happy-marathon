# Testing

Test base classes in `yudao-spring-boot-starter-test`:

| Base class | When to use |
|------------|-------------|
| `BaseMockitoUnitTest` | Pure Mockito, no Spring context — for unit tests that only need mocking |
| `BaseDbUnitTest` | Spins up an in-memory H2 database + MyBatis Plus — for mapper/service tests |
| `BaseDbAndRedisUnitTest` | Same as above + embedded Redis — for tests that need both |
| `BaseRedisUnitTest` | Embedded Redis only |

Tests use `application-unit-test` profile. Use `AssertUtils` for common assertion patterns.

## Running tests

```bash
# Run all tests
mvn test

# Run a single test class
mvn test -pl yudao-module-system -Dtest=AuthControllerTest

# Run tests for a specific module
mvn test -pl yudao-module-system
```
