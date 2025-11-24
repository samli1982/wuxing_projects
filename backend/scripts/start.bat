@echo off
REM 五行项目后端启动脚本 (Windows)

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=dev

REM 设置JVM参数
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC

echo ======================================
echo 五行项目后端启动中...
echo 环境: %SPRING_PROFILES_ACTIVE%
echo ======================================

REM 切换到项目根目录
cd /d %~dp0..

REM 使用Maven启动
call mvn spring-boot:run

pause
