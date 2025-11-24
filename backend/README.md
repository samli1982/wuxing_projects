# 五行项目后端 - Spring Boot

基于 Spring Boot 3.x 的五行项目后端服务。

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/wuxing/
│   │   │   ├── config/              # 配置类
│   │   │   │   ├── MybatisConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── SwaggerConfig.java
│   │   │   ├── controller/          # 控制器层（接收HTTP请求）
│   │   │   │   ├── HealthController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   │   ├── request/         # 请求DTO
│   │   │   │   └── response/        # 响应DTO
│   │   │   ├── entity/              # 实体类（数据库映射）
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   └── BaseEntity.java
│   │   │   ├── enums/               # 枚举类
│   │   │   │   └── ResultCode.java
│   │   │   ├── exception/           # 异常处理
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── BusinessException.java
│   │   │   ├── filter/              # 过滤器
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── interceptor/         # 拦截器
│   │   │   │   └── LogInterceptor.java
│   │   │   ├── mapper/              # MyBatis Mapper接口
│   │   │   │   └── UserMapper.java
│   │   │   ├── repository/          # JPA Repository（可选）
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/             # 业务逻辑层
│   │   │   │   ├── UserService.java
│   │   │   │   └── impl/            # 业务逻辑实现
│   │   │   │       └── UserServiceImpl.java
│   │   │   ├── utils/               # 工具类
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── RedisUtil.java
│   │   │   │   └── ResultUtil.java
│   │   │   └── WuxingApplication.java  # 启动类
│   │   └── resources/
│   │       ├── mapper/              # MyBatis XML映射文件
│   │       │   └── UserMapper.xml
│   │       ├── static/              # 静态资源
│   │       ├── templates/           # 模板文件
│   │       ├── application.yml      # 主配置文件
│   │       ├── application-dev.yml  # 开发环境配置
│   │       └── application-prod.yml # 生产环境配置
│   └── test/                        # 测试代码
│       └── java/com/wuxing/
├── docs/                            # 项目文档
├── scripts/                         # 脚本文件
├── sql/                             # SQL脚本
│   ├── schema.sql                   # 表结构
│   └── data.sql                     # 初始数据
├── docker/                          # Docker相关文件
│   └── Dockerfile
├── .gitignore
├── pom.xml                          # Maven依赖配置
└── README.md
```

## 技术栈

- **Spring Boot 3.2.x** - 核心框架
- **Spring Security** - 安全认证
- **MyBatis-Plus** - ORM框架（增强版MyBatis）
- **MySQL 8.0+** - 关系型数据库
- **Redis** - 缓存
- **JWT** - Token认证
- **Swagger/Knife4j** - API文档
- **Lombok** - 代码简化
- **Validation** - 参数校验
- **Maven** - 项目构建

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 安装依赖

```bash
mvn clean install
```

### 配置数据库

1. 创建数据库
```sql
CREATE DATABASE wuxing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行SQL脚本
```bash
mysql -u root -p wuxing_db < sql/schema.sql
mysql -u root -p wuxing_db < sql/data.sql
```

3. 修改配置文件 `application-dev.yml`

### 运行项目

```bash
# 开发环境
mvn spring-boot:run

# 或使用IDE直接运行 WuxingApplication.java
```

### 打包部署

```bash
# 打包
mvn clean package -DskipTests

# 运行jar包
java -jar target/wuxing-backend-1.0.0.jar --spring.profiles.active=prod
```

## Docker 部署

```bash
# 构建镜像
docker build -t wuxing-backend:latest -f docker/Dockerfile .

# 运行容器
docker run -d -p 8080:8080 --name wuxing-backend wuxing-backend:latest
```

## API 文档

启动项目后访问:
- Swagger UI: http://localhost:8080/swagger-ui.html
- Knife4j UI: http://localhost:8080/doc.html

## 项目分层说明

### Controller层
- 接收HTTP请求
- 参数验证
- 调用Service层
- 返回响应结果

### Service层
- 业务逻辑处理
- 事务控制
- 调用Mapper/Repository层

### Mapper/Repository层
- 数据访问层
- 与数据库交互
- CRUD操作

### 其他组件
- **Config**: 框架配置、Bean注册
- **DTO**: 数据传输对象，避免直接暴露Entity
- **Entity**: 数据库实体映射
- **Exception**: 统一异常处理
- **Filter/Interceptor**: 请求预处理
- **Utils**: 工具类

## License

MIT
