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
