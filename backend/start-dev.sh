#!/bin/bash

# 加载 .env.local 文件
if [ -f ".env.local" ]; then
    # 读取 .env.local 文件并导出环境变量
    export $(cat .env.local | grep -v '#' | xargs)
    echo "[✓] 已加载 .env.local 环境变量"
else
    echo "[✗] .env.local 文件不存在"
    exit 1
fi

# 验证环境变量是否已加载
echo "数据库配置："
echo "  主机: $DB_HOST"
echo "  端口: $DB_PORT"
echo "  数据库: $DB_NAME"
echo "  用户: $DB_USERNAME"
echo "  密码: ${DB_PASSWORD:0:3}****"

# 通过 JVM 参数显式传入所有环境变量到 Spring Boot
mvn spring-boot:run \
  -Dspring-boot.run.arguments=" \
    --spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true \
    --spring.datasource.username=${DB_USERNAME} \
    --spring.datasource.password=${DB_PASSWORD} \
    --spring.datasource.hikari.username=${DB_USERNAME} \
    --spring.datasource.hikari.password=${DB_PASSWORD}"
