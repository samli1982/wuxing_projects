# .env.local 本地开发配置指南

## ✅ 已完成的配置

### 1. 创建 `.env.local` 文件
位置：项目根目录 `/Users/saml/GoProjects/src/wuxing_projects/.env.local`

存储本地开发的敏感配置：
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=wuxing_db
DB_USERNAME=root
DB_PASSWORD=your_db_password_here

ALIYUN_ACCESS_KEY_ID=your_aliyun_access_key_id
ALIYUN_ACCESS_KEY_SECRET=your_aliyun_access_key_secret
ALIYUN_BUCKET_NAME=wuxing-files
ALIYUN_OSS_ENDPOINT=oss-cn-shenzhen.aliyuncs.com
ALIYUN_DOMAIN=
ALIYUN_PREFIX=wuxing/

JWT_SECRET=your_jwt_secret_here

WECHAT_APPID=your_wechat_appid
WECHAT_SECRET=your_wechat_secret
```

### 2. 添加 Maven 依赖
在 `backend/pom.xml` 中添加了 `dotenv-java` 库：
```xml
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.0.0</version>
</dependency>
```

### 3. 创建 Spring Boot 配置类
文件：`backend/src/main/java/com/wuxing/config/DotEnvConfig.java`

在应用启动时自动加载 `.env.local` 中的环境变量到系统属性中。

### 4. 修改 `application.yml`
已将所有敏感配置改为使用环境变量形式：
```yaml
datasource:
  url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:wuxing_db}...
  username: ${DB_USERNAME:root}
  password: ${DB_PASSWORD:}
```

## 🔐 安全性

- ✅ `.env.local` 已添加到 `.gitignore`，不会上传到 GitHub
- ✅ `application.yml` 中只包含占位符，不包含实际密钥
- ✅ GitHub 推送保护不会再被触发

## 🚀 使用方式

### 本地开发
1. 确保 `.env.local` 文件存在于项目根目录
2. 启动后端服务：
   ```bash
   cd backend
   mvn spring-boot:run
   ```
3. DotEnvConfig 会自动加载 `.env.local` 中的变量

### 生产环境
使用系统环境变量或 Docker 环境变量来提供配置：
```bash
export DB_HOST=prod-db-host
export DB_PORT=3306
export DB_PASSWORD=prod-password
# 其他环境变量...
```

## 📝 注意事项

1. **不要提交 `.env.local` 到 Git** - 它已在 `.gitignore` 中
2. **敏感密钥需要重置** - 考虑在阿里云后台重置 AccessKey，因为之前已暴露
3. **环境变量优先级** - 如果同时设置了系统环境变量和 `.env.local`，系统环境变量会覆盖
4. **默认值支持** - 如果环境变量未设置，会使用 `application.yml` 中冒号后的默认值

## ✨ 工作流总结

```
.env.local (本地密钥配置)
    ↓
DotEnvConfig (自动加载)
    ↓
System.setProperty() (设置系统属性)
    ↓
application.yml (读取 ${变量名})
    ↓
Spring Boot 应用 (正常运行)
```

---

**状态**: ✅ 完成  
**最后更新**: 2025-12-07
