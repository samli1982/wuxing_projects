-- ============================================================
-- 完整初始化脚本：包含表结构 + 权限 + 测试数据
-- ============================================================
-- 执行命令: mysql -h localhost -u root -p < init_all.sql
-- 或: mysql -h localhost -u root -p wuxing_db < init_all.sql

-- 第一步：执行表结构
SOURCE schema.sql;

-- 第二步：执行权限和初始数据
SOURCE data.sql;

-- 第三步：执行测试数据（会员和命盘）
SOURCE migration_data.sql;

-- ============================================================
-- 数据验证
-- ============================================================

-- 验证权限是否完整
SELECT 
  CONCAT('权限ID: ', id) as 权限信息,
  permission_name,
  permission_code,
  IF(permission_type = 1, '菜单', '按钮') as 类型
FROM sys_permission 
WHERE permission_code LIKE 'palmtree:%' OR permission_code LIKE 'system:member'
ORDER BY id;

-- 验证权限是否分配给超级管理员
SELECT 
  CONCAT('超级管理员权限数: ', COUNT(*)) as 权限统计
FROM sys_role_permission 
WHERE role_id = 1;

-- 验证会员和命盘数据
SELECT 
  CONCAT('会员总数: ', COUNT(*)) as 统计
FROM member_user 
WHERE deleted = 0;

SELECT 
  CONCAT('命盘总数: ', COUNT(*)) as 统计
FROM palmtree 
WHERE deleted = 0;

SELECT 
  CONCAT('命盘详情数: ', COUNT(*)) as 统计
FROM palmtree_detail 
WHERE deleted = 0;

-- 显示会员及其命盘数量
SELECT 
  '========== 会员命盘关联情况 ==========' as 信息
UNION ALL
SELECT 
  CONCAT(mu.nickname, ' (', mu.openid, '): ', COALESCE(COUNT(p.id), 0), ' 个命盘')
FROM member_user mu
LEFT JOIN palmtree p ON mu.id = p.member_id AND p.deleted = 0
WHERE mu.deleted = 0
GROUP BY mu.id, mu.nickname, mu.openid
ORDER BY mu.create_time DESC;
