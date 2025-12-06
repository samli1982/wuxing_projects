# 干支纪年历快速启动指南

## 功能概述

实现了干支纪年历页面的完整前后端集成，包括：
- ✅ 日期导航（前后一天切换）
- ✅ 日期选择器（支持公历和农历）
- ✅ 宜忌信息展示
- ✅ 详细信息展示（纳音、冲煞、值神等）
- ✅ 时辰吉凶展示
- ✅ 后端 API 支持

## 文件变更清单

### 后端文件

#### 新建文件
- `backend/src/main/java/com/wuxing/service/EmperorCalendarService.java` - 干支纪年历服务

#### 修改文件
- `backend/src/main/java/com/wuxing/controller/HomeController.java`
  - 添加 `@Autowired` 注入 EmperorCalendarService
  - 添加 2 个新的 API 端点：
    - `GET /api/home/emperor-calendar/yi-ji` - 获取宜忌信息
    - `GET /api/home/emperor-calendar/detail` - 获取详细信息

### 前端文件

#### 修改文件

1. **miniprogram/api/home.js**
   - 添加 `getEmperorCalendarYiJi(date)` 方法
   - 添加 `getEmperorCalendarDetail(date)` 方法

2. **miniprogram/pages/emperor-calendar/index.js**
   - 更新 `loadYiJiInfo()` 方法，调用后端 API
   - 添加错误处理和默认数据降级

3. **miniprogram/pages/emperor-calendar/index.wxml**
   - 调整时辰吉凶的顺序（移到详细信息上面）
   - 添加时间段显示（23:00-01:00 格式）

4. **miniprogram/pages/emperor-calendar/index.wxss**
   - 添加 `.shichen-time` 样式
   - 调整顶部导航样式

## API 端点说明

### 1. 获取宜忌信息

**请求**
```http
GET /api/home/emperor-calendar/yi-ji?date=2025-11-25
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "date": "2025-11-25",
    "yi": "合帐 裁衣 嫁娶 安床 入殓 移柩 破土 造畜稠",
    "ji": "置产 造船 开光 掘井 作灶"
  }
}
```

### 2. 获取详细信息

**请求**
```http
GET /api/home/emperor-calendar/detail?date=2025-11-25
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "date": "2025-11-25",
    "nayin": "平地木",
    "chongsha": "冲龙 煞北",
    "zhishen": "金匮(吉)",
    "jianzhu": "闭",
    "jishen": "益后 金匮",
    "taishen": "房床栖 房内中",
    "xiongshen": "月煞 月虚 血支 五虚 绝阳",
    "xingxiu": "室火猪 吉",
    "pengzu": "戊不受田田主不祥 戌不吃犬作怪上床"
  }
}
```

## 运行步骤

### 1. 启动后端服务

```bash
cd /Users/saml/GoProjects/src/wuxing_projects
./start-backend.sh
```

后端启动后访问 `http://localhost:8080/swagger-ui.html` 查看 API 文档。

### 2. 启动前端开发服务

```bash
cd /Users/saml/GoProjects/src/wuxing_projects/miniprogram
npm install  # 如需要
```

使用微信开发者工具打开小程序，在项目设置中确保 `http://localhost:8080` 可访问。

### 3. 测试页面

在微信小程序中点击首页的"干支纪年历 →"进入页面，验证：
- [ ] 页面初始显示当前日期信息
- [ ] 左右箭头可切换日期
- [ ] 日历图标可打开日期选择器
- [ ] 宜忌信息正确显示
- [ ] 时辰吉凶正确显示
- [ ] 详细信息正确显示

## 关键实现细节

### 服务层计算逻辑

`EmperorCalendarService` 使用以下算法：

1. **冲煞计算**: 根据农历日期模 12 从冲煞数组取值
2. **值神计算**: 根据日期模 12 从值神数组取值
3. **建除十二神**: 根据 (日期-1) % 12 计算
4. **二十八星宿**: 根据 (日期-1) % 28 计算
5. **彭祖百忌**: 根据天干 (日期-1) % 10 计算

### 错误处理

前端在 API 调用失败时会自动使用默认数据，确保用户体验不受影响：

```javascript
homeApi.getEmperorCalendarYiJi(dateStr)
  .then(data => { /* 成功处理 */ })
  .catch(err => { 
    // 使用默认数据
    this.setData({ yijiInfo: defaultData });
  });
```

## 性能优化建议

1. **缓存**: 在微信小程序客户端缓存已加载过的日期数据
2. **预加载**: 预加载前后几天的数据
3. **分页**: 如支持范围查询，实现分页加载
4. **数据库**: 如使用数据库存储，添加日期索引提高查询速度

## 扩展功能计划

- [ ] 支持时辰吉凶根据实时时间动态计算
- [ ] 添加农历到公历的 API 转换
- [ ] 支持用户自定义宜忌数据
- [ ] 添加流年大运相关功能
- [ ] 支持导出日历信息为图片

## 调试技巧

### 查看后端日志

```bash
# 查看服务启动日志
tail -f /Users/saml/GoProjects/src/wuxing_projects/logs/wuxing.log
```

### 测试 API

使用 curl 或 Postman 测试：

```bash
# 测试宜忌 API
curl -X GET "http://localhost:8080/api/home/emperor-calendar/yi-ji?date=2025-11-25" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"

# 测试详细信息 API
curl -X GET "http://localhost:8080/api/home/emperor-calendar/detail?date=2025-11-25" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

### 前端网络调试

在微信小程序开发者工具的"网络"标签页可查看所有 API 请求和响应。

## 常见问题

**Q: API 返回 401 错误**
A: 检查是否已登录，确保在本地存储中有有效的 token。

**Q: 页面显示默认数据而非真实数据**
A: 检查后端服务是否启动，查看前端控制台是否有 API 调用失败的日志。

**Q: 日期切换不工作**
A: 确保已调用 `loadEmperorCalendarInfo()` 方法，检查日期格式是否为 `YYYY-MM-DD`。

## 联系支持

如有问题或建议，请提交 Issue 或 Pull Request。
