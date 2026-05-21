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

## Game Module (赛事管理)

The game module is a custom module under `yudao-module-system` that manages marathon events and personal race registrations.

### Tables

| Table | Purpose | Seq |
|-------|---------|-----|
| `system_game` | Marathon event/race data | `system_game_seq` |
| `system_game_registration` | Per-person registration/participation records | `system_game_registration_seq` |

Both tables inherit `TenantBaseDO → BaseDO`, meaning they have `creator`, `create_time`, `updater`, `update_time`, `deleted` (smallint 0/1, NOT boolean), `tenant_id`. IDs are manually assigned via sequences (MyBatis Plus `@KeySequence`).

### Java files

```
yudao-module-system/src/main/java/cn/iocoder/yudao/module/system/
├── controller/admin/game/
│   ├── GameController.java              # REST /admin-api/system/game
│   └── vo/
│       ├── GameSaveReqVO.java
│       ├── GameRespVO.java
│       ├── GamePageReqVO.java           # extends SortablePageParam (for server-side sorting)
│       └── GameSimpleRespVO.java
├── controller/admin/gameregistration/
│   ├── GameRegistrationController.java  # REST /admin-api/system/game-registration
│   └── vo/
│       ├── GameRegistrationSaveReqVO.java
│       ├── GameRegistrationRespVO.java
│       └── GameRegistrationPageReqVO.java
├── service/game/
│   ├── GameService.java / GameServiceImpl.java
│   └── GameRegistrationService.java / GameRegistrationServiceImpl.java
├── dal/dataobject/game/
│   ├── GameDO.java                      # @TableName("system_game")
│   └── GameRegistrationDO.java          # @TableName("system_game_registration")
└── dal/mysql/game/
    ├── GameMapper.java                  # extends BaseMapperX<GameDO>
    └── GameRegistrationMapper.java
```

Error codes: `ErrorCodeConstants.GAME_NOT_FOUND` (1_002_029_000), `GAME_REGISTRATION_NOT_FOUND` (1_002_029_001)

### Dictionary codes

The game module uses dynamic dictionaries (stored in `system_dict_type` / `system_dict_data`). All field values are **English code strings**, not Chinese labels.

**`game_type`** (dict type 3002, values 4019-4022):
| Code | Label |
|------|-------|
| `marathon` | 马拉松 |
| `half_marathon` | 半程马拉松 |
| `road_run` | 路跑赛事 |
| `trail_run` | 越野赛 |

**`game_world_athletics_label_level`** (dict type 3004, values 4006-4009):
| Code | Label |
|------|-------|
| `platinum` | 白金标 |
| `gold` | 金标 |
| `elite` | 精英标 |
| `label` | 标牌 |

**`game_china_road_run_game_level`** (dict type 3005, values 4001-4005):
| Code | Label |
|------|-------|
| `a` | A |
| `a1` | A1 |
| `a2` | A2 |
| `b` | B |
| `c` | C |

**`game_status`** (dict type 3003, values 4010-4018):
| Code | Label | Meaning |
|------|-------|---------|
| `uncertain` | 规划中 | Game date/status unconfirmed |
| `announced` | 定档 | Date confirmed, registration not yet open |
| `registration_open` | 报名中 | Registration open |
| `awaiting_draw` | 待抽签 | Registration closed, waiting for lottery |
| `multi_round_lottery` | 多轮抽签 | Multi-round lottery in progress |
| `waitlist_phase` | 候补阶段 | Waitlist phase after lottery |
| `prepared` | 待开始 | Pre-race ready |
| `finished` | 已结束 | Race completed |
| `canceled` | 已取消 | Race canceled |

**`game_registration_status`** (dict type 3006, values 4025-4034):
| Code | Label | Meaning |
|------|-------|---------|
| `not_participate` | 不参加 | Chose not to participate |
| `unregistered` | 未报名 | Not yet registered |
| `registered` | 已报名 | Registered (includes lottery-pending) |
| `waitlisted` | 候补中 | On waitlist |
| `accepted` | 已中签 | Won lottery |
| `completed` | 已完赛 | Finished the race |
| `not_accepted` | 未中签 | Lost lottery |
| `give_up_race` | 退赛 | Withdrew from race |
| `give_up_lottery` | 中签放弃 | Won lottery but gave up spot |

**`game_tag`** (dict type 3001, values 4023-4024):
| Code | Label |
|------|-------|
| `wmm` | 世界马拉松大满贯 |
| `cmm` | 中国马拉松大满贯 |

Tags field is comma-separated. Non-dictionary tags (like `省会`, `浙江`, `上马`) are stored as plain text alongside dict codes (e.g., `"cmm,省会"`).

### CSV → Database mapping rules

When converting marathon race CSV data to `system_game` + `system_game_registration` INSERTs:

1. **Deduplicate games by name** — same game name = same `system_game` row. Assign sequential IDs.
2. **Sort by `game_date`** ascending before assigning IDs.
3. **`game_type`**: map CSV "Group" column → dict code. "Full Marathon"→`marathon`, "Half Marathon"→`half_marathon`, "10KM"→`road_run`, "Trail Run"→`trail_run`, "Road Run"→`road_run`.
4. **`world_athletics_level`**: map "Platinum Label"→`platinum`, "Gold Label"→`gold`, "Elite Label"→`elite`, "Label"→`label`.
5. **`china_road_run_level`**: map "A"→`a`, "A1"→`a1`, "B"→`b`, "C"→`c`.
6. **`status` (game)**: derive from game date + registration status. If CSV status="Canceled" → `canceled`. If `game_date` < today → `finished`. Otherwise → `announced`.
7. **`registration_status`**: map "Completed"→`completed`, "Unsuccess"→`not_accepted`, "Give up Lottery"→`give_up_lottery`, "Not participating"→`not_participate`, "Canceled"→`give_up_race`, "Uncertain"/"Unregistered"→`unregistered`, "To be drawn"→`waitlisted`, "Registered"→`registered`.
8. **`deleted`** column is **smallint, not boolean**. Use `0` (not deleted) / `1` (deleted). Never use `'false'`/`'true'`.
9. **`creator`/`updater`**: use `'1'` (admin user ID as string).
10. **`tenant_id`**: always `1`.
11. **`gun_time_ms`**: parse "H:MM:SS" or "MM:SS" → total milliseconds. NULL if empty.
12. **`net_time_ms`**: parse `Net Time(Seconds)` column × 1000. NULL if 0 or empty.
13. **`priority`**: integer only. Decimal values (8.5, 7.5) should be rounded or left NULL — the DO field is `Integer`.
14. **Date parsing**: extract year from first `(19|20)\d{2}` in game name. Parse month abbreviation from Date column. Handle formats like "Dec 1" and "Nov 2 7:00 AM (GMT+8)".

### SQL seed data conventions

- File: `sql/postgresql/marathon.sql`
- Contains: DDL (CREATE TABLE, sequences, indexes, comments) → dict_type/dict_data → menu INSERTs → game data INSERTs → directory-disabling UPDATEs
- Dict IDs start at 3001 (type) / 4001 (data) to avoid collision with ruoyi-vue-pro.sql defaults (which start at 1)
- Menu IDs start at 6200 (game) / 6300 (registration)
- Game data is sorted by `game_date` ASC
- All `deleted` values are `0` (smallint)
- All `creator`/`updater` are `'1'`
- Use `NOW()` for timestamps in menu INSERTs; use a fixed timestamp for game data

### Server-side sorting

To add sortable columns to a list page:

**Backend** — change the PageReqVO to extend `SortablePageParam` instead of `PageParam`:
```java
// GamePageReqVO extends SortablePageParam  (not PageParam)
// Frontend sends: { sortingFields: [{ field: "gameDate", order: "desc" }] }
```
The existing `gameMapper.selectPage(pageReqVO)` call will automatically resolve to `BaseMapperX.selectPage(SortablePageParam, Wrapper)` which applies ORDER BY from `sortingFields`.

**Frontend** — add `sortable` on the `el-table-column`, `default-sort` on `el-table`, and a `@sort-change` handler using `buildSortingField`:
```typescript
import { buildSortingField } from '@/utils'
// In queryParams:
sortingFields: [] as { field: string; order: string }[]
// Handler:
const handleSortChange = (params: any) => {
  queryParams.sortingFields = [buildSortingField(params)]
  handleQuery()
}
```
Template: `<el-table @sort-change="handleSortChange" default-sort="{prop: 'gameDate', order: 'descending'}">`
Column: `<el-table-column prop="gameDate" sortable ... />`

### Frontend files

```
happy-marathon-ui/src/
├── api/system/game/index.ts              # GameVO, GameSimpleVO, API functions
├── api/system/gameRegistration/index.ts  # GameRegistrationVO, API functions
├── views/system/game/index.vue           # Game list page
├── views/system/game/GameForm.vue        # Game create/edit dialog
├── views/system/gameRegistration/index.vue
├── views/system/gameRegistration/GameRegistrationForm.vue
└── utils/dict.ts                         # DICT_TYPE constants (lines 337-342)
```

The game list page (`index.vue`) uses `dict-tag` components to render dictionary values as colored tags. Filter fields: name (input), gameDate (date range picker), gameType (select from dict), status (select from dict).
