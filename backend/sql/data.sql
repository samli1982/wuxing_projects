USE wuxing_db;

-- 插入默认角色
INSERT INTO sys_role (role_name, role_code, description, sort, status) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员', 1, 1),
('管理员', 'ADMIN', '系统管理员', 2, 1),
('普通用户', 'USER', '普通用户', 3, 1);

-- 插入默认用户 (密码: admin123)
INSERT INTO sys_user (username, password, nickname, email, status) VALUES
('admin', '$2a$10$XfGHc25n/TdUpvDH3I6R9eNxhFs4cc4QjVCl7xsWjaTIPApIDnHpO', '超级管理员', 'admin@example.com', 1);

-- 分配角色给用户
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 插入默认权限
INSERT INTO sys_permission (permission_name, permission_code, permission_type, parent_id, path, icon, sort, status) VALUES
('系统管理', 'system', 1, 0, '/system', 'system', 1, 1),
('用户管理', 'system:user', 1, 1, '/user', 'user', 1, 1),
('用户查询', 'system:user:query', 2, 2, NULL, NULL, 1, 1),
('用户列表', 'system:user:list', 2, 2, NULL, NULL, 2, 1),
('用户新增', 'system:user:add', 2, 2, NULL, NULL, 3, 1),
('用户编辑', 'system:user:edit', 2, 2, NULL, NULL, 4, 1),
('用户删除', 'system:user:delete', 2, 2, NULL, NULL, 5, 1),
('角色管理', 'system:role', 1, 1, '/role', 'role', 2, 1),
('角色查询', 'system:role:query', 2, 8, NULL, NULL, 1, 1),
('角色列表', 'system:role:list', 2, 8, NULL, NULL, 2, 1),
('角色新增', 'system:role:add', 2, 8, NULL, NULL, 3, 1),
('角色编辑', 'system:role:edit', 2, 8, NULL, NULL, 4, 1),
('角色删除', 'system:role:delete', 2, 8, NULL, NULL, 5, 1),
('角色分配权限', 'system:role:assign', 2, 8, NULL, NULL, 6, 1),
('权限管理', 'system:permission', 1, 1, '/permission', 'permission', 3, 1),
('权限查询', 'system:permission:query', 2, 15, NULL, NULL, 1, 1),
('权限列表', 'system:permission:list', 2, 15, NULL, NULL, 2, 1),
('权限新增', 'system:permission:add', 2, 15, NULL, NULL, 3, 1),
('权限编辑', 'system:permission:edit', 2, 15, NULL, NULL, 4, 1),
('权限删除', 'system:permission:delete', 2, 15, NULL, NULL, 5, 1),
('字典管理', 'system:dict', 1, 1, '/dictionary', 'dict', 4, 1),
('字典查询', 'system:dict:query', 2, 21, NULL, NULL, 1, 1),
('字典列表', 'system:dict:list', 2, 21, NULL, NULL, 2, 1),
('字典新增', 'system:dict:add', 2, 21, NULL, NULL, 3, 1),
('字典编辑', 'system:dict:edit', 2, 21, NULL, NULL, 4, 1),
('字典删除', 'system:dict:delete', 2, 21, NULL, NULL, 5, 1),
('首页', 'dashboard', 1, 0, '/dashboard', 'dashboard', 0, 1),
('首页查看', 'dashboard:view', 2, 28, NULL, NULL, 1, 1),
('操作日志', 'system:log', 1, 1, '/log/operation', 'log', 5, 1),
('日志查询', 'system:log:query', 2, 30, NULL, NULL, 1, 1),
('日志列表', 'system:log:list', 2, 30, NULL, NULL, 2, 1),
('日志详情', 'system:log:detail', 2, 30, NULL, NULL, 3, 1),
('文件管理', 'system:file', 1, 1, '/file', 'file', 5, 1),
('文件列表', 'system:file:list', 2, 34, NULL, NULL, 1, 1),
('文件上传', 'system:file:upload', 2, 34, NULL, NULL, 2, 1),
('文件下载', 'system:file:download', 2, 34, NULL, NULL, 3, 1),
('文件删除', 'system:file:delete', 2, 34, NULL, NULL, 4, 1),
('菜单管理', 'system:menu', 1, 1, '/menu', 'menu', 6, 1),
('菜单查询', 'system:menu:query', 2, 39, NULL, NULL, 1, 1),
('菜单列表', 'system:menu:list', 2, 39, NULL, NULL, 2, 1),
('菜单新增', 'system:menu:add', 2, 39, NULL, NULL, 3, 1),
('菜单编辑', 'system:menu:edit', 2, 39, NULL, NULL, 4, 1),
('菜单删除', 'system:menu:delete', 2, 39, NULL, NULL, 5, 1),
('数据库查询', 'system:database', 1, 1, '/database', 'database', 7, 1),
('查看表列表', 'system:database:tables', 2, 45, NULL, NULL, 1, 1),
('查看表结构', 'system:database:structure', 2, 45, NULL, NULL, 2, 1),
('查看表统计', 'system:database:stats', 2, 45, NULL, NULL, 3, 1),
('执行SQL查询', 'system:database:query', 2, 45, NULL, NULL, 4, 1),
('缓存管理', 'system:cache', 1, 1, '/cache', 'cache', 8, 1),
('缓存列表', 'system:cache:list', 2, 50, NULL, NULL, 1, 1),
('缓存查询', 'system:cache:query', 2, 50, NULL, NULL, 2, 1),
('缓存删除', 'system:cache:delete', 2, 50, NULL, NULL, 3, 1),
('清空缓存', 'system:cache:clear', 2, 50, NULL, NULL, 4, 1),
('Redis信息', 'system:cache:info', 2, 50, NULL, NULL, 5, 1),
('药精管理', 'herb:manage', 1, 0, '/herb', 'leaf', 2, 1),
('药精查看', 'herb:manage:view', 2, 55, NULL, NULL, 1, 1),
('药精新增', 'herb:manage:add', 2, 55, NULL, NULL, 2, 1),
('药精编辑', 'herb:manage:edit', 2, 55, NULL, NULL, 3, 1),
('药精删除', 'herb:manage:delete', 2, 55, NULL, NULL, 4, 1),
('命盘管理', 'palmtree:manage', 1, 0, '/palmtree', 'circle-check', 3, 1),
('命盘查看', 'palmtree:manage:view', 2, 60, NULL, NULL, 1, 1),
('命盘新增', 'palmtree:manage:add', 2, 60, NULL, NULL, 2, 1),
('命盘编辑', 'palmtree:manage:edit', 2, 60, NULL, NULL, 3, 1),
('命盘删除', 'palmtree:manage:delete', 2, 60, NULL, NULL, 4, 1),
('会员管理', 'system:member', 1, 1, '/member', 'user-filled', 7, 1),
('会员查看', 'system:member:view', 2, 82, NULL, NULL, 1, 1),
('会员新增', 'system:member:add', 2, 82, NULL, NULL, 2, 1),
('会员编辑', 'system:member:edit', 2, 82, NULL, NULL, 3, 1),
('会员删除', 'system:member:delete', 2, 82, NULL, NULL, 4, 1);

-- 分配权限给超级管理员角色（拥有所有权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20),
(1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26),
(1, 28), (1, 29),
(1, 30), (1, 31), (1, 32), (1, 33),
(1, 34), (1, 35), (1, 36), (1, 37),
(1, 39), (1, 40), (1, 41), (1, 42), (1, 43),
(1, 45), (1, 46), (1, 47), (1, 48),
(1, 50), (1, 51), (1, 52), (1, 53), (1, 54),
(1, 55), (1, 56), (1, 57), (1, 58), (1, 59),
(1, 60), (1, 61), (1, 62), (1, 63), (1, 64),
(1, 82), (1, 83), (1, 84), (1, 85), (1, 86);

-- 分配权限给管理员角色（有用户和角色管理权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7),
(2, 8), (2, 9), (2, 10), (2, 11), (2, 12), (2, 13),
(2, 28), (2, 29),
(2, 30), (2, 31), (2, 32), (2, 33),
(2, 60), (2, 61), (2, 62), (2, 63), (2, 64),
(2, 82), (2, 83), (2, 84), (2, 85), (2, 86);

-- 分配权限给普通用户角色（只有查询权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 1), (3, 2), (3, 3), (3, 4), (3, 8), (3, 9), (3, 10), (3, 28), (3, 29),
(3, 60), (3, 61), (3, 82), (3, 83);

-- 插入默认字典数据
INSERT INTO sys_dictionary (dict_type, dict_type_name, dict_key, dict_value, sort, status, remark) VALUES
-- 用户状态
('user_status', '用户状态', '0', '禁用', 1, 1, '用户被禁用'),
('user_status', '用户状态', '1', '正常', 2, 1, '用户正常状态'),
-- 性别
('gender', '性别', '0', '未知', 1, 1, '未知性别'),
('gender', '性别', '1', '男', 2, 1, '男性'),
('gender', '性别', '2', '女', 3, 1, '女性'),
-- 通用状态
('common_status', '通用状态', '0', '禁用', 1, 1, '禁用状态'),
('common_status', '通用状态', '1', '启用', 2, 1, '启用状态'),
-- 是否
('yes_no', '是否', '0', '否', 1, 1, '否'),
('yes_no', '是否', '1', '是', 2, 1, '是'),
-- 权限类型
('permission_type', '权限类型', '1', '菜单', 1, 1, '菜单权限'),
('permission_type', '权限类型', '2', '按钮', 2, 1, '按钮权限');

-- 插入初始药精数据
INSERT INTO herb_info (number, name, alias, element, category, category_icon, properties, taste, nature_class, effects, sort, status) VALUES
-- 木中木
('1-①', '肉桂', '广紫桂', '木', '木中木', '🌲', '温', '辛,甘', 'warm', '补火助阳,引火归元', 1, 1),
-- 木中火
('1-②', '桂枝', '柳桂', '木', '木中火', '🔥', '温', '辛,甘', 'warm', '发汗解肌,温通经脉', 2, 1),
-- 木中土
('1-③', '白术', '于术', '木', '木中土', '🪐', '温', '苦,甘', 'warm', '健脾益气,燥湿利水', 3, 1),
-- 火中木
('2-①', '柴胡', '茈胡', '火', '火中木', '🌲', '凉', '苦,辛', 'cool', '和解少阳,疏肝解郁', 4, 1),
-- 火中火
('2-②', '黄芩', '黄芬', '火', '火中火', '🔥', '寒', '苦', 'cold', '清热燥湿,泻火解毒', 5, 1),
-- 火中土
('2-③', '甘草', '蜜草', '火', '火中土', '🪐', '平', '甘', 'neutral', '调和诸药,健脾益气', 6, 1),
-- 土中木
('3-①', '防风', '风肉', '土', '土中木', '🌲', '温', '甘,辛', 'warm', '祛风解表,胜湿止挛', 7, 1),
-- 土中火
('3-②', '干姜', '白姜', '土', '土中火', '🔥', '热', '辛', 'hot', '温中止呕,温肺化痰', 8, 1),
-- 土中土
('3-③', '茯苓', '云苓', '土', '土中土', '🪐', '平', '甘,淡', 'neutral', '利水渗湿,健脾宁心', 9, 1),
-- 金中木
('4-①', '桑叶', '冬桑叶', '金', '金中木', '🌲', '凉', '苦,甘', 'cool', '疏散风热,清肺润肠', 10, 1),
-- 金中火
('4-②', '黄芗', '黄耆', '金', '金中火', '🔥', '温', '甘', 'warm', '补气升阳,固表止汗', 11, 1),
-- 金中土
('4-③', '薏米', '薏苡', '金', '金中土', '🪐', '凉', '甘,淡', 'cool', '利水渗湿,健脾止泻', 12, 1),
-- 水中木
('5-①', '泽泻', '荸荠', '水', '水中木', '🌲', '寒', '甘,淡', 'cold', '利水渗湿,泄热', 13, 1),
-- 水中火
('5-②', '麦冬', '麦门冬', '水', '水中火', '🔥', '凉', '甘,苦', 'cool', '养阴润肺,清心安神', 14, 1),
-- 水中土
('5-③', '生地黄', '鲜地黄', '水', '水中土', '🪐', '寒', '甘,苦', 'cold', '清热凉血,养阴生津', 15, 1);
