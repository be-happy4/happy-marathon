-- =============================================
-- Disable directories (override ruoyi-vue-pro.sql defaults)
-- =============================================

UPDATE system_menu SET status = 1 WHERE id = 5100; -- MES 系统
UPDATE system_menu SET status = 1 WHERE id = 4000; -- IoT 物联网
UPDATE system_menu SET status = 1 WHERE id = 2563; -- ERP 系统
UPDATE system_menu SET status = 1 WHERE id = 2397; -- CRM 系统
UPDATE system_menu SET status = 1 WHERE id = 2362; -- 商城系统
UPDATE system_menu SET status = 1 WHERE id = 2160; -- Cloud 开发文档
UPDATE system_menu SET status = 1 WHERE id = 2159; -- Boot 开发文档
UPDATE system_menu SET status = 1 WHERE id = 1254; -- 作者动态
UPDATE system_menu SET status = 1 WHERE id = 1185; -- 工作流程

-- ----------------------------
-- Table structure for system_game
-- ----------------------------
DROP TABLE IF EXISTS system_game_registration;
DROP TABLE IF EXISTS system_game_category;
DROP TABLE IF EXISTS system_game;

CREATE TABLE system_game (
    id int8 NOT NULL,
    name varchar(200) NOT NULL DEFAULT '',
    game_date date NULL DEFAULT NULL,
    game_type varchar(32) NULL DEFAULT NULL,
    world_athletics_level varchar(32) NULL DEFAULT NULL,
    china_road_run_level varchar(32) NULL DEFAULT NULL,
    status varchar(32) NULL DEFAULT NULL,
    tags varchar(500) NULL DEFAULT '',
    url varchar(512) NULL DEFAULT '',
    remark varchar(500) NULL DEFAULT NULL,
    creator varchar(64) NULL DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) NULL DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE system_game ADD CONSTRAINT pk_system_game PRIMARY KEY (id);

CREATE INDEX idx_system_game_01 ON system_game (game_date);
CREATE INDEX idx_system_game_02 ON system_game (status);

COMMENT ON COLUMN system_game.name IS '赛事名称';
COMMENT ON COLUMN system_game.game_date IS '比赛日期';
COMMENT ON COLUMN system_game.game_type IS '赛事类型，字典 game_type';
COMMENT ON COLUMN system_game.world_athletics_level IS '国际田联标牌等级，字典 game_world_athletics_label_level';
COMMENT ON COLUMN system_game.china_road_run_level IS '中国田联路跑赛事等级，字典 game_china_road_run_game_level';
COMMENT ON COLUMN system_game.status IS '赛事状态，字典 game_status';
COMMENT ON COLUMN system_game.tags IS '赛事标签，多个以逗号分隔，字典 game_tag';
COMMENT ON COLUMN system_game.url IS '赛事官网链接';
COMMENT ON COLUMN system_game.remark IS '备注';
COMMENT ON COLUMN system_game.creator IS '创建者';
COMMENT ON COLUMN system_game.create_time IS '创建时间';
COMMENT ON COLUMN system_game.updater IS '更新者';
COMMENT ON COLUMN system_game.update_time IS '更新时间';
COMMENT ON COLUMN system_game.deleted IS '是否删除';
COMMENT ON COLUMN system_game.tenant_id IS '租户编号';
COMMENT ON TABLE system_game IS '赛事表';

DROP SEQUENCE IF EXISTS system_game_seq;
CREATE SEQUENCE system_game_seq
    START 1;

-- ----------------------------
-- Table structure for system_game_registration
-- ----------------------------
CREATE TABLE system_game_registration (
    id int8 NOT NULL,
    game_id int8 NOT NULL,
    user_id int8 NOT NULL,
    registration_status varchar(32) NULL DEFAULT NULL,
    priority int4 NULL DEFAULT NULL,
    tags varchar(500) NULL DEFAULT '',
    bib_number varchar(32) NULL DEFAULT NULL,
    gun_time_ms int8 NULL DEFAULT NULL,
    net_time_ms int8 NULL DEFAULT NULL,
    gender_place int4 NULL DEFAULT NULL,
    overall_place int4 NULL DEFAULT NULL,
    game_category_id int8 NULL DEFAULT NULL,
    remark varchar(500) NULL DEFAULT NULL,
    creator varchar(64) NULL DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) NULL DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE system_game_registration ADD CONSTRAINT pk_system_game_registration PRIMARY KEY (id);

CREATE INDEX idx_system_game_registration_01 ON system_game_registration (game_id);
CREATE INDEX idx_system_game_registration_02 ON system_game_registration (user_id);
CREATE INDEX idx_system_game_registration_03 ON system_game_registration (registration_status);

COMMENT ON COLUMN system_game_registration.id IS '报名编号';
COMMENT ON COLUMN system_game_registration.game_id IS '赛事编号';
COMMENT ON COLUMN system_game_registration.user_id IS '用户编号';
COMMENT ON COLUMN system_game_registration.registration_status IS '个人报名状态，字典 game_registration_status';
COMMENT ON COLUMN system_game_registration.priority IS '个人优先级';
COMMENT ON COLUMN system_game_registration.tags IS '个人标签，多个以逗号分隔，字典 game_tag';
COMMENT ON COLUMN system_game_registration.bib_number IS '参赛号码';
COMMENT ON COLUMN system_game_registration.gun_time_ms IS '枪声成绩（毫秒）';
COMMENT ON COLUMN system_game_registration.net_time_ms IS '净成绩（毫秒）';
COMMENT ON COLUMN system_game_registration.gender_place IS '性别排名';
COMMENT ON COLUMN system_game_registration.overall_place IS '总排名';
COMMENT ON COLUMN system_game_registration.game_category_id IS '参赛组别编号，关联 system_game_category';
COMMENT ON COLUMN system_game_registration.remark IS '备注';
COMMENT ON COLUMN system_game_registration.creator IS '创建者';
COMMENT ON COLUMN system_game_registration.create_time IS '创建时间';
COMMENT ON COLUMN system_game_registration.updater IS '更新者';
COMMENT ON COLUMN system_game_registration.update_time IS '更新时间';
COMMENT ON COLUMN system_game_registration.deleted IS '是否删除';
COMMENT ON COLUMN system_game_registration.tenant_id IS '租户编号';
COMMENT ON TABLE system_game_registration IS '赛事个人报名表';

DROP SEQUENCE IF EXISTS system_game_registration_seq;
CREATE SEQUENCE system_game_registration_seq
    START 1;

-- ----------------------------
-- Table structure for system_game_category
-- ----------------------------
CREATE TABLE system_game_category (
    id int8 NOT NULL,
    game_id int8 NOT NULL,
    game_type varchar(32) NULL DEFAULT NULL,
    distance_m numeric(10, 2) NULL DEFAULT NULL,
    creator varchar(64) NULL DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) NULL DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE system_game_category ADD CONSTRAINT pk_system_game_category PRIMARY KEY (id);

CREATE INDEX idx_system_game_category_01 ON system_game_category (game_id);

COMMENT ON COLUMN system_game_category.id IS '组别编号';
COMMENT ON COLUMN system_game_category.game_id IS '赛事编号';
COMMENT ON COLUMN system_game_category.game_type IS '组别类型，字典 game_type';
COMMENT ON COLUMN system_game_category.distance_m IS '该组别距离（米）';
COMMENT ON COLUMN system_game_category.creator IS '创建者';
COMMENT ON COLUMN system_game_category.create_time IS '创建时间';
COMMENT ON COLUMN system_game_category.updater IS '更新者';
COMMENT ON COLUMN system_game_category.update_time IS '更新时间';
COMMENT ON COLUMN system_game_category.deleted IS '是否删除';
COMMENT ON COLUMN system_game_category.tenant_id IS '租户编号';
COMMENT ON TABLE system_game_category IS '赛事组别表';

DROP SEQUENCE IF EXISTS system_game_category_seq;
CREATE SEQUENCE system_game_category_seq
    START 1;

-- ----------------------------
-- Marathon dictionary data
-- ----------------------------
INSERT INTO public.system_dict_type (id,name,"type",status,remark,creator,create_time,updater,update_time,deleted,deleted_time) VALUES
	 (3001,'赛事标签','game_tag',0,'','1','2026-05-11 00:00:00','1','2026-05-11 00:00:00',0,'1970-01-01 00:00:00'),
	 (3002,'赛事类型','game_type',0,'','1','2026-05-11 00:00:00','1','2026-05-11 00:00:00',0,'1970-01-01 00:00:00'),
	 (3003,'赛事状态','game_status',0,'','1','2026-05-11 00:00:00','1','2026-05-11 00:00:00',0,'1970-01-01 00:00:00'),
	 (3004,'国际田联标牌等级','game_world_athletics_label_level',0,'https://worldathletics.org/competitions/world-athletics-label-road-races','1','2026-05-11 00:00:00','1','2026-05-11 00:00:00',0,'1970-01-01 00:00:00'),
	 (3005,'中国田联路跑赛事等级','game_china_road_run_game_level',0,'https://www.athletics.org.cn/bulletin/hygd/mls/2023/0320/452980.html?f_link_type=f_linkinlinenote&flow_extra=eyJkb2NfaWQiOiIzYWM2MDlhMjI5ZjgxMzI5LTFjNDczYWIxOWM0ZTNkNTkiLCJpbmxpbmVfZGlzcGxheV9wb3NpdGlvbiI6MCwiZG9jX3Bvc2l0aW9uIjowfQ%3D%3D','1','2026-05-11 00:00:00','1','2026-05-11 00:00:00',0,'1970-01-01 00:00:00'),
	 (3006,'个人报名状态','game_registration_status',0,'','1','2026-05-11 00:00:00','1','2026-05-11 00:00:00',0,'1970-01-01 00:00:00');

INSERT INTO public.system_dict_type (id,name,"type",status,remark,creator,create_time,updater,update_time,deleted,deleted_time) VALUES
	 (3007,'组别类型','game_category_type',0,'','1','2026-05-11 00:00:00','1','2026-05-11 00:00:00',0,'1970-01-01 00:00:00');

INSERT INTO public.system_dict_data(id, sort, "label", value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
     (4001, 5, 'A2', 'a2', 'game_china_road_run_game_level', 0, 'danger', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4002, 4, 'A1', 'a1', 'game_china_road_run_game_level', 0, 'danger', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4003, 3, 'C', 'c', 'game_china_road_run_game_level', 0, 'success', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4004, 2, 'B', 'b', 'game_china_road_run_game_level', 0, 'warning', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4005, 1, 'A', 'a', 'game_china_road_run_game_level', 0, 'danger', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4006, 4, '标牌', 'label', 'game_world_athletics_label_level', 0, 'success', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4007, 3, '精英标', 'elite', 'game_world_athletics_label_level', 0, '', '#a78bfa', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4008, 2, '金标', 'gold', 'game_world_athletics_label_level', 0, 'warning', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4009, 1, '白金标', 'platinum', 'game_world_athletics_label_level', 0, 'info', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4010, 9, '已取消', 'canceled', 'game_status', 0, 'danger', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4011, 8, '已结束', 'finished', 'game_status', 0, 'success', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4012, 7, '待开始', 'prepared', 'game_status', 0, 'info', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4013, 6, '候补阶段', 'waitlist_phase', 'game_status', 0, 'warning', '', '抽签结束进入候补', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4014, 5, '多轮抽签', 'multi_round_lottery', 'game_status', 0, 'warning', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4015, 4, '待抽签', 'awaiting_draw', 'game_status', 0, 'warning', '', '报名结束待抽签', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4016, 3, '报名中', 'registration_open', 'game_status', 0, 'primary', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4017, 2, '定档', 'announced', 'game_status', 0, 'default', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4018, 1, '规划中', 'uncertain', 'game_status', 0, 'default', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4019, 4, '越野赛', 'trail_run', 'game_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4020, 3, '路跑赛事', 'road_run', 'game_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4021, 2, '半程马拉松', 'half_marathon', 'game_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4022, 1, '马拉松', 'marathon', 'game_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4023, 1, '世界马拉松大满贯', 'wmm', 'game_tag', 0, 'primary', '', 'WMM', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4024, 2, '中国马拉松大满贯', 'cmm', 'game_tag', 0, 'success', '', 'CMM', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4025, 1, '不参加', 'not_participate', 'game_registration_status', 0, 'default', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4026, 2, '未报名', 'unregistered', 'game_registration_status', 0, 'default', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4027, 3, '已报名', 'registered', 'game_registration_status', 0, 'primary', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4028, 4, '候补中', 'waitlisted', 'game_registration_status', 0, 'warning', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4029, 5, '已中签', 'accepted', 'game_registration_status', 0, 'success', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4030, 6, '已完赛', 'completed', 'game_registration_status', 0, 'success', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4031, 7, '未中签', 'not_accepted', 'game_registration_status', 0, 'danger', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4033, 8, '退赛', 'give_up_race', 'game_registration_status', 0, 'danger', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4034, 9, '中签放弃', 'give_up_lottery', 'game_registration_status', 0, 'danger', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4035, 1, '马拉松', 'marathon', 'game_category_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4036, 2, '半程马拉松', 'half_marathon', 'game_category_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4037, 3, '10km', '10km', 'game_category_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0),
     (4038, 4, '自定义', 'custom', 'game_category_type', 0, '', '', '', '1', '2026-05-11 00:00:00', '1', '2026-05-11 00:00:00', 0)
;

-- 马拉松模块菜单（PostgreSQL）
-- 前端页面位于 happy-marathon-ui 项目，菜单 component 路径与之对应
-- 挂载在「系统管理」(id=1) 下，执行后需在角色管理中分配权限

-- 赛事管理
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6200, '赛事管理', '', 2, 50, 1,
    'game', 'ep:trophy', 'system/game/index', 'SystemGame', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6201, '赛事查询', 'system:game:query', 3, 1, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6202, '赛事创建', 'system:game:create', 3, 2, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6203, '赛事更新', 'system:game:update', 3, 3, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6204, '赛事删除', 'system:game:delete', 3, 4, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 报名管理
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6300, '报名管理', '', 2, 51, 1,
    'game-registration', 'ep:user', 'system/gameRegistration/index', 'SystemGameRegistration', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6301, '报名查询', 'system:game-registration:query', 3, 1, 6300, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6302, '报名创建', 'system:game-registration:create', 3, 2, 6300, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6303, '报名更新', 'system:game-registration:update', 3, 3, 6300, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6304, '报名删除', 'system:game-registration:delete', 3, 4, 6300, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 组别管理按钮权限
INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6205, '组别查询', 'system:game-category:query', 3, 5, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6206, '组别创建', 'system:game-category:create', 3, 6, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6207, '组别更新', 'system:game-category:update', 3, 7, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6208, '组别删除', 'system:game-category:delete', 3, 8, 6200, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 地区管理
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6400, '地区管理', '', 2, 52, 1,
    'region', 'fa:map-marker', 'system/region/index', 'SystemRegion', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6401, '地区查询', 'system:region:query', 3, 1, 6400, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 爬虫管理
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6500, '爬虫管理', '', 1, 60, 0,
    '/crawler', 'ep:monitor', '', '', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

-- 数据源管理
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6501, '数据源管理', '', 2, 2, 6500,
    'source', 'ep:connection', 'crawler/source/index', 'CrawlerSource', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6502, '数据源查询', 'crawler:source:query', 3, 1, 6501, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6503, '数据源创建', 'crawler:source:create', 3, 2, 6501, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6504, '数据源更新', 'crawler:source:update', 3, 3, 6501, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6505, '数据源删除', 'crawler:source:delete', 3, 4, 6501, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6506, '手动触发爬取', 'crawler:source:trigger', 3, 5, 6501, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 任务日志
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6510, '任务日志', '', 2, 2, 6500,
    'task-log', 'ep:document', 'crawler/taskLog/index', 'CrawlerTaskLog', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6511, '任务日志查询', 'crawler:task-log:query', 3, 1, 6510, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 爬取赛事
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6520, '爬取赛事', '', 2, 3, 6500,
    'game', 'ep:medal', 'crawler/game/index', 'CrawlerGame', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6521, '爬取赛事查询', 'crawler:game:query', 3, 1, 6520, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6522, '赛事导入', 'crawler:game:import', 3, 2, 6520, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 爬取成绩
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6530, '爬取成绩', '', 2, 4, 6500,
    'result', 'ep:trophy', 'crawler/result/index', 'CrawlerResult', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6531, '爬取成绩查询', 'crawler:result:query', 3, 1, 6530, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6532, '成绩确认导入', 'crawler:result:confirm', 3, 2, 6530, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6533, '成绩忽略', 'crawler:result:ignore', 3, 3, 6530, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6534, '手动匹配', 'crawler:result:match', 3, 4, 6530, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- 个人成绩查询
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6540, '个人成绩查询', '', 2, 5, 6500,
    'personal-score', 'ep:user-filled', 'crawler/personalScore/index', 'CrawlerPersonalScore', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6541, '个人成绩查询', 'crawler:personal-score:query', 3, 1, 6540, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0),
(6542, '批量查询', 'crawler:personal-score:batch', 3, 2, 6540, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

-- ----------------------------
-- Table structure for system_region
-- ----------------------------
DROP TABLE IF EXISTS system_region;

CREATE TABLE system_region (
    id int8 NOT NULL,
    name varchar(100) NOT NULL DEFAULT '',
    code varchar(10) NULL DEFAULT NULL,
    type int2 NOT NULL DEFAULT 2,
    parent_id int8 NULL DEFAULT NULL,
    sort int4 NOT NULL DEFAULT 0,
    creator varchar(64) NULL DEFAULT '',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64) NULL DEFAULT '',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted int2 NOT NULL DEFAULT 0,
    tenant_id int8 NOT NULL DEFAULT 0
);

ALTER TABLE system_region ADD CONSTRAINT pk_system_region PRIMARY KEY (id);
CREATE INDEX idx_system_region_parent ON system_region (parent_id);

COMMENT ON COLUMN system_region.id IS '地区编号';
COMMENT ON COLUMN system_region.name IS '地区名称';
COMMENT ON COLUMN system_region.code IS 'ISO 3166 代码';
COMMENT ON COLUMN system_region.type IS '地区类型：1=洲 2=国家 3=省份/州 4=城市';
COMMENT ON COLUMN system_region.parent_id IS '父级编号';
COMMENT ON COLUMN system_region.sort IS '排序';
COMMENT ON TABLE system_region IS '世界地区表';

DROP SEQUENCE IF EXISTS system_region_seq;
CREATE SEQUENCE system_region_seq
    START 1000;

-- system_game: region_id + remark→text
ALTER TABLE system_game ADD COLUMN IF NOT EXISTS region_id int8 NULL;
ALTER TABLE system_game ALTER COLUMN remark TYPE text;
COMMENT ON COLUMN system_game.remark IS '赛事主页，支持 Markdown 格式';
COMMENT ON COLUMN system_game.region_id IS '地区编号，关联 system_region';


-- Region seed data (ISO 3166-1 countries + China provinces)
INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1, '亚洲', 'AS', 1, 0, 1, '1', NOW(), '1', NOW(), 0, 0),
(2, '欧洲', 'EU', 1, 0, 2, '1', NOW(), '1', NOW(), 0, 0),
(3, '非洲', 'AF', 1, 0, 3, '1', NOW(), '1', NOW(), 0, 0),
(4, '北美洲', 'NA', 1, 0, 4, '1', NOW(), '1', NOW(), 0, 0),
(5, '南美洲', 'SA', 1, 0, 5, '1', NOW(), '1', NOW(), 0, 0),
(6, '大洋洲', 'OC', 1, 0, 6, '1', NOW(), '1', NOW(), 0, 0),
(7, '南极洲', 'AN', 1, 0, 7, '1', NOW(), '1', NOW(), 0, 0),
(10, '阿富汗', 'AF', 2, 1, 10, '1', NOW(), '1', NOW(), 0, 0),
(11, '奥兰群岛', 'AX', 2, 1, 11, '1', NOW(), '1', NOW(), 0, 0),
(12, '阿尔巴尼亚', 'AL', 2, 2, 12, '1', NOW(), '1', NOW(), 0, 0),
(13, '阿尔及利亚', 'DZ', 2, 3, 13, '1', NOW(), '1', NOW(), 0, 0),
(14, '美属萨摩亚', 'AS', 2, 6, 14, '1', NOW(), '1', NOW(), 0, 0),
(15, '安道尔', 'AD', 2, 2, 15, '1', NOW(), '1', NOW(), 0, 0),
(16, '安哥拉', 'AO', 2, 3, 16, '1', NOW(), '1', NOW(), 0, 0),
(17, '安圭拉', 'AI', 2, 1, 17, '1', NOW(), '1', NOW(), 0, 0),
(18, '南极洲', 'AQ', 2, 7, 18, '1', NOW(), '1', NOW(), 0, 0),
(19, '安提瓜和巴布达', 'AG', 2, 4, 19, '1', NOW(), '1', NOW(), 0, 0),
(20, '阿根廷', 'AR', 2, 5, 20, '1', NOW(), '1', NOW(), 0, 0),
(21, '亚美尼亚', 'AM', 2, 1, 21, '1', NOW(), '1', NOW(), 0, 0),
(22, '阿鲁巴', 'AW', 2, 1, 22, '1', NOW(), '1', NOW(), 0, 0),
(23, '澳大利亚', 'AU', 2, 6, 23, '1', NOW(), '1', NOW(), 0, 0),
(24, '奥地利', 'AT', 2, 2, 24, '1', NOW(), '1', NOW(), 0, 0),
(25, '阿塞拜疆', 'AZ', 2, 1, 25, '1', NOW(), '1', NOW(), 0, 0),
(26, '巴哈马', 'BS', 2, 4, 26, '1', NOW(), '1', NOW(), 0, 0),
(27, '巴林', 'BH', 2, 1, 27, '1', NOW(), '1', NOW(), 0, 0),
(28, '孟加拉国', 'BD', 2, 1, 28, '1', NOW(), '1', NOW(), 0, 0),
(29, '巴巴多斯', 'BB', 2, 4, 29, '1', NOW(), '1', NOW(), 0, 0),
(30, '白俄罗斯', 'BY', 2, 2, 30, '1', NOW(), '1', NOW(), 0, 0),
(31, '比利时', 'BE', 2, 2, 31, '1', NOW(), '1', NOW(), 0, 0),
(32, '伯利兹', 'BZ', 2, 4, 32, '1', NOW(), '1', NOW(), 0, 0),
(33, '贝宁', 'BJ', 2, 3, 33, '1', NOW(), '1', NOW(), 0, 0),
(34, '百慕大', 'BM', 2, 4, 34, '1', NOW(), '1', NOW(), 0, 0),
(35, '不丹', 'BT', 2, 1, 35, '1', NOW(), '1', NOW(), 0, 0),
(36, '玻利维亚', 'BO', 2, 5, 36, '1', NOW(), '1', NOW(), 0, 0),
(37, '波黑', 'BA', 2, 2, 37, '1', NOW(), '1', NOW(), 0, 0),
(38, '博茨瓦纳', 'BW', 2, 3, 38, '1', NOW(), '1', NOW(), 0, 0),
(39, '布维岛', 'BV', 2, 7, 39, '1', NOW(), '1', NOW(), 0, 0),
(40, '巴西', 'BR', 2, 5, 40, '1', NOW(), '1', NOW(), 0, 0),
(41, '文莱', 'BN', 2, 1, 41, '1', NOW(), '1', NOW(), 0, 0),
(42, '保加利亚', 'BG', 2, 2, 42, '1', NOW(), '1', NOW(), 0, 0),
(43, '布基纳法索', 'BF', 2, 3, 43, '1', NOW(), '1', NOW(), 0, 0),
(44, '布隆迪', 'BI', 2, 3, 44, '1', NOW(), '1', NOW(), 0, 0),
(45, '柬埔寨', 'KH', 2, 1, 45, '1', NOW(), '1', NOW(), 0, 0),
(46, '喀麦隆', 'CM', 2, 3, 46, '1', NOW(), '1', NOW(), 0, 0),
(47, '加拿大', 'CA', 2, 4, 47, '1', NOW(), '1', NOW(), 0, 0),
(48, '佛得角', 'CV', 2, 3, 48, '1', NOW(), '1', NOW(), 0, 0),
(49, '开曼群岛', 'KY', 2, 4, 49, '1', NOW(), '1', NOW(), 0, 0),
(50, '中非', 'CF', 2, 3, 50, '1', NOW(), '1', NOW(), 0, 0),
(51, '乍得', 'TD', 2, 3, 51, '1', NOW(), '1', NOW(), 0, 0),
(52, '智利', 'CL', 2, 5, 52, '1', NOW(), '1', NOW(), 0, 0),
(53, '中国', 'CN', 2, 1, 53, '1', NOW(), '1', NOW(), 0, 0),
(54, '哥伦比亚', 'CO', 2, 5, 54, '1', NOW(), '1', NOW(), 0, 0),
(55, '科摩罗', 'KM', 2, 3, 55, '1', NOW(), '1', NOW(), 0, 0),
(56, '刚果（布）', 'CG', 2, 3, 56, '1', NOW(), '1', NOW(), 0, 0),
(57, '刚果（金）', 'CD', 2, 3, 57, '1', NOW(), '1', NOW(), 0, 0),
(58, '库克群岛', 'CK', 2, 6, 58, '1', NOW(), '1', NOW(), 0, 0),
(59, '哥斯达黎加', 'CR', 2, 4, 59, '1', NOW(), '1', NOW(), 0, 0),
(60, '科特迪瓦', 'CI', 2, 3, 60, '1', NOW(), '1', NOW(), 0, 0),
(61, '克罗地亚', 'HR', 2, 2, 61, '1', NOW(), '1', NOW(), 0, 0),
(62, '古巴', 'CU', 2, 4, 62, '1', NOW(), '1', NOW(), 0, 0),
(63, '塞浦路斯', 'CY', 2, 1, 63, '1', NOW(), '1', NOW(), 0, 0),
(64, '捷克', 'CZ', 2, 2, 64, '1', NOW(), '1', NOW(), 0, 0),
(65, '丹麦', 'DK', 2, 2, 65, '1', NOW(), '1', NOW(), 0, 0),
(66, '吉布提', 'DJ', 2, 3, 66, '1', NOW(), '1', NOW(), 0, 0),
(67, '多米尼克', 'DM', 2, 4, 67, '1', NOW(), '1', NOW(), 0, 0),
(68, '多米尼加', 'DO', 2, 4, 68, '1', NOW(), '1', NOW(), 0, 0),
(69, '厄瓜多尔', 'EC', 2, 5, 69, '1', NOW(), '1', NOW(), 0, 0),
(70, '埃及', 'EG', 2, 3, 70, '1', NOW(), '1', NOW(), 0, 0),
(71, '萨尔瓦多', 'SV', 2, 4, 71, '1', NOW(), '1', NOW(), 0, 0),
(72, '赤道几内亚', 'GQ', 2, 3, 72, '1', NOW(), '1', NOW(), 0, 0),
(73, '厄立特里亚', 'ER', 2, 3, 73, '1', NOW(), '1', NOW(), 0, 0),
(74, '爱沙尼亚', 'EE', 2, 2, 74, '1', NOW(), '1', NOW(), 0, 0),
(75, '埃塞俄比亚', 'ET', 2, 3, 75, '1', NOW(), '1', NOW(), 0, 0),
(76, '斐济', 'FJ', 2, 6, 76, '1', NOW(), '1', NOW(), 0, 0),
(77, '芬兰', 'FI', 2, 2, 77, '1', NOW(), '1', NOW(), 0, 0),
(78, '法国', 'FR', 2, 2, 78, '1', NOW(), '1', NOW(), 0, 0),
(79, '加蓬', 'GA', 2, 3, 79, '1', NOW(), '1', NOW(), 0, 0),
(80, '冈比亚', 'GM', 2, 3, 80, '1', NOW(), '1', NOW(), 0, 0),
(81, '格鲁吉亚', 'GE', 2, 1, 81, '1', NOW(), '1', NOW(), 0, 0),
(82, '德国', 'DE', 2, 2, 82, '1', NOW(), '1', NOW(), 0, 0),
(83, '加纳', 'GH', 2, 3, 83, '1', NOW(), '1', NOW(), 0, 0),
(84, '直布罗陀', 'GI', 2, 1, 84, '1', NOW(), '1', NOW(), 0, 0),
(85, '希腊', 'GR', 2, 2, 85, '1', NOW(), '1', NOW(), 0, 0),
(86, '格陵兰', 'GL', 2, 1, 86, '1', NOW(), '1', NOW(), 0, 0),
(87, '格林纳达', 'GD', 2, 4, 87, '1', NOW(), '1', NOW(), 0, 0),
(88, '关岛', 'GU', 2, 6, 88, '1', NOW(), '1', NOW(), 0, 0),
(89, '危地马拉', 'GT', 2, 4, 89, '1', NOW(), '1', NOW(), 0, 0),
(90, '根西岛', 'GG', 2, 1, 90, '1', NOW(), '1', NOW(), 0, 0),
(91, '几内亚', 'GN', 2, 3, 91, '1', NOW(), '1', NOW(), 0, 0),
(92, '几内亚比绍', 'GW', 2, 3, 92, '1', NOW(), '1', NOW(), 0, 0),
(93, '圭亚那', 'GY', 2, 5, 93, '1', NOW(), '1', NOW(), 0, 0),
(94, '海地', 'HT', 2, 4, 94, '1', NOW(), '1', NOW(), 0, 0),
(95, '洪都拉斯', 'HN', 2, 4, 95, '1', NOW(), '1', NOW(), 0, 0),
(96, '中国香港', 'HK', 2, 1, 96, '1', NOW(), '1', NOW(), 0, 0),
(97, '匈牙利', 'HU', 2, 2, 97, '1', NOW(), '1', NOW(), 0, 0),
(98, '冰岛', 'IS', 2, 2, 98, '1', NOW(), '1', NOW(), 0, 0),
(99, '印度', 'IN', 2, 1, 99, '1', NOW(), '1', NOW(), 0, 0),
(100, '印度尼西亚', 'ID', 2, 1, 100, '1', NOW(), '1', NOW(), 0, 0),
(101, '伊朗', 'IR', 2, 1, 101, '1', NOW(), '1', NOW(), 0, 0),
(102, '伊拉克', 'IQ', 2, 1, 102, '1', NOW(), '1', NOW(), 0, 0),
(103, '爱尔兰', 'IE', 2, 2, 103, '1', NOW(), '1', NOW(), 0, 0),
(104, '马恩岛', 'IM', 2, 1, 104, '1', NOW(), '1', NOW(), 0, 0),
(105, '以色列', 'IL', 2, 1, 105, '1', NOW(), '1', NOW(), 0, 0),
(106, '意大利', 'IT', 2, 2, 106, '1', NOW(), '1', NOW(), 0, 0),
(107, '牙买加', 'JM', 2, 4, 107, '1', NOW(), '1', NOW(), 0, 0),
(108, '日本', 'JP', 2, 1, 108, '1', NOW(), '1', NOW(), 0, 0),
(109, '泽西岛', 'JE', 2, 1, 109, '1', NOW(), '1', NOW(), 0, 0),
(110, '约旦', 'JO', 2, 1, 110, '1', NOW(), '1', NOW(), 0, 0),
(111, '哈萨克斯坦', 'KZ', 2, 1, 111, '1', NOW(), '1', NOW(), 0, 0),
(112, '肯尼亚', 'KE', 2, 3, 112, '1', NOW(), '1', NOW(), 0, 0),
(113, '基里巴斯', 'KI', 2, 6, 113, '1', NOW(), '1', NOW(), 0, 0),
(114, '朝鲜', 'KP', 2, 1, 114, '1', NOW(), '1', NOW(), 0, 0),
(115, '韩国', 'KR', 2, 1, 115, '1', NOW(), '1', NOW(), 0, 0),
(116, '科威特', 'KW', 2, 1, 116, '1', NOW(), '1', NOW(), 0, 0),
(117, '吉尔吉斯斯坦', 'KG', 2, 1, 117, '1', NOW(), '1', NOW(), 0, 0),
(118, '老挝', 'LA', 2, 1, 118, '1', NOW(), '1', NOW(), 0, 0),
(119, '拉脱维亚', 'LV', 2, 2, 119, '1', NOW(), '1', NOW(), 0, 0),
(120, '黎巴嫩', 'LB', 2, 1, 120, '1', NOW(), '1', NOW(), 0, 0),
(121, '莱索托', 'LS', 2, 3, 121, '1', NOW(), '1', NOW(), 0, 0),
(122, '利比里亚', 'LR', 2, 3, 122, '1', NOW(), '1', NOW(), 0, 0),
(123, '利比亚', 'LY', 2, 3, 123, '1', NOW(), '1', NOW(), 0, 0),
(124, '列支敦士登', 'LI', 2, 2, 124, '1', NOW(), '1', NOW(), 0, 0),
(125, '立陶宛', 'LT', 2, 2, 125, '1', NOW(), '1', NOW(), 0, 0),
(126, '卢森堡', 'LU', 2, 2, 126, '1', NOW(), '1', NOW(), 0, 0),
(127, '中国澳门', 'MO', 2, 1, 127, '1', NOW(), '1', NOW(), 0, 0),
(128, '北马其顿', 'MK', 2, 2, 128, '1', NOW(), '1', NOW(), 0, 0),
(129, '马达加斯加', 'MG', 2, 3, 129, '1', NOW(), '1', NOW(), 0, 0),
(130, '马拉维', 'MW', 2, 3, 130, '1', NOW(), '1', NOW(), 0, 0),
(131, '马来西亚', 'MY', 2, 1, 131, '1', NOW(), '1', NOW(), 0, 0),
(132, '马尔代夫', 'MV', 2, 1, 132, '1', NOW(), '1', NOW(), 0, 0),
(133, '马里', 'ML', 2, 3, 133, '1', NOW(), '1', NOW(), 0, 0),
(134, '马耳他', 'MT', 2, 2, 134, '1', NOW(), '1', NOW(), 0, 0),
(135, '马绍尔群岛', 'MH', 2, 6, 135, '1', NOW(), '1', NOW(), 0, 0),
(136, '马提尼克', 'MQ', 2, 4, 136, '1', NOW(), '1', NOW(), 0, 0),
(137, '毛里塔尼亚', 'MR', 2, 3, 137, '1', NOW(), '1', NOW(), 0, 0),
(138, '毛里求斯', 'MU', 2, 3, 138, '1', NOW(), '1', NOW(), 0, 0),
(139, '墨西哥', 'MX', 2, 4, 139, '1', NOW(), '1', NOW(), 0, 0),
(140, '密克罗尼西亚', 'FM', 2, 6, 140, '1', NOW(), '1', NOW(), 0, 0),
(141, '摩尔多瓦', 'MD', 2, 2, 141, '1', NOW(), '1', NOW(), 0, 0),
(142, '摩纳哥', 'MC', 2, 2, 142, '1', NOW(), '1', NOW(), 0, 0),
(143, '蒙古', 'MN', 2, 1, 143, '1', NOW(), '1', NOW(), 0, 0),
(144, '黑山', 'ME', 2, 2, 144, '1', NOW(), '1', NOW(), 0, 0),
(145, '蒙特塞拉特', 'MS', 2, 4, 145, '1', NOW(), '1', NOW(), 0, 0),
(146, '摩洛哥', 'MA', 2, 3, 146, '1', NOW(), '1', NOW(), 0, 0),
(147, '莫桑比克', 'MZ', 2, 3, 147, '1', NOW(), '1', NOW(), 0, 0),
(148, '缅甸', 'MM', 2, 1, 148, '1', NOW(), '1', NOW(), 0, 0),
(149, '纳米比亚', 'NA', 2, 3, 149, '1', NOW(), '1', NOW(), 0, 0),
(150, '瑙鲁', 'NR', 2, 6, 150, '1', NOW(), '1', NOW(), 0, 0),
(151, '尼泊尔', 'NP', 2, 1, 151, '1', NOW(), '1', NOW(), 0, 0),
(152, '荷兰', 'NL', 2, 2, 152, '1', NOW(), '1', NOW(), 0, 0),
(153, '新喀里多尼亚', 'NC', 2, 6, 153, '1', NOW(), '1', NOW(), 0, 0),
(154, '新西兰', 'NZ', 2, 6, 154, '1', NOW(), '1', NOW(), 0, 0),
(155, '尼加拉瓜', 'NI', 2, 4, 155, '1', NOW(), '1', NOW(), 0, 0),
(156, '尼日尔', 'NE', 2, 3, 156, '1', NOW(), '1', NOW(), 0, 0),
(157, '尼日利亚', 'NG', 2, 3, 157, '1', NOW(), '1', NOW(), 0, 0),
(158, '纽埃', 'NU', 2, 6, 158, '1', NOW(), '1', NOW(), 0, 0),
(159, '诺福克岛', 'NF', 2, 6, 159, '1', NOW(), '1', NOW(), 0, 0),
(160, '挪威', 'NO', 2, 2, 160, '1', NOW(), '1', NOW(), 0, 0),
(161, '阿曼', 'OM', 2, 1, 161, '1', NOW(), '1', NOW(), 0, 0),
(162, '巴基斯坦', 'PK', 2, 1, 162, '1', NOW(), '1', NOW(), 0, 0),
(163, '帕劳', 'PW', 2, 6, 163, '1', NOW(), '1', NOW(), 0, 0),
(164, '巴勒斯坦', 'PS', 2, 1, 164, '1', NOW(), '1', NOW(), 0, 0),
(165, '巴拿马', 'PA', 2, 4, 165, '1', NOW(), '1', NOW(), 0, 0),
(166, '巴布亚新几内亚', 'PG', 2, 6, 166, '1', NOW(), '1', NOW(), 0, 0),
(167, '巴拉圭', 'PY', 2, 5, 167, '1', NOW(), '1', NOW(), 0, 0),
(168, '秘鲁', 'PE', 2, 5, 168, '1', NOW(), '1', NOW(), 0, 0),
(169, '菲律宾', 'PH', 2, 1, 169, '1', NOW(), '1', NOW(), 0, 0),
(170, '皮特凯恩群岛', 'PN', 2, 6, 170, '1', NOW(), '1', NOW(), 0, 0),
(171, '波兰', 'PL', 2, 2, 171, '1', NOW(), '1', NOW(), 0, 0),
(172, '葡萄牙', 'PT', 2, 2, 172, '1', NOW(), '1', NOW(), 0, 0),
(173, '波多黎各', 'PR', 2, 4, 173, '1', NOW(), '1', NOW(), 0, 0),
(174, '卡塔尔', 'QA', 2, 1, 174, '1', NOW(), '1', NOW(), 0, 0),
(175, '留尼汪', 'RE', 2, 1, 175, '1', NOW(), '1', NOW(), 0, 0),
(176, '罗马尼亚', 'RO', 2, 2, 176, '1', NOW(), '1', NOW(), 0, 0),
(177, '俄罗斯', 'RU', 2, 2, 177, '1', NOW(), '1', NOW(), 0, 0),
(178, '卢旺达', 'RW', 2, 3, 178, '1', NOW(), '1', NOW(), 0, 0),
(179, '圣赫勒拿', 'SH', 2, 1, 179, '1', NOW(), '1', NOW(), 0, 0),
(180, '圣基茨和尼维斯', 'KN', 2, 4, 180, '1', NOW(), '1', NOW(), 0, 0),
(181, '圣卢西亚', 'LC', 2, 4, 181, '1', NOW(), '1', NOW(), 0, 0),
(182, '圣皮埃尔和密克隆', 'PM', 2, 4, 182, '1', NOW(), '1', NOW(), 0, 0),
(183, '圣文森特和格林纳丁斯', 'VC', 2, 4, 183, '1', NOW(), '1', NOW(), 0, 0),
(184, '萨摩亚', 'WS', 2, 6, 184, '1', NOW(), '1', NOW(), 0, 0),
(185, '圣马力诺', 'SM', 2, 2, 185, '1', NOW(), '1', NOW(), 0, 0),
(186, '圣多美和普林西比', 'ST', 2, 3, 186, '1', NOW(), '1', NOW(), 0, 0),
(187, '沙特阿拉伯', 'SA', 2, 1, 187, '1', NOW(), '1', NOW(), 0, 0),
(188, '塞内加尔', 'SN', 2, 3, 188, '1', NOW(), '1', NOW(), 0, 0),
(189, '塞尔维亚', 'RS', 2, 2, 189, '1', NOW(), '1', NOW(), 0, 0),
(190, '塞舌尔', 'SC', 2, 3, 190, '1', NOW(), '1', NOW(), 0, 0),
(191, '塞拉利昂', 'SL', 2, 3, 191, '1', NOW(), '1', NOW(), 0, 0),
(192, '新加坡', 'SG', 2, 1, 192, '1', NOW(), '1', NOW(), 0, 0),
(193, '斯洛伐克', 'SK', 2, 2, 193, '1', NOW(), '1', NOW(), 0, 0),
(194, '斯洛文尼亚', 'SI', 2, 2, 194, '1', NOW(), '1', NOW(), 0, 0),
(195, '所罗门群岛', 'SB', 2, 6, 195, '1', NOW(), '1', NOW(), 0, 0),
(196, '索马里', 'SO', 2, 3, 196, '1', NOW(), '1', NOW(), 0, 0),
(197, '南非', 'ZA', 2, 3, 197, '1', NOW(), '1', NOW(), 0, 0),
(198, '南苏丹', 'SS', 2, 3, 198, '1', NOW(), '1', NOW(), 0, 0),
(199, '西班牙', 'ES', 2, 2, 199, '1', NOW(), '1', NOW(), 0, 0),
(200, '斯里兰卡', 'LK', 2, 1, 200, '1', NOW(), '1', NOW(), 0, 0),
(201, '苏丹', 'SD', 2, 3, 201, '1', NOW(), '1', NOW(), 0, 0),
(202, '苏里南', 'SR', 2, 5, 202, '1', NOW(), '1', NOW(), 0, 0),
(203, '斯威士兰', 'SZ', 2, 3, 203, '1', NOW(), '1', NOW(), 0, 0),
(204, '瑞典', 'SE', 2, 2, 204, '1', NOW(), '1', NOW(), 0, 0),
(205, '瑞士', 'CH', 2, 2, 205, '1', NOW(), '1', NOW(), 0, 0),
(206, '叙利亚', 'SY', 2, 1, 206, '1', NOW(), '1', NOW(), 0, 0),
(207, '中国台湾', 'TW', 2, 1, 207, '1', NOW(), '1', NOW(), 0, 0),
(208, '塔吉克斯坦', 'TJ', 2, 1, 208, '1', NOW(), '1', NOW(), 0, 0),
(209, '坦桑尼亚', 'TZ', 2, 3, 209, '1', NOW(), '1', NOW(), 0, 0),
(210, '泰国', 'TH', 2, 1, 210, '1', NOW(), '1', NOW(), 0, 0),
(211, '东帝汶', 'TL', 2, 1, 211, '1', NOW(), '1', NOW(), 0, 0),
(212, '多哥', 'TG', 2, 3, 212, '1', NOW(), '1', NOW(), 0, 0),
(213, '托克劳', 'TK', 2, 6, 213, '1', NOW(), '1', NOW(), 0, 0),
(214, '汤加', 'TO', 2, 6, 214, '1', NOW(), '1', NOW(), 0, 0),
(215, '特立尼达和多巴哥', 'TT', 2, 4, 215, '1', NOW(), '1', NOW(), 0, 0),
(216, '突尼斯', 'TN', 2, 3, 216, '1', NOW(), '1', NOW(), 0, 0),
(217, '土耳其', 'TR', 2, 1, 217, '1', NOW(), '1', NOW(), 0, 0),
(218, '土库曼斯坦', 'TM', 2, 1, 218, '1', NOW(), '1', NOW(), 0, 0),
(219, '特克斯和凯科斯群岛', 'TC', 2, 4, 219, '1', NOW(), '1', NOW(), 0, 0),
(220, '图瓦卢', 'TV', 2, 6, 220, '1', NOW(), '1', NOW(), 0, 0),
(221, '乌干达', 'UG', 2, 3, 221, '1', NOW(), '1', NOW(), 0, 0),
(222, '乌克兰', 'UA', 2, 2, 222, '1', NOW(), '1', NOW(), 0, 0),
(223, '阿联酋', 'AE', 2, 1, 223, '1', NOW(), '1', NOW(), 0, 0),
(224, '英国', 'GB', 2, 2, 224, '1', NOW(), '1', NOW(), 0, 0),
(225, '美国', 'US', 2, 4, 225, '1', NOW(), '1', NOW(), 0, 0),
(226, '乌拉圭', 'UY', 2, 5, 226, '1', NOW(), '1', NOW(), 0, 0),
(227, '乌兹别克斯坦', 'UZ', 2, 1, 227, '1', NOW(), '1', NOW(), 0, 0),
(228, '瓦努阿图', 'VU', 2, 6, 228, '1', NOW(), '1', NOW(), 0, 0),
(229, '梵蒂冈', 'VA', 2, 2, 229, '1', NOW(), '1', NOW(), 0, 0),
(230, '委内瑞拉', 'VE', 2, 5, 230, '1', NOW(), '1', NOW(), 0, 0),
(231, '越南', 'VN', 2, 1, 231, '1', NOW(), '1', NOW(), 0, 0),
(232, '英属维尔京群岛', 'VG', 2, 4, 232, '1', NOW(), '1', NOW(), 0, 0),
(233, '美属维尔京群岛', 'VI', 2, 4, 233, '1', NOW(), '1', NOW(), 0, 0),
(234, '瓦利斯和富图纳', 'WF', 2, 6, 234, '1', NOW(), '1', NOW(), 0, 0),
(235, '西撒哈拉', 'EH', 2, 3, 235, '1', NOW(), '1', NOW(), 0, 0),
(236, '也门', 'YE', 2, 1, 236, '1', NOW(), '1', NOW(), 0, 0),
(237, '赞比亚', 'ZM', 2, 3, 237, '1', NOW(), '1', NOW(), 0, 0),
(238, '津巴布韦', 'ZW', 2, 3, 238, '1', NOW(), '1', NOW(), 0, 0),
(239, '北京市', 'CN-BJ', 3, 53, 239, '1', NOW(), '1', NOW(), 0, 0),
(240, '天津市', 'CN-TJ', 3, 53, 240, '1', NOW(), '1', NOW(), 0, 0),
(241, '河北省', 'CN-HE', 3, 53, 241, '1', NOW(), '1', NOW(), 0, 0),
(242, '山西省', 'CN-SX', 3, 53, 242, '1', NOW(), '1', NOW(), 0, 0),
(243, '内蒙古自治区', 'CN-NM', 3, 53, 243, '1', NOW(), '1', NOW(), 0, 0),
(244, '辽宁省', 'CN-LN', 3, 53, 244, '1', NOW(), '1', NOW(), 0, 0),
(245, '吉林省', 'CN-JL', 3, 53, 245, '1', NOW(), '1', NOW(), 0, 0),
(246, '黑龙江省', 'CN-HL', 3, 53, 246, '1', NOW(), '1', NOW(), 0, 0),
(247, '上海市', 'CN-SH', 3, 53, 247, '1', NOW(), '1', NOW(), 0, 0),
(248, '江苏省', 'CN-JS', 3, 53, 248, '1', NOW(), '1', NOW(), 0, 0),
(249, '浙江省', 'CN-ZJ', 3, 53, 249, '1', NOW(), '1', NOW(), 0, 0),
(250, '安徽省', 'CN-AH', 3, 53, 250, '1', NOW(), '1', NOW(), 0, 0),
(251, '福建省', 'CN-FJ', 3, 53, 251, '1', NOW(), '1', NOW(), 0, 0),
(252, '江西省', 'CN-JX', 3, 53, 252, '1', NOW(), '1', NOW(), 0, 0),
(253, '山东省', 'CN-SD', 3, 53, 253, '1', NOW(), '1', NOW(), 0, 0),
(254, '河南省', 'CN-HA', 3, 53, 254, '1', NOW(), '1', NOW(), 0, 0),
(255, '湖北省', 'CN-HB', 3, 53, 255, '1', NOW(), '1', NOW(), 0, 0),
(256, '湖南省', 'CN-HN', 3, 53, 256, '1', NOW(), '1', NOW(), 0, 0),
(257, '广东省', 'CN-GD', 3, 53, 257, '1', NOW(), '1', NOW(), 0, 0),
(258, '广西壮族自治区', 'CN-GX', 3, 53, 258, '1', NOW(), '1', NOW(), 0, 0),
(259, '海南省', 'CN-HI', 3, 53, 259, '1', NOW(), '1', NOW(), 0, 0),
(260, '重庆市', 'CN-CQ', 3, 53, 260, '1', NOW(), '1', NOW(), 0, 0),
(261, '四川省', 'CN-SC', 3, 53, 261, '1', NOW(), '1', NOW(), 0, 0),
(262, '贵州省', 'CN-GZ', 3, 53, 262, '1', NOW(), '1', NOW(), 0, 0),
(263, '云南省', 'CN-YN', 3, 53, 263, '1', NOW(), '1', NOW(), 0, 0),
(264, '西藏自治区', 'CN-XZ', 3, 53, 264, '1', NOW(), '1', NOW(), 0, 0),
(265, '陕西省', 'CN-SN', 3, 53, 265, '1', NOW(), '1', NOW(), 0, 0),
(266, '甘肃省', 'CN-GS', 3, 53, 266, '1', NOW(), '1', NOW(), 0, 0),
(267, '青海省', 'CN-QH', 3, 53, 267, '1', NOW(), '1', NOW(), 0, 0),
(268, '宁夏回族自治区', 'CN-NX', 3, 53, 268, '1', NOW(), '1', NOW(), 0, 0),
(269, '新疆维吾尔自治区', 'CN-XJ', 3, 53, 269, '1', NOW(), '1', NOW(), 0, 0),
(270, '香港特别行政区', 'CN-HK', 3, 53, 270, '1', NOW(), '1', NOW(), 0, 0),
(271, '澳门特别行政区', 'CN-MO', 3, 53, 271, '1', NOW(), '1', NOW(), 0, 0),
(272, '台湾省', 'CN-TW', 3, 53, 272, '1', NOW(), '1', NOW(), 0, 0);

-- ============================================================
-- system_users 扩展字段：个人身份信息（用于爬虫成绩匹配）
-- ============================================================
ALTER TABLE system_users ADD COLUMN IF NOT EXISTS real_name varchar(64);
ALTER TABLE system_users ADD COLUMN IF NOT EXISTS id_card varchar(32);
ALTER TABLE system_users ADD COLUMN IF NOT EXISTS birthday date;

-- ============================================================
-- crawler_source：个人成绩查询数据源
-- ============================================================
INSERT INTO crawler_source (id, name, source_key, crawl_type, source_type, handler_type, enabled,  creator, create_time, updater, update_time, deleted)
VALUES (nextval('crawler_source_seq'), '中国马拉松官网-个人成绩查询', 'runchina_personal', 'RESULT', 'OFFICIAL', 'CUSTOM', true, '1', NOW(), '1', NOW(), 0)
ON CONFLICT (source_key) DO NOTHING;
