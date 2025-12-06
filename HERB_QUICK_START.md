# 药精功能快速启动指南

## 一、前置准备

### 1.1 环境要求
- Java 17+
- MySQL 5.7+
- Maven 3.6+
- 微信开发者工具（用于小程序开发）

### 1.2 数据库初始化
执行以下命令初始化数据库和数据:
```bash
cd /Users/saml/GoProjects/src/wuxing_projects/backend

# 创建数据库和表
mysql -u root -p < sql/schema.sql

# 插入初始数据
mysql -u root -p < sql/data.sql
```

或者：
```bash
# 修改以下命令中的用户名和密码为实际值
mysql -u root -pYourPassword < backend/sql/schema.sql
mysql -u root -pYourPassword < backend/sql/data.sql
```

验证数据库:
```sql
USE wuxing_db;
SELECT COUNT(*) as count FROM herb_info;
-- 应该返回 15 行初始数据
```

---

## 二、后端启动

### 2.1 配置数据库连接
编辑 `/backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wuxing_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root
    password: yourPassword  # 修改为实际密码
```

### 2.2 编译和打包
```bash
cd backend
mvn clean package
```

### 2.3 运行应用
```bash
# 方式 1: 使用 Maven 直接运行
mvn spring-boot:run

# 方式 2: 使用 JAR 包运行
java -jar target/wuxing-backend-1.0.0.jar

# 方式 3: 在后台运行
nohup java -jar target/wuxing-backend-1.0.0.jar > app.log 2>&1 &
```

### 2.4 验证后端启动
```bash
# 检查应用是否正常运行
curl http://localhost:8080/api/health

# 访问 API 文档
# Swagger: http://localhost:8080/swagger-ui.html
# Knife4j: http://localhost:8080/doc.html

# 测试药精 API
curl http://localhost:8080/api/herb/list
```

---

## 三、小程序配置

### 3.1 配置后端地址
编辑 `/miniprogram/api/herb.js`:

```javascript
// 修改为实际的后端服务器地址
const BASE_URL = 'http://localhost:8080/api';  // 开发环境
// const BASE_URL = 'https://your-server.com/api';  // 生产环境
```

### 3.2 在微信开发者工具中配置
在微信开发者工具的详情中，添加合法域名:
- 开发环境: 关闭"不校验合法域名、web-view(业务域名)、TLS 版本以及 HTTPS 证书"
- 生产环境: 将后端服务器域名添加到合法域名列表

### 3.3 项目配置
小程序项目已配置在 `miniprogram/` 目录，无需额外配置。

---

## 四、API 快速测试

### 4.1 测试列表接口
```bash
# 获取木类药精 (前 10 条)
curl "http://localhost:8080/api/herb/list?element=木&size=10"

# 获取木中木的药精
curl "http://localhost:8080/api/herb/list?category=木中木"
```

### 4.2 测试按五行查询
```bash
# 查询所有"木"类药精
curl http://localhost:8080/api/herb/element/木

# 查询所有"火"类药精
curl http://localhost:8080/api/herb/element/火
```

### 4.3 测试搜索功能
```bash
# 搜索名称为"肉桂"的药精
curl "http://localhost:8080/api/herb/search?keyword=肉桂"

# 搜索功效包含"补气"的药精
curl "http://localhost:8080/api/herb/search?keyword=补气"
```

### 4.4 测试详情接口
```bash
# 获取 ID 为 1 的药精详情
curl http://localhost:8080/api/herb/1
```

---

## 五、页面功能测试

### 5.1 列表页面测试
1. 打开微信开发者工具，导入 `miniprogram` 目录
2. 点击"编译"运行小程序
3. 验证以下功能:
   - ✓ 页面加载时显示木类药精
   - ✓ 点击五行标签页切换分类
   - ✓ 点击子分类标签页筛选药精
   - ✓ 搜索功能正常工作
   - ✓ 点击药精卡片跳转到详情页

### 5.2 详情页面测试
1. 从列表页点击任意药精卡片
2. 验证以下内容:
   - ✓ 药精名称、编号、别名正确显示
   - ✓ 分类和图标显示正确
   - ✓ 性质和五味标签显示正确
   - ✓ 功效列表显示完整
   - ✓ 收藏按钮可用
   - ✓ 返回按钮返回列表页

### 5.3 搜索功能测试
1. 在列表页点击搜索图标
2. 输入关键词 (如 "肉桂")
3. 验证:
   - ✓ 搜索结果显示
   - ✓ 点击搜索结果跳转到详情页
   - ✓ 搜索框可关闭

---

## 六、常见问题排查

### Q: 后端启动失败
**症状**: `Could not resolve 'mysql'`

**解决方案**:
1. 确保 MySQL 已安装并启动
2. 检查数据库连接配置是否正确
3. 确认数据库用户名和密码
```bash
# 测试数据库连接
mysql -u root -p -h localhost
```

---

### Q: 小程序无法连接到后端
**症状**: 页面加载失败，显示"加载数据失败"

**解决方案**:
1. 确保后端应用已启动: `curl http://localhost:8080/api/health`
2. 检查 `api/herb.js` 中的 BASE_URL 是否正确
3. 打开微信开发者工具控制台，查看详细错误信息
4. 如果使用真实手机测试，需要:
   - 后端部署到公网服务器
   - 配置 HTTPS
   - 添加域名到合法域名列表

---

### Q: 搜索结果为空
**症状**: 搜索时返回无结果

**解决方案**:
1. 验证数据库中确实有数据:
   ```sql
   SELECT COUNT(*) FROM herb_info WHERE status = 1;
   ```
2. 检查关键词是否准确（区分大小写）
3. 查看后端日志是否有错误

---

### Q: 详情页面加载失败
**症状**: 点击列表项跳转详情页后显示加载状态不消失

**解决方案**:
1. 检查传递的 ID 是否正确
2. 验证后端是否能查询到该 ID 的数据
3. 查看浏览器控制台错误日志

---

## 七、数据库视图

### 7.1 查看所有药精
```sql
SELECT id, number, name, alias, element, category, properties, nature_class 
FROM herb_info 
WHERE status = 1 
ORDER BY sort ASC, id ASC;
```

### 7.2 按五行统计
```sql
SELECT element, COUNT(*) as count 
FROM herb_info 
WHERE status = 1 
GROUP BY element;
```

### 7.3 按子分类统计
```sql
SELECT category, COUNT(*) as count 
FROM herb_info 
WHERE status = 1 
GROUP BY category 
ORDER BY element ASC;
```

---

## 八、性能优化建议

### 8.1 查询优化
数据库索引已创建:
```sql
INDEX idx_element (element)       -- 按五行查询优化
INDEX idx_category (category)     -- 按子分类查询优化
INDEX idx_name (name)             -- 按名称搜索优化
UNIQUE KEY uk_number (number)     -- 编号唯一性保证
```

### 8.2 前端缓存优化
建议在小程序中缓存药精数据:
```javascript
// 示例: 缓存5分钟
const cacheKey = 'herb_list_' + element;
const cached = wx.getStorageSync(cacheKey);
if (cached && cached.timestamp > Date.now() - 5*60*1000) {
  // 使用缓存
  this.setData({ list: cached.data });
} else {
  // 重新加载并缓存
  herbApi.getHerbList(...).then(data => {
    wx.setStorageSync(cacheKey, { data, timestamp: Date.now() });
  });
}
```

---

## 九、生产环境部署检查清单

- [ ] 后端修改 MySQL 连接为生产环境地址
- [ ] 后端启用 HTTPS
- [ ] 小程序 API_BASE_URL 修改为生产环境地址
- [ ] 小程序添加合法域名到腾讯小程序后台
- [ ] 数据库备份和恢复方案已制定
- [ ] API 认证和授权逻辑已审查
- [ ] 性能测试和监控已部署
- [ ] 日志记录和告警已配置
- [ ] 数据库和应用已进行安全加固

---

## 十、相关文档

- [完整 API 对接指南](./API_HERB_INTEGRATION.md)
- [数据库 Schema](./backend/sql/schema.sql)
- [初始数据](./backend/sql/data.sql)
- [项目架构文档](./项目整体架构.md)

---

## 快速命令参考

```bash
# 一键启动 (假设已配置 MySQL)
cd backend
mvn clean compile                    # 编译
java -jar target/wuxing-*.jar       # 运行

# 一键测试
curl http://localhost:8080/api/health               # 健康检查
curl http://localhost:8080/api/herb/list            # 列表
curl http://localhost:8080/api/herb/element/木      # 按五行查询
curl "http://localhost:8080/api/herb/search?keyword=肉桂" # 搜索
curl http://localhost:8080/api/herb/1               # 详情
```

---

祝你使用愉快！如有任何问题，请查阅完整的 API 对接指南或联系开发团队。
