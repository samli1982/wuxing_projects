#!/bin/bash

# 五行项目后端启动脚本

# 设置环境变量
export SPRING_PROFILES_ACTIVE=dev

# 设置JVM参数
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# 获取脚本所在目录
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)

echo "======================================"
echo "五行项目后端启动中..."
echo "项目目录: $PROJECT_DIR"
echo "环境: $SPRING_PROFILES_ACTIVE"
echo "======================================"

cd "$PROJECT_DIR" || exit

# 使用Maven启动
mvn spring-boot:run
