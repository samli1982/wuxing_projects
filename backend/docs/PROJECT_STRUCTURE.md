# 五行项目后端 - 项目结构说明

## 技术栈

- **框架**: Spring Boot 3.2.0
- **Java版本**: JDK 17
- **构建工具**: Maven
- **ORM框架**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **安全框架**: Spring Security
- **API文档**: Knife4j (Swagger)
- **JWT**: jsonwebtoken 0.12.3
- **工具类**: Hutool 5.8.24

## 项目分层架构

```
┌─────────────────────────────────────────┐
│          Controller 层（控制器）           │  接收HTTP请求，参数验证，调用Service
├─────────────────────────────────────────┤
│          Service 层（业务逻辑）            │  业务逻辑处理，事务控制
├─────────────────────────────────────────┤
│     Mapper/Repository 层（数据访问）       │  与数据库交互，CRUD操作
├─────────────────────────────────────────┤
│          Entity 层（实体映射）             │  数据库表映射
└─────────────────────────────────────────┘
```

## 目录结构详解

### 1. src/main/java/com/wuxing

#### 1.1 config/ - 配置类
- `MybatisPlusConfig.java`: MyBatis-Plus配置（分页插件等）
- `RedisConfig.java`: Redis配置（序列化等）
- `SecurityConfig.java`: Spring Security安全配置
- `SwaggerConfig.java`: API文档配置
- `WebConfig.java`: Web MVC配置（跨域、拦截器等）

#### 1.2 controller/ - 控制器层
负责接收HTTP请求并返回响应
- `UserController.java`: 用户相关接口（注册、登录、查询等）
- `HealthController.java`: 健康检查接口

**职责**:
- 接收前端请求
- 参数验证（使用@Valid）
- 调用Service层
- 返回统一响应格式

#### 1.3 dto/ - 数据传输对象
- `request/`: 请求DTO
  - `LoginRequest.java`: 登录请求参数
  - `RegisterRequest.java`: 注册请求参数
- `response/`: 响应DTO
  - `Result.java`: 统一响应结果封装

**作用**: 避免直接暴露Entity，提供更好的接口设计

#### 1.4 entity/ - 实体类
与数据库表一一对应
- `BaseEntity.java`: 基础实体（包含公共字段：id、创建时间等）
- `User.java`: 用户实体
- `Role.java`: 角色实体

**特点**:
- 使用@TableName指定表名
- 继承BaseEntity获得公共字段
- 使用Lombok简化代码

#### 1.5 enums/ - 枚举类
- `ResultCode.java`: 响应状态码枚举

#### 1.6 exception/ - 异常处理
- `BusinessException.java`: 业务异常类
- `GlobalExceptionHandler.java`: 全局异常处理器

**功能**: 统一异常处理，返回规范的错误信息

#### 1.7 filter/ - 过滤器
用于请求预处理（如JWT验证）

#### 1.8 interceptor/ - 拦截器
用于请求拦截（如日志记录）

#### 1.9 mapper/ - MyBatis Mapper接口
- `UserMapper.java`: 用户数据访问接口

**说明**: 
- 继承BaseMapper获得基础CRUD方法
- 可自定义SQL方法

#### 1.10 repository/ - JPA Repository
（可选）如果使用Spring Data JPA

#### 1.11 service/ - 业务逻辑层
- `UserService.java`: 用户服务接口
- `impl/UserServiceImpl.java`: 用户服务实现

**职责**:
- 业务逻辑处理
- 事务管理（@Transactional）
- 调用Mapper进行数据操作

#### 1.12 utils/ - 工具类
- `JwtUtil.java`: JWT工具类（生成、验证Token）
- `RedisUtil.java`: Redis工具类
- `ResultUtil.java`: 响应结果工具类

### 2. src/main/resources

- `mapper/`: MyBatis XML映射文件
  - `UserMapper.xml`: 用户Mapper的SQL映射
- `static/`: 静态资源（CSS、JS、图片等）
- `templates/`: 模板文件（如Thymeleaf模板）
- `application.yml`: 主配置文件
- `application-dev.yml`: 开发环境配置
- `application-prod.yml`: 生产环境配置

### 3. src/test/java

单元测试和集成测试代码

### 4. sql/

- `schema.sql`: 数据库表结构脚本
- `data.sql`: 初始化数据脚本

### 5. docker/

- `Dockerfile`: Docker镜像构建文件

### 6. scripts/

- `start.sh`: Linux/Mac启动脚本
- `start.bat`: Windows启动脚本

### 7. docs/

项目文档目录

## 核心功能说明

### 1. 用户认证流程

```
1. 用户注册
   UserController.register() 
   → UserService.register() 
   → 密码加密 
   → UserMapper.insert()

2. 用户登录
   UserController.login() 
   → UserService.login() 
   → 验证用户名密码 
   → JwtUtil.generateToken() 
   → 返回Token

3. 接口访问
   请求携带Token 
   → JwtAuthenticationFilter验证 
   → SecurityConfig授权检查 
   → Controller处理
```

### 2. 数据访问流程

```
Controller 
→ Service（业务逻辑） 
→ Mapper（数据访问） 
→ MyBatis（ORM） 
→ MySQL数据库
```

### 3. 异常处理流程

```
业务代码抛出异常 
→ GlobalExceptionHandler捕获 
→ 返回统一格式错误信息
```

## 配置说明

### application.yml 配置项

1. **数据库配置**: 修改`spring.datasource`
2. **Redis配置**: 修改`spring.data.redis`
3. **JWT配置**: 修改`jwt.secret`和`jwt.expiration`
4. **服务器配置**: 修改`server.port`

### 环境切换

通过`spring.profiles.active`切换环境：
- `dev`: 开发环境
- `prod`: 生产环境

## 扩展开发指南

### 添加新功能模块

1. **创建Entity**: 在`entity/`下创建实体类
2. **创建Mapper**: 在`mapper/`下创建Mapper接口及XML
3. **创建Service**: 在`service/`下创建接口和实现类
4. **创建Controller**: 在`controller/`下创建控制器
5. **创建DTO**: 在`dto/`下创建请求和响应对象

### 最佳实践

1. **分层清晰**: Controller只做参数验证和调用，业务逻辑放在Service
2. **异常处理**: 使用BusinessException抛出业务异常
3. **事务管理**: Service层方法添加@Transactional
4. **参数验证**: 使用@Valid和验证注解
5. **日志记录**: 使用@Slf4j和log记录关键操作

## 常用命令

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package

# 运行（开发环境）
mvn spring-boot:run

# 运行（生产环境）
java -jar target/wuxing-backend-1.0.0.jar --spring.profiles.active=prod
```

## API文档访问

启动项目后访问:
- Knife4j文档: http://localhost:8080/doc.html
- Swagger文档: http://localhost:8080/swagger-ui.html
