#!/bin/bash

echo "========================================"
echo "🚀 五行项目后端启动脚本"
echo "========================================"
echo ""

# 切换到后端目录
cd /Users/saml/GoProjects/src/wuxing_projects/backend

# 检查 Java
echo ">>> 检查 Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java 未安装或未配置"
    exit 1
fi
echo "✅ Java: $(java -version 2>&1 | head -n 1)"

# 检查 Maven
echo ""
echo ">>> 检查 Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven 未安装或未配置"
    exit 1
fi
echo "✅ Maven: $(mvn -version 2>&1 | head -n 1)"

# 检查 Redis
echo ""
echo ">>> 检查 Redis..."
if ! redis-cli ping &> /dev/null; then
    echo "⚠️  Redis 未运行，正在启动..."
    brew services start redis
    sleep 2
fi
if redis-cli ping &> /dev/null; then
    echo "✅ Redis: 运行中"
else
    echo "❌ Redis 启动失败"
    exit 1
fi

# 检查 MySQL
echo ""
echo ">>> 检查 MySQL..."
if mysql -u root -e "SELECT 1;" &> /dev/null || mysql -u root -p -e "SELECT 1;" &> /dev/null; then
    echo "✅ MySQL: 运行中"
else
    echo "❌ MySQL 未运行或无法连接"
    exit 1
fi

# 检查数据库
echo ""
echo ">>> 检查数据库..."
if mysql -u root -e "USE wuxing_db;" &> /dev/null || mysql -u root -p -e "USE wuxing_db;" &> /dev/null; then
    echo "✅ 数据库 wuxing_db 已存在"
else
    echo "❌ 数据库 wuxing_db 不存在，请先运行 ./setup-database.sh"
    exit 1
fi

echo ""
echo "========================================"
echo "环境检查通过，开始启动项目..."
echo "========================================"
echo ""
echo "提示："
echo "  - API 文档: http://localhost:8080/doc.html"
echo "  - 健康检查: http://localhost:8080/api/health"
echo "  - 默认账号: admin / admin123"
echo ""
echo "按 Ctrl+C 停止项目"
echo ""
echo "========================================"
echo ""

# 启动项目
mvn spring-boot:run
