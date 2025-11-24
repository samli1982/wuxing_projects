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
('用户管理', 'system:user', 1, 1, '/system/user', 'user', 1, 1),
('用户查询', 'system:user:query', 2, 2, NULL, NULL, 1, 1),
('用户列表', 'system:user:list', 2, 2, NULL, NULL, 2, 1),
('用户新增', 'system:user:add', 2, 2, NULL, NULL, 3, 1),
('用户编辑', 'system:user:edit', 2, 2, NULL, NULL, 4, 1),
('用户删除', 'system:user:delete', 2, 2, NULL, NULL, 5, 1),
('角色管理', 'system:role', 1, 1, '/system/role', 'role', 2, 1),
('角色查询', 'system:role:query', 2, 8, NULL, NULL, 1, 1),
('角色列表', 'system:role:list', 2, 8, NULL, NULL, 2, 1),
('角色新增', 'system:role:add', 2, 8, NULL, NULL, 3, 1),
('角色编辑', 'system:role:edit', 2, 8, NULL, NULL, 4, 1),
('角色删除', 'system:role:delete', 2, 8, NULL, NULL, 5, 1),
('角色分配权限', 'system:role:assign', 2, 8, NULL, NULL, 6, 1),
('权限管理', 'system:permission', 1, 1, '/system/permission', 'permission', 3, 1),
('权限查询', 'system:permission:query', 2, 15, NULL, NULL, 1, 1),
('权限列表', 'system:permission:list', 2, 15, NULL, NULL, 2, 1),
('权限新增', 'system:permission:add', 2, 15, NULL, NULL, 3, 1),
('权限编辑', 'system:permission:edit', 2, 15, NULL, NULL, 4, 1),
('权限删除', 'system:permission:delete', 2, 15, NULL, NULL, 5, 1),
('字典管理', 'system:dict', 1, 1, '/system/dict', 'dict', 4, 1),
('字典查询', 'system:dict:query', 2, 21, NULL, NULL, 1, 1),
('字典列表', 'system:dict:list', 2, 21, NULL, NULL, 2, 1),
('字典新增', 'system:dict:add', 2, 21, NULL, NULL, 3, 1),
('字典编辑', 'system:dict:edit', 2, 21, NULL, NULL, 4, 1),
('字典删除', 'system:dict:delete', 2, 21, NULL, NULL, 5, 1);

-- 分配权限给超级管理员角色（拥有所有权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20),
(1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26);

-- 分配权限给管理员角色（有用户和角色管理权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7),
(2, 8), (2, 9), (2, 10), (2, 11), (2, 12), (2, 13);

-- 分配权限给普通用户角色（只有查询权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 1), (3, 2), (3, 3), (3, 4), (3, 8), (3, 9), (3, 10);

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
