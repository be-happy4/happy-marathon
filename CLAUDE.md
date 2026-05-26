# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Unified monorepo: backend (Spring Boot) + frontend (Vue 3) in `yudao-ui/yudao-ui-admin-vue3/`.

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

```bash
# Start frontend dev server (port 81)
cd yudao-ui/yudao-ui-admin-vue3 && pnpm install && pnpm dev
```

## Project overview

This is **Yudao** (芋道), a forked/evolved version of ruoyi-vue-pro — a Spring Boot 3 multi-module rapid development platform. **Currently active modules**: `system`, `infra`, `crawler`. All other modules are commented out in root `pom.xml`.

**Local dependencies**: PostgreSQL (`127.0.0.1:5432/marathon`, user `postgres`/`root`) + Redis (`127.0.0.1:6379`).

## Knowledge base

Detailed documentation is organized under `.claude/knowledge/`:

| File | Content |
|------|---------|
| [architecture.md](.claude/knowledge/architecture.md) | Module layout, layer conventions, framework starters reference |
| [database.md](.claude/knowledge/database.md) | Database config, CSV→DB mapping rules, SQL seed data conventions |
| [auth-and-config.md](.claude/knowledge/auth-and-config.md) | Auth model, key config properties, Lombok config |
| [testing.md](.claude/knowledge/testing.md) | Test base classes and how to run tests |
| [game-module.md](.claude/knowledge/game-module.md) | Game module (赛事管理): tables, Java files, dictionary codes, server-side sorting |
| [frontend.md](.claude/knowledge/frontend.md) | Frontend project structure and patterns |
| [crawler-design.md](.claude/knowledge/crawler-design.md) | Crawler architecture design, data model, ETL consistency plan |
| [crawler-module.md](.claude/knowledge/crawler-module.md) | Crawler module code reference: tables, Java files, API endpoints |

## Git Remotes

- `origin`: https://github.com/be-happy4/happy-marathon (fork)
- `upstream`: https://github.com/YunaiV/ruoyi-vue-pro (Yudao upstream)

Sync upstream: `git fetch upstream && git merge upstream/master-jdk17` (only framework/business modules; resolve conflicts in game/crawler/frontend manually).

## Git Convention

Format: `type(scope): description`

| Type | Usage |
|------|-------|
| `feat` | New feature |
| `fix` | Bug fix |
| `chore` | Maintenance, tooling |

Scope: module name (`crawler`, `game`, `system`, etc.). No scope for cross-cutting.

Examples: `feat(crawler): add runchina data source` / `fix(crawler): tenant filtering`

Two repos: `happy-marathon-server` and `happy-marathon-ui`. Commit separately.
