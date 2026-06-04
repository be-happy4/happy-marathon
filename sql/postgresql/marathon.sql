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

-- 数据分析
INSERT INTO system_menu (
    id, name, permission, type, sort, parent_id,
    path, icon, component, component_name, status, visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
) VALUES (
    6550, '数据分析', '', 2, 6, 6500,
    'analytics', 'ep:data-analysis', 'crawler/analytics/index', 'CrawlerAnalytics', 0, '1', '1', '1',
    '1', NOW(), '1', NOW(), 0
);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, path, icon, component, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted) VALUES
(6551, '数据分析查询', 'crawler:analytics:query', 3, 1, 6550, '', '', '', 0, '1', '1', '1', '1', NOW(), '1', NOW(), 0);

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
    gb_code varchar(10) NULL DEFAULT NULL,
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
COMMENT ON COLUMN system_region.gb_code IS 'GB/T 2260 行政区划代码';
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
(1, '亚洲', 'AS', 1, 0, 1, '1', NOW(), '1', NOW(), 0, 1),
(2, '欧洲', 'EU', 1, 0, 2, '1', NOW(), '1', NOW(), 0, 1),
(3, '非洲', 'AF', 1, 0, 3, '1', NOW(), '1', NOW(), 0, 1),
(4, '北美洲', 'NA', 1, 0, 4, '1', NOW(), '1', NOW(), 0, 1),
(5, '南美洲', 'SA', 1, 0, 5, '1', NOW(), '1', NOW(), 0, 1),
(6, '大洋洲', 'OC', 1, 0, 6, '1', NOW(), '1', NOW(), 0, 1),
(7, '南极洲', 'AN', 1, 0, 7, '1', NOW(), '1', NOW(), 0, 1),
(10, '阿富汗', 'AF', 2, 1, 10, '1', NOW(), '1', NOW(), 0, 1),
(11, '奥兰群岛', 'AX', 2, 1, 11, '1', NOW(), '1', NOW(), 0, 1),
(12, '阿尔巴尼亚', 'AL', 2, 2, 12, '1', NOW(), '1', NOW(), 0, 1),
(13, '阿尔及利亚', 'DZ', 2, 3, 13, '1', NOW(), '1', NOW(), 0, 1),
(14, '美属萨摩亚', 'AS', 2, 6, 14, '1', NOW(), '1', NOW(), 0, 1),
(15, '安道尔', 'AD', 2, 2, 15, '1', NOW(), '1', NOW(), 0, 1),
(16, '安哥拉', 'AO', 2, 3, 16, '1', NOW(), '1', NOW(), 0, 1),
(17, '安圭拉', 'AI', 2, 1, 17, '1', NOW(), '1', NOW(), 0, 1),
(18, '南极洲', 'AQ', 2, 7, 18, '1', NOW(), '1', NOW(), 0, 1),
(19, '安提瓜和巴布达', 'AG', 2, 4, 19, '1', NOW(), '1', NOW(), 0, 1),
(20, '阿根廷', 'AR', 2, 5, 20, '1', NOW(), '1', NOW(), 0, 1),
(21, '亚美尼亚', 'AM', 2, 1, 21, '1', NOW(), '1', NOW(), 0, 1),
(22, '阿鲁巴', 'AW', 2, 1, 22, '1', NOW(), '1', NOW(), 0, 1),
(23, '澳大利亚', 'AU', 2, 6, 23, '1', NOW(), '1', NOW(), 0, 1),
(24, '奥地利', 'AT', 2, 2, 24, '1', NOW(), '1', NOW(), 0, 1),
(25, '阿塞拜疆', 'AZ', 2, 1, 25, '1', NOW(), '1', NOW(), 0, 1),
(26, '巴哈马', 'BS', 2, 4, 26, '1', NOW(), '1', NOW(), 0, 1),
(27, '巴林', 'BH', 2, 1, 27, '1', NOW(), '1', NOW(), 0, 1),
(28, '孟加拉国', 'BD', 2, 1, 28, '1', NOW(), '1', NOW(), 0, 1),
(29, '巴巴多斯', 'BB', 2, 4, 29, '1', NOW(), '1', NOW(), 0, 1),
(30, '白俄罗斯', 'BY', 2, 2, 30, '1', NOW(), '1', NOW(), 0, 1),
(31, '比利时', 'BE', 2, 2, 31, '1', NOW(), '1', NOW(), 0, 1),
(32, '伯利兹', 'BZ', 2, 4, 32, '1', NOW(), '1', NOW(), 0, 1),
(33, '贝宁', 'BJ', 2, 3, 33, '1', NOW(), '1', NOW(), 0, 1),
(34, '百慕大', 'BM', 2, 4, 34, '1', NOW(), '1', NOW(), 0, 1),
(35, '不丹', 'BT', 2, 1, 35, '1', NOW(), '1', NOW(), 0, 1),
(36, '玻利维亚', 'BO', 2, 5, 36, '1', NOW(), '1', NOW(), 0, 1),
(37, '波黑', 'BA', 2, 2, 37, '1', NOW(), '1', NOW(), 0, 1),
(38, '博茨瓦纳', 'BW', 2, 3, 38, '1', NOW(), '1', NOW(), 0, 1),
(39, '布维岛', 'BV', 2, 7, 39, '1', NOW(), '1', NOW(), 0, 1),
(40, '巴西', 'BR', 2, 5, 40, '1', NOW(), '1', NOW(), 0, 1),
(41, '文莱', 'BN', 2, 1, 41, '1', NOW(), '1', NOW(), 0, 1),
(42, '保加利亚', 'BG', 2, 2, 42, '1', NOW(), '1', NOW(), 0, 1),
(43, '布基纳法索', 'BF', 2, 3, 43, '1', NOW(), '1', NOW(), 0, 1),
(44, '布隆迪', 'BI', 2, 3, 44, '1', NOW(), '1', NOW(), 0, 1),
(45, '柬埔寨', 'KH', 2, 1, 45, '1', NOW(), '1', NOW(), 0, 1),
(46, '喀麦隆', 'CM', 2, 3, 46, '1', NOW(), '1', NOW(), 0, 1),
(47, '加拿大', 'CA', 2, 4, 47, '1', NOW(), '1', NOW(), 0, 1),
(48, '佛得角', 'CV', 2, 3, 48, '1', NOW(), '1', NOW(), 0, 1),
(49, '开曼群岛', 'KY', 2, 4, 49, '1', NOW(), '1', NOW(), 0, 1),
(50, '中非', 'CF', 2, 3, 50, '1', NOW(), '1', NOW(), 0, 1),
(51, '乍得', 'TD', 2, 3, 51, '1', NOW(), '1', NOW(), 0, 1),
(52, '智利', 'CL', 2, 5, 52, '1', NOW(), '1', NOW(), 0, 1),
(53, '中国', 'CN', 2, 1, 53, '1', NOW(), '1', NOW(), 0, 1),
(54, '哥伦比亚', 'CO', 2, 5, 54, '1', NOW(), '1', NOW(), 0, 1),
(55, '科摩罗', 'KM', 2, 3, 55, '1', NOW(), '1', NOW(), 0, 1),
(56, '刚果（布）', 'CG', 2, 3, 56, '1', NOW(), '1', NOW(), 0, 1),
(57, '刚果（金）', 'CD', 2, 3, 57, '1', NOW(), '1', NOW(), 0, 1),
(58, '库克群岛', 'CK', 2, 6, 58, '1', NOW(), '1', NOW(), 0, 1),
(59, '哥斯达黎加', 'CR', 2, 4, 59, '1', NOW(), '1', NOW(), 0, 1),
(60, '科特迪瓦', 'CI', 2, 3, 60, '1', NOW(), '1', NOW(), 0, 1),
(61, '克罗地亚', 'HR', 2, 2, 61, '1', NOW(), '1', NOW(), 0, 1),
(62, '古巴', 'CU', 2, 4, 62, '1', NOW(), '1', NOW(), 0, 1),
(63, '塞浦路斯', 'CY', 2, 1, 63, '1', NOW(), '1', NOW(), 0, 1),
(64, '捷克', 'CZ', 2, 2, 64, '1', NOW(), '1', NOW(), 0, 1),
(65, '丹麦', 'DK', 2, 2, 65, '1', NOW(), '1', NOW(), 0, 1),
(66, '吉布提', 'DJ', 2, 3, 66, '1', NOW(), '1', NOW(), 0, 1),
(67, '多米尼克', 'DM', 2, 4, 67, '1', NOW(), '1', NOW(), 0, 1),
(68, '多米尼加', 'DO', 2, 4, 68, '1', NOW(), '1', NOW(), 0, 1),
(69, '厄瓜多尔', 'EC', 2, 5, 69, '1', NOW(), '1', NOW(), 0, 1),
(70, '埃及', 'EG', 2, 3, 70, '1', NOW(), '1', NOW(), 0, 1),
(71, '萨尔瓦多', 'SV', 2, 4, 71, '1', NOW(), '1', NOW(), 0, 1),
(72, '赤道几内亚', 'GQ', 2, 3, 72, '1', NOW(), '1', NOW(), 0, 1),
(73, '厄立特里亚', 'ER', 2, 3, 73, '1', NOW(), '1', NOW(), 0, 1),
(74, '爱沙尼亚', 'EE', 2, 2, 74, '1', NOW(), '1', NOW(), 0, 1),
(75, '埃塞俄比亚', 'ET', 2, 3, 75, '1', NOW(), '1', NOW(), 0, 1),
(76, '斐济', 'FJ', 2, 6, 76, '1', NOW(), '1', NOW(), 0, 1),
(77, '芬兰', 'FI', 2, 2, 77, '1', NOW(), '1', NOW(), 0, 1),
(78, '法国', 'FR', 2, 2, 78, '1', NOW(), '1', NOW(), 0, 1),
(79, '加蓬', 'GA', 2, 3, 79, '1', NOW(), '1', NOW(), 0, 1),
(80, '冈比亚', 'GM', 2, 3, 80, '1', NOW(), '1', NOW(), 0, 1),
(81, '格鲁吉亚', 'GE', 2, 1, 81, '1', NOW(), '1', NOW(), 0, 1),
(82, '德国', 'DE', 2, 2, 82, '1', NOW(), '1', NOW(), 0, 1),
(83, '加纳', 'GH', 2, 3, 83, '1', NOW(), '1', NOW(), 0, 1),
(84, '直布罗陀', 'GI', 2, 1, 84, '1', NOW(), '1', NOW(), 0, 1),
(85, '希腊', 'GR', 2, 2, 85, '1', NOW(), '1', NOW(), 0, 1),
(86, '格陵兰', 'GL', 2, 1, 86, '1', NOW(), '1', NOW(), 0, 1),
(87, '格林纳达', 'GD', 2, 4, 87, '1', NOW(), '1', NOW(), 0, 1),
(88, '关岛', 'GU', 2, 6, 88, '1', NOW(), '1', NOW(), 0, 1),
(89, '危地马拉', 'GT', 2, 4, 89, '1', NOW(), '1', NOW(), 0, 1),
(90, '根西岛', 'GG', 2, 1, 90, '1', NOW(), '1', NOW(), 0, 1),
(91, '几内亚', 'GN', 2, 3, 91, '1', NOW(), '1', NOW(), 0, 1),
(92, '几内亚比绍', 'GW', 2, 3, 92, '1', NOW(), '1', NOW(), 0, 1),
(93, '圭亚那', 'GY', 2, 5, 93, '1', NOW(), '1', NOW(), 0, 1),
(94, '海地', 'HT', 2, 4, 94, '1', NOW(), '1', NOW(), 0, 1),
(95, '洪都拉斯', 'HN', 2, 4, 95, '1', NOW(), '1', NOW(), 0, 1),
(96, '中国香港', 'HK', 2, 1, 96, '1', NOW(), '1', NOW(), 0, 1),
(97, '匈牙利', 'HU', 2, 2, 97, '1', NOW(), '1', NOW(), 0, 1),
(98, '冰岛', 'IS', 2, 2, 98, '1', NOW(), '1', NOW(), 0, 1),
(99, '印度', 'IN', 2, 1, 99, '1', NOW(), '1', NOW(), 0, 1),
(100, '印度尼西亚', 'ID', 2, 1, 100, '1', NOW(), '1', NOW(), 0, 1),
(101, '伊朗', 'IR', 2, 1, 101, '1', NOW(), '1', NOW(), 0, 1),
(102, '伊拉克', 'IQ', 2, 1, 102, '1', NOW(), '1', NOW(), 0, 1),
(103, '爱尔兰', 'IE', 2, 2, 103, '1', NOW(), '1', NOW(), 0, 1),
(104, '马恩岛', 'IM', 2, 1, 104, '1', NOW(), '1', NOW(), 0, 1),
(105, '以色列', 'IL', 2, 1, 105, '1', NOW(), '1', NOW(), 0, 1),
(106, '意大利', 'IT', 2, 2, 106, '1', NOW(), '1', NOW(), 0, 1),
(107, '牙买加', 'JM', 2, 4, 107, '1', NOW(), '1', NOW(), 0, 1),
(108, '日本', 'JP', 2, 1, 108, '1', NOW(), '1', NOW(), 0, 1),
(109, '泽西岛', 'JE', 2, 1, 109, '1', NOW(), '1', NOW(), 0, 1),
(110, '约旦', 'JO', 2, 1, 110, '1', NOW(), '1', NOW(), 0, 1),
(111, '哈萨克斯坦', 'KZ', 2, 1, 111, '1', NOW(), '1', NOW(), 0, 1),
(112, '肯尼亚', 'KE', 2, 3, 112, '1', NOW(), '1', NOW(), 0, 1),
(113, '基里巴斯', 'KI', 2, 6, 113, '1', NOW(), '1', NOW(), 0, 1),
(114, '朝鲜', 'KP', 2, 1, 114, '1', NOW(), '1', NOW(), 0, 1),
(115, '韩国', 'KR', 2, 1, 115, '1', NOW(), '1', NOW(), 0, 1),
(116, '科威特', 'KW', 2, 1, 116, '1', NOW(), '1', NOW(), 0, 1),
(117, '吉尔吉斯斯坦', 'KG', 2, 1, 117, '1', NOW(), '1', NOW(), 0, 1),
(118, '老挝', 'LA', 2, 1, 118, '1', NOW(), '1', NOW(), 0, 1),
(119, '拉脱维亚', 'LV', 2, 2, 119, '1', NOW(), '1', NOW(), 0, 1),
(120, '黎巴嫩', 'LB', 2, 1, 120, '1', NOW(), '1', NOW(), 0, 1),
(121, '莱索托', 'LS', 2, 3, 121, '1', NOW(), '1', NOW(), 0, 1),
(122, '利比里亚', 'LR', 2, 3, 122, '1', NOW(), '1', NOW(), 0, 1),
(123, '利比亚', 'LY', 2, 3, 123, '1', NOW(), '1', NOW(), 0, 1),
(124, '列支敦士登', 'LI', 2, 2, 124, '1', NOW(), '1', NOW(), 0, 1),
(125, '立陶宛', 'LT', 2, 2, 125, '1', NOW(), '1', NOW(), 0, 1),
(126, '卢森堡', 'LU', 2, 2, 126, '1', NOW(), '1', NOW(), 0, 1),
(127, '中国澳门', 'MO', 2, 1, 127, '1', NOW(), '1', NOW(), 0, 1),
(128, '北马其顿', 'MK', 2, 2, 128, '1', NOW(), '1', NOW(), 0, 1),
(129, '马达加斯加', 'MG', 2, 3, 129, '1', NOW(), '1', NOW(), 0, 1),
(130, '马拉维', 'MW', 2, 3, 130, '1', NOW(), '1', NOW(), 0, 1),
(131, '马来西亚', 'MY', 2, 1, 131, '1', NOW(), '1', NOW(), 0, 1),
(132, '马尔代夫', 'MV', 2, 1, 132, '1', NOW(), '1', NOW(), 0, 1),
(133, '马里', 'ML', 2, 3, 133, '1', NOW(), '1', NOW(), 0, 1),
(134, '马耳他', 'MT', 2, 2, 134, '1', NOW(), '1', NOW(), 0, 1),
(135, '马绍尔群岛', 'MH', 2, 6, 135, '1', NOW(), '1', NOW(), 0, 1),
(136, '马提尼克', 'MQ', 2, 4, 136, '1', NOW(), '1', NOW(), 0, 1),
(137, '毛里塔尼亚', 'MR', 2, 3, 137, '1', NOW(), '1', NOW(), 0, 1),
(138, '毛里求斯', 'MU', 2, 3, 138, '1', NOW(), '1', NOW(), 0, 1),
(139, '墨西哥', 'MX', 2, 4, 139, '1', NOW(), '1', NOW(), 0, 1),
(140, '密克罗尼西亚', 'FM', 2, 6, 140, '1', NOW(), '1', NOW(), 0, 1),
(141, '摩尔多瓦', 'MD', 2, 2, 141, '1', NOW(), '1', NOW(), 0, 1),
(142, '摩纳哥', 'MC', 2, 2, 142, '1', NOW(), '1', NOW(), 0, 1),
(143, '蒙古', 'MN', 2, 1, 143, '1', NOW(), '1', NOW(), 0, 1),
(144, '黑山', 'ME', 2, 2, 144, '1', NOW(), '1', NOW(), 0, 1),
(145, '蒙特塞拉特', 'MS', 2, 4, 145, '1', NOW(), '1', NOW(), 0, 1),
(146, '摩洛哥', 'MA', 2, 3, 146, '1', NOW(), '1', NOW(), 0, 1),
(147, '莫桑比克', 'MZ', 2, 3, 147, '1', NOW(), '1', NOW(), 0, 1),
(148, '缅甸', 'MM', 2, 1, 148, '1', NOW(), '1', NOW(), 0, 1),
(149, '纳米比亚', 'NA', 2, 3, 149, '1', NOW(), '1', NOW(), 0, 1),
(150, '瑙鲁', 'NR', 2, 6, 150, '1', NOW(), '1', NOW(), 0, 1),
(151, '尼泊尔', 'NP', 2, 1, 151, '1', NOW(), '1', NOW(), 0, 1),
(152, '荷兰', 'NL', 2, 2, 152, '1', NOW(), '1', NOW(), 0, 1),
(153, '新喀里多尼亚', 'NC', 2, 6, 153, '1', NOW(), '1', NOW(), 0, 1),
(154, '新西兰', 'NZ', 2, 6, 154, '1', NOW(), '1', NOW(), 0, 1),
(155, '尼加拉瓜', 'NI', 2, 4, 155, '1', NOW(), '1', NOW(), 0, 1),
(156, '尼日尔', 'NE', 2, 3, 156, '1', NOW(), '1', NOW(), 0, 1),
(157, '尼日利亚', 'NG', 2, 3, 157, '1', NOW(), '1', NOW(), 0, 1),
(158, '纽埃', 'NU', 2, 6, 158, '1', NOW(), '1', NOW(), 0, 1),
(159, '诺福克岛', 'NF', 2, 6, 159, '1', NOW(), '1', NOW(), 0, 1),
(160, '挪威', 'NO', 2, 2, 160, '1', NOW(), '1', NOW(), 0, 1),
(161, '阿曼', 'OM', 2, 1, 161, '1', NOW(), '1', NOW(), 0, 1),
(162, '巴基斯坦', 'PK', 2, 1, 162, '1', NOW(), '1', NOW(), 0, 1),
(163, '帕劳', 'PW', 2, 6, 163, '1', NOW(), '1', NOW(), 0, 1),
(164, '巴勒斯坦', 'PS', 2, 1, 164, '1', NOW(), '1', NOW(), 0, 1),
(165, '巴拿马', 'PA', 2, 4, 165, '1', NOW(), '1', NOW(), 0, 1),
(166, '巴布亚新几内亚', 'PG', 2, 6, 166, '1', NOW(), '1', NOW(), 0, 1),
(167, '巴拉圭', 'PY', 2, 5, 167, '1', NOW(), '1', NOW(), 0, 1),
(168, '秘鲁', 'PE', 2, 5, 168, '1', NOW(), '1', NOW(), 0, 1),
(169, '菲律宾', 'PH', 2, 1, 169, '1', NOW(), '1', NOW(), 0, 1),
(170, '皮特凯恩群岛', 'PN', 2, 6, 170, '1', NOW(), '1', NOW(), 0, 1),
(171, '波兰', 'PL', 2, 2, 171, '1', NOW(), '1', NOW(), 0, 1),
(172, '葡萄牙', 'PT', 2, 2, 172, '1', NOW(), '1', NOW(), 0, 1),
(173, '波多黎各', 'PR', 2, 4, 173, '1', NOW(), '1', NOW(), 0, 1),
(174, '卡塔尔', 'QA', 2, 1, 174, '1', NOW(), '1', NOW(), 0, 1),
(175, '留尼汪', 'RE', 2, 1, 175, '1', NOW(), '1', NOW(), 0, 1),
(176, '罗马尼亚', 'RO', 2, 2, 176, '1', NOW(), '1', NOW(), 0, 1),
(177, '俄罗斯', 'RU', 2, 2, 177, '1', NOW(), '1', NOW(), 0, 1),
(178, '卢旺达', 'RW', 2, 3, 178, '1', NOW(), '1', NOW(), 0, 1),
(179, '圣赫勒拿', 'SH', 2, 1, 179, '1', NOW(), '1', NOW(), 0, 1),
(180, '圣基茨和尼维斯', 'KN', 2, 4, 180, '1', NOW(), '1', NOW(), 0, 1),
(181, '圣卢西亚', 'LC', 2, 4, 181, '1', NOW(), '1', NOW(), 0, 1),
(182, '圣皮埃尔和密克隆', 'PM', 2, 4, 182, '1', NOW(), '1', NOW(), 0, 1),
(183, '圣文森特和格林纳丁斯', 'VC', 2, 4, 183, '1', NOW(), '1', NOW(), 0, 1),
(184, '萨摩亚', 'WS', 2, 6, 184, '1', NOW(), '1', NOW(), 0, 1),
(185, '圣马力诺', 'SM', 2, 2, 185, '1', NOW(), '1', NOW(), 0, 1),
(186, '圣多美和普林西比', 'ST', 2, 3, 186, '1', NOW(), '1', NOW(), 0, 1),
(187, '沙特阿拉伯', 'SA', 2, 1, 187, '1', NOW(), '1', NOW(), 0, 1),
(188, '塞内加尔', 'SN', 2, 3, 188, '1', NOW(), '1', NOW(), 0, 1),
(189, '塞尔维亚', 'RS', 2, 2, 189, '1', NOW(), '1', NOW(), 0, 1),
(190, '塞舌尔', 'SC', 2, 3, 190, '1', NOW(), '1', NOW(), 0, 1),
(191, '塞拉利昂', 'SL', 2, 3, 191, '1', NOW(), '1', NOW(), 0, 1),
(192, '新加坡', 'SG', 2, 1, 192, '1', NOW(), '1', NOW(), 0, 1),
(193, '斯洛伐克', 'SK', 2, 2, 193, '1', NOW(), '1', NOW(), 0, 1),
(194, '斯洛文尼亚', 'SI', 2, 2, 194, '1', NOW(), '1', NOW(), 0, 1),
(195, '所罗门群岛', 'SB', 2, 6, 195, '1', NOW(), '1', NOW(), 0, 1),
(196, '索马里', 'SO', 2, 3, 196, '1', NOW(), '1', NOW(), 0, 1),
(197, '南非', 'ZA', 2, 3, 197, '1', NOW(), '1', NOW(), 0, 1),
(198, '南苏丹', 'SS', 2, 3, 198, '1', NOW(), '1', NOW(), 0, 1),
(199, '西班牙', 'ES', 2, 2, 199, '1', NOW(), '1', NOW(), 0, 1),
(200, '斯里兰卡', 'LK', 2, 1, 200, '1', NOW(), '1', NOW(), 0, 1),
(201, '苏丹', 'SD', 2, 3, 201, '1', NOW(), '1', NOW(), 0, 1),
(202, '苏里南', 'SR', 2, 5, 202, '1', NOW(), '1', NOW(), 0, 1),
(203, '斯威士兰', 'SZ', 2, 3, 203, '1', NOW(), '1', NOW(), 0, 1),
(204, '瑞典', 'SE', 2, 2, 204, '1', NOW(), '1', NOW(), 0, 1),
(205, '瑞士', 'CH', 2, 2, 205, '1', NOW(), '1', NOW(), 0, 1),
(206, '叙利亚', 'SY', 2, 1, 206, '1', NOW(), '1', NOW(), 0, 1),
(207, '中国台湾', 'TW', 2, 1, 207, '1', NOW(), '1', NOW(), 0, 1),
(208, '塔吉克斯坦', 'TJ', 2, 1, 208, '1', NOW(), '1', NOW(), 0, 1),
(209, '坦桑尼亚', 'TZ', 2, 3, 209, '1', NOW(), '1', NOW(), 0, 1),
(210, '泰国', 'TH', 2, 1, 210, '1', NOW(), '1', NOW(), 0, 1),
(211, '东帝汶', 'TL', 2, 1, 211, '1', NOW(), '1', NOW(), 0, 1),
(212, '多哥', 'TG', 2, 3, 212, '1', NOW(), '1', NOW(), 0, 1),
(213, '托克劳', 'TK', 2, 6, 213, '1', NOW(), '1', NOW(), 0, 1),
(214, '汤加', 'TO', 2, 6, 214, '1', NOW(), '1', NOW(), 0, 1),
(215, '特立尼达和多巴哥', 'TT', 2, 4, 215, '1', NOW(), '1', NOW(), 0, 1),
(216, '突尼斯', 'TN', 2, 3, 216, '1', NOW(), '1', NOW(), 0, 1),
(217, '土耳其', 'TR', 2, 1, 217, '1', NOW(), '1', NOW(), 0, 1),
(218, '土库曼斯坦', 'TM', 2, 1, 218, '1', NOW(), '1', NOW(), 0, 1),
(219, '特克斯和凯科斯群岛', 'TC', 2, 4, 219, '1', NOW(), '1', NOW(), 0, 1),
(220, '图瓦卢', 'TV', 2, 6, 220, '1', NOW(), '1', NOW(), 0, 1),
(221, '乌干达', 'UG', 2, 3, 221, '1', NOW(), '1', NOW(), 0, 1),
(222, '乌克兰', 'UA', 2, 2, 222, '1', NOW(), '1', NOW(), 0, 1),
(223, '阿联酋', 'AE', 2, 1, 223, '1', NOW(), '1', NOW(), 0, 1),
(224, '英国', 'GB', 2, 2, 224, '1', NOW(), '1', NOW(), 0, 1),
(225, '美国', 'US', 2, 4, 225, '1', NOW(), '1', NOW(), 0, 1),
(226, '乌拉圭', 'UY', 2, 5, 226, '1', NOW(), '1', NOW(), 0, 1),
(227, '乌兹别克斯坦', 'UZ', 2, 1, 227, '1', NOW(), '1', NOW(), 0, 1),
(228, '瓦努阿图', 'VU', 2, 6, 228, '1', NOW(), '1', NOW(), 0, 1),
(229, '梵蒂冈', 'VA', 2, 2, 229, '1', NOW(), '1', NOW(), 0, 1),
(230, '委内瑞拉', 'VE', 2, 5, 230, '1', NOW(), '1', NOW(), 0, 1),
(231, '越南', 'VN', 2, 1, 231, '1', NOW(), '1', NOW(), 0, 1),
(232, '英属维尔京群岛', 'VG', 2, 4, 232, '1', NOW(), '1', NOW(), 0, 1),
(233, '美属维尔京群岛', 'VI', 2, 4, 233, '1', NOW(), '1', NOW(), 0, 1),
(234, '瓦利斯和富图纳', 'WF', 2, 6, 234, '1', NOW(), '1', NOW(), 0, 1),
(235, '西撒哈拉', 'EH', 2, 3, 235, '1', NOW(), '1', NOW(), 0, 1),
(236, '也门', 'YE', 2, 1, 236, '1', NOW(), '1', NOW(), 0, 1),
(237, '赞比亚', 'ZM', 2, 3, 237, '1', NOW(), '1', NOW(), 0, 1),
(238, '津巴布韦', 'ZW', 2, 3, 238, '1', NOW(), '1', NOW(), 0, 1);

-- 中国省份/直辖市/自治区/特别行政区 (parent_id = 53 中国)
-- 4个直辖市
INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(239, '北京市', 'CN-BJ', 3, 53, 239, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(240, '天津市', 'CN-TJ', 3, 53, 240, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(247, '上海市', 'CN-SH', 3, 53, 247, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(260, '重庆市', 'CN-CQ', 3, 53, 260, '1', NOW(), '1', NOW(), 0, 1);

-- 5个自治区
INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(243, '内蒙古自治区', 'CN-NM', 3, 53, 243, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(258, '广西壮族自治区', 'CN-GX', 3, 53, 258, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(264, '西藏自治区', 'CN-XZ', 3, 53, 264, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(268, '宁夏回族自治区', 'CN-NX', 3, 53, 268, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(269, '新疆维吾尔自治区', 'CN-XJ', 3, 53, 269, '1', NOW(), '1', NOW(), 0, 1);

-- 2个特别行政区
INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(270, '香港特别行政区', 'CN-HK', 3, 53, 270, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(271, '澳门特别行政区', 'CN-MO', 3, 53, 271, '1', NOW(), '1', NOW(), 0, 1);

-- 23个省
INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(241, '河北省', 'CN-HE', 3, 53, 241, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(242, '山西省', 'CN-SX', 3, 53, 242, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(244, '辽宁省', 'CN-LN', 3, 53, 244, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(245, '吉林省', 'CN-JL', 3, 53, 245, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(246, '黑龙江省', 'CN-HL', 3, 53, 246, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(248, '江苏省', 'CN-JS', 3, 53, 248, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(249, '浙江省', 'CN-ZJ', 3, 53, 249, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(250, '安徽省', 'CN-AH', 3, 53, 250, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(251, '福建省', 'CN-FJ', 3, 53, 251, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(252, '江西省', 'CN-JX', 3, 53, 252, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(253, '山东省', 'CN-SD', 3, 53, 253, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(254, '河南省', 'CN-HA', 3, 53, 254, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(255, '湖北省', 'CN-HB', 3, 53, 255, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(256, '湖南省', 'CN-HN', 3, 53, 256, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(257, '广东省', 'CN-GD', 3, 53, 257, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(259, '海南省', 'CN-HI', 3, 53, 259, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(261, '四川省', 'CN-SC', 3, 53, 261, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(262, '贵州省', 'CN-GZ', 3, 53, 262, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(263, '云南省', 'CN-YN', 3, 53, 263, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(265, '陕西省', 'CN-SN', 3, 53, 265, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(266, '甘肃省', 'CN-GS', 3, 53, 266, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(267, '青海省', 'CN-QH', 3, 53, 267, '1', NOW(), '1', NOW(), 0, 1);

INSERT INTO system_region (id, name, code, type, parent_id, sort, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(272, '台湾省', 'CN-TW', 3, 53, 272, '1', NOW(), '1', NOW(), 0, 1);

-- ============================================================
-- 中国城市数据 (type=4, GB/T 2260, 按省份分隔)
-- ============================================================
-- 北京市
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1003, '丰台区', 'CN-BJ-FT', 4, 239, 1003, '110106', '1', NOW(), '1', NOW(), 0, 1),
(1000, '东城区', 'CN-BJ-DC', 4, 239, 1000, '110101', '1', NOW(), '1', NOW(), 0, 1),
(1004, '石景山区', 'CN-BJ-SJS', 4, 239, 1004, '110107', '1', NOW(), '1', NOW(), 0, 1),
(1005, '海淀区', 'CN-BJ-HD', 4, 239, 1005, '110108', '1', NOW(), '1', NOW(), 0, 1),
(1006, '门头沟区', 'CN-BJ-MTG', 4, 239, 1006, '110109', '1', NOW(), '1', NOW(), 0, 1),
(1007, '房山区', 'CN-BJ-FS', 4, 239, 1007, '110111', '1', NOW(), '1', NOW(), 0, 1),
(1008, '通州区', 'CN-BJ-TZ', 4, 239, 1008, '110112', '1', NOW(), '1', NOW(), 0, 1),
(1009, '顺义区', 'CN-BJ-SY', 4, 239, 1009, '110113', '1', NOW(), '1', NOW(), 0, 1),
(1010, '昌平区', 'CN-BJ-CP', 4, 239, 1010, '110114', '1', NOW(), '1', NOW(), 0, 1),
(1011, '大兴区', 'CN-BJ-DX', 4, 239, 1011, '110115', '1', NOW(), '1', NOW(), 0, 1),
(1002, '朝阳区', 'CN-BJ-CY', 4, 239, 1002, '110105', '1', NOW(), '1', NOW(), 0, 1),
(1012, '怀柔区', 'CN-BJ-HR', 4, 239, 1012, '110116', '1', NOW(), '1', NOW(), 0, 1),
(1001, '西城区', 'CN-BJ-XC', 4, 239, 1001, '110102', '1', NOW(), '1', NOW(), 0, 1),
(1013, '平谷区', 'CN-BJ-PG', 4, 239, 1013, '110117', '1', NOW(), '1', NOW(), 0, 1),
(1014, '密云区', 'CN-BJ-MY', 4, 239, 1014, '110118', '1', NOW(), '1', NOW(), 0, 1),
(1015, '延庆区', 'CN-BJ-YQ', 4, 239, 1015, '110119', '1', NOW(), '1', NOW(), 0, 1);

-- 天津市
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1026, '武清区', 'CN-TJ-WQ', 4, 240, 1026, '120114', '1', NOW(), '1', NOW(), 0, 1),
(1016, '和平区', 'CN-TJ-HP', 4, 240, 1016, '120101', '1', NOW(), '1', NOW(), 0, 1),
(1017, '河东区', 'CN-TJ-HD', 4, 240, 1017, '120102', '1', NOW(), '1', NOW(), 0, 1),
(1018, '河西区', 'CN-TJ-HX', 4, 240, 1018, '120103', '1', NOW(), '1', NOW(), 0, 1),
(1019, '南开区', 'CN-TJ-NK', 4, 240, 1019, '120104', '1', NOW(), '1', NOW(), 0, 1),
(1020, '河北区', 'CN-TJ-HB', 4, 240, 1020, '120105', '1', NOW(), '1', NOW(), 0, 1),
(1021, '红桥区', 'CN-TJ-HQ', 4, 240, 1021, '120106', '1', NOW(), '1', NOW(), 0, 1),
(1022, '东丽区', 'CN-TJ-DL', 4, 240, 1022, '120110', '1', NOW(), '1', NOW(), 0, 1),
(1023, '西青区', 'CN-TJ-XQ', 4, 240, 1023, '120111', '1', NOW(), '1', NOW(), 0, 1),
(1024, '津南区', 'CN-TJ-JN', 4, 240, 1024, '120112', '1', NOW(), '1', NOW(), 0, 1),
(1025, '北辰区', 'CN-TJ-BC', 4, 240, 1025, '120113', '1', NOW(), '1', NOW(), 0, 1),
(1027, '宝坻区', 'CN-TJ-BD', 4, 240, 1027, '120115', '1', NOW(), '1', NOW(), 0, 1),
(1028, '滨海新区', 'CN-TJ-BH', 4, 240, 1028, '120116', '1', NOW(), '1', NOW(), 0, 1),
(1029, '宁河区', 'CN-TJ-NH', 4, 240, 1029, '120117', '1', NOW(), '1', NOW(), 0, 1),
(1030, '静海区', 'CN-TJ-JH', 4, 240, 1030, '120118', '1', NOW(), '1', NOW(), 0, 1),
(1031, '蓟州区', 'CN-TJ-JZ', 4, 240, 1031, '120119', '1', NOW(), '1', NOW(), 0, 1);

-- 河北省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1032, '石家庄市', 'CN-HE-SJZ', 4, 241, 1032, '130100', '1', NOW(), '1', NOW(), 0, 1),
(1033, '唐山市', 'CN-HE-TS', 4, 241, 1033, '130200', '1', NOW(), '1', NOW(), 0, 1),
(1042, '衡水市', 'CN-HE-HS', 4, 241, 1042, '131100', '1', NOW(), '1', NOW(), 0, 1),
(1034, '秦皇岛市', 'CN-HE-QHD', 4, 241, 1034, '130300', '1', NOW(), '1', NOW(), 0, 1),
(1035, '邯郸市', 'CN-HE-HD', 4, 241, 1035, '130400', '1', NOW(), '1', NOW(), 0, 1),
(1036, '邢台市', 'CN-HE-XT', 4, 241, 1036, '130500', '1', NOW(), '1', NOW(), 0, 1),
(1037, '保定市', 'CN-HE-BD', 4, 241, 1037, '130600', '1', NOW(), '1', NOW(), 0, 1),
(1038, '张家口市', 'CN-HE-ZJK', 4, 241, 1038, '130700', '1', NOW(), '1', NOW(), 0, 1),
(1039, '承德市', 'CN-HE-CD', 4, 241, 1039, '130800', '1', NOW(), '1', NOW(), 0, 1),
(1040, '沧州市', 'CN-HE-CZ', 4, 241, 1040, '130900', '1', NOW(), '1', NOW(), 0, 1),
(1041, '廊坊市', 'CN-HE-LF', 4, 241, 1041, '131000', '1', NOW(), '1', NOW(), 0, 1);

-- 山西省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1050, '运城市', 'CN-SX-YC', 4, 242, 1050, '140800', '1', NOW(), '1', NOW(), 0, 1),
(1043, '太原市', 'CN-SX-TY', 4, 242, 1043, '140100', '1', NOW(), '1', NOW(), 0, 1),
(1044, '大同市', 'CN-SX-DT', 4, 242, 1044, '140200', '1', NOW(), '1', NOW(), 0, 1),
(1045, '阳泉市', 'CN-SX-YQ', 4, 242, 1045, '140300', '1', NOW(), '1', NOW(), 0, 1),
(1046, '长治市', 'CN-SX-CZ', 4, 242, 1046, '140400', '1', NOW(), '1', NOW(), 0, 1),
(1047, '晋城市', 'CN-SX-JC', 4, 242, 1047, '140500', '1', NOW(), '1', NOW(), 0, 1),
(1048, '朔州市', 'CN-SX-SZ', 4, 242, 1048, '140600', '1', NOW(), '1', NOW(), 0, 1),
(1049, '晋中市', 'CN-SX-JZ', 4, 242, 1049, '140700', '1', NOW(), '1', NOW(), 0, 1),
(1051, '忻州市', 'CN-SX-XZ', 4, 242, 1051, '140900', '1', NOW(), '1', NOW(), 0, 1),
(1052, '临汾市', 'CN-SX-LF', 4, 242, 1052, '141000', '1', NOW(), '1', NOW(), 0, 1),
(1053, '吕梁市', 'CN-SX-LL', 4, 242, 1053, '141100', '1', NOW(), '1', NOW(), 0, 1);

-- 内蒙古自治区
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1065, '阿拉善盟', 'CN-NM-ALS', 4, 243, 1065, '152900', '1', NOW(), '1', NOW(), 0, 1),
(1054, '呼和浩特市', 'CN-NM-HHHT', 4, 243, 1054, '150100', '1', NOW(), '1', NOW(), 0, 1),
(1055, '包头市', 'CN-NM-BT', 4, 243, 1055, '150200', '1', NOW(), '1', NOW(), 0, 1),
(1056, '乌海市', 'CN-NM-WH', 4, 243, 1056, '150300', '1', NOW(), '1', NOW(), 0, 1),
(1057, '赤峰市', 'CN-NM-CF', 4, 243, 1057, '150400', '1', NOW(), '1', NOW(), 0, 1),
(1058, '通辽市', 'CN-NM-TL', 4, 243, 1058, '150500', '1', NOW(), '1', NOW(), 0, 1),
(1059, '鄂尔多斯市', 'CN-NM-EEDS', 4, 243, 1059, '150600', '1', NOW(), '1', NOW(), 0, 1),
(1060, '呼伦贝尔市', 'CN-NM-HLBE', 4, 243, 1060, '150700', '1', NOW(), '1', NOW(), 0, 1),
(1061, '巴彦淖尔市', 'CN-NM-BYNE', 4, 243, 1061, '150800', '1', NOW(), '1', NOW(), 0, 1),
(1062, '乌兰察布市', 'CN-NM-WLCB', 4, 243, 1062, '150900', '1', NOW(), '1', NOW(), 0, 1),
(1063, '兴安盟', 'CN-NM-XAM', 4, 243, 1063, '152200', '1', NOW(), '1', NOW(), 0, 1),
(1064, '锡林郭勒盟', 'CN-NM-XLGL', 4, 243, 1064, '152500', '1', NOW(), '1', NOW(), 0, 1);

-- 辽宁省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1071, '丹东市', 'CN-LN-DD', 4, 244, 1071, '210600', '1', NOW(), '1', NOW(), 0, 1),
(1066, '沈阳市', 'CN-LN-SY', 4, 244, 1066, '210100', '1', NOW(), '1', NOW(), 0, 1),
(1067, '大连市', 'CN-LN-DL', 4, 244, 1067, '210200', '1', NOW(), '1', NOW(), 0, 1),
(1068, '鞍山市', 'CN-LN-AS', 4, 244, 1068, '210300', '1', NOW(), '1', NOW(), 0, 1),
(1069, '抚顺市', 'CN-LN-FS', 4, 244, 1069, '210400', '1', NOW(), '1', NOW(), 0, 1),
(1070, '本溪市', 'CN-LN-BX', 4, 244, 1070, '210500', '1', NOW(), '1', NOW(), 0, 1),
(1072, '锦州市', 'CN-LN-JZ', 4, 244, 1072, '210700', '1', NOW(), '1', NOW(), 0, 1),
(1073, '营口市', 'CN-LN-YK', 4, 244, 1073, '210800', '1', NOW(), '1', NOW(), 0, 1),
(1074, '阜新市', 'CN-LN-FX', 4, 244, 1074, '210900', '1', NOW(), '1', NOW(), 0, 1),
(1075, '辽阳市', 'CN-LN-LY', 4, 244, 1075, '211000', '1', NOW(), '1', NOW(), 0, 1),
(1076, '盘锦市', 'CN-LN-PJ', 4, 244, 1076, '211100', '1', NOW(), '1', NOW(), 0, 1),
(1077, '铁岭市', 'CN-LN-TL', 4, 244, 1077, '211200', '1', NOW(), '1', NOW(), 0, 1),
(1078, '朝阳市', 'CN-LN-CY', 4, 244, 1078, '211300', '1', NOW(), '1', NOW(), 0, 1),
(1079, '葫芦岛市', 'CN-LN-HLD', 4, 244, 1079, '211400', '1', NOW(), '1', NOW(), 0, 1);

-- 吉林省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1083, '辽源市', 'CN-JL-LY', 4, 245, 1083, '220400', '1', NOW(), '1', NOW(), 0, 1),
(1084, '通化市', 'CN-JL-TH', 4, 245, 1084, '220500', '1', NOW(), '1', NOW(), 0, 1),
(1085, '白山市', 'CN-JL-BS', 4, 245, 1085, '220600', '1', NOW(), '1', NOW(), 0, 1),
(1086, '松原市', 'CN-JL-SY', 4, 245, 1086, '220700', '1', NOW(), '1', NOW(), 0, 1),
(1087, '白城市', 'CN-JL-BC', 4, 245, 1087, '220800', '1', NOW(), '1', NOW(), 0, 1),
(1088, '延边朝鲜族自治州', 'CN-JL-YB', 4, 245, 1088, '222400', '1', NOW(), '1', NOW(), 0, 1),
(1080, '长春市', 'CN-JL-CC', 4, 245, 1080, '220100', '1', NOW(), '1', NOW(), 0, 1),
(1081, '吉林市', 'CN-JL-JL', 4, 245, 1081, '220200', '1', NOW(), '1', NOW(), 0, 1),
(1082, '四平市', 'CN-JL-SP', 4, 245, 1082, '220300', '1', NOW(), '1', NOW(), 0, 1);

-- 黑龙江省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1096, '佳木斯市', 'CN-HL-JMS', 4, 246, 1096, '230800', '1', NOW(), '1', NOW(), 0, 1),
(1097, '七台河市', 'CN-HL-QTH', 4, 246, 1097, '230900', '1', NOW(), '1', NOW(), 0, 1),
(1101, '大兴安岭地区', 'CN-HL-DXAL', 4, 246, 1101, '232700', '1', NOW(), '1', NOW(), 0, 1),
(1099, '黑河市', 'CN-HL-HH', 4, 246, 1099, '231100', '1', NOW(), '1', NOW(), 0, 1),
(1098, '牡丹江市', 'CN-HL-MDJ', 4, 246, 1098, '231000', '1', NOW(), '1', NOW(), 0, 1),
(1089, '哈尔滨市', 'CN-HL-HEB', 4, 246, 1089, '230100', '1', NOW(), '1', NOW(), 0, 1),
(1090, '齐齐哈尔市', 'CN-HL-QQHE', 4, 246, 1090, '230200', '1', NOW(), '1', NOW(), 0, 1),
(1091, '鸡西市', 'CN-HL-JX', 4, 246, 1091, '230300', '1', NOW(), '1', NOW(), 0, 1),
(1092, '鹤岗市', 'CN-HL-HG', 4, 246, 1092, '230400', '1', NOW(), '1', NOW(), 0, 1),
(1100, '绥化市', 'CN-HL-SH', 4, 246, 1100, '231200', '1', NOW(), '1', NOW(), 0, 1),
(1093, '双鸭山市', 'CN-HL-SYS', 4, 246, 1093, '230500', '1', NOW(), '1', NOW(), 0, 1),
(1094, '大庆市', 'CN-HL-DQ', 4, 246, 1094, '230600', '1', NOW(), '1', NOW(), 0, 1),
(1095, '伊春市', 'CN-HL-YC', 4, 246, 1095, '230700', '1', NOW(), '1', NOW(), 0, 1);

-- 上海市
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1111, '嘉定区', 'CN-SH-JD', 4, 247, 1111, '310114', '1', NOW(), '1', NOW(), 0, 1),
(1102, '黄浦区', 'CN-SH-HP', 4, 247, 1102, '310101', '1', NOW(), '1', NOW(), 0, 1),
(1103, '徐汇区', 'CN-SH-XH', 4, 247, 1103, '310104', '1', NOW(), '1', NOW(), 0, 1),
(1104, '长宁区', 'CN-SH-CN', 4, 247, 1104, '310105', '1', NOW(), '1', NOW(), 0, 1),
(1105, '静安区', 'CN-SH-JA', 4, 247, 1105, '310106', '1', NOW(), '1', NOW(), 0, 1),
(1106, '普陀区', 'CN-SH-PT', 4, 247, 1106, '310107', '1', NOW(), '1', NOW(), 0, 1),
(1107, '虹口区', 'CN-SH-HK', 4, 247, 1107, '310109', '1', NOW(), '1', NOW(), 0, 1),
(1108, '杨浦区', 'CN-SH-YP', 4, 247, 1108, '310110', '1', NOW(), '1', NOW(), 0, 1),
(1109, '闵行区', 'CN-SH-MH', 4, 247, 1109, '310112', '1', NOW(), '1', NOW(), 0, 1),
(1110, '宝山区', 'CN-SH-BS', 4, 247, 1110, '310113', '1', NOW(), '1', NOW(), 0, 1),
(1112, '浦东新区', 'CN-SH-PD', 4, 247, 1112, '310115', '1', NOW(), '1', NOW(), 0, 1),
(1113, '金山区', 'CN-SH-JS', 4, 247, 1113, '310116', '1', NOW(), '1', NOW(), 0, 1),
(1114, '松江区', 'CN-SH-SJ', 4, 247, 1114, '310117', '1', NOW(), '1', NOW(), 0, 1),
(1115, '青浦区', 'CN-SH-QP', 4, 247, 1115, '310118', '1', NOW(), '1', NOW(), 0, 1),
(1116, '奉贤区', 'CN-SH-FX', 4, 247, 1116, '310120', '1', NOW(), '1', NOW(), 0, 1),
(1117, '崇明区', 'CN-SH-CM', 4, 247, 1117, '310151', '1', NOW(), '1', NOW(), 0, 1);

-- 江苏省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1130, '宿迁市', 'CN-JS-SQ', 4, 248, 1130, '321300', '1', NOW(), '1', NOW(), 0, 1),
(1119, '无锡市', 'CN-JS-WX', 4, 248, 1119, '320200', '1', NOW(), '1', NOW(), 0, 1),
(1120, '徐州市', 'CN-JS-XZ', 4, 248, 1120, '320300', '1', NOW(), '1', NOW(), 0, 1),
(1121, '常州市', 'CN-JS-CZ', 4, 248, 1121, '320400', '1', NOW(), '1', NOW(), 0, 1),
(1118, '南京市', 'CN-JS-NJ', 4, 248, 1118, '320100', '1', NOW(), '1', NOW(), 0, 1),
(1122, '苏州市', 'CN-JS-SZ', 4, 248, 1122, '320500', '1', NOW(), '1', NOW(), 0, 1),
(1123, '南通市', 'CN-JS-NT', 4, 248, 1123, '320600', '1', NOW(), '1', NOW(), 0, 1),
(1124, '连云港市', 'CN-JS-LYG', 4, 248, 1124, '320700', '1', NOW(), '1', NOW(), 0, 1),
(1125, '淮安市', 'CN-JS-HA', 4, 248, 1125, '320800', '1', NOW(), '1', NOW(), 0, 1),
(1126, '盐城市', 'CN-JS-YC', 4, 248, 1126, '320900', '1', NOW(), '1', NOW(), 0, 1),
(1127, '扬州市', 'CN-JS-YZ', 4, 248, 1127, '321000', '1', NOW(), '1', NOW(), 0, 1),
(1128, '镇江市', 'CN-JS-ZJ', 4, 248, 1128, '321100', '1', NOW(), '1', NOW(), 0, 1),
(1129, '泰州市', 'CN-JS-TZ', 4, 248, 1129, '321200', '1', NOW(), '1', NOW(), 0, 1);

-- 浙江省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1135, '湖州市', 'CN-ZJ-HU', 4, 249, 1135, '330500', '1', NOW(), '1', NOW(), 0, 1),
(1131, '杭州市', 'CN-ZJ-HZ', 4, 249, 1131, '330100', '1', NOW(), '1', NOW(), 0, 1),
(1132, '宁波市', 'CN-ZJ-NB', 4, 249, 1132, '330200', '1', NOW(), '1', NOW(), 0, 1),
(1133, '温州市', 'CN-ZJ-WZ', 4, 249, 1133, '330300', '1', NOW(), '1', NOW(), 0, 1),
(1134, '嘉兴市', 'CN-ZJ-JX', 4, 249, 1134, '330400', '1', NOW(), '1', NOW(), 0, 1),
(1136, '绍兴市', 'CN-ZJ-SX', 4, 249, 1136, '330600', '1', NOW(), '1', NOW(), 0, 1),
(1137, '金华市', 'CN-ZJ-JH', 4, 249, 1137, '330700', '1', NOW(), '1', NOW(), 0, 1),
(1138, '衢州市', 'CN-ZJ-QZ', 4, 249, 1138, '330800', '1', NOW(), '1', NOW(), 0, 1),
(1139, '舟山市', 'CN-ZJ-ZS', 4, 249, 1139, '330900', '1', NOW(), '1', NOW(), 0, 1),
(1140, '台州市', 'CN-ZJ-TZ', 4, 249, 1140, '331000', '1', NOW(), '1', NOW(), 0, 1),
(1141, '丽水市', 'CN-ZJ-LS', 4, 249, 1141, '331100', '1', NOW(), '1', NOW(), 0, 1);

-- 安徽省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1142, '合肥市', 'CN-AH-HF', 4, 250, 1142, '340100', '1', NOW(), '1', NOW(), 0, 1),
(1143, '芜湖市', 'CN-AH-WH', 4, 250, 1143, '340200', '1', NOW(), '1', NOW(), 0, 1),
(1144, '蚌埠市', 'CN-AH-BB', 4, 250, 1144, '340300', '1', NOW(), '1', NOW(), 0, 1),
(1156, '池州市', 'CN-AH-CZH', 4, 250, 1156, '341700', '1', NOW(), '1', NOW(), 0, 1),
(1145, '淮南市', 'CN-AH-HN', 4, 250, 1145, '340400', '1', NOW(), '1', NOW(), 0, 1),
(1146, '马鞍山市', 'CN-AH-MAS', 4, 250, 1146, '340500', '1', NOW(), '1', NOW(), 0, 1),
(1147, '淮北市', 'CN-AH-HB', 4, 250, 1147, '340600', '1', NOW(), '1', NOW(), 0, 1),
(1148, '铜陵市', 'CN-AH-TL', 4, 250, 1148, '340700', '1', NOW(), '1', NOW(), 0, 1),
(1149, '安庆市', 'CN-AH-AQ', 4, 250, 1149, '340800', '1', NOW(), '1', NOW(), 0, 1),
(1150, '黄山市', 'CN-AH-HS', 4, 250, 1150, '341000', '1', NOW(), '1', NOW(), 0, 1),
(1151, '滁州市', 'CN-AH-CZ', 4, 250, 1151, '341100', '1', NOW(), '1', NOW(), 0, 1),
(1152, '阜阳市', 'CN-AH-FY', 4, 250, 1152, '341200', '1', NOW(), '1', NOW(), 0, 1),
(1153, '宿州市', 'CN-AH-SZ', 4, 250, 1153, '341300', '1', NOW(), '1', NOW(), 0, 1),
(1154, '六安市', 'CN-AH-LA', 4, 250, 1154, '341500', '1', NOW(), '1', NOW(), 0, 1),
(1155, '亳州市', 'CN-AH-BZ', 4, 250, 1155, '341600', '1', NOW(), '1', NOW(), 0, 1),
(1157, '宣城市', 'CN-AH-XC', 4, 250, 1157, '341800', '1', NOW(), '1', NOW(), 0, 1);

-- 福建省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1165, '龙岩市', 'CN-FJ-LY', 4, 251, 1165, '350800', '1', NOW(), '1', NOW(), 0, 1),
(1158, '福州市', 'CN-FJ-FZ', 4, 251, 1158, '350100', '1', NOW(), '1', NOW(), 0, 1),
(1159, '厦门市', 'CN-FJ-XM', 4, 251, 1159, '350200', '1', NOW(), '1', NOW(), 0, 1),
(1160, '莆田市', 'CN-FJ-PT', 4, 251, 1160, '350300', '1', NOW(), '1', NOW(), 0, 1),
(1161, '三明市', 'CN-FJ-SM', 4, 251, 1161, '350400', '1', NOW(), '1', NOW(), 0, 1),
(1162, '泉州市', 'CN-FJ-QZ', 4, 251, 1162, '350500', '1', NOW(), '1', NOW(), 0, 1),
(1163, '漳州市', 'CN-FJ-ZZ', 4, 251, 1163, '350600', '1', NOW(), '1', NOW(), 0, 1),
(1164, '南平市', 'CN-FJ-NP', 4, 251, 1164, '350700', '1', NOW(), '1', NOW(), 0, 1),
(1166, '宁德市', 'CN-FJ-ND', 4, 251, 1166, '350900', '1', NOW(), '1', NOW(), 0, 1);

-- 江西省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1171, '新余市', 'CN-JX-XY', 4, 252, 1171, '360500', '1', NOW(), '1', NOW(), 0, 1),
(1167, '南昌市', 'CN-JX-NC', 4, 252, 1167, '360100', '1', NOW(), '1', NOW(), 0, 1),
(1168, '景德镇市', 'CN-JX-JDZ', 4, 252, 1168, '360200', '1', NOW(), '1', NOW(), 0, 1),
(1169, '萍乡市', 'CN-JX-PX', 4, 252, 1169, '360300', '1', NOW(), '1', NOW(), 0, 1),
(1170, '九江市', 'CN-JX-JJ', 4, 252, 1170, '360400', '1', NOW(), '1', NOW(), 0, 1),
(1172, '鹰潭市', 'CN-JX-YT', 4, 252, 1172, '360600', '1', NOW(), '1', NOW(), 0, 1),
(1173, '赣州市', 'CN-JX-GZ', 4, 252, 1173, '360700', '1', NOW(), '1', NOW(), 0, 1),
(1174, '吉安市', 'CN-JX-JA', 4, 252, 1174, '360800', '1', NOW(), '1', NOW(), 0, 1),
(1175, '宜春市', 'CN-JX-YC', 4, 252, 1175, '360900', '1', NOW(), '1', NOW(), 0, 1),
(1176, '抚州市', 'CN-JX-FZ', 4, 252, 1176, '361000', '1', NOW(), '1', NOW(), 0, 1),
(1177, '上饶市', 'CN-JX-SR', 4, 252, 1177, '361100', '1', NOW(), '1', NOW(), 0, 1);

-- 山东省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1181, '枣庄市', 'CN-SD-ZZ', 4, 253, 1181, '370400', '1', NOW(), '1', NOW(), 0, 1),
(1182, '东营市', 'CN-SD-DY', 4, 253, 1182, '370500', '1', NOW(), '1', NOW(), 0, 1),
(1183, '烟台市', 'CN-SD-YT', 4, 253, 1183, '370600', '1', NOW(), '1', NOW(), 0, 1),
(1184, '潍坊市', 'CN-SD-WF', 4, 253, 1184, '370700', '1', NOW(), '1', NOW(), 0, 1),
(1186, '泰安市', 'CN-SD-TA', 4, 253, 1186, '370900', '1', NOW(), '1', NOW(), 0, 1),
(1187, '威海市', 'CN-SD-WH', 4, 253, 1187, '371000', '1', NOW(), '1', NOW(), 0, 1),
(1188, '日照市', 'CN-SD-RZ', 4, 253, 1188, '371100', '1', NOW(), '1', NOW(), 0, 1),
(1189, '临沂市', 'CN-SD-LY', 4, 253, 1189, '371300', '1', NOW(), '1', NOW(), 0, 1),
(1190, '德州市', 'CN-SD-DZ', 4, 253, 1190, '371400', '1', NOW(), '1', NOW(), 0, 1),
(1191, '聊城市', 'CN-SD-LC', 4, 253, 1191, '371500', '1', NOW(), '1', NOW(), 0, 1),
(1192, '滨州市', 'CN-SD-BZ', 4, 253, 1192, '371600', '1', NOW(), '1', NOW(), 0, 1),
(1178, '济南市', 'CN-SD-JN', 4, 253, 1178, '370100', '1', NOW(), '1', NOW(), 0, 1),
(1193, '菏泽市', 'CN-SD-HZ', 4, 253, 1193, '371700', '1', NOW(), '1', NOW(), 0, 1),
(1179, '青岛市', 'CN-SD-QD', 4, 253, 1179, '370200', '1', NOW(), '1', NOW(), 0, 1),
(1180, '淄博市', 'CN-SD-ZB', 4, 253, 1180, '370300', '1', NOW(), '1', NOW(), 0, 1),
(1185, '济宁市', 'CN-SD-JNI', 4, 253, 1185, '370800', '1', NOW(), '1', NOW(), 0, 1);

-- 河南省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1210, '驻马店市', 'CN-HA-ZMD', 4, 254, 1210, '411700', '1', NOW(), '1', NOW(), 0, 1),
(1207, '商丘市', 'CN-HA-SQ', 4, 254, 1207, '411400', '1', NOW(), '1', NOW(), 0, 1),
(1194, '郑州市', 'CN-HA-ZZ', 4, 254, 1194, '410100', '1', NOW(), '1', NOW(), 0, 1),
(1195, '开封市', 'CN-HA-KF', 4, 254, 1195, '410200', '1', NOW(), '1', NOW(), 0, 1),
(1196, '洛阳市', 'CN-HA-LY', 4, 254, 1196, '410300', '1', NOW(), '1', NOW(), 0, 1),
(1197, '平顶山市', 'CN-HA-PDS', 4, 254, 1197, '410400', '1', NOW(), '1', NOW(), 0, 1),
(1198, '安阳市', 'CN-HA-AY', 4, 254, 1198, '410500', '1', NOW(), '1', NOW(), 0, 1),
(1199, '鹤壁市', 'CN-HA-HB', 4, 254, 1199, '410600', '1', NOW(), '1', NOW(), 0, 1),
(1200, '新乡市', 'CN-HA-XX', 4, 254, 1200, '410700', '1', NOW(), '1', NOW(), 0, 1),
(1201, '焦作市', 'CN-HA-JZ', 4, 254, 1201, '410800', '1', NOW(), '1', NOW(), 0, 1),
(1202, '濮阳市', 'CN-HA-PY', 4, 254, 1202, '410900', '1', NOW(), '1', NOW(), 0, 1),
(1203, '许昌市', 'CN-HA-XC', 4, 254, 1203, '411000', '1', NOW(), '1', NOW(), 0, 1),
(1204, '漯河市', 'CN-HA-LH', 4, 254, 1204, '411100', '1', NOW(), '1', NOW(), 0, 1),
(1205, '三门峡市', 'CN-HA-SMX', 4, 254, 1205, '411200', '1', NOW(), '1', NOW(), 0, 1),
(1206, '南阳市', 'CN-HA-NY', 4, 254, 1206, '411300', '1', NOW(), '1', NOW(), 0, 1),
(1208, '信阳市', 'CN-HA-XY', 4, 254, 1208, '411500', '1', NOW(), '1', NOW(), 0, 1),
(1209, '周口市', 'CN-HA-ZK', 4, 254, 1209, '411600', '1', NOW(), '1', NOW(), 0, 1);

-- 湖北省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1212, '黄石市', 'CN-HB-HS', 4, 255, 1212, '420200', '1', NOW(), '1', NOW(), 0, 1),
(1213, '十堰市', 'CN-HB-SY', 4, 255, 1213, '420300', '1', NOW(), '1', NOW(), 0, 1),
(1214, '宜昌市', 'CN-HB-YC', 4, 255, 1214, '420500', '1', NOW(), '1', NOW(), 0, 1),
(1215, '襄阳市', 'CN-HB-XY', 4, 255, 1215, '420600', '1', NOW(), '1', NOW(), 0, 1),
(1216, '鄂州市', 'CN-HB-EZ', 4, 255, 1216, '420700', '1', NOW(), '1', NOW(), 0, 1),
(1217, '荆门市', 'CN-HB-JM', 4, 255, 1217, '420800', '1', NOW(), '1', NOW(), 0, 1),
(1218, '孝感市', 'CN-HB-XG', 4, 255, 1218, '420900', '1', NOW(), '1', NOW(), 0, 1),
(1211, '武汉市', 'CN-HB-WH', 4, 255, 1211, '420100', '1', NOW(), '1', NOW(), 0, 1),
(1219, '荆州市', 'CN-HB-JZ', 4, 255, 1219, '421000', '1', NOW(), '1', NOW(), 0, 1),
(1220, '黄冈市', 'CN-HB-HG', 4, 255, 1220, '421100', '1', NOW(), '1', NOW(), 0, 1),
(1221, '咸宁市', 'CN-HB-XN', 4, 255, 1221, '421200', '1', NOW(), '1', NOW(), 0, 1),
(1222, '随州市', 'CN-HB-SZ', 4, 255, 1222, '421300', '1', NOW(), '1', NOW(), 0, 1),
(1223, '恩施土家族苗族自治州', 'CN-HB-ES', 4, 255, 1223, '422800', '1', NOW(), '1', NOW(), 0, 1);

-- 湖南省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1227, '衡阳市', 'CN-HN-HY', 4, 256, 1227, '430400', '1', NOW(), '1', NOW(), 0, 1),
(1224, '长沙市', 'CN-HN-CS', 4, 256, 1224, '430100', '1', NOW(), '1', NOW(), 0, 1),
(1225, '株洲市', 'CN-HN-ZZ', 4, 256, 1225, '430200', '1', NOW(), '1', NOW(), 0, 1),
(1226, '湘潭市', 'CN-HN-XT', 4, 256, 1226, '430300', '1', NOW(), '1', NOW(), 0, 1),
(1228, '邵阳市', 'CN-HN-SY', 4, 256, 1228, '430500', '1', NOW(), '1', NOW(), 0, 1),
(1229, '岳阳市', 'CN-HN-YY', 4, 256, 1229, '430600', '1', NOW(), '1', NOW(), 0, 1),
(1230, '常德市', 'CN-HN-CD', 4, 256, 1230, '430700', '1', NOW(), '1', NOW(), 0, 1),
(1231, '张家界市', 'CN-HN-ZJJ', 4, 256, 1231, '430800', '1', NOW(), '1', NOW(), 0, 1),
(1233, '郴州市', 'CN-HN-CZ', 4, 256, 1233, '431000', '1', NOW(), '1', NOW(), 0, 1),
(1234, '永州市', 'CN-HN-YZ', 4, 256, 1234, '431100', '1', NOW(), '1', NOW(), 0, 1),
(1235, '怀化市', 'CN-HN-HH', 4, 256, 1235, '431200', '1', NOW(), '1', NOW(), 0, 1),
(1236, '娄底市', 'CN-HN-LD', 4, 256, 1236, '431300', '1', NOW(), '1', NOW(), 0, 1),
(1237, '湘西土家族苗族自治州', 'CN-HN-XX', 4, 256, 1237, '433100', '1', NOW(), '1', NOW(), 0, 1),
(1232, '益阳市', 'CN-HN-YIY', 4, 256, 1232, '430900', '1', NOW(), '1', NOW(), 0, 1);

-- 广东省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1243, '佛山市', 'CN-GD-FS', 4, 257, 1243, '440600', '1', NOW(), '1', NOW(), 0, 1),
(1245, '湛江市', 'CN-GD-ZJ', 4, 257, 1245, '440800', '1', NOW(), '1', NOW(), 0, 1),
(1238, '广州市', 'CN-GD-GZ', 4, 257, 1238, '440100', '1', NOW(), '1', NOW(), 0, 1),
(1244, '江门市', 'CN-GD-JM', 4, 257, 1244, '440700', '1', NOW(), '1', NOW(), 0, 1),
(1247, '肇庆市', 'CN-GD-ZQ', 4, 257, 1247, '441200', '1', NOW(), '1', NOW(), 0, 1),
(1248, '惠州市', 'CN-GD-HZ', 4, 257, 1248, '441300', '1', NOW(), '1', NOW(), 0, 1),
(1249, '梅州市', 'CN-GD-MZ', 4, 257, 1249, '441400', '1', NOW(), '1', NOW(), 0, 1),
(1250, '汕尾市', 'CN-GD-SW', 4, 257, 1250, '441500', '1', NOW(), '1', NOW(), 0, 1),
(1251, '河源市', 'CN-GD-HY', 4, 257, 1251, '441600', '1', NOW(), '1', NOW(), 0, 1),
(1252, '阳江市', 'CN-GD-YJ', 4, 257, 1252, '441700', '1', NOW(), '1', NOW(), 0, 1),
(1253, '清远市', 'CN-GD-QY', 4, 257, 1253, '441800', '1', NOW(), '1', NOW(), 0, 1),
(1254, '东莞市', 'CN-GD-DG', 4, 257, 1254, '441900', '1', NOW(), '1', NOW(), 0, 1),
(1255, '中山市', 'CN-GD-ZS', 4, 257, 1255, '442000', '1', NOW(), '1', NOW(), 0, 1),
(1256, '潮州市', 'CN-GD-CZ', 4, 257, 1256, '445100', '1', NOW(), '1', NOW(), 0, 1),
(1257, '揭阳市', 'CN-GD-JY', 4, 257, 1257, '445200', '1', NOW(), '1', NOW(), 0, 1),
(1258, '云浮市', 'CN-GD-YF', 4, 257, 1258, '445300', '1', NOW(), '1', NOW(), 0, 1),
(1242, '汕头市', 'CN-GD-ST', 4, 257, 1242, '440500', '1', NOW(), '1', NOW(), 0, 1),
(1241, '珠海市', 'CN-GD-ZH', 4, 257, 1241, '440400', '1', NOW(), '1', NOW(), 0, 1),
(1240, '深圳市', 'CN-GD-SZ', 4, 257, 1240, '440300', '1', NOW(), '1', NOW(), 0, 1),
(1246, '茂名市', 'CN-GD-MM', 4, 257, 1246, '440900', '1', NOW(), '1', NOW(), 0, 1),
(1239, '韶关市', 'CN-GD-SG', 4, 257, 1239, '440200', '1', NOW(), '1', NOW(), 0, 1);

-- 广西壮族自治区
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1263, '北海市', 'CN-GX-BH', 4, 258, 1263, '450500', '1', NOW(), '1', NOW(), 0, 1),
(1265, '钦州市', 'CN-GX-QZ', 4, 258, 1265, '450700', '1', NOW(), '1', NOW(), 0, 1),
(1260, '柳州市', 'CN-GX-LZ', 4, 258, 1260, '450200', '1', NOW(), '1', NOW(), 0, 1),
(1261, '桂林市', 'CN-GX-GL', 4, 258, 1261, '450300', '1', NOW(), '1', NOW(), 0, 1),
(1262, '梧州市', 'CN-GX-WZ', 4, 258, 1262, '450400', '1', NOW(), '1', NOW(), 0, 1),
(1264, '防城港市', 'CN-GX-FCG', 4, 258, 1264, '450600', '1', NOW(), '1', NOW(), 0, 1),
(1266, '贵港市', 'CN-GX-GG', 4, 258, 1266, '450800', '1', NOW(), '1', NOW(), 0, 1),
(1267, '玉林市', 'CN-GX-YL', 4, 258, 1267, '450900', '1', NOW(), '1', NOW(), 0, 1),
(1268, '百色市', 'CN-GX-BS', 4, 258, 1268, '451000', '1', NOW(), '1', NOW(), 0, 1),
(1269, '贺州市', 'CN-GX-HZ', 4, 258, 1269, '451100', '1', NOW(), '1', NOW(), 0, 1),
(1270, '河池市', 'CN-GX-HC', 4, 258, 1270, '451200', '1', NOW(), '1', NOW(), 0, 1),
(1271, '来宾市', 'CN-GX-LB', 4, 258, 1271, '451300', '1', NOW(), '1', NOW(), 0, 1),
(1272, '崇左市', 'CN-GX-CZ', 4, 258, 1272, '451400', '1', NOW(), '1', NOW(), 0, 1),
(1259, '南宁市', 'CN-GX-NN', 4, 258, 1259, '450100', '1', NOW(), '1', NOW(), 0, 1);

-- 海南省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1274, '三亚市', 'CN-HI-SY', 4, 259, 1274, '460200', '1', NOW(), '1', NOW(), 0, 1),
(1275, '三沙市', 'CN-HI-SS', 4, 259, 1275, '460300', '1', NOW(), '1', NOW(), 0, 1),
(1276, '儋州市', 'CN-HI-DZ', 4, 259, 1276, '460400', '1', NOW(), '1', NOW(), 0, 1),
(1273, '海口市', 'CN-HI-HK', 4, 259, 1273, '460100', '1', NOW(), '1', NOW(), 0, 1);

-- 重庆市
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1282, '沙坪坝区', 'CN-CQ-SPB', 4, 260, 1282, '500106', '1', NOW(), '1', NOW(), 0, 1),
(1290, '黔江区', 'CN-CQ-QJI', 4, 260, 1290, '500114', '1', NOW(), '1', NOW(), 0, 1),
(1277, '万州区', 'CN-CQ-WZ', 4, 260, 1277, '500101', '1', NOW(), '1', NOW(), 0, 1),
(1278, '涪陵区', 'CN-CQ-FL', 4, 260, 1278, '500102', '1', NOW(), '1', NOW(), 0, 1),
(1279, '渝中区', 'CN-CQ-YZ', 4, 260, 1279, '500103', '1', NOW(), '1', NOW(), 0, 1),
(1280, '大渡口区', 'CN-CQ-DDK', 4, 260, 1280, '500104', '1', NOW(), '1', NOW(), 0, 1),
(1281, '江北区', 'CN-CQ-JB', 4, 260, 1281, '500105', '1', NOW(), '1', NOW(), 0, 1),
(1283, '九龙坡区', 'CN-CQ-JLP', 4, 260, 1283, '500107', '1', NOW(), '1', NOW(), 0, 1),
(1284, '南岸区', 'CN-CQ-NA', 4, 260, 1284, '500108', '1', NOW(), '1', NOW(), 0, 1),
(1285, '北碚区', 'CN-CQ-BB', 4, 260, 1285, '500109', '1', NOW(), '1', NOW(), 0, 1),
(1286, '綦江区', 'CN-CQ-QJ', 4, 260, 1286, '500110', '1', NOW(), '1', NOW(), 0, 1),
(1287, '大足区', 'CN-CQ-DZ', 4, 260, 1287, '500111', '1', NOW(), '1', NOW(), 0, 1),
(1288, '渝北区', 'CN-CQ-YB', 4, 260, 1288, '500112', '1', NOW(), '1', NOW(), 0, 1),
(1289, '巴南区', 'CN-CQ-BN', 4, 260, 1289, '500113', '1', NOW(), '1', NOW(), 0, 1),
(1291, '长寿区', 'CN-CQ-CS', 4, 260, 1291, '500115', '1', NOW(), '1', NOW(), 0, 1),
(1292, '江津区', 'CN-CQ-JJ', 4, 260, 1292, '500116', '1', NOW(), '1', NOW(), 0, 1),
(1293, '合川区', 'CN-CQ-HC', 4, 260, 1293, '500117', '1', NOW(), '1', NOW(), 0, 1),
(1294, '永川区', 'CN-CQ-YC', 4, 260, 1294, '500118', '1', NOW(), '1', NOW(), 0, 1),
(1295, '南川区', 'CN-CQ-NC', 4, 260, 1295, '500119', '1', NOW(), '1', NOW(), 0, 1),
(1296, '璧山区', 'CN-CQ-BS', 4, 260, 1296, '500120', '1', NOW(), '1', NOW(), 0, 1),
(1297, '铜梁区', 'CN-CQ-TL', 4, 260, 1297, '500151', '1', NOW(), '1', NOW(), 0, 1),
(1298, '潼南区', 'CN-CQ-TN', 4, 260, 1298, '500152', '1', NOW(), '1', NOW(), 0, 1),
(1299, '荣昌区', 'CN-CQ-RC', 4, 260, 1299, '500153', '1', NOW(), '1', NOW(), 0, 1),
(1300, '开州区', 'CN-CQ-KZ', 4, 260, 1300, '500154', '1', NOW(), '1', NOW(), 0, 1),
(1301, '梁平区', 'CN-CQ-LP', 4, 260, 1301, '500155', '1', NOW(), '1', NOW(), 0, 1),
(1302, '武隆区', 'CN-CQ-WL', 4, 260, 1302, '500156', '1', NOW(), '1', NOW(), 0, 1);

-- 四川省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1310, '遂宁市', 'CN-SC-SN', 4, 261, 1310, '510900', '1', NOW(), '1', NOW(), 0, 1),
(1311, '内江市', 'CN-SC-NJ', 4, 261, 1311, '511000', '1', NOW(), '1', NOW(), 0, 1),
(1312, '乐山市', 'CN-SC-LS', 4, 261, 1312, '511100', '1', NOW(), '1', NOW(), 0, 1),
(1313, '南充市', 'CN-SC-NC', 4, 261, 1313, '511300', '1', NOW(), '1', NOW(), 0, 1),
(1314, '眉山市', 'CN-SC-MS', 4, 261, 1314, '511400', '1', NOW(), '1', NOW(), 0, 1),
(1323, '凉山彝族自治州', 'CN-SC-LSH', 4, 261, 1323, '513400', '1', NOW(), '1', NOW(), 0, 1),
(1303, '成都市', 'CN-SC-CD', 4, 261, 1303, '510100', '1', NOW(), '1', NOW(), 0, 1),
(1304, '自贡市', 'CN-SC-ZG', 4, 261, 1304, '510300', '1', NOW(), '1', NOW(), 0, 1),
(1305, '攀枝花市', 'CN-SC-PZH', 4, 261, 1305, '510400', '1', NOW(), '1', NOW(), 0, 1),
(1315, '宜宾市', 'CN-SC-YB', 4, 261, 1315, '511500', '1', NOW(), '1', NOW(), 0, 1),
(1316, '广安市', 'CN-SC-GA', 4, 261, 1316, '511600', '1', NOW(), '1', NOW(), 0, 1),
(1317, '达州市', 'CN-SC-DZ', 4, 261, 1317, '511700', '1', NOW(), '1', NOW(), 0, 1),
(1318, '雅安市', 'CN-SC-YA', 4, 261, 1318, '511800', '1', NOW(), '1', NOW(), 0, 1),
(1319, '巴中市', 'CN-SC-BZ', 4, 261, 1319, '511900', '1', NOW(), '1', NOW(), 0, 1),
(1320, '资阳市', 'CN-SC-ZY', 4, 261, 1320, '512000', '1', NOW(), '1', NOW(), 0, 1),
(1321, '阿坝藏族羌族自治州', 'CN-SC-AB', 4, 261, 1321, '513200', '1', NOW(), '1', NOW(), 0, 1),
(1322, '甘孜藏族自治州', 'CN-SC-GZ', 4, 261, 1322, '513300', '1', NOW(), '1', NOW(), 0, 1),
(1307, '德阳市', 'CN-SC-DY', 4, 261, 1307, '510600', '1', NOW(), '1', NOW(), 0, 1),
(1306, '泸州市', 'CN-SC-LZ', 4, 261, 1306, '510500', '1', NOW(), '1', NOW(), 0, 1),
(1308, '绵阳市', 'CN-SC-MY', 4, 261, 1308, '510700', '1', NOW(), '1', NOW(), 0, 1),
(1309, '广元市', 'CN-SC-GY', 4, 261, 1309, '510800', '1', NOW(), '1', NOW(), 0, 1);

-- 贵州省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1325, '六盘水市', 'CN-GZ-LPS', 4, 262, 1325, '520200', '1', NOW(), '1', NOW(), 0, 1),
(1324, '贵阳市', 'CN-GZ-GY', 4, 262, 1324, '520100', '1', NOW(), '1', NOW(), 0, 1),
(1326, '遵义市', 'CN-GZ-ZY', 4, 262, 1326, '520300', '1', NOW(), '1', NOW(), 0, 1),
(1327, '安顺市', 'CN-GZ-AS', 4, 262, 1327, '520400', '1', NOW(), '1', NOW(), 0, 1),
(1328, '毕节市', 'CN-GZ-BJ', 4, 262, 1328, '520500', '1', NOW(), '1', NOW(), 0, 1),
(1329, '铜仁市', 'CN-GZ-TR', 4, 262, 1329, '520600', '1', NOW(), '1', NOW(), 0, 1),
(1330, '黔西南布依族苗族自治州', 'CN-GZ-QXN', 4, 262, 1330, '522300', '1', NOW(), '1', NOW(), 0, 1),
(1331, '黔东南苗族侗族自治州', 'CN-GZ-QDN', 4, 262, 1331, '522600', '1', NOW(), '1', NOW(), 0, 1),
(1332, '黔南布依族苗族自治州', 'CN-GZ-QN', 4, 262, 1332, '522700', '1', NOW(), '1', NOW(), 0, 1);

-- 云南省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1337, '昭通市', 'CN-YN-ZT', 4, 263, 1337, '530600', '1', NOW(), '1', NOW(), 0, 1),
(1338, '丽江市', 'CN-YN-LJ', 4, 263, 1338, '530700', '1', NOW(), '1', NOW(), 0, 1),
(1339, '普洱市', 'CN-YN-PE', 4, 263, 1339, '530800', '1', NOW(), '1', NOW(), 0, 1),
(1340, '临沧市', 'CN-YN-LC', 4, 263, 1340, '530900', '1', NOW(), '1', NOW(), 0, 1),
(1341, '楚雄彝族自治州', 'CN-YN-CX', 4, 263, 1341, '532300', '1', NOW(), '1', NOW(), 0, 1),
(1342, '红河哈尼族彝族自治州', 'CN-YN-HH', 4, 263, 1342, '532500', '1', NOW(), '1', NOW(), 0, 1),
(1343, '文山壮族苗族自治州', 'CN-YN-WS', 4, 263, 1343, '532600', '1', NOW(), '1', NOW(), 0, 1),
(1344, '西双版纳傣族自治州', 'CN-YN-XSBN', 4, 263, 1344, '532800', '1', NOW(), '1', NOW(), 0, 1),
(1345, '大理白族自治州', 'CN-YN-DL', 4, 263, 1345, '532900', '1', NOW(), '1', NOW(), 0, 1),
(1346, '德宏傣族景颇族自治州', 'CN-YN-DH', 4, 263, 1346, '533100', '1', NOW(), '1', NOW(), 0, 1),
(1347, '怒江傈僳族自治州', 'CN-YN-NJ', 4, 263, 1347, '533300', '1', NOW(), '1', NOW(), 0, 1),
(1348, '迪庆藏族自治州', 'CN-YN-DQ', 4, 263, 1348, '533400', '1', NOW(), '1', NOW(), 0, 1),
(1336, '保山市', 'CN-YN-BS', 4, 263, 1336, '530500', '1', NOW(), '1', NOW(), 0, 1),
(1335, '玉溪市', 'CN-YN-YX', 4, 263, 1335, '530400', '1', NOW(), '1', NOW(), 0, 1),
(1334, '曲靖市', 'CN-YN-QJ', 4, 263, 1334, '530300', '1', NOW(), '1', NOW(), 0, 1),
(1333, '昆明市', 'CN-YN-KM', 4, 263, 1333, '530100', '1', NOW(), '1', NOW(), 0, 1);

-- 西藏自治区
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1349, '拉萨市', 'CN-XZ-LS', 4, 264, 1349, '540100', '1', NOW(), '1', NOW(), 0, 1),
(1350, '日喀则市', 'CN-XZ-RKZ', 4, 264, 1350, '540200', '1', NOW(), '1', NOW(), 0, 1),
(1351, '昌都市', 'CN-XZ-CD', 4, 264, 1351, '540300', '1', NOW(), '1', NOW(), 0, 1),
(1352, '林芝市', 'CN-XZ-LZ', 4, 264, 1352, '540400', '1', NOW(), '1', NOW(), 0, 1),
(1353, '山南市', 'CN-XZ-SN', 4, 264, 1353, '540500', '1', NOW(), '1', NOW(), 0, 1),
(1354, '那曲市', 'CN-XZ-NQ', 4, 264, 1354, '540600', '1', NOW(), '1', NOW(), 0, 1),
(1355, '阿里地区', 'CN-XZ-AL', 4, 264, 1355, '542500', '1', NOW(), '1', NOW(), 0, 1);

-- 陕西省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1362, '汉中市', 'CN-SN-HZ', 4, 265, 1362, '610700', '1', NOW(), '1', NOW(), 0, 1),
(1358, '宝鸡市', 'CN-SN-BJ', 4, 265, 1358, '610300', '1', NOW(), '1', NOW(), 0, 1),
(1359, '咸阳市', 'CN-SN-XY', 4, 265, 1359, '610400', '1', NOW(), '1', NOW(), 0, 1),
(1361, '延安市', 'CN-SN-YA', 4, 265, 1361, '610600', '1', NOW(), '1', NOW(), 0, 1),
(1365, '商洛市', 'CN-SN-SL', 4, 265, 1365, '611000', '1', NOW(), '1', NOW(), 0, 1),
(1360, '渭南市', 'CN-SN-WN', 4, 265, 1360, '610500', '1', NOW(), '1', NOW(), 0, 1),
(1364, '安康市', 'CN-SN-AK', 4, 265, 1364, '610900', '1', NOW(), '1', NOW(), 0, 1),
(1363, '榆林市', 'CN-SN-YL', 4, 265, 1363, '610800', '1', NOW(), '1', NOW(), 0, 1),
(1356, '西安市', 'CN-SN-XA', 4, 265, 1356, '610100', '1', NOW(), '1', NOW(), 0, 1),
(1357, '铜川市', 'CN-SN-TC', 4, 265, 1357, '610200', '1', NOW(), '1', NOW(), 0, 1);

-- 甘肃省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1368, '金昌市', 'CN-GS-JC', 4, 266, 1368, '620300', '1', NOW(), '1', NOW(), 0, 1),
(1369, '白银市', 'CN-GS-BY', 4, 266, 1369, '620400', '1', NOW(), '1', NOW(), 0, 1),
(1370, '天水市', 'CN-GS-TS', 4, 266, 1370, '620500', '1', NOW(), '1', NOW(), 0, 1),
(1372, '张掖市', 'CN-GS-ZY', 4, 266, 1372, '620700', '1', NOW(), '1', NOW(), 0, 1),
(1371, '武威市', 'CN-GS-WW', 4, 266, 1371, '620600', '1', NOW(), '1', NOW(), 0, 1),
(1366, '兰州市', 'CN-GS-LZ', 4, 266, 1366, '620100', '1', NOW(), '1', NOW(), 0, 1),
(1373, '平凉市', 'CN-GS-PL', 4, 266, 1373, '620800', '1', NOW(), '1', NOW(), 0, 1),
(1374, '酒泉市', 'CN-GS-JQ', 4, 266, 1374, '620900', '1', NOW(), '1', NOW(), 0, 1),
(1375, '庆阳市', 'CN-GS-QY', 4, 266, 1375, '621000', '1', NOW(), '1', NOW(), 0, 1),
(1376, '定西市', 'CN-GS-DX', 4, 266, 1376, '621100', '1', NOW(), '1', NOW(), 0, 1),
(1367, '嘉峪关市', 'CN-GS-JYG', 4, 266, 1367, '620200', '1', NOW(), '1', NOW(), 0, 1),
(1377, '陇南市', 'CN-GS-LN', 4, 266, 1377, '621200', '1', NOW(), '1', NOW(), 0, 1),
(1378, '临夏回族自治州', 'CN-GS-LX', 4, 266, 1378, '622900', '1', NOW(), '1', NOW(), 0, 1),
(1379, '甘南藏族自治州', 'CN-GS-GN', 4, 266, 1379, '623000', '1', NOW(), '1', NOW(), 0, 1);

-- 青海省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1386, '玉树藏族自治州', 'CN-QH-YS', 4, 267, 1386, '632700', '1', NOW(), '1', NOW(), 0, 1),
(1384, '海南藏族自治州', 'CN-QH-HNN', 4, 267, 1384, '632500', '1', NOW(), '1', NOW(), 0, 1),
(1380, '西宁市', 'CN-QH-XN', 4, 267, 1380, '630100', '1', NOW(), '1', NOW(), 0, 1),
(1381, '海东市', 'CN-QH-HD', 4, 267, 1381, '630200', '1', NOW(), '1', NOW(), 0, 1),
(1382, '海北藏族自治州', 'CN-QH-HB', 4, 267, 1382, '632200', '1', NOW(), '1', NOW(), 0, 1),
(1383, '黄南藏族自治州', 'CN-QH-HN', 4, 267, 1383, '632300', '1', NOW(), '1', NOW(), 0, 1),
(1385, '果洛藏族自治州', 'CN-QH-GL', 4, 267, 1385, '632600', '1', NOW(), '1', NOW(), 0, 1),
(1387, '海西蒙古族藏族自治州', 'CN-QH-HX', 4, 267, 1387, '632800', '1', NOW(), '1', NOW(), 0, 1);

-- 宁夏回族自治区
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1392, '中卫市', 'CN-NX-ZW', 4, 268, 1392, '640500', '1', NOW(), '1', NOW(), 0, 1),
(1391, '固原市', 'CN-NX-GY', 4, 268, 1391, '640400', '1', NOW(), '1', NOW(), 0, 1),
(1390, '吴忠市', 'CN-NX-WZ', 4, 268, 1390, '640300', '1', NOW(), '1', NOW(), 0, 1),
(1389, '石嘴山市', 'CN-NX-SZS', 4, 268, 1389, '640200', '1', NOW(), '1', NOW(), 0, 1),
(1388, '银川市', 'CN-NX-YC', 4, 268, 1388, '640100', '1', NOW(), '1', NOW(), 0, 1);

-- 新疆维吾尔自治区
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1398, '博尔塔拉蒙古自治州', 'CN-XJ-BETL', 4, 269, 1398, '652700', '1', NOW(), '1', NOW(), 0, 1),
(1399, '巴音郭楞蒙古自治州', 'CN-XJ-BYGL', 4, 269, 1399, '652800', '1', NOW(), '1', NOW(), 0, 1),
(1400, '阿克苏地区', 'CN-XJ-AKS', 4, 269, 1400, '652900', '1', NOW(), '1', NOW(), 0, 1),
(1401, '克孜勒苏柯尔克孜自治州', 'CN-XJ-KZLS', 4, 269, 1401, '653000', '1', NOW(), '1', NOW(), 0, 1),
(1393, '乌鲁木齐市', 'CN-XJ-WLMQ', 4, 269, 1393, '650100', '1', NOW(), '1', NOW(), 0, 1),
(1403, '和田地区', 'CN-XJ-HT', 4, 269, 1403, '653200', '1', NOW(), '1', NOW(), 0, 1),
(1404, '伊犁哈萨克自治州', 'CN-XJ-YL', 4, 269, 1404, '654000', '1', NOW(), '1', NOW(), 0, 1),
(1405, '塔城地区', 'CN-XJ-TC', 4, 269, 1405, '654200', '1', NOW(), '1', NOW(), 0, 1),
(1406, '阿勒泰地区', 'CN-XJ-ALT', 4, 269, 1406, '654300', '1', NOW(), '1', NOW(), 0, 1),
(1402, '喀什地区', 'CN-XJ-KS', 4, 269, 1402, '653100', '1', NOW(), '1', NOW(), 0, 1),
(1394, '克拉玛依市', 'CN-XJ-KLMY', 4, 269, 1394, '650200', '1', NOW(), '1', NOW(), 0, 1),
(1395, '吐鲁番市', 'CN-XJ-TLF', 4, 269, 1395, '650400', '1', NOW(), '1', NOW(), 0, 1),
(1396, '哈密市', 'CN-XJ-HM', 4, 269, 1396, '650500', '1', NOW(), '1', NOW(), 0, 1),
(1397, '昌吉回族自治州', 'CN-XJ-CJ', 4, 269, 1397, '652300', '1', NOW(), '1', NOW(), 0, 1);

-- 香港特别行政区
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1422, '沙田区', 'CN-HK-ST', 4, 270, 1422, '810016', '1', NOW(), '1', NOW(), 0, 1),
(1407, '中西区', 'CN-HK-CW', 4, 270, 1407, '810001', '1', NOW(), '1', NOW(), 0, 1),
(1408, '湾仔区', 'CN-HK-WC', 4, 270, 1408, '810002', '1', NOW(), '1', NOW(), 0, 1),
(1409, '东区', 'CN-HK-EA', 4, 270, 1409, '810003', '1', NOW(), '1', NOW(), 0, 1),
(1410, '南区', 'CN-HK-SO', 4, 270, 1410, '810004', '1', NOW(), '1', NOW(), 0, 1),
(1411, '油尖旺区', 'CN-HK-YTM', 4, 270, 1411, '810005', '1', NOW(), '1', NOW(), 0, 1),
(1412, '深水埗区', 'CN-HK-SSP', 4, 270, 1412, '810006', '1', NOW(), '1', NOW(), 0, 1),
(1413, '九龙城区', 'CN-HK-KC', 4, 270, 1413, '810007', '1', NOW(), '1', NOW(), 0, 1),
(1414, '黄大仙区', 'CN-HK-WTS', 4, 270, 1414, '810008', '1', NOW(), '1', NOW(), 0, 1),
(1415, '观塘区', 'CN-HK-KT', 4, 270, 1415, '810009', '1', NOW(), '1', NOW(), 0, 1),
(1417, '荃湾区', 'CN-HK-TW', 4, 270, 1417, '810011', '1', NOW(), '1', NOW(), 0, 1),
(1418, '屯门区', 'CN-HK-TM', 4, 270, 1418, '810012', '1', NOW(), '1', NOW(), 0, 1),
(1419, '元朗区', 'CN-HK-YL', 4, 270, 1419, '810013', '1', NOW(), '1', NOW(), 0, 1),
(1420, '北区', 'CN-HK-NO', 4, 270, 1420, '810014', '1', NOW(), '1', NOW(), 0, 1),
(1421, '大埔区', 'CN-HK-TP', 4, 270, 1421, '810015', '1', NOW(), '1', NOW(), 0, 1),
(1423, '西贡区', 'CN-HK-SK', 4, 270, 1423, '810017', '1', NOW(), '1', NOW(), 0, 1),
(1424, '离岛区', 'CN-HK-IS', 4, 270, 1424, '810018', '1', NOW(), '1', NOW(), 0, 1),
(1416, '葵青区', 'CN-HK-KWT', 4, 270, 1416, '810010', '1', NOW(), '1', NOW(), 0, 1);

-- 澳门特别行政区
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1432, '路氹城', 'CN-MO-CT', 4, 271, 1432, '820008', '1', NOW(), '1', NOW(), 0, 1),
(1430, '嘉模堂区', 'CN-MO-TA', 4, 271, 1430, '820006', '1', NOW(), '1', NOW(), 0, 1),
(1428, '望德堂区', 'CN-MO-SL', 4, 271, 1428, '820004', '1', NOW(), '1', NOW(), 0, 1),
(1425, '花地玛堂区', 'CN-MO-NP', 4, 271, 1425, '820001', '1', NOW(), '1', NOW(), 0, 1),
(1427, '大堂区', 'CN-MO-CA', 4, 271, 1427, '820003', '1', NOW(), '1', NOW(), 0, 1),
(1426, '圣安多尼堂区', 'CN-MO-SA', 4, 271, 1426, '820002', '1', NOW(), '1', NOW(), 0, 1),
(1429, '风顺堂区', 'CN-MO-SLR', 4, 271, 1429, '820005', '1', NOW(), '1', NOW(), 0, 1),
(1431, '圣方济各堂区', 'CN-MO-CO', 4, 271, 1431, '820007', '1', NOW(), '1', NOW(), 0, 1);

-- 台湾省
INSERT INTO system_region (id, name, code, type, parent_id, sort, gb_code, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
(1440, '新北市', 'CN-TW-NTP', 4, 272, 1440, '710800', '1', NOW(), '1', NOW(), 0, 1),
(1441, '桃园市', 'CN-TW-TY', 4, 272, 1441, '710900', '1', NOW(), '1', NOW(), 0, 1),
(1443, '苗栗县', 'CN-TW-ML', 4, 272, 1443, '711200', '1', NOW(), '1', NOW(), 0, 1),
(1444, '彰化县', 'CN-TW-CH', 4, 272, 1444, '711300', '1', NOW(), '1', NOW(), 0, 1),
(1445, '南投县', 'CN-TW-NT', 4, 272, 1445, '711400', '1', NOW(), '1', NOW(), 0, 1),
(1446, '云林县', 'CN-TW-YL', 4, 272, 1446, '711500', '1', NOW(), '1', NOW(), 0, 1),
(1448, '屏东县', 'CN-TW-PT', 4, 272, 1448, '711700', '1', NOW(), '1', NOW(), 0, 1),
(1449, '宜兰县', 'CN-TW-IL', 4, 272, 1449, '711800', '1', NOW(), '1', NOW(), 0, 1),
(1450, '花莲县', 'CN-TW-HL', 4, 272, 1450, '711900', '1', NOW(), '1', NOW(), 0, 1),
(1451, '台东县', 'CN-TW-TT', 4, 272, 1451, '712000', '1', NOW(), '1', NOW(), 0, 1),
(1452, '澎湖县', 'CN-TW-PH', 4, 272, 1452, '712100', '1', NOW(), '1', NOW(), 0, 1),
(1447, '嘉义县', 'CN-TW-CYX', 4, 272, 1447, '711600', '1', NOW(), '1', NOW(), 0, 1),
(1442, '新竹县', 'CN-TW-HSX', 4, 272, 1442, '711100', '1', NOW(), '1', NOW(), 0, 1),
(1433, '台北市', 'CN-TW-TP', 4, 272, 1433, '710100', '1', NOW(), '1', NOW(), 0, 1),
(1434, '高雄市', 'CN-TW-KH', 4, 272, 1434, '710200', '1', NOW(), '1', NOW(), 0, 1),
(1435, '台中市', 'CN-TW-TC', 4, 272, 1435, '710300', '1', NOW(), '1', NOW(), 0, 1),
(1436, '台南市', 'CN-TW-TN', 4, 272, 1436, '710400', '1', NOW(), '1', NOW(), 0, 1),
(1437, '基隆市', 'CN-TW-KL', 4, 272, 1437, '710500', '1', NOW(), '1', NOW(), 0, 1),
(1438, '新竹市', 'CN-TW-HC', 4, 272, 1438, '710600', '1', NOW(), '1', NOW(), 0, 1),
(1439, '嘉义市', 'CN-TW-CY', 4, 272, 1439, '710700', '1', NOW(), '1', NOW(), 0, 1);



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
