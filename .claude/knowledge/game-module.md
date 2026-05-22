# Game Module (赛事管理)

The game module is a custom module under `yudao-module-system` that manages marathon events and personal race registrations.

## Tables

| Table | Purpose | Seq |
|-------|---------|-----|
| `system_game` | Marathon event/race data (has `region_id`, `remark` is markdown-capable text) | `system_game_seq` |
| `system_game_category` | Event categories/groups (组别), managed via sub-page | `system_game_category_seq` |
| `system_game_registration` | Per-person registration/participation records | `system_game_registration_seq` |
| `system_region` | World regions (ISO 3166 countries + China provinces), tree structure | `system_region_seq` |

All tables inherit `TenantBaseDO → BaseDO`, meaning they have `creator`, `create_time`, `updater`, `update_time`, `deleted` (smallint 0/1, NOT boolean), `tenant_id`. IDs are manually assigned via sequences (MyBatis Plus `@KeySequence`).

### `system_game_category`

| Column | Type | Note |
|--------|------|------|
| `id` | int8 PK | |
| `game_id` | int8 FK → system_game | |
| `game_type` | varchar(32) | dict `game_type` |
| `distance_km` | numeric(8,3) | Category distance |

A game can have multiple categories (e.g., a single event with "全程马拉松" and "半程马拉松" groups). Registration selects from available categories via `game_category_id`.

Note: `distance_km` was removed from `system_game` — distance is now per-category.

## Java files

```
yudao-module-system/src/main/java/cn/iocoder/yudao/module/system/
├── controller/admin/game/
│   ├── GameController.java              # REST /admin-api/system/game, +/categories
│   └── vo/
│       ├── GameSaveReqVO.java            # includes regionId
│       ├── GameRespVO.java               # includes regionId, regionName
│       ├── GamePageReqVO.java            # extends SortablePageParam, multi-select filters
│       ├── GameSimpleRespVO.java
│       └── GameCategoryRespVO.java
├── controller/admin/gamecategory/
│   ├── GameCategoryController.java      # REST /admin-api/system/game-category (CRUD)
│   └── vo/
│       ├── GameCategorySaveReqVO.java
│       └── GameCategoryPageReqVO.java
├── controller/admin/gameregistration/
│   ├── GameRegistrationController.java
│   └── vo/...
├── controller/admin/region/
│   ├── RegionController.java            # GET /system/region/tree
│   └── vo/RegionNodeRespVO.java
├── service/game/
│   ├── GameService.java / GameServiceImpl.java
│   ├── GameCategoryService.java / GameCategoryServiceImpl.java
│   ├── GameRegistrationService.java / GameRegistrationServiceImpl.java
│   └── RegionService.java / RegionServiceImpl.java
├── dal/dataobject/game/
│   ├── GameDO.java                      # @TableName("system_game") — has regionId, remark→markdown
│   ├── GameCategoryDO.java              # @TableName("system_game_category")
│   ├── GameRegistrationDO.java          # @TableName("system_game_registration")
│   └── RegionDO.java                    # @TableName("system_region")
└── dal/mysql/game/
    ├── GameMapper.java                  # multi-select inIfPresent filters
    ├── GameCategoryMapper.java          # CRUD + selectPage
    ├── GameRegistrationMapper.java
    └── RegionMapper.java               # selectAll, selectByParentId
```

Error codes: `ErrorCodeConstants.GAME_NOT_FOUND` (1_002_029_000), `GAME_REGISTRATION_NOT_FOUND` (1_002_029_001)

## Multi-select filters

`GamePageReqVO` supports both single-value (`gameType`, `status`, `worldAthleticsLevel`, `chinaRoadRunLevel`) and multi-select (`gameTypes`, `statuses`, `worldAthleticsLevels`, `chinaRoadRunLevels`) filters. The mapper uses `eqIfPresent` for single + `inIfPresent` for lists.

## Dictionary codes

The game module uses dynamic dictionaries (stored in `system_dict_type` / `system_dict_data`). All field values are **English code strings**, not Chinese labels.

### `game_type` (dict type 3002, values 4019-4022)

| Code | Label |
|------|-------|
| `marathon` | 马拉松 |
| `half_marathon` | 半程马拉松 |
| `road_run` | 路跑赛事 |
| `trail_run` | 越野赛 |

### `game_world_athletics_label_level` (dict type 3004, values 4006-4009)

| Code | Label | Color |
|------|-------|-------|
| `platinum` | 白金标 | `info` (blue) |
| `gold` | 金标 | `warning` (orange) |
| `elite` | 精英标 | `#a78bfa` (muted purple, via cssClass) |
| `label` | 标牌 | `success` (green) |

### `game_china_road_run_game_level` (dict type 3005, values 4001-4005)

| Code | Label |
|------|-------|
| `a` | A |
| `a1` | A1 |
| `a2` | A2 |
| `b` | B |
| `c` | C |

### `game_status` (dict type 3003, values 4010-4018)

| Code | Label | Meaning |
|------|-------|---------|
| `uncertain` | 规划中 | Game date/status unconfirmed (default for new games) |
| `announced` | 定档 | Date confirmed, registration not yet open |
| `registration_open` | 报名中 | Registration open |
| `awaiting_draw` | 待抽签 | Registration closed, waiting for lottery |
| `multi_round_lottery` | 多轮抽签 | Multi-round lottery in progress |
| `waitlist_phase` | 候补阶段 | Waitlist phase after lottery |
| `prepared` | 待开始 | Pre-race ready |
| `finished` | 已结束 | Race completed |
| `canceled` | 已取消 | Race canceled |

### `game_registration_status` (dict type 3006, values 4025-4034)

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

### `game_tag` (dict type 3001, values 4023-4024)

| Code | Label |
|------|-------|
| `wmm` | 世界马拉松大满贯 |
| `cmm` | 中国马拉松大满贯 |

Tags field is comma-separated. Non-dictionary tags (like `省会`, `浙江`, `上马`) are stored as plain text alongside dict codes (e.g., `"cmm,省会"`).

## Region (地区)

World region tree backed by `system_region` table. Uses ISO 3166 standard:
- Type 1: Continents (7)
- Type 2: Countries (249, with ISO alpha-2 codes)
- Type 3: Provinces/States (34 China provinces, expandable)
- Type 4: Cities (future)

Games link to regions via `region_id`. Frontend uses `el-cascader` with `filterable` for tree search — the industry standard for tree-select components.

## 赛事主页 (Markdown)

The `remark` column is now `text` type (unlimited length) for Markdown content. Editing uses a tabbed layout (edit textarea + MarkdownView preview). The existing `MarkdownView` component (markdown-it + highlight.js) renders the content.

## Sub-page: 组别管理

Accessible from the game list via "组别" button → navigates to `/game/category/:gameId`. Full CRUD for categories within a game. Uses the hidden route pattern (`hidden: true, canTo: true, activeMenu: '/system/game'`).

## Server-side sorting

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

Template:
```html
<el-table @sort-change="handleSortChange" default-sort="{prop: 'gameDate', order: 'descending'}">
  <el-table-column prop="gameDate" sortable ... />
</el-table>
```

### 计算字段排序（规划中）

当前计算字段（如配速 `pace`、时速 `speed`）不支持排序。排序必须发生在存储端，不能内存排序。

后续方案：在 DB 中为计算所需的基础值增加虚拟列（如距离米数、净成绩秒数），结合一套单位转换框架，在 SQL 层完成计算后再排序。需要单独出一套方案设计。

Currently, `speed` 排序请求会被 service 层静默剥离，不报错但也不生效。
