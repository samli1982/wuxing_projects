# 药精功能 API 对接指南

## 项目概述

本文档说明药精（Herb）功能的后端 API 开发和小程序前端对接的详细信息。

## 后端 API 接口

### 1. 基础信息
- **API 前缀**: `/api/herb`
- **认证**: 公开接口，无需认证
- **响应格式**: JSON

### 2. API 端点列表

#### 2.1 分页查询药精
```
GET /api/herb/list
```

**请求参数**:
| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| current | Integer | 否 | 当前页码，默认 1 |
| size | Integer | 否 | 每页大小，默认 10 |
| element | String | 否 | 五行分类（木/火/土/金/水） |
| category | String | 否 | 子分类（如：木中木、火中火等） |
| keyword | String | 否 | 搜索关键词 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 15,
    "pages": 2,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "number": "1-①",
        "name": "肉桂",
        "alias": "广紫桂",
        "element": "木",
        "category": "木中木",
        "categoryIcon": "🌲",
        "properties": "温",
        "taste": "辛,甘",
        "natureClass": "warm",
        "effects": "补火助阳,引火归元",
        "description": "...",
        "sort": 1,
        "status": 1,
        "createTime": "2024-01-01T00:00:00"
      }
    ]
  },
  "timestamp": 1704067200000
}
```

---

#### 2.2 根据五行分类查询
```
GET /api/herb/element/{element}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| element | String | 五行分类（木/火/土/金/水） |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "number": "1-①",
      "name": "肉桂",
      ...
    }
  ],
  "timestamp": 1704067200000
}
```

---

#### 2.3 根据子分类查询
```
GET /api/herb/category/{category}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| category | String | 子分类（如：木中木、火中火等） |

**响应示例**: 同 2.2

---

#### 2.4 搜索药精
```
GET /api/herb/search
```

**请求参数**:
| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| keyword | String | 是 | 搜索关键词 |

**搜索范围**: 名称、别名、功效、五味、性味

**响应示例**: 同 2.2

---

#### 2.5 获取药精详情
```
GET /api/herb/{id}
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 药精ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "number": "1-①",
    "name": "肉桂",
    "alias": "广紫桂",
    "element": "木",
    "category": "木中木",
    "categoryIcon": "🌲",
    "properties": "温",
    "taste": "辛,甘",
    "natureClass": "warm",
    "effects": "补火助阳,引火归元",
    "description": "肉桂为樟科植物肉桂的树皮...",
    "sort": 1,
    "status": 1,
    "remark": "...",
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00"
  },
  "timestamp": 1704067200000
}
```

---

## 数据库结构

### herb_info 表

```sql
CREATE TABLE herb_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    number VARCHAR(20) NOT NULL COMMENT '药精编号',
    name VARCHAR(50) NOT NULL COMMENT '药精名称',
    alias VARCHAR(200) COMMENT '别名',
    element VARCHAR(10) NOT NULL COMMENT '五行分类',
    category VARCHAR(50) NOT NULL COMMENT '子分类',
    category_icon VARCHAR(10) COMMENT '分类图标',
    properties VARCHAR(100) COMMENT '性味',
    taste VARCHAR(100) COMMENT '五味',
    nature_class VARCHAR(20) COMMENT '性质分类',
    effects VARCHAR(500) COMMENT '功效',
    description TEXT COMMENT '详细描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    ...
);
```

---

## 小程序前端集成

### 1. API 配置文件
位置: `/miniprogram/api/herb.js`

配置后端地址:
```javascript
const BASE_URL = 'http://localhost:8080/api'; // 改为实际的服务器地址
```

### 2. 页面文件结构
```
pages/herb/
├── index.js          // 药精列表页面
├── index.wxml        // 列表视图
├── index.wxss        // 列表样式
├── index.json        // 列表配置
├── detail.js         // 详情页面
├── detail.wxml       // 详情视图
├── detail.wxss       // 详情样式
└── detail.json       // 详情配置
```

### 3. 页面功能说明

#### 列表页面 (index)
- **功能**: 展示按五行、子分类筛选的药精列表
- **关键函数**:
  - `loadHerbs()` - 加载当前五行的药精数据
  - `switchParent()` - 切换五行分类
  - `switchSub()` - 切换子分类
  - `filterList()` - 筛选列表
  - `onSearchInput()` - 搜索功能
  - `onCardTap()` - 导航到详情页

#### 详情页面 (detail)
- **功能**: 展示单个药精的详细信息
- **关键函数**:
  - `loadDetail()` - 加载药精详情
  - `formatHerbData()` - 格式化数据
  - `getNatureColor()` - 获取性质颜色

### 4. 数据格式说明

小程序前端数据结构:
```javascript
{
  id: 1,
  number: '1-①',
  name: '肉桂',
  alias: '广紫桂',
  category: '木中木',
  categoryIcon: '🌲',
  properties: '温',
  taste: ['辛', '甘'],           // 数组格式
  natureClass: 'warm',
  effects: ['补火助阳', '引火归元'], // 数组格式
  is_collected: false
}
```

**数据转换说明**:
- 后端 `taste` (逗号分隔字符串) → 前端 `taste` (数组)
- 后端 `effects` (逗号分隔字符串) → 前端 `effects` (数组)

### 5. 错误处理
- API 请求失败时，小程序会显示 Toast 提示
- 网络错误时自动回退到本地数据
- 详情页面加载失败会返回上一页

---

## 部署和配置

### 后端配置

1. **数据库初始化**
   ```bash
   mysql -u root -p < backend/sql/schema.sql
   mysql -u root -p < backend/sql/data.sql
   ```

2. **应用启动**
   ```bash
   cd backend
   mvn clean package
   java -jar target/wuxing-app.jar
   ```

3. **API 文档访问**
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Knife4j: http://localhost:8080/doc.html

### 小程序配置

1. **修改 API 地址**
   编辑 `api/herb.js`:
   ```javascript
   const BASE_URL = 'https://your-api-server.com/api';
   ```

2. **建议事项**
   - 使用 HTTPS 连接
   - 配置合法域名白名单
   - 添加请求超时处理

---

## 五行和性质对应关系

### 五行分类
| 五行 | 子分类 | 图标 | 颜色 |
|------|--------|------|------|
| 木 | 木中木、木中火、木中土、木中金、木中水 | 🌲🔥🪐🟡💧 | #3A9E8F |
| 火 | 火中木、火中火、火中土、火中金、火中水 | 🌲🔥🪐🟡💧 | #E63946 |
| 土 | 土中木、土中火、土中土、土中金、土中水 | 🌲🔥🪐🟡💧 | #F4D06F |
| 金 | 金中木、金中火、金中土、金中金、金中水 | 🌲🔥🪐🟡💧 | #CCCCCC |
| 水 | 水中木、水中火、水中土、水中金、水中水 | 🌲🔥🪐🟡💧 | #457B9D |

### 性质分类
| 性质 | 含义 | 颜色 | 类别代码 |
|------|------|------|---------|
| 热 | 温热性最强 | #E63946 | hot |
| 温 | 温性 | #F4D06F | warm |
| 平 | 平性 | #CCCCCC | neutral |
| 凉 | 凉性 | #457B9D | cool |
| 寒 | 寒性最强 | #457B9D | cold |

---

## 测试用例

### 1. 列表查询测试
```bash
curl http://localhost:8080/api/herb/list?current=1&size=10
```

### 2. 五行查询测试
```bash
curl http://localhost:8080/api/herb/element/木
```

### 3. 搜索测试
```bash
curl "http://localhost:8080/api/herb/search?keyword=肉桂"
```

### 4. 详情查询测试
```bash
curl http://localhost:8080/api/herb/1
```

---

## 常见问题

### Q1: 小程序无法连接到后端 API？
**A**: 
1. 检查后端是否已启动
2. 检查 `api/herb.js` 中的 `BASE_URL` 是否正确
3. 检查小程序开发工具中是否已配置服务器地址到合法域名
4. 检查防火墙设置

### Q2: 搜索结果为空？
**A**: 
1. 检查数据库中是否有插入初始数据
2. 确保 `status = 1` (启用状态)
3. 尝试用已知的药精名称测试

### Q3: 详情页面加载失败？
**A**:
1. 检查传递的 ID 是否正确
2. 查看浏览器控制台错误日志
3. 确认 API 响应是否正常

### Q4: 如何添加新的药精数据？
**A**:
方式 1 - 数据库直接插入:
```sql
INSERT INTO herb_info (number, name, alias, element, category, category_icon, properties, taste, nature_class, effects, sort, status) 
VALUES ('6-①', '新药精', '别名', '木', '木中木', '🌲', '温', '甘', 'warm', '功效描述', 1, 1);
```

方式 2 - 通过后端管理系统界面 (待开发)

---

## 后续优化建议

1. **数据缓存**: 使用本地存储缓存药精列表数据，减少 API 调用
2. **性能优化**: 分页加载，虚拟列表优化
3. **功能扩展**: 
   - 添加药精收藏功能（需要用户认证）
   - 添加药精评价功能
   - 添加药精推荐功能
4. **管理后台**: 开发药精管理界面，支持增删改查
5. **离线支持**: 支持离线浏览已缓存的药精信息

---

## 技术栈总结

### 后端
- Spring Boot 3.x
- MyBatis-Plus
- MySQL
- JWT 认证
- Spring Security
- Knife4j API 文档

### 前端
- WeChat Mini Program
- JavaScript
- WXML/WXSS
- Pinia (状态管理，可选)

---

## 相关文件列表

### 后端文件
- `/backend/src/main/java/com/wuxing/entity/Herb.java` - 实体类
- `/backend/src/main/java/com/wuxing/mapper/HerbMapper.java` - 数据访问层
- `/backend/src/main/java/com/wuxing/service/HerbService.java` - 服务接口
- `/backend/src/main/java/com/wuxing/service/impl/HerbServiceImpl.java` - 服务实现
- `/backend/src/main/java/com/wuxing/controller/HerbController.java` - 控制器
- `/backend/sql/schema.sql` - 数据库建表语句
- `/backend/sql/data.sql` - 初始数据

### 小程序文件
- `/miniprogram/api/herb.js` - API 接口定义
- `/miniprogram/pages/herb/index.js` - 列表页面逻辑
- `/miniprogram/pages/herb/index.wxml` - 列表页面模板
- `/miniprogram/pages/herb/index.wxss` - 列表页面样式
- `/miniprogram/pages/herb/detail.js` - 详情页面逻辑
- `/miniprogram/pages/herb/detail.wxml` - 详情页面模板
- `/miniprogram/pages/herb/detail.wxss` - 详情页面样式

---

## 联系和支持

如有任何问题或建议，请查阅项目文档或联系开发团队。
