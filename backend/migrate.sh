#!/bin/bash

# 数据迁移脚本 - 快速执行
# 用法: bash migrate.sh
# 说明: 自动执行测试数据迁移

set -e

echo "🔄 开始数据迁移..."
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SQL_FILE="${SCRIPT_DIR}/sql/migration_data.sql"

# 检查SQL文件是否存在
if [ ! -f "$SQL_FILE" ]; then
    echo "❌ 错误：找不到迁移文件 $SQL_FILE"
    exit 1
fi

echo "📄 迁移文件: $SQL_FILE"
echo ""

# 提示用户确认
echo "⚠️  本操作将向数据库导入以下数据："
echo "   - 5个示例会员"
echo "   - 6个示例命盘"
echo "   - 3条命盘详情"
echo ""
read -p "请确认是否继续？(y/n): " -r CONFIRM

if [[ ! $CONFIRM =~ ^[Yy]$ ]]; then
    echo "❌ 已取消操作"
    exit 1
fi

echo ""
echo "🔌 正在连接数据库..."
echo ""

# 执行迁移脚本
# 注意：如果MySQL服务器不在本地或需要密码，请修改以下命令
mysql -h localhost -u root -p wuxing_db < "$SQL_FILE"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 数据迁移成功！"
    echo ""
    echo "📊 数据统计："
    mysql -h localhost -u root -p wuxing_db -e "
        SELECT 
            (SELECT COUNT(*) FROM member_user WHERE deleted = 0) as '会员总数',
            (SELECT COUNT(*) FROM palmtree WHERE deleted = 0) as '命盘总数',
            (SELECT COUNT(*) FROM palmtree_detail WHERE deleted = 0) as '命盘详情总数'
    "
    echo ""
    echo "🚀 下一步："
    echo "   1. 启动后端: cd backend && mvn spring-boot:run"
    echo "   2. 启动前端: cd frontend && npm run dev"
    echo "   3. 访问后台: http://localhost:5173"
    echo "   4. 登录账号: admin / admin123"
    echo "   5. 进入'命盘管理'菜单查看数据"
else
    echo ""
    echo "❌ 数据迁移失败！"
    exit 1
fi
