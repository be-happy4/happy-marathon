# Crawler Module (爬虫管理)

独立模块 `yudao-module-crawler`，定时爬取官方数据源获取赛事和成绩信息。爬虫数据落入 `crawler_*` 临时表，通过 ETL 导入生产表。

## Tables

| Table | Purpose | Seq |
|-------|---------|-----|
| `crawler_source` | 数据源配置（页面 CRUD） | `crawler_source_seq` |
| `crawler_task_log` | 爬取任务日志 | `crawler_task_log_seq` |
| `crawler_game` | 临时赛事（隔离表） | `crawler_game_seq` |
| `crawler_game_category` | 临时组别 | `crawler_game_category_seq` |
| `crawler_game_result` | 临时成绩（隔离表） | `crawler_game_result_seq` |

所有表继承 `BaseDO`（无 tenant_id，爬虫数据不按租户隔离）。

### `crawler_source`

| Column | Type | Note |
|--------|------|------|
| `id` | int8 PK | |
| `name` | varchar(128) | 显示名称 |
| `source_key` | varchar(64) UNIQUE | 程序标识 |
| `crawl_type` | varchar(16) | GAME / RESULT |
| `source_type` | varchar(16) | OFFICIAL / PLATFORM |
| `handler_type` | varchar(16) | CUSTOM / GENERIC |
| `handler_class` | varchar(256) | CUSTOM 的实现类名 |
| `base_url` | varchar(512) | API 地址 |
| `auth_config` | jsonb | 认证配置 |
| `request_config` | jsonb | 请求配置 |
| `response_mapping` | jsonb | 响应映射（GENERIC 模式） |
| `cron_expression` | varchar(64) | 定时表达式 |
| `enabled` | boolean | 启停开关 |
| `last_run_time` / `last_run_status` / `last_run_summary` | | 最近运行状态 |

### `crawler_game`

结构 ≈ `system_game` + 来源追踪 + 导入状态。关键字段：`source_id`, `source_game_id`, `name`, `game_date`, `region_name`, `world_athletics_level`, `china_road_run_level`, `raw_data`(jsonb), `import_status`, `import_game_id`, `import_etl_version`.

### `crawler_game_result`

成绩临时表。关键字段：`source_id`, `crawler_game_id`, `crawler_category_id`, `bib_number`, `name`, `id_card`, `passport`, `gun_time_ms`, `net_time_ms`, `rank`, `raw_data`(jsonb), `match_status`, `match_user_id`, `import_status`.

## Java files

```
yudao-module-crawler/src/main/java/cn/iocoder/yudao/module/crawler/
├── core/                                    # 核心抽象
│   ├── Crawler.java                         # 爬虫接口
│   ├── CrawlerContext.java                  # 爬取上下文
│   ├── CrawlResult.java                     # 爬取结果封装
│   ├── CrawlType.java                       # GAME / RESULT
│   ├── CrawlMode.java                       # FULL / INCREMENTAL
│   └── CrawlerRegistry.java                 # 注册中心
├── dal/
│   ├── dataobject/
│   │   ├── CrawlerSourceDO.java
│   │   ├── CrawlerTaskLogDO.java
│   │   ├── CrawlerGameDO.java
│   │   ├── CrawlerGameCategoryDO.java
│   │   └── CrawlerGameResultDO.java
│   └── mysql/
│       ├── CrawlerSourceMapper.java
│       ├── CrawlerTaskLogMapper.java
│       ├── CrawlerGameMapper.java
│       ├── CrawlerGameCategoryMapper.java
│       └── CrawlerGameResultMapper.java
├── pipeline/                                # 数据管道
│   ├── DataPipeline.java
│   ├── PipelineStats.java
│   ├── GamePipeline.java                    # CrawlResult → crawler_game
│   └── ResultPipeline.java                  # CrawlResult → crawler_game_result
├── merge/                                   # 去重合并
│   ├── MergeAction.java
│   ├── MergeStrategy.java
│   ├── GameMergeStrategy.java               # (name, game_date) 去重
│   └── ResultMergeStrategy.java             # (crawler_game_id, category_id, id_card)
├── match/                                   # 成绩匹配
│   ├── MatchStrategy.java
│   └── MatchEngine.java
├── job/                                     # Quartz Job
│   ├── CrawlGameJob.java                    # 赛事爬取
│   └── CrawlResultJob.java                  # 成绩爬取
├── service/
│   ├── CrawlerSourceService.java / impl
│   ├── CrawlerTaskLogService.java / impl
│   └── CrawlerGameResultService.java / impl
├── controller/admin/
│   ├── CrawlerSourceController.java         # /admin-api/crawler/source
│   ├── CrawlerTaskLogController.java        # /admin-api/crawler/task-log
│   ├── CrawlerGameResultController.java     # /admin-api/crawler/result
│   └── vo/...
├── convert/                                 # MapStruct
│   ├── CrawlerSourceConvert.java
│   ├── CrawlerTaskLogConvert.java
│   └── CrawlerGameResultConvert.java
├── enums/
│   └── ErrorCodeConstants.java              # 1_002_030_000+
└── config/
    └── CrawlerProperties.java

yudao-module-crawler/src/main/java/cn/iocoder/yudao/module/crawler/generic/
    └── GenericApiCrawler.java               # GENERIC 模式（暂未实现）
```

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/admin-api/crawler/source/create` | 创建数据源 |
| PUT | `/admin-api/crawler/source/update` | 更新数据源 |
| DELETE | `/admin-api/crawler/source/delete?id=` | 删除数据源 |
| GET | `/admin-api/crawler/source/get?id=` | 获取详情 |
| GET | `/admin-api/crawler/source/page` | 分页列表 |
| GET | `/admin-api/crawler/task-log/page` | 任务日志分页 |
| GET | `/admin-api/crawler/task-log/get?id=` | 日志详情 |
| GET | `/admin-api/crawler/result/page` | 临时成绩分页 |
| POST | `/admin-api/crawler/result/confirm` | 批量确认导入 |
| POST | `/admin-api/crawler/result/ignore` | 批量忽略 |
| POST | `/admin-api/crawler/result/manual-match` | 手动匹配用户 |

## Error codes

| Code | Constant |
|------|----------|
| `1_002_030_000` | `SOURCE_NOT_FOUND` |
| `1_002_030_001` | `TASK_LOG_NOT_FOUND` |
| `1_002_030_002` | `CRAWLER_RESULT_NOT_FOUND` |

## Module dependency

```
yudao-module-crawler → yudao-module-system → yudao-module-infra → framework starters
```

需要 `lombok.config` 文件在模块根目录下（从父目录复制）。

## SQL

DDL 脚本：`sql/postgresql/marathon-crawler.sql`（建表 + 序列 + 索引）
