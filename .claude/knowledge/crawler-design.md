# 爬虫定时任务设计文档 v4

## 1. 总体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        管理后台 UI                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐  │
│  │ 数据源管理    │  │ 任务监控      │  │ 数据导入（ETL）       │  │
│  │ CRUD + 启停  │  │ 运行历史/状态 │  │ 赛事导入 + 成绩确认   │  │
│  └──────────────┘  └──────────────┘  └──────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                      业务层                                      │
│  ┌──────────┐  ┌──────────┐  ┌────────────┐  ┌─────────────┐  │
│  │ Crawler  │  │ Pipeline │  │  Match     │  │  ETL        │  │
│  │ Engine   │→ │          │→ │  Engine    │→ │  Service    │  │
│  └──────────┘  └──────────┘  └────────────┘  └─────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                      存储层                                      │
│  ┌──────────────────────┐     ┌──────────────────────────────┐  │
│  │ crawler_* 临时表      │ ETL │ system_* 生产表（不变）       │  │
│  │ （爬虫数据完全隔离）    │ →→  │ system_game                  │  │
│  │ crawler_game          │     │ system_game_category         │  │
│  │ crawler_game_category │     │ system_game_registration     │  │
│  │ crawler_game_result   │     │                              │  │
│  │ crawler_source        │     │                              │  │
│  │ crawler_task_log      │     │                              │  │
│  └──────────────────────┘     └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

**核心原则**：
- 爬虫数据 100% 落入 `crawler_*` 临时表，**绝不直接写生产表**
- 生产表仅在 ETL 导入环节写入，可追溯、可回滚
- 数据源可在页面增删改查，不硬编码
- 调试阶段可考虑临时表放在独立 schema 甚至独立 DB 中，通过动态数据源切换

## 2. 模块结构

独立模块 `yudao-module-crawler`。

```
yudao-module-crawler/
└── src/main/java/cn/iocoder/yudao/module/crawler/
    ├── core/                                    # 核心抽象
    │   ├── Crawler.java                         # 爬虫接口
    │   ├── CrawlerContext.java                  # 爬取上下文（模式、时间范围、分页游标）
    │   ├── CrawlResult.java                     # 爬取结果封装（含 rawData）
    │   ├── CrawlType.java                       # 枚举：GAME / RESULT
    │   ├── CrawlMode.java                       # 枚举：FULL / INCREMENTAL
    │   └── CrawlerRegistry.java                 # 注册中心（发现所有 Crawler 实现）
    │
    ├── source/                                  # 自定义爬虫实现（复杂源）
    │   └── worldathletics/
    │       ├── WorldAthleticsCrawler.java       # 实现 Crawler，处理认证+分页+解析
    │       └── WorldAthleticsParser.java
    │
    ├── generic/                                 # 通用爬虫（配置化，简单 API 源）
    │   ├── GenericApiCrawler.java               # 根据 crawler_source 配置执行爬取
    │   └── ResponseMapper.java                  # 根据 response_mapping JSON 映射字段
    │
    ├── pipeline/                                # 管道：CrawlResult → 临时表 DO
    │   ├── DataPipeline.java                    # 管道接口
    │   ├── GamePipeline.java                    # → crawler_game + crawler_game_category
    │   └── ResultPipeline.java                  # → crawler_game_result
    │
    ├── merge/                                   # 去重
    │   ├── MergeStrategy.java
    │   ├── GameMergeStrategy.java               # (name, game_date)
    │   └── ResultMergeStrategy.java             # 见 4.4 节
    │
    ├── match/                                   # 成绩匹配引擎 ★新增
    │   ├── MatchStrategy.java                   # 匹配策略接口
    │   ├── IdCardMatchStrategy.java             # 身份证号哈希匹配（国内）
    │   ├── PassportMatchStrategy.java           # 护照号哈希匹配（国际）
    │   ├── BibNameGameMatchStrategy.java        # 参赛号+姓名+赛事（国际）
    │   └── MatchEngine.java                     # 编排多个策略，返回匹配结果+置信度
    │
    ├── etl/                                     # ETL 导入服务 ★新增
    │   ├── EtlGameService.java                  # 赛事数据：crawler_game → system_game
    │   ├── EtlResultService.java                # 成绩数据：确认 → system_game_registration
    │   └── EtlLogService.java                   # 导入日志
    │
    ├── job/                                     # Quartz JobHandler
    │   ├── CrawlGameJob.java                    # 定时爬取赛事
    │   └── CrawlResultJob.java                  # 定时爬取成绩
    │
    ├── service/                                 # 业务服务
    │   ├── CrawlerSourceService.java            # 数据源 CRUD
    │   ├── CrawlerTaskLogService.java           # 任务日志查询
    │   └── CrawlerGameResultService.java        # 临时成绩管理
    │
    ├── controller/admin/                        # 管理后台 API
    │   ├── CrawlerSourceController.java         # /admin-api/crawler/source CRUD
    │   ├── CrawlerTaskLogController.java        # /admin-api/crawler/task-log 列表
    │   ├── CrawlerGameController.java           # /admin-api/crawler/game 临时赛事列表+导入
    │   ├── CrawlerGameResultController.java     # /admin-api/crawler/result 临时成绩+匹配+确认
    │   └── vo/...
    │
    ├── dal/
    │   ├── dataobject/
    │   │   ├── CrawlerSourceDO.java
    │   │   ├── CrawlerTaskLogDO.java
    │   │   ├── CrawlerGameDO.java               # 临时赛事（结构≈system_game + source 追踪）
    │   │   ├── CrawlerGameCategoryDO.java       # 临时组别
    │   │   └── CrawlerGameResultDO.java         # 临时成绩
    │   └── mysql/
    │       ├── CrawlerSourceMapper.java
    │       ├── CrawlerTaskLogMapper.java
    │       ├── CrawlerGameMapper.java
    │       ├── CrawlerGameCategoryMapper.java
    │       └── CrawlerGameResultMapper.java
    │
    ├── convert/                                 # MapStruct
    │   ├── CrawlerSourceConvert.java
    │   ├── CrawlerGameResultConvert.java
    │   └── EtlConvert.java                      # ETL 转换器（crawler DO → system DO）
    │
    └── config/
        └── CrawlerProperties.java               # yaml 全局配置
```

## 3. 数据库设计

### 3.1 数据源配置 `crawler_source`（页面可 CRUD）

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | int8 PK | |
| `name` | varchar(128) | 显示名称，如"世界田联" |
| `source_key` | varchar(64) UNIQUE | 程序标识，如 `world_athletics` |
| `crawl_type` | varchar(16) | `GAME` / `RESULT` / `BOTH` |
| `source_type` | varchar(16) | `OFFICIAL`（官方机构）/ `PLATFORM`（赛事平台） |
| `handler_type` | varchar(16) | `GENERIC`（通用配置化）/ `CUSTOM`（自定义实现类） |
| `handler_class` | varchar(256) | CUSTOM 时填充，如 `cn.iocoder...WorldAthleticsCrawler` |
| `base_url` | varchar(512) | API 基础地址 |
| `auth_config` | jsonb | `{"type":"api_key","key":"xxx","header_name":"X-Api-Key"}` |
| `request_config` | jsonb | `{"pagination_type":"page","page_param":"page","size_param":"size","rate_limit_ms":1000}` |
| `response_mapping` | jsonb | GENERIC 模式下的响应字段映射，见 3.1.1 |
| `cron_expression` | varchar(64) | 定时表达式，如 `0 30 3 * * ?` |
| `enabled` | boolean | 启停开关 |
| `sort_order` | int4 | 排序 |
| `last_run_time` | timestamp | 最近一次执行时间 |
| `last_run_status` | varchar(16) | `SUCCESS` / `FAILED` / `RUNNING` |
| `last_run_summary` | text | 最近一次摘要 |
| `creator` / `create_time` / `updater` / `update_time` / `deleted` / `tenant_id` | | 标准字段 |

#### 3.1.1 `response_mapping` 结构（GENERIC 模式用）

```json
{
  "result_path": "$.data.items",         // JSONPath 到结果数组
  "total_path": "$.data.total",           // 总数（分页用）
  "field_mapping": {
    "name": "$.eventName",                // 源字段 JSONPath → DO 字段
    "game_date": "$.startDate",
    "region_name": "$.location.country",
    "world_athletics_level": "$.waLabel",
    "categories": {
      "path": "$.categories",
      "fields": {
        "game_type": "$.type",
        "distance_km": "$.distance"
      }
    }
  },
  "value_transform": {                    // 值转换规则
    "game_date": "DATE:yyyy-MM-dd",
    "world_athletics_level": "MAP:{'Platinum Label':'platinum','Gold Label':'gold','Elite Label':'elite','Label':'label'}"
  }
}
```

### 3.2 任务日志 `crawler_task_log`

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | int8 PK | |
| `source_id` | int8 FK→crawler_source | |
| `crawl_type` | varchar(16) | GAME / RESULT |
| `crawl_mode` | varchar(16) | FULL / INCREMENTAL |
| `start_time` | timestamp | |
| `end_time` | timestamp | |
| `status` | varchar(16) | RUNNING / SUCCESS / FAILED |
| `total_fetched` | int4 | 爬取原始条数 |
| `total_new` | int4 | 新增到临时表 |
| `total_updated` | int4 | 更新临时表已有 |
| `total_ignored` | int4 | 跳过（去重） |
| `error_msg` | text | 异常信息 |
| `create_time` | timestamp | |

### 3.3 临时赛事 `crawler_game`（隔离表，不直接写 system_game）

结构 ≈ `system_game` + 来源追踪 + 导入状态。

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | int8 PK | |
| `source_id` | int8 FK→crawler_source | 来自哪个数据源 |
| `source_game_id` | varchar(128) | 源系统中的原始 ID |
| `name` | varchar(256) | 赛事名称 |
| `name_en` | varchar(256) | 英文名称 |
| `game_date` | date | |
| `region_name` | varchar(256) | 原始地区名称（爬取值） |
| `region_id` | int8 | ETL 后匹配到 system_region 的 ID |
| `world_athletics_level` | varchar(32) | dict code |
| `china_road_run_level` | varchar(32) | dict code |
| `status` | varchar(32) | 赛事状态 dict code |
| `remark` | text | 来源系统描述（非用户编辑内容） |
| `tags` | varchar(512) | 标签（逗号分隔） |
| `raw_data` | jsonb | 完整原始返回数据 |
| `import_status` | varchar(16) | `PENDING` / `IMPORTED` / `IGNORED` |
| `import_time` | timestamp | ETL 导入时间 |
| `import_game_id` | int8 | 导入后对应的 system_game.id |
| `create_time` / `update_time` / `deleted` / `tenant_id` | | 标准字段 |

### 3.4 临时组别 `crawler_game_category`

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | int8 PK | |
| `crawler_game_id` | int8 FK→crawler_game | |
| `game_type` | varchar(32) | 组别类型 dict code |
| `distance_km` | numeric(8,3) | 距离 |
| `create_time` / `update_time` / `deleted` / `tenant_id` | | |

### 3.5 临时成绩 `crawler_game_result`

| 列 | 类型 | 说明 |
|----|------|------|
| `id` | int8 PK | |
| `source_id` | int8 FK→crawler_source | 数据来源 |
| `crawler_game_id` | int8 FK→crawler_game | 临时赛事 ID |
| `crawler_category_id` | int8 FK→crawler_game_category | 临时组别 ID |
| `game_name` | varchar(256) | 赛事名称（冗余，便于展示） |
| `game_date` | date | 赛事日期（冗余） |
| `bib_number` | varchar(32) | 参赛号 |
| `name` | varchar(128) | 选手姓名 |
| `name_en` | varchar(128) | 英文名/拼音 |
| `nationality` | varchar(64) | 国籍（国际赛事匹配用） |
| `gender` | varchar(8) | M / F |
| `age_group` | varchar(32) | 年龄组 |
| `id_card` | varchar(32) | 身份证号（国内源） |
| `passport` | varchar(32) | 护照号（国际源） |
| `gun_time_ms` | int8 | 枪声成绩（毫秒） |
| `net_time_ms` | int8 | 净成绩（毫秒） |
| `rank` | int4 | 总排名 |
| `gender_rank` | int4 | 性别排名 |
| `category_rank` | int4 | 组别排名 |
| `raw_data` | jsonb | 原始返回数据 |
| `match_status` | varchar(16) | 匹配状态，见 4.5.1 |
| `match_user_id` | int8 | 匹配到的系统用户 ID |
| `match_confidence` | varchar(16) | `HIGH` / `MEDIUM` / `LOW` |
| `match_strategy` | varchar(32) | 使用的匹配策略 |
| `import_status` | varchar(16) | 导入状态，见 4.5.1 |
| `import_user_id` | int8 | 确认导入的用户 ID |
| `import_registration_id` | int8 | 导入后的 system_game_registration.id |
| `import_time` | timestamp | |
| `create_time` / `update_time` / `deleted` / `tenant_id` | | |

### 3.6 隔离策略

**调试阶段**（当前）：所有 `crawler_*` 表在同一 PostgreSQL database 中，独立于生产表。数据不会影响现有 `system_*` 表。

**生产阶段**（未来可选）：可配置独立的 datasource，将爬虫临时表放在独立 schema 或独立 DB 中。框架已有动态数据源能力（`yudao-spring-boot-starter-mybatis`），只需配置多数据源。

```yaml
# 未来：独立数据源（可选）
spring:
  datasource:
    dynamic:
      datasource:
        master: ...       # 生产库
        crawler:          # 爬虫临时库（独立 schema/DB）
          url: jdbc:postgresql://127.0.0.1:5432/marathon_crawler
          username: postgres
          password: root
```

## 4. 核心设计

### 4.1 数据源注册方式：双轨制

| 方式 | 适用场景 | 配置来源 |
|------|---------|---------|
| **GENERIC**（通用配置化） | API 结构简单，JSONPath 映射即可 | DB `crawler_source` 表的 `response_mapping` JSON |
| **CUSTOM**（自定义实现） | 需要签名认证、复杂分页、HTML 解析等 | 代码实现 `Crawler` 接口，`crawler_source.handler_class` 指向实现类 |

```
crawler_source 表：

┌────┬────────────┬──────────────┬──────────────────────────┐
│ id │ name       │ handler_type │ handler_class            │
├────┼────────────┼──────────────┼──────────────────────────┤
│  1 │ 数字心动    │ CUSTOM       │ ...RunchinaCrawler       │
│  2 │ 某简单 API │ GENERIC      │ NULL（用 response_mapping）│
└────┴────────────┴──────────────┴──────────────────────────┘
```

CrawlerRegistry 启动时的逻辑：

```java
// 1. 扫描所有 @Component Crawler 实现（CUSTOM 类型）
// 2. 加载 crawler_source 表中 handler_type=GENERIC 的记录
// 3. 为每条 GENERIC 记录创建 GenericApiCrawler 实例
// 4. 统一管理在一个 Map<sourceKey, Crawler> 中
```

### 4.2 两条数据流（全部走临时表）

```
赛事（Game）：
  Scheduler → Crawler.crawl() → Pipeline → crawler_game / crawler_game_category
                                             │ import_status = PENDING
                                             │
                                    ┌────────┘
                                    ▼
                              管理后台"赛事导入"
                              批量选择 → ETL →
                                    ▼
                              system_game / system_game_category
                              crawler_game.import_status = IMPORTED

成绩（Result）：
  Scheduler → Crawler.crawl() → Pipeline → crawler_game_result
                                             │ import_status = PENDING
                                             │ match_status = UNMATCHED
                                             │
                                    ┌────────┘
                                    ▼
                              MatchEngine.autoMatch()
                              各策略尝试匹配用户
                              → MATCHED / UNMATCHED
                                    │
                                    ▼
                              管理后台"成绩确认"
                              ├─ 已匹配：批量确认导入
                              ├─ 未匹配：用户认领 or 管理员手动匹配
                              └─ 忽略/标记重复
                                    │
                                    ▼
                              ETL → system_game_registration
                              crawler_game_result.import_status = CONFIRMED
```

### 4.3 Crawler 接口

```java
public interface Crawler {

    /** 对应的 source_key */
    String getSourceKey();

    /** 爬取类型 */
    CrawlType getType();

    /** 执行爬取，返回原始数据列表 */
    List<CrawlerResult> crawl(CrawlerContext ctx) throws Exception;
}
```

### 4.4 全量/增量

由 Job 传递 `CrawlMode` 到 `CrawlerContext`：

| 模式 | 行为 | 触发 |
|------|------|------|
| `FULL` | 清空该 source 的临时数据，重新全量拉取 | 首次接入、手动触发、数据修复 |
| `INCREMENTAL` | 根据上次成功时间拉取增量 | 每日定时任务（主模式） |

对于不支持增量 API 的源，INCREMENTAL 退化为全量拉取 + merge 跳过已有。

### 4.5 成绩匹配引擎（核心复杂点）

#### 4.5.1 状态定义

`crawler_game_result` 有两组状态：

**match_status**（匹配状态）：
```
UNMATCHED  → 未匹配（待处理）
MATCHED    → 已匹配到用户（可导入）
CONFLICT   → 多个用户匹配（需人工判断）
DUPLICATE  → 与已有爬虫记录重复
```

**import_status**（导入状态）：
```
PENDING    → 待导入
CONFIRMED  → 已确认导入
IGNORED    → 已忽略
CLAIMED    → 用户已认领（待管理员确认）
```

#### 4.5.2 匹配策略链（按置信度排序）

```
策略1: 身份证号匹配（国内，置信度 HIGH）
  条件: id_card 不为空
  逻辑: id_card vs 用户表绑定的身份证号
  结果: 唯一匹配 → MATCHED + HIGH

策略2: 护照号匹配（国际，置信度 HIGH）
  条件: passport 不为空
  逻辑: passport vs 用户表绑定的护照号
  结果: 唯一匹配 → MATCHED + HIGH

策略3: 参赛号+姓名+赛事匹配（国际通用，置信度 MEDIUM）
  条件: bib_number + name + game_id 都不为空
  逻辑: 用户在 system_game_registration 中是否已有同赛事同参赛号记录
  结果: 匹配 → MATCHED + MEDIUM

策略4: 姓名+国籍+赛事匹配（国际 fallback，置信度 LOW）
  条件: name + nationality + game_id 不为空
  逻辑: 同赛事中是否有同名同国籍的注册记录
  结果: 匹配 → MATCHED + LOW（需人工确认）

策略5: 用户主动认领（置信度由用户确认保证）
  条件: 用户在前端搜索并认领
  逻辑: 用户说"这是我的成绩"
  结果: → CLAIMED
```

#### 4.5.3 冲突场景

| 场景 | 处理 |
|------|------|
| 同一身份证号匹配到多个用户 | → `CONFLICT`，人工处理 |
| 同一人同一赛事多个来源有成绩 | → `DUPLICATE`，按 `source_type` 优先级选 OFFICIAL > PLATFORM |
| 用户已有手动录入成绩 vs 爬虫成绩 | → 提示冲突，用户选择保留哪个 |
| 同一爬虫源多次爬取到同一人同一赛事 | → MergeStrategy 自动 UPDATE 并标记 |

#### 4.5.4 用户认领流程

```
用户端：
  1. 访问"我的成绩" → "认领成绩" tab
  2. 系统展示 match_status=UNMATCHED 的成绩列表
  3. 可按赛事名称、日期搜索
  4. 用户找到自己的成绩 → 点击"认领"
  5. 填写确认信息（参赛号、身份证/护照后4位验证）
  6. → import_status=CLAIMED，等待管理员确认

管理员端：
  1. "成绩确认"页面，查看 CLAIMED 列表
  2. 核验用户提交的验证信息
  3. 通过 → ETL 导入
  4. 拒绝 → 退回 UNMATCHED
```

#### 4.5.5 国际赛事匹配的特殊考量

国际赛事通常没有身份证号。关键匹配信息：
- **官方成绩册**（PDF/网页）：姓名、参赛号、国籍、年龄组、成绩
- **护照号**：只有报名平台（如 World Athletics 旗下平台）才可能有
- **姓名歧义**：拼音重名率高（如 "Wei Wang"），必须结合国籍/参赛号/年龄组
- **生日**：如果用户在系统中有生日，可作为辅助匹配条件（年龄组验证）

**建议优先级**：先实现国内身份证匹配（数据源确定、匹配准确），国际匹配先保留接口，后续根据实际数据源情况逐步实现。

### 4.6 ETL 导入

ETL 是爬虫数据进入生产表的唯一通道。

```
                  crawler_game                system_game
               ┌────────────────┐         ┌────────────────┐
               │ id=1, name=北马 │  ETL   │ id=100, name=北马│
               │ source=数字心动  │ ───→  │ source=crawler  │
               │ import_status=  │        │ source_id=1     │
               │   IMPORTED      │        │                 │
               │ import_game_id  │        │                 │
               │   =100          │        │                 │
               └────────────────┘         └────────────────┘
```

ETL 步骤：
1. 校验数据完整性（必填字段检查）
2. 匹配 `system_region`（`region_name` → `region_id`）
3. 匹配字典值（确认 dict code 有效）
4. 去重检查（是否已有同名+同日期的 system_game）
5. 写入生产表
6. 回写 `crawler_game.import_status = IMPORTED` + `import_game_id`

## 5. 任务调度

不再硬编码 cron 表达式，每个数据源的 cron 存储在 `crawler_source.cron_expression` 中。

### 统一调度 Job

```java
@Component
public class CrawlGameJob implements JobHandler {

    @Resource
    private CrawlerSourceService sourceService;
    @Resource
    private CrawlerRegistry registry;

    @Override
    public String execute(String param) {
        // 查找所有 crawlType=GAME/BOTH 且 enabled=true 的数据源
        // 对每个源：判断是否到了该源的 cron 时间 → 执行爬取
        // 记录 crawler_task_log
    }
}
```

Quartz 管理后台配置 `CrawlGameJob` 每 5 分钟触发一次，具体是否执行由每个 source 的 cron 决定。这样所有源的调度统一管理，不需要为每个源建一个 Quartz job。

> 替代方案：每个启用的 source 动态注册一个 Quartz job。初期先用统一触发器方式，简单可控。

## 6. 管理后台页面规划

| 页面 | 路由 | 功能 |
|------|------|------|
| 数据源管理 | `/crawler/source` | 列表、新增、编辑、删除、启停、手动触发 |
| 任务日志 | `/crawler/task-log` | 按数据源/时间/状态筛选，查看详情和错误信息 |
| 赛事导入 | `/crawler/game` | 临时赛事列表，批量选择 → 导入到 system_game |
| 成绩确认 | `/crawler/result` | 按匹配状态筛选，批量确认，冲突处理，认领审核 |

### 数据源管理页

```
┌──────────────────────────────────────────────────────────────┐
│ 数据源管理                                     [+ 新增数据源]   │
├────┬──────────┬──────┬──────┬────────┬──────┬──────┬──────────┤
│ ID │ 名称     │ 类型  │ 方式  │ 最近运行 │ 状态 │ 启用 │ 操作     │
├────┼──────────┼──────┼──────┼────────┼──────┼──────┼──────────┤
│  1 │ 数字心动  │ RESULT│CUSTOM│05-25   │成功  │ [✓]  │编辑 触发 │
│  2 │ 世界田联  │ GAME  │CUSTOM│05-25   │失败  │ [✓]  │编辑 触发 │
│  3 │ 某赛事API │ BOTH  │GENERIC│--    │--   │ [ ]  │编辑 触发 │
└────┴──────────┴──────┴──────┴────────┴──────┴──────┴──────────┘
```

### 任务监控（Dashboard 区域或独立页）

```
数据源: 数字心动 [最近30天]
┌─────────────────────────────────────────┐
│  ● 最近7天爬取趋势                        │
│  ████████▌  (05-25: +120 新增, 3 更新)   │
│  ███████   (05-24: +95 新增, 5 更新)    │
│  █████████ (05-23: +200 新增, 0 更新)   │
│  ...                                    │
├─────────────────────────────────────────┤
│  最近一次: 2026-05-25 03:30  SUCCESS     │
│  爬取 500 条, 新增 120, 更新 3, 跳过 377 │
│  耗时 12.3s                              │
└─────────────────────────────────────────┘
```

## 7. ETL Schema 一致性方案

### 7.1 问题

`system_game`、`system_game_registration` 等生产表在开发阶段可能变化（加字段、改名、调整类型）。当前 ETL 写死映射逻辑，后期改表容易遗漏导致数据丢失或不一致。

### 7.2 目标

生产表变更时，ETL 代码能**在编译期暴露所有受影响点**，同时支持**不重新爬取即可回填历史数据**。

### 7.3 方案：编译期安全 + raw_data 兜底

```
                        ┌──────────────────────┐
                        │   crawler_game        │
                        │   ├── name            │
                        │   ├── game_date       │
                        │   ├── ...             │
                        │   └── raw_data (JSONB)│  ← 完整原始数据
                        └──────────┬───────────┘
                                   │
                    ┌──────────────┴──────────────┐
                    │   EtlConvert (MapStruct)     │  ← 编译期检查
                    │   crawler DO → system DO     │
                    │   字段变更 → 编译失败 → 修   │
                    └──────────────┬──────────────┘
                                   │
                        ┌──────────▼───────────┐
                        │   system_game         │
                        │   (生产表 DO)         │
                        └──────────────────────┘
```

**三层防线**：

#### 第一层：MapStruct 编译期检查

ETL 转换用 MapStruct 接口，编译期生成实现代码。生产表 DO 字段变更 → 编译报错 → 精确到哪一行哪个字段。

```java
// EtlGameConvert.java
@Mapper
public interface EtlGameConvert {
    EtlGameConvert INSTANCE = Mappers.getMapper(EtlGameConvert.class);

    @Mapping(target = "id", ignore = true)              // 新 ID，不沿用
    @Mapping(target = "source", constant = "crawler")   // 标记来源
    @Mapping(target = "sourceId", source = "id")        // crawler_game.id
    @Mapping(target = "sourceUpdatedAt", source = "createTime")
    @Mapping(target = "remark", ignore = true)           // 用户内容，爬虫不填
    GameDO toGameDO(CrawlerGameDO source);

    @Mapping(target = "id", ignore = true)
    GameCategoryDO toCategoryDO(CrawlerGameCategoryDO source);
}
```

当 `GameDO` 新增 `courseRecord` 字段时：
1. MapStruct 默认 warning（unmapped target property）
2. 配置 `unmappedTargetPolicy = ERROR` → 编译直接失败
3. 开发者被迫决定：从 raw_data 提取？忽略？从其他字段推导？

#### 第二层：raw_data 回填

每条 `crawler_game` 和 `crawler_game_result` 都有 `raw_data` JSONB 保存完整原始返回。当生产表新增字段且原始数据中包含对应信息时，不需要重新爬取，直接 re-ETL：

```sql
-- 假设 system_game 新增了 organizer（主办方）字段
-- 而原始 API 返回中本就有 organizer 信息
-- 只需更新 EtlConvert 映射：

@Mapping(target = "organizer", expression = "java(extractFromRaw(source.getRawData(), \"$.organizer\"))")
GameDO toGameDO(CrawlerGameDO source);

-- 然后对未导入的 crawler_game 重新 ETL 即可
-- 已导入的可以通过 import_game_id 找到对应记录并 UPDATE
```

#### 第三层：ETL 版本追踪

`crawler_task_log` 记录 `etl_version`（git commit hash 或自增版本号）。`crawler_game.import_time` 记录导入时间。可以回答：
- 这批数据是用哪个版本的 ETL 逻辑导入的
- 从哪个版本开始新增了某个字段的映射
- 哪些历史数据需要 re-ETL

```
crawler_game:
  import_status = IMPORTED
  import_etl_version = "abc1234"  ← 导入时的 ETL 版本

crawler_task_log:
  etl_version = "def5678"         ← 本次运行的 ETL 版本
```

### 7.4 变更场景演练

| 生产表变更 | 处理方式 | 是否需要重爬 |
|-----------|---------|-------------|
| 新增字段（原始数据有） | 更新 EtlConvert 映射，re-ETL 未导入数据，已导入数据 UPDATE | 否 |
| 新增字段（原始数据没有） | 仅新爬取的数据有，re-ETL 时该字段为 NULL | 下次增量覆盖 |
| 字段改名 | 编译报错 → 更新 EtlConvert | 否 |
| 字段删除 | 编译报错 → 移除映射 | 否 |
| 字段类型变更 | 编译报错 → 调整转换逻辑 | 否 |
| 字典值变更（如新增赛事等级） | 更新 EtlConvert 的值映射表 | 否 |

### 7.5 单元测试保证

每个 EtlConvert 配一个测试，验证字段覆盖率和典型数据转换：

```java
@Test
void testGameMapping() {
    CrawlerGameDO crawler = new CrawlerGameDO();
    crawler.setName("北京马拉松");
    crawler.setGameDate(LocalDate.of(2026, 10, 18));
    // ... 填充所有有值的字段

    GameDO game = EtlGameConvert.INSTANCE.toGameDO(crawler);

    assertEquals("北京马拉松", game.getName());
    assertEquals("crawler", game.getSource());
    assertNull(game.getRemark());  // remark 不应被填写
    // ... 逐一断言
}
```

当 `GameDO` 变化时，这个测试会在编译期或断言失败，强制开发者处理。

### 7.6 为什么不走配置化映射

配置化映射（JSONPath + 字段对应表）看似灵活，但：
- 字段改名时配置静默失效（找不到字段 → 跳过，不报错）
- 没有编译期检查，依赖运行时才发现
- 调试映射规则本身就是一个问题

在开发阶段频繁改表的情况下，**编译期报错 > 运行时静默失败**。

## 8. 数据源探测记录

### 8.1 World Athletics（已接入）

| 项目 | 内容 |
|------|------|
| **接入方式** | 通过 nimarion REST API (`https://worldathletics.nimarion.de`) |
| **端点** | `GET /competitions?name=Marathon` — 赛事搜索 |
|  | `GET /competitions/:id/results` — 成绩查询 |
|  | `GET /competitions/:id/organiser` — 组织者信息 |
| **认证** | 无需认证 |
| **代理** | 需境外代理（HTTP 127.0.0.1:7897） |
| **数据量** | ~299 条路跑赛事，含 186 条中国赛事 |
| **中国覆盖** | 北马、上马、厦马、广马、深马、杭马、汉马、无锡马、大连马、南京马、中国田协10km精英赛系列等 |
| **更新频率** | 实时（WA 官方数据源） |
| **实现文件** | `source/worldathletics/WorldAthleticsCrawler.java`, `WorldAthleticsGraphQLClient.java` |

### 8.2 中国马拉松官网 (runchina.org.cn)

| 项目 | 内容 |
|------|------|
| **域名** | `www.runchina.org.cn` — 前端（Tencent EdgeOne CDN 保护，有反爬 JS 挑战） |
| **API 网关** | `api-changzheng.chinaath.com` — Spring Cloud Gateway（"changzheng"） |
| **公开端点** | `/homePage/official/recentMatch` — 最近一场赛事摘要 |
|  | `/homePage/official/searchList` — CMS 内容列表（pageTitle=0~6 对应不同栏） |
|  | `/homePage/official/searchContent` — CMS 内容搜索 |
| **认证端点** | `/race/*`, `/match/*`, `/calendar/*`, `/user/*` 全部返回 `Token为空` |
| **认证方式** | 阿里云号码认证（运营商一键登录），Token 从阿里云 SDK 获取后传给网关 |
| **结论** | CMS 层可公开访问但无赛事数据；赛事数据层需 APP 内 Token，无法服务器模拟 |
| **参考实现** | MAKAKA 小程序可查赛事列表、通过身份证关联成绩证书，说明认证后的 API 功能完整 |
| **探测脚本** | `scripts/crawler/runchina_probe.py` |

### 8.3 数字心动 APP

| 项目 | 内容 |
|------|------|
| **包名** | `com.chinaath.szxd` |
| **技术栈** | Flutter (libapp.so ~7MB) + 阿里云号码认证 + 高德地图 |
| **API 网关** | 同 runchina — `api-changzheng.chinaath.com` |
| **Node 服务** | `node-api.shuzixindong.com` (图片处理、工具服务) |
| **小程序** | `race.shuzixindong.com` (赛历 Webview) |
| **APK 位置** | 未提交（130MB） |
| **结论** | APP 与 runchina 共用同一 API 网关，Token 获取依赖手机 SIM 卡 |

### 8.4 后续数据源接入优先级

1. **runchina 认证 API**（需 Token）— 最完整，含成绩+证书，等手机抓包
2. **中国田协官网** (`athletics.org.cn`) — 赛事日历页面可解析，不需要认证

## 9. 脚本与 Java 代码的架构决策

### 当前状态

`scripts/crawler/` 下有 Python 原型脚本，用于 API 探测和数据验证。

### 推荐方案：Java 实现

原因：
- 已搭建完整 Java 爬虫框架（`Crawler` 接口 → `Pipeline` → 临时表 → ETL）
- Quartz 定时任务运行在 JVM 内，直接调用 Java 代码
- 编译期类型安全，不会出现 Python 脚本的运行时字段缺失
- 统一日志、监控、异常处理
- Python 脚本仅保留用于**数据源探测和验证**

### MAKAKA 参考

MAKAKA 小程序能做到赛事列表 + 身份证关联成绩证书，验证了 runchina API 的完整能力。我们的架构已为此设计好对应的 Pipeline 和 MatchEngine。

## 10. 已确认决策

| # | 问题 | 决策 |
|---|------|------|
| 1 | 临时表隔离级别 | 同一 DB，`crawler_*` 前缀隔离，调通后再考虑独立 |
| 2 | GENERIC 通用爬虫 | 第一期不做，全部 CUSTOM |
| 3 | 成绩匹配范围 | 先国内（身份证+护照），国际接口预留 |
| 4 | 用户身份证号存储 | `system_users` 当前无此字段，需新增：`id_card`、`passport`、`real_name`、`nationality` |
| 5 | 成绩认领方式 | 用户自助认领为主，管理员手动确认为辅 |
| 6 | 多源赛事合并 | ETL 时 (name+date) 去重合并，字段 OFFICIAL > PLATFORM |

### 8.1 system_users 需新增字段

当前 `AdminUserDO` 没有身份信息字段，需增加：

```sql
ALTER TABLE system_users ADD COLUMN real_name varchar(64);
ALTER TABLE system_users ADD COLUMN id_card varchar(32);         -- 身份证号
ALTER TABLE system_users ADD COLUMN passport varchar(32);        -- 护照号
ALTER TABLE system_users ADD COLUMN nationality varchar(64);     -- 国籍，ISO 3166 alpha-2
ALTER TABLE system_users ADD COLUMN birthday date;               -- 辅助年龄组匹配
```

> 身份证号和护照号暂时明文存储，后续需要再加密。

## 9. 遗留待后续确认

- 国际赛事匹配策略的具体实现时机（等有国际数据源时再启动）
- 独立 schema/DB 的迁移时机（调通后再评估）
- 用户端"认领成绩"页面的 UI 设计（需要时另行设计）

---

> **变更记录**：
> - v4：新增 ETL Schema 一致性方案（编译期安全 + raw_data 兜底 + 版本追踪）；确认全部 6 项决策；system_users 字段扩展方案
> - v3：所有数据走临时表隔离 + ETL 导入；数据源改为 DB 配置 + 页面 CRUD；成绩匹配引擎；任务监控页面；GENERAL/CUSTOM 双轨制
> - v2：模块独立；成绩临时表+确认流程；全量/增量双模式；字段保护分析；API 优先策略
> - v1：初版架构设计
