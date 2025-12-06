-- 数据迁移脚本：初始化示例会员和命盘数据
-- 注意：此脚本仅用于开发测试，生产环境应通过前端应用正常流程创建数据
-- 执行前提：已执行过 schema.sql 和 data.sql
-- 命令: mysql -h localhost -u root -p wuxing_db < migration_data.sql

USE wuxing_db;

-- ============================================================
-- 第一部分：插入示例会员数据（member_user表）
-- ============================================================

INSERT INTO member_user (
    openid, unionid, nickname, real_name, email, phone, avatar, 
    gender, birthday, province, city, country, language, 
    member_level, member_expire_time, points, status, remark, 
    create_time, update_time
) VALUES
-- 会员1：张三
(
    'openid_test_001', 'unionid_test_001', '张三', '张三',
    'zhangsan@example.com', '13800138001', NULL,
    1, '1990-05-15', '北京', '北京', '中国', 'zh_CN',
    1, NULL, 100, 1, '测试会员1',
    NOW(), NOW()
),
-- 会员2：李四
(
    'openid_test_002', 'unionid_test_002', '李四', '李四',
    'lisi@example.com', '13800138002', NULL,
    2, '1992-08-20', '上海', '上海', '中国', 'zh_CN',
    1, NULL, 200, 1, '测试会员2',
    NOW(), NOW()
),
-- 会员3：王五
(
    'openid_test_003', 'unionid_test_003', '王五', '王五',
    'wangwu@example.com', '13800138003', NULL,
    1, '1988-12-10', '广东', '深圳', '中国', 'zh_CN',
    2, DATE_ADD(NOW(), INTERVAL 1 YEAR), 500, 1, 'VIP会员',
    NOW(), NOW()
),
-- 会员4：赵六
(
    'openid_test_004', 'unionid_test_004', '赵六', '赵六',
    'zhaoliu@example.com', '13800138004', NULL,
    2, '1995-03-25', '浙江', '杭州', '中国', 'zh_CN',
    1, NULL, 50, 1, '测试会员4',
    NOW(), NOW()
),
-- 会员5：孙七
(
    'openid_test_005', 'unionid_test_005', '孙七', '孙七',
    'sunqi@example.com', '13800138005', NULL,
    1, '1993-07-08', '江苏', '南京', '中国', 'zh_CN',
    1, NULL, 150, 1, '测试会员5',
    NOW(), NOW()
);

-- ============================================================
-- 第二部分：插入示例命盘数据（palmtree表）
-- ============================================================

-- 为会员1（张三）插入命盘数据
INSERT INTO palmtree (
    member_id, nickname, real_name, birth_year, birth_month, birth_day,
    birth_hour_index, birth_hour_type, birth_city, birth_lng, birth_lat,
    calendar_type, gender, for_health_analysis, status, remark,
    create_time, update_time
) VALUES
(
    1, '张三命盘1', '张三',
    1990, 5, 15, 2, 'precise', '北京',
    116.4074, 39.9042, 'gregorian', 'male', 1, 1,
    '张三的第一个命盘',
    NOW(), NOW()
),
(
    1, '张三命盘2', '张三',
    1990, 5, 15, 8, 'unknown', '北京',
    116.4074, 39.9042, 'gregorian', 'male', 0, 1,
    '张三的第二个命盘',
    NOW(), NOW()
);

-- 为会员2（李四）插入命盘数据
INSERT INTO palmtree (
    member_id, nickname, real_name, birth_year, birth_month, birth_day,
    birth_hour_index, birth_hour_type, birth_city, birth_lng, birth_lat,
    calendar_type, gender, for_health_analysis, status, remark,
    create_time, update_time
) VALUES
(
    2, '李四命盘', '李四',
    1992, 8, 20, 3, 'precise', '上海',
    121.4737, 31.2304, 'gregorian', 'female', 1, 1,
    '李四的命盘',
    NOW(), NOW()
);

-- 为会员3（王五）插入命盘数据
INSERT INTO palmtree (
    member_id, nickname, real_name, birth_year, birth_month, birth_day,
    birth_hour_index, birth_hour_type, birth_city, birth_lng, birth_lat,
    calendar_type, gender, for_health_analysis, status, remark,
    create_time, update_time
) VALUES
(
    3, '王五命盘1', '王五',
    1988, 12, 10, 4, 'precise', '深圳',
    114.0579, 22.5431, 'gregorian', 'male', 1, 1,
    '王五的第一个命盘',
    NOW(), NOW()
),
(
    3, '王五命盘2（农历）', '王五',
    1988, 10, 25, 11, 'unknown', '深圳',
    114.0579, 22.5431, 'lunar', 'male', 0, 1,
    '王五的农历命盘',
    NOW(), NOW()
),
(
    3, '王五命盘3', '王五',
    1988, 12, 10, 10, 'precise', '深圳',
    114.0579, 22.5431, 'gregorian', 'male', 1, 1,
    '王五的第三个命盘',
    NOW(), NOW()
);

-- ============================================================
-- 第三部分：插入示例命盘详情数据（palmtree_detail表）
-- ============================================================

-- 为张三的第一个命盘插入详情
INSERT INTO palmtree_detail (
    palmtree_id, year_heavenly_stem, year_earthly_branch,
    month_heavenly_stem, month_earthly_branch,
    day_heavenly_stem, day_earthly_branch,
    hour_heavenly_stem, hour_earthly_branch,
    nayin, kongwang, taiyuan, wuxing_data, constitution_type,
    create_time, update_time
) VALUES
(
    1, '庚', '午', '辛', '巳', '癸', '卯', '癸', '丑',
    '路旁土,蛇头火,水上火,洞下水',
    '申酉空', '壬午',
    '{\"金\": 15, \"木\": 8, \"水\": 28, \"火\": 25, \"土\": 14}',
    '阳虚质', NOW(), NOW()
);

-- 为李四的命盘插入详情
INSERT INTO palmtree_detail (
    palmtree_id, year_heavenly_stem, year_earthly_branch,
    month_heavenly_stem, month_earthly_branch,
    day_heavenly_stem, day_earthly_branch,
    hour_heavenly_stem, hour_earthly_branch,
    nayin, kongwang, taiyuan, wuxing_data, constitution_type,
    create_time, update_time
) VALUES
(
    3, '壬', '申', '己', '酉', '辛', '丑', '辛', '卯',
    '剑锋金,大驿土,壁上土,松柏木',
    '寅卯空', '甲戌',
    '{\"金\": 22, \"木\": 12, \"水\": 18, \"火\": 16, \"土\": 22}',
    '痰湿质', NOW(), NOW()
);

-- 为王五的第一个命盘插入详情
INSERT INTO palmtree_detail (
    palmtree_id, year_heavenly_stem, year_earthly_branch,
    month_heavenly_stem, month_earthly_branch,
    day_heavenly_stem, day_earthly_branch,
    hour_heavenly_stem, hour_earthly_branch,
    nayin, kongwang, taiyuan, wuxing_data, constitution_type,
    create_time, update_time
) VALUES
(
    5, '戊', '辰', '丙', '子', '庚', '寅', '庚', '午',
    '大林木,黑水,白蜡金,洛下水',
    '午未空', '戊寅',
    '{\"金\": 25, \"木\": 20, \"水\": 12, \"火\": 28, \"土\": 15}',
    '湿热质', NOW(), NOW()
);

-- ============================================================
-- 第四部分：验证数据插入结果
-- ============================================================

-- 验证会员数据
-- SELECT COUNT(*) as 会员总数 FROM member_user WHERE deleted = 0;
-- SELECT COUNT(*) as 命盘总数 FROM palmtree WHERE deleted = 0;
-- SELECT COUNT(*) as 命盘详情总数 FROM palmtree_detail WHERE deleted = 0;

-- ============================================================
-- 数据迁移说明
-- ============================================================
/*
此脚本创建了5个示例会员和对应的命盘数据：

会员1（张三）：2个命盘，其中1个有详情数据
会员2（李四）：1个命盘，有详情数据
会员3（王五）：3个命盘，其中1个有详情数据
会员4（赵六）：0个命盘
会员5（孙七）：0个命盘

执行此脚本后，可以在后台管理系统中：
1. 进入"会员管理"菜单查看所有会员
2. 进入"命盘管理"菜单，选择会员后查看对应的命盘列表
3. 点击"查看详情"可以查看命盘的详细信息

注意事项：
- 此脚本使用固定的 openid 进行测试
- 生产环境应该根据实际需求调整会员等级、积分等信息
- 命盘详情中的五行数据和体质信息仅作示例，实际应从八字算法计算得出
- 若需要删除测试数据，可执行相应的 DELETE 语句
*/
