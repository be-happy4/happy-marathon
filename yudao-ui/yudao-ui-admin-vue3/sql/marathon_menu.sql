-- 马拉松模块菜单（PostgreSQL）
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
