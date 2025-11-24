# 快速开始指南

## 前置要求

确保已安装以下软件：

- **JDK 17+** ([下载地址](https://adoptium.net/))
- **Maven 3.8+** ([下载地址](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([下载地址](https://dev.mysql.com/downloads/mysql/))
- **Redis 6.0+** ([下载地址](https://redis.io/download))
- **IDE**: IntelliJ IDEA 或 Eclipse

## 步骤1: 克隆/下载项目

```bash
cd /path/to/your/workspace
# 如果是从Git克隆
git clone <repository-url>
```

## 步骤2: 配置数据库

### 2.1 启动MySQL

```bash
# Mac/Linux
mysql.server start

# Windows
net start MySQL80
```

### 2.2 创建数据库并导入数据

```bash
# 登录MySQL
mysql -u root -p

# 或直接执行脚本
mysql -u root -p < sql/schema.sql
mysql -u root -p wuxing_db < sql/data.sql
```

### 2.3 修改数据库配置

编辑 `src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wuxing_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的MySQL密码
```

## 步骤3: 配置Redis

### 3.1 启动Redis

```bash
# Mac (使用Homebrew)
brew services start redis

# Linux
redis-server

# Windows
redis-server.exe
```

### 3.2 验证Redis连接

```bash
redis-cli ping
# 应该返回: PONG
```

如果Redis有密码，修改 `application-dev.yml`:

```yaml
spring:
  data:
    redis:
      password: your_redis_password
```

## 步骤4: 安装Maven依赖

```bash
cd backend
mvn clean install
```

> 首次执行会下载所有依赖，可能需要几分钟时间

## 步骤5: 运行项目

### 方式1: 使用Maven命令

```bash
# Linux/Mac
./scripts/start.sh

# Windows
scripts\start.bat

# 或直接使用Maven
mvn spring-boot:run
```

### 方式2: 使用IDE

1. 用IntelliJ IDEA打开项目
2. 找到 `src/main/java/com/wuxing/WuxingApplication.java`
3. 右键点击 → Run 'WuxingApplication'

### 方式3: 运行JAR包

```bash
# 先打包
mvn clean package -DskipTests

# 运行
java -jar target/wuxing-backend-1.0.0.jar
```

## 步骤6: 验证启动成功

### 6.1 查看控制台输出

启动成功后应该看到:

```
====================================
五行项目启动成功！
API文档地址: http://localhost:8080/doc.html
====================================
```

### 6.2 访问健康检查接口

```bash
curl http://localhost:8080/api/health
```

应该返回:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "status": "UP",
    "timestamp": 1234567890,
    "service": "wuxing-backend"
  }
}
```

### 6.3 访问API文档

浏览器打开: http://localhost:8080/doc.html

## 步骤7: 测试API

### 7.1 用户注册

```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "13800138000"
  }'
```

### 7.2 用户登录

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

返回Token:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

### 7.3 使用Token访问接口

```bash
curl -X GET "http://localhost:8080/api/user/info?userId=1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

## 常见问题

### Q1: 端口8080已被占用

**解决方案**: 修改 `application.yml` 中的 `server.port`

```yaml
server:
  port: 8081  # 改为其他端口
```

### Q2: 连接MySQL失败

**检查项**:
1. MySQL是否已启动
2. 用户名密码是否正确
3. 数据库wuxing_db是否已创建
4. 端口3306是否正确

### Q3: 连接Redis失败

**检查项**:
1. Redis是否已启动: `redis-cli ping`
2. Redis端口是否为6379
3. 如果有密码，是否已配置

### Q4: Maven依赖下载失败

**解决方案**:
1. 检查网络连接
2. 配置Maven国内镜像（修改settings.xml）:

```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

### Q5: JWT Token验证失败

**检查项**:
1. Token是否正确携带在Header中
2. Token是否已过期（默认24小时）
3. jwt.secret配置是否一致

## 开发建议

### 1. IDE配置

**IntelliJ IDEA**:
- 安装Lombok插件
- 启用注解处理器: Settings → Build → Compiler → Annotation Processors → Enable

### 2. 代码热重载

添加spring-boot-devtools依赖，修改代码后自动重启:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### 3. 调试模式

```bash
# Maven调试模式
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# 然后在IDE中配置远程调试，连接localhost:5005
```

## 下一步

- 查看 [API文档](http://localhost:8080/doc.html)
- 阅读 [项目结构说明](PROJECT_STRUCTURE.md)
- 开始开发新功能

## 需要帮助？

- 查看项目README.md
- 查看docs目录下的文档
- 提交Issue
