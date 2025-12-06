-- 创建数据库
CREATE DATABASE IF NOT EXISTS wuxing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wuxing_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    gender TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    remark VARCHAR(500) COMMENT '备注',
    openid VARCHAR(100) COMMENT '微信openid',
    unionid VARCHAR(100) COMMENT '微信unionid',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    INDEX idx_openid (openid),
    INDEX idx_unionid (unionid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_role_code (role_code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    permission_type TINYINT DEFAULT 1 COMMENT '权限类型 1菜单 2按钮',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(255) COMMENT '路由地址',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 字典表
CREATE TABLE IF NOT EXISTS sys_dictionary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    dict_type VARCHAR(50) NOT NULL COMMENT '字典类型',
    dict_type_name VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    dict_key VARCHAR(50) NOT NULL COMMENT '字典键',
    dict_value VARCHAR(200) NOT NULL COMMENT '字典值',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_dict_type (dict_type),
    INDEX idx_dict_key (dict_key),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    module VARCHAR(100) COMMENT '操作模块',
    operation_type VARCHAR(50) COMMENT '操作类型',
    description VARCHAR(255) COMMENT '操作描述',
    method VARCHAR(255) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    result TEXT COMMENT '返回结果',
    ip VARCHAR(50) COMMENT '操作IP',
    location VARCHAR(100) COMMENT '操作地址',
    operator VARCHAR(50) COMMENT '操作人',
    operator_id BIGINT COMMENT '操作人ID',
    duration BIGINT COMMENT '执行时长(毫秒)',
    status TINYINT DEFAULT 1 COMMENT '状态 0失败 1成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_module (module),
    INDEX idx_operator (operator),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 文件信息表
CREATE TABLE IF NOT EXISTS sys_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件原始名称',
    file_key VARCHAR(500) NOT NULL COMMENT '文件存储名称',
    file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_type VARCHAR(100) COMMENT '文件类型',
    storage_type TINYINT DEFAULT 2 COMMENT '存储类型 1-本地 2-OSS',
    uploader VARCHAR(50) COMMENT '上传人',
    uploader_id BIGINT COMMENT '上传人ID',
    category VARCHAR(50) COMMENT '文件分类',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_file_name (file_name),
    INDEX idx_category (category),
    INDEX idx_uploader (uploader),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 药精信息表
CREATE TABLE IF NOT EXISTS herb_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    number VARCHAR(20) NOT NULL COMMENT '药精编号 例如: 1-①',
    name VARCHAR(50) NOT NULL COMMENT '药精名称',
    alias VARCHAR(200) COMMENT '别名，多个别名用逗号分隔',
    element VARCHAR(10) NOT NULL COMMENT '五行分类（木/火/土/金/水）',
    category VARCHAR(50) NOT NULL COMMENT '子分类 例如: 木中木、木中火等',
    category_icon VARCHAR(10) COMMENT '分类图标emoji',
    properties VARCHAR(100) COMMENT '性味（寒/温/热/凉/平），多个用逗号分隔',
    taste VARCHAR(100) COMMENT '五味（酸/苦/甘/辛/咸），多个用逗号分隔',
    nature_class VARCHAR(20) COMMENT '性质分类（hot/warm/neutral/cool/cold）',
    effects VARCHAR(500) COMMENT '功效，多个用逗号分隔',
    description TEXT COMMENT '详细描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    UNIQUE KEY uk_number (number),
    INDEX idx_element (element),
    INDEX idx_category (category),
    INDEX idx_name (name),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='药精信息表';

-- 会员用户表
CREATE TABLE IF NOT EXISTS member_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    openid VARCHAR(100) COMMENT '微信openid',
    unionid VARCHAR(100) COMMENT '微信unionid',
    nickname VARCHAR(50) COMMENT '昵称',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    gender TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    birthday VARCHAR(20) COMMENT '生日',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    country VARCHAR(50) COMMENT '国家',
    language VARCHAR(20) COMMENT '语言',
    member_level TINYINT DEFAULT 1 COMMENT '会员等级 1普通会员 2VIP 3SVIP',
    member_expire_time DATETIME COMMENT '会员过期时间',
    points INT DEFAULT 0 COMMENT '积分',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    remark VARCHAR(500) COMMENT '备注',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_openid (openid),
    INDEX idx_unionid (unionid),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_member_level (member_level),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员用户表';

-- 命盘基本信息表
CREATE TABLE IF NOT EXISTS palmtree (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    nickname VARCHAR(50) COMMENT '命盘是称',
    real_name VARCHAR(50) COMMENT '真实姓名',
    birth_year INT COMMENT '出生年份',
    birth_month INT COMMENT '出生月份',
    birth_day INT COMMENT '出生日期',
    birth_hour_index INT COMMENT '出生时辰索引 0-12: 不详、子、丑....亥',
    birth_hour_type VARCHAR(20) COMMENT '出生时辰类型 precise精确 unknown不详',
    birth_city VARCHAR(100) COMMENT '出生地城市',
    birth_lng DOUBLE COMMENT '出生地经度',
    birth_lat DOUBLE COMMENT '出生地纬度',
    calendar_type VARCHAR(20) COMMENT '歴法类型 gregorian公历 lunar农历',
    gender VARCHAR(20) COMMENT '性别 male男 female女 other其他',
    for_health_analysis TINYINT DEFAULT 0 COMMENT '是否用于健康分析 0否 1是',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_member_id (member_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='命盘基本信息表';

-- 命盘详情信息表
CREATE TABLE IF NOT EXISTS palmtree_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    palmtree_id BIGINT NOT NULL COMMENT '命盘ID',
    year_heavenly_stem VARCHAR(10) COMMENT '年柱天干',
    year_earthly_branch VARCHAR(10) COMMENT '年柱地支',
    month_heavenly_stem VARCHAR(10) COMMENT '月柱天干',
    month_earthly_branch VARCHAR(10) COMMENT '月柱地支',
    day_heavenly_stem VARCHAR(10) COMMENT '日柱天干',
    day_earthly_branch VARCHAR(10) COMMENT '日柱地支',
    hour_heavenly_stem VARCHAR(10) COMMENT '时柱天干',
    hour_earthly_branch VARCHAR(10) COMMENT '时柱地支',
    nayin VARCHAR(50) COMMENT '纳音',
    kongwang VARCHAR(50) COMMENT '空亏',
    taiyuan VARCHAR(50) COMMENT '胎元',
    wuxing_data LONGTEXT COMMENT '五行数据 JSON格式',
    constitution_type VARCHAR(100) COMMENT '体质类型',
    constitution_analysis LONGTEXT COMMENT '体质分析 JSON格式',
    wuyunliuqi_data LONGTEXT COMMENT '五运六气数据 JSON格式',
    useful_gods LONGTEXT COMMENT '喜用神 JSON数组格式',
    adjustment_suggestions LONGTEXT COMMENT '调理建议 JSON格式',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    INDEX idx_palmtree_id (palmtree_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='命盘详情信息表';