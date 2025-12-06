@echo off
REM 数据迁移脚本 - Windows 版本
REM 用法: migrate.bat
REM 说明: 自动执行测试数据迁移

setlocal enabledelayedexpansion

echo.
echo 🔄 开始数据迁移...
echo.

REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0
set SQL_FILE=!SCRIPT_DIR!sql\migration_data.sql

REM 检查SQL文件是否存在
if not exist "!SQL_FILE!" (
    echo ❌ 错误：找不到迁移文件 !SQL_FILE!
    pause
    exit /b 1
)

echo 📄 迁移文件: !SQL_FILE!
echo.

REM 提示用户确认
echo ⚠️  本操作将向数据库导入以下数据：
echo    - 5个示例会员
echo    - 6个示例命盘
echo    - 3条命盘详情
echo.

set /p CONFIRM="请确认是否继续？(y/n): "

if /i not "!CONFIRM!"=="y" (
    echo ❌ 已取消操作
    pause
    exit /b 1
)

echo.
echo 🔌 正在连接数据库...
echo.

REM 执行迁移脚本
REM 注意：需要先安装MySQL并将mysql.exe添加到系统路径中
REM 或者修改以下命令使用完整路径，如 "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
mysql -h localhost -u root -p wuxing_db < "!SQL_FILE!"

if !ERRORLEVEL! equ 0 (
    echo.
    echo ✅ 数据迁移成功！
    echo.
    echo 📊 数据统计：
    mysql -h localhost -u root -p wuxing_db -e "SELECT (SELECT COUNT(*) FROM member_user WHERE deleted = 0) as '会员总数', (SELECT COUNT(*) FROM palmtree WHERE deleted = 0) as '命盘总数', (SELECT COUNT(*) FROM palmtree_detail WHERE deleted = 0) as '命盘详情总数';"
    echo.
    echo 🚀 下一步：
    echo    1. 启动后端: cd backend ^&^& mvn spring-boot:run
    echo    2. 启动前端: cd frontend ^&^& npm run dev
    echo    3. 访问后台: http://localhost:5173
    echo    4. 登录账号: admin / admin123
    echo    5. 进入'命盘管理'菜单查看数据
) else (
    echo.
    echo ❌ 数据迁移失败！
    pause
    exit /b 1
)

pause
