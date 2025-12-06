-- 添加精确的出生时间字段
ALTER TABLE palmtree 
ADD COLUMN birth_hour INT COMMENT '出生小时（0-23）' AFTER birth_day,
ADD COLUMN birth_minute INT COMMENT '出生分钟（0-59）' AFTER birth_hour;

-- 从旧的hour_index迁移数据
UPDATE palmtree 
SET birth_hour = CASE 
    WHEN birth_hour_index = 0 THEN 23  -- 子时
    ELSE (birth_hour_index - 1) * 2 + 1  -- 其他时辰取起始时间
END,
birth_minute = 0
WHERE birth_hour IS NULL AND birth_hour_index IS NOT NULL;
