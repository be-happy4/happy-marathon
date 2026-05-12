# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build the full project (skip tests for speed)
mvn clean install -DskipTests

# Run with the local profile (default)
cd yudao-server && mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -pl yudao-module-system -Dtest=AuthControllerTest

# Run tests for a specific module
mvn test -pl yudao-module-system
```

No Maven wrapper — use the system-installed `mvn`. The project requires JDK 25 and uses `application-local.yaml` (the default active profile). The server starts on port **48080**.

## Architecture

This is **Yudao** (芋道), a forked/evolved version of ruoyi-vue-pro — a Spring Boot 3 multi-module rapid development platform.

### Module layout

```
yudao-dependencies/       — Maven BOM, single source of truth for all dependency versions
yudao-framework/          — ~14 framework starter modules (common, web, security, mybatis, redis, mq, tenant, data-permission, etc.)
yudao-server/             — Runnable Spring Boot app; a "shell" that wires together yudao-module-* dependencies
yudao-module-system/      — System features: users, roles, menus, tenants, OAuth2, auth, etc. (required)
yudao-module-infra/       — Infrastructure: codegen, file storage, job scheduling, WebSocket, API logging (required)
yudao-module-{bpm|pay|mall|crm|erp|mes|ai|iot|mp|report|member}/ — Optional business modules, commented out in root pom.xml
```

**Currently active modules** (enabled in root `pom.xml`): `system`, `infra`. All other modules are commented out.

### How `yudao-server` works

`YudaoServerApplication` scans both `${yudao.info.base-package}.server` and `${yudao.info.base-package}.module` (where `base-package` = `cn.iocoder.yudao`). Each `yudao-module-*` is a self-contained Maven module with its own controllers, services, and DAL. Adding a module means uncommenting it in the root POM _and_ adding it as a dependency in `yudao-server/pom.xml`.

### Layer conventions (inside each module)

Every module follows the same package layout:

```
cn.iocoder.yudao.module.<name>.controller.admin.xxx  — Admin API controllers (path: /admin-api/<module>/...)
cn.iocoder.yudao.module.<name>.controller.app.xxx    — App/user-facing API controllers (path: /app-api/<module>/...)
cn.iocoder.yudao.module.<name>.service               — Service interfaces + impl package
cn.iocoder.yudao.module.<name>.dal.dataobject        — MyBatis Plus entities
cn.iocoder.yudao.module.<name>.dal.mysql             — MyBatis Plus mappers
cn.iocoder.yudao.module.<name>.dal.redis             — Redis DAOs
cn.iocoder.yudao.module.<name>.convert               — MapStruct converters (XxxConvert.INSTANCE pattern)
cn.iocoder.yudao.module.<name>.enums                 — Module-specific enums
```

Key types:
- `CommonResult<T>` — the standard API response wrapper (code + data + msg)
- `PageResult<T>` — paginated response (list + total)
- `PageParam` / `SortablePageParam` — pagination request params
- `ErrorCode` + `ServiceException` — business exceptions with error codes (managed via the error-code system)

### Framework starters (yudao-framework)

| Starter | Purpose |
|---------|---------|
| `yudao-common` | Shared enums, utils, `CommonResult`, `PageResult`, validation annotations |
| `yudao-spring-boot-starter-web` | Global exception handler, XSS filter, Swagger/Knife4j config, API encryption |
| `yudao-spring-boot-starter-security` | Token-based auth filter, `@PreAuthorize` with `@PermitAll` bypass |
| `yudao-spring-boot-starter-mybatis` | MyBatis Plus config, `BaseMapperX` (extends MP's `BaseMapper` with batch ops), encryption |
| `yudao-spring-boot-starter-redis` | Redisson + Spring Data Redis config |
| `yudao-spring-boot-starter-biz-tenant` | Multi-tenant: ignores tables/caches/URLs, auto-filters by tenant ID |
| `yudao-spring-boot-starter-biz-data-permission` | Row-level data scope via `@DataPermission` annotation |
| `yudao-spring-boot-starter-mq` | Message queue abstraction (Redis Stream, RabbitMQ, Kafka, RocketMQ) |
| `yudao-spring-boot-starter-protection` | Distributed lock (Lock4j), idempotency, rate limiting |
| `yudao-spring-boot-starter-test` | Base test classes (see Testing section) |

### Database

Configured in `application-local.yaml` with **dynamic datasource** (`master` + `slave`). Currently uses **PostgreSQL** on `127.0.0.1:5432/marathon`. SQL init scripts for all supported databases live in `sql/` (MySQL, PostgreSQL, Oracle, DM, OpenGauss, SQL Server, Kingbase).

MyBatis Plus `id-type: NONE` — the `IdTypeEnvironmentPostProcessor` auto-detects the database type and switches between AUTO (MySQL) and INPUT (Oracle/PostgreSQL/Kingbase).

### Auth model

Stateless token authentication via Spring Security. The `TokenAuthenticationFilter` reads a token from the `Authorization` header, validates it against Redis, and sets `SecurityContextHolder`. Controllers use `@PreAuthorize` with permission strings. URLs listed in `yudao.security.permit-all_urls` bypass authentication. Multi-tenant is enabled by default (`yudao.tenant.enable: true`).

### Lombok config

- `toString`/`equalsAndHashCode` call super by default
- Fluent accessor chaining is on (`@Accessors(chain = true)`)

## Testing

Test base classes in `yudao-spring-boot-starter-test`:

| Base class | When to use |
|------------|-------------|
| `BaseMockitoUnitTest` | Pure Mockito, no Spring context — for unit tests that only need mocking |
| `BaseDbUnitTest` | Spins up an in-memory H2 database + MyBatis Plus — for mapper/service tests |
| `BaseDbAndRedisUnitTest` | Same as above + embedded Redis — for tests that need both |
| `BaseRedisUnitTest` | Embedded Redis only |

Tests use `application-unit-test` profile. Use `AssertUtils` for common assertion patterns.

## Key config properties (application.yaml)

- `yudao.info.base-package` = `cn.iocoder.yudao` — drives component scanning, MyBatis alias packages, codegen
- `yudao.tenant.enable` — toggles multi-tenant globally
- `yudao.security.mock-enable` — when true, allows mock login in dev (set in `application-local.yaml`)
- `yudao.demo` — demo mode, must be `false` for normal operation
- `yudao.captcha.enable` — toggle captcha (disabled in local profile)
