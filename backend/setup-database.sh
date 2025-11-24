#!/bin/bash

echo "========================================"
echo "数据库配置脚本"
echo "========================================"
echo ""

# 数据库配置
DB_USER="root"
DB_NAME="wuxing_db"
SQL_SCHEMA="/Users/saml/GoProjects/src/wuxing_projects/backend/sql/schema.sql"
SQL_DATA="/Users/saml/GoProjects/src/wuxing_projects/backend/sql/data.sql"

echo "请输入 MySQL root 密码（如果没有设置密码，直接回车）:"
read -s DB_PASSWORD

echo ""
echo ">>> 步骤 1: 创建数据库..."

if [ -z "$DB_PASSWORD" ]; then
    mysql -u $DB_USER -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
else
    mysql -u $DB_USER -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
fi

if [ $? -eq 0 ]; then
    echo "✅ 数据库 $DB_NAME 创建成功！"
else
    echo "❌ 数据库创建失败，请检查密码是否正确"
    exit 1
fi

echo ""
echo ">>> 步骤 2: 导入表结构..."

if [ -z "$DB_PASSWORD" ]; then
    mysql -u $DB_USER $DB_NAME < $SQL_SCHEMA 2>/dev/null
else
    mysql -u $DB_USER -p"$DB_PASSWORD" $DB_NAME < $SQL_SCHEMA 2>/dev/null
fi

if [ $? -eq 0 ]; then
    echo "✅ 表结构导入成功！"
else
    echo "❌ 表结构导入失败"
    exit 1
fi

echo ""
echo ">>> 步骤 3: 导入初始数据..."

if [ -z "$DB_PASSWORD" ]; then
    mysql -u $DB_USER $DB_NAME < $SQL_DATA 2>/dev/null
else
    mysql -u $DB_USER -p"$DB_PASSWORD" $DB_NAME < $SQL_DATA 2>/dev/null
fi

if [ $? -eq 0 ]; then
    echo "✅ 初始数据导入成功！"
else
    echo "❌ 初始数据导入失败"
    exit 1
fi

echo ""
echo ">>> 步骤 4: 验证数据库..."

if [ -z "$DB_PASSWORD" ]; then
    TABLE_COUNT=$(mysql -u $DB_USER $DB_NAME -e "SHOW TABLES;" 2>/dev/null | wc -l)
else
    TABLE_COUNT=$(mysql -u $DB_USER -p"$DB_PASSWORD" $DB_NAME -e "SHOW TABLES;" 2>/dev/null | wc -l)
fi

echo "✅ 数据库中共有 $((TABLE_COUNT - 1)) 个表"

echo ""
echo "========================================"
echo "🎉 数据库配置完成！"
echo "========================================"
echo ""
echo "数据库信息："
echo "  数据库名: $DB_NAME"
echo "  用户名: $DB_USER"
echo "  字符集: utf8mb4"
echo ""
echo "默认管理员账号："
echo "  用户名: admin"
echo "  密码: admin123"
echo ""
