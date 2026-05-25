-- ==============================================
-- 爬虫模块 DDL
-- 所有 crawler_* 表与生产表隔离
-- ==============================================

-- 序列
CREATE SEQUENCE IF NOT EXISTS crawler_source_seq START 1;
CREATE SEQUENCE IF NOT EXISTS crawler_task_log_seq START 1;
CREATE SEQUENCE IF NOT EXISTS crawler_game_seq START 1;
CREATE SEQUENCE IF NOT EXISTS crawler_game_category_seq START 1;
CREATE SEQUENCE IF NOT EXISTS crawler_game_result_seq START 1;

-- 数据源配置
CREATE TABLE IF NOT EXISTS crawler_source (
    id BIGINT NOT NULL DEFAULT nextval('crawler_source_seq'),
    name VARCHAR(128) NOT NULL,
    source_key VARCHAR(64) NOT NULL,
    crawl_type VARCHAR(16),
    source_type VARCHAR(16),
    handler_type VARCHAR(16),
    handler_class VARCHAR(256),
    base_url VARCHAR(512),
    auth_config JSONB,
    request_config JSONB,
    response_mapping JSONB,
    cron_expression VARCHAR(64),
    enabled BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    last_run_time TIMESTAMP,
    last_run_status VARCHAR(16),
    last_run_summary TEXT,
    creator VARCHAR(64) DEFAULT '1',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '1',
    update_time TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_crawler_source_key ON crawler_source(source_key);

-- 任务日志
CREATE TABLE IF NOT EXISTS crawler_task_log (
    id BIGINT NOT NULL DEFAULT nextval('crawler_task_log_seq'),
    source_id BIGINT,
    crawl_type VARCHAR(16),
    crawl_mode VARCHAR(16),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(16),
    total_fetched INT DEFAULT 0,
    total_new INT DEFAULT 0,
    total_updated INT DEFAULT 0,
    total_ignored INT DEFAULT 0,
    error_msg TEXT,
    etl_version VARCHAR(64),
    creator VARCHAR(64) DEFAULT '1',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '1',
    update_time TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_task_log_source ON crawler_task_log(source_id);
CREATE INDEX IF NOT EXISTS idx_task_log_create_time ON crawler_task_log(create_time);

-- 临时赛事
CREATE TABLE IF NOT EXISTS crawler_game (
    id BIGINT NOT NULL DEFAULT nextval('crawler_game_seq'),
    source_id BIGINT,
    source_game_id VARCHAR(128),
    name VARCHAR(256),
    name_en VARCHAR(256),
    game_date DATE,
    region_name VARCHAR(256),
    region_id BIGINT,
    world_athletics_level VARCHAR(32),
    china_road_run_level VARCHAR(32),
    status VARCHAR(32),
    tags VARCHAR(512),
    remark TEXT,
    raw_data JSONB,
    import_status VARCHAR(16) DEFAULT 'PENDING',
    import_time TIMESTAMP,
    import_game_id BIGINT,
    import_etl_version VARCHAR(64),
    creator VARCHAR(64) DEFAULT '1',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '1',
    update_time TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_crawler_game_source ON crawler_game(source_id);
CREATE INDEX IF NOT EXISTS idx_crawler_game_name_date ON crawler_game(name, game_date);
CREATE INDEX IF NOT EXISTS idx_crawler_game_import_status ON crawler_game(import_status);

-- 临时组别
CREATE TABLE IF NOT EXISTS crawler_game_category (
    id BIGINT NOT NULL DEFAULT nextval('crawler_game_category_seq'),
    crawler_game_id BIGINT NOT NULL,
    game_type VARCHAR(32),
    distance_km NUMERIC(8,3),
    creator VARCHAR(64) DEFAULT '1',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '1',
    update_time TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_crawler_category_game ON crawler_game_category(crawler_game_id);

-- 临时成绩
CREATE TABLE IF NOT EXISTS crawler_game_result (
    id BIGINT NOT NULL DEFAULT nextval('crawler_game_result_seq'),
    source_id BIGINT,
    crawler_game_id BIGINT,
    crawler_category_id BIGINT,
    game_name VARCHAR(256),
    game_date DATE,
    bib_number VARCHAR(32),
    name VARCHAR(128),
    name_en VARCHAR(128),
    nationality VARCHAR(64),
    gender VARCHAR(8),
    age_group VARCHAR(32),
    id_card VARCHAR(32),
    passport VARCHAR(32),
    gun_time_ms BIGINT,
    net_time_ms BIGINT,
    rank INT,
    gender_rank INT,
    category_rank INT,
    raw_data JSONB,
    match_status VARCHAR(16) DEFAULT 'UNMATCHED',
    match_user_id BIGINT,
    match_confidence VARCHAR(16),
    match_strategy VARCHAR(32),
    import_status VARCHAR(16) DEFAULT 'PENDING',
    import_user_id BIGINT,
    import_registration_id BIGINT,
    import_time TIMESTAMP,
    creator VARCHAR(64) DEFAULT '1',
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    updater VARCHAR(64) DEFAULT '1',
    update_time TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS idx_result_source ON crawler_game_result(source_id);
CREATE INDEX IF NOT EXISTS idx_result_game ON crawler_game_result(crawler_game_id);
CREATE INDEX IF NOT EXISTS idx_result_match_status ON crawler_game_result(match_status);
CREATE INDEX IF NOT EXISTS idx_result_import_status ON crawler_game_result(import_status);
CREATE INDEX IF NOT EXISTS idx_result_id_card ON crawler_game_result(id_card);
CREATE INDEX IF NOT EXISTS idx_result_uk ON crawler_game_result(crawler_game_id, crawler_category_id, id_card);
