# Database

> **多端同步**：本文件随 git 同步，换电脑 clone 后即可生效。另一台电脑的数据库连接信息可能不同，修改 `application-local.yaml` 后请同步更新下方连接信息。

Configured in `application-local.yaml` with **dynamic datasource** (`master` + `slave`). Currently uses **PostgreSQL** on `127.0.0.1:5432/marathon`. SQL init scripts for all supported databases live in `sql/` (MySQL, PostgreSQL, Oracle, DM, OpenGauss, SQL Server, Kingbase).

MyBatis Plus `id-type: NONE` — the `IdTypeEnvironmentPostProcessor` auto-detects the database type and switches between AUTO (MySQL) and INPUT (Oracle/PostgreSQL/Kingbase).

## Quick connect

```bash
psql -h 127.0.0.1 -p 5432 -U postgres -d marathon
# Password: root
```

## Connection info (local profile)

| Item | Value |
|------|-------|
| Host | `127.0.0.1` |
| Port | `5432` |
| Database | `marathon` |
| Username | `postgres` |
| Password | `root` |
| Redis Host | `127.0.0.1:6379` |
| Redis DB | `0` |
| Server Port | `48080` |

Full connection string: `jdbc:postgresql://127.0.0.1:5432/marathon`

配置文件位置：`yudao-server/src/main/resources/application-local.yaml`

## CSV → Database mapping rules

When converting marathon race CSV data to `system_game` + `system_game_category` + `system_game_registration` INSERTs:

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
15. **Categories**: for each game, create a `system_game_category` row with the game's `game_type` and `distance_km`. A game can have multiple categories (e.g., a marathon event with both "全程马拉松" and "半程马拉松" groups).
16. **Registration category**: set `game_category_id` on registration rows matching the game's category.

## SQL seed data conventions

- **`sql/postgresql/marathon.sql`** — DDL + metadata: CREATE TABLE, sequences, indexes, comments, dict_type/dict_data, menu INSERTs, directory-disabling UPDATEs
- **`sql/postgresql/marathon-data.sql`** — Business data: game INSERTs, category INSERTs, registration INSERTs
- Tables: `system_game` (event, no `distance_km`), `system_game_category` (categories per event), `system_game_registration` (with `game_category_id`)
- Dict IDs start at 3001 (type) / 4001 (data) to avoid collision with ruoyi-vue-pro.sql defaults (which start at 1)
- Menu IDs start at 6200 (game) / 6300 (registration)
- Game data is sorted by `game_date` ASC
- All `deleted` values are `0` (smallint)
- All `creator`/`updater` are `'1'`
- Use `NOW()` for timestamps in menu INSERTs; use a fixed timestamp for game data
