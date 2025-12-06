# 干支纪年历 API 集成文档

## 项目概述

本文档说明如何在后端实现干支纪年历的 API，以及与前端小程序进行对接。

## 后端 API 实现

### 1. 服务层（Service）

**文件位置**: `/backend/src/main/java/com/wuxing/service/EmperorCalendarService.java`

提供以下关键方法：

#### `getYiJiInfo(LocalDate date): YiJiInfo`
- 获取指定日期的宜忌信息
- 返回结构：
  ```json
  {
    "date": "2025-11-25",
    "yi": "合帐 裁衣 嫁娶 安床...",
    "ji": "置产 造船 开光 掘井..."
  }
  ```

#### `getDetailInfo(LocalDate date): DetailInfo`
- 获取指定日期的详细信息（纳音、冲煞等）
- 返回结构：
  ```json
  {
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
  ```

### 2. 控制器层（Controller）

**文件位置**: `/backend/src/main/java/com/wuxing/controller/HomeController.java`

新增 API 端点：

#### `GET /api/home/emperor-calendar/yi-ji`
- 获取宜忌信息
- 参数：`date` (可选，格式 yyyy-MM-dd)
- 返回：宜忌信息对象

#### `GET /api/home/emperor-calendar/detail`
- 获取详细信息
- 参数：`date` (可选，格式 yyyy-MM-dd)
- 返回：详细信息对象

## 前端 API 客户端

### 1. API 模块

**文件位置**: `/miniprogram/api/home.js`

新增导出方法：

#### `getEmperorCalendarYiJi(date): Promise`
```javascript
export const getEmperorCalendarYiJi = (date) => {
  // 调用 GET /api/home/emperor-calendar/yi-ji
  // 返回 Promise<YiJiInfo>
}
```

#### `getEmperorCalendarDetail(date): Promise`
```javascript
export const getEmperorCalendarDetail = (date) => {
  // 调用 GET /api/home/emperor-calendar/detail
  // 返回 Promise<DetailInfo>
}
```

### 2. 页面逻辑

**文件位置**: `/miniprogram/pages/emperor-calendar/index.js`

#### 数据加载流程

```javascript
loadEmperorCalendarInfo(date) {
  const dateStr = formatDate(date);
  
  // 1. 获取干支历信息
  homeApi.getSixtiesCycleInfo(dateStr)
    .then(data => {
      // 更新 ganzhiInfo、selectedDate 等
    });
  
  // 2. 获取农历信息
  homeApi.getLunarInfo(dateStr)
    .then(data => {
      // 更新 lunarInfo
    });
  
  // 3. 获取宜忌信息（新增）
  homeApi.getEmperorCalendarYiJi(dateStr)
    .then(data => {
      // 更新 yijiInfo
    });
  
  // 4. 获取详细信息（新增）
  homeApi.getEmperorCalendarDetail(dateStr)
    .then(data => {
      // 更新 detailInfo
    });
}
```

#### 日期导航

- `onPrevDay()`: 切换到前一天
- `onNextDay()`: 切换到后一天
- `onSelectDate()`: 打开日期选择器选择日期

## 数据流程图

```
用户交互（选择日期）
       ↓
前端 loadEmperorCalendarInfo()
       ↓
并行调用后端 4 个 API：
├─ /api/home/sixties-cycle
├─ /api/home/lunar
├─ /api/home/emperor-calendar/yi-ji (新)
└─ /api/home/emperor-calendar/detail (新)
       ↓
后端使用 TymeCalendarUtils 计算干支纪年历信息
       ↓
返回 JSON 数据到前端
       ↓
前端 setData() 更新页面显示
```

## 页面显示结构

```
┌─────────────────────────────────────────┐
│  顶部导航：📅 干支纪年历                 │
├─────────────────────────────────────────┤
│  日期导航：‹ 2025-11-25 星期二 ›         │
│            十月初六                      │
│            乙巳(蛇)年 丁亥月 戊戌日     │
├─────────────────────────────────────────┤
│  宜：合帐 裁衣 嫁娶 安床...             │
│  忌：置产 造船 开光 掘井...             │
├─────────────────────────────────────────┤
│  时辰吉凶：                              │
│  [23:00-01:00 壬子 凶]  [01:00-03:00... │
│  [03:00-05:00 甲寅 吉]  [05:00-07:00... │
│  ...                                     │
├─────────────────────────────────────────┤
│  详细信息：                              │
│  纳音：平地木                            │
│  冲煞：冲龙 煞北                         │
│  值神：金匮(吉)                          │
│  ...                                     │
└─────────────────────────────────────────┘
```

## 测试 API

### 使用 curl 测试

```bash
# 获取宜忌信息
curl -X GET "http://localhost:8080/api/home/emperor-calendar/yi-ji?date=2025-11-25" \
  -H "Authorization: Bearer {token}"

# 获取详细信息
curl -X GET "http://localhost:8080/api/home/emperor-calendar/detail?date=2025-11-25" \
  -H "Authorization: Bearer {token}"
```

### 使用 Swagger 测试

访问 `http://localhost:8080/swagger-ui.html`，在"首页"模块下找到新增的两个 API 端点进行测试。

## 数据库扩展建议（未来）

当前实现使用计算方式获取宜忌和详细信息。如需存储历史数据，建议创建以下表结构：

```sql
-- 宜忌信息表
CREATE TABLE emperor_calendar_yiji (
  id BIGINT PRIMARY KEY,
  date DATE NOT NULL UNIQUE,
  yi VARCHAR(500),
  ji VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_date (date)
);

-- 详细信息表
CREATE TABLE emperor_calendar_detail (
  id BIGINT PRIMARY KEY,
  date DATE NOT NULL UNIQUE,
  nayin VARCHAR(100),
  chongsha VARCHAR(100),
  zhishen VARCHAR(100),
  jianzhu VARCHAR(100),
  jishen VARCHAR(200),
  taishen VARCHAR(200),
  xiongshen VARCHAR(200),
  xingxiu VARCHAR(100),
  pengzu VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_date (date)
);
```

## 关键技术点

1. **日期处理**: 使用 `LocalDate` 确保时区一致性
2. **干支纪年**: 通过 `TymeCalendarUtils` 计算天干地支
3. **生肖识别**: 从干支年份的地支部分解析生肖
4. **异步并行**: 前端多个 API 并行加载提高性能
5. **降级策略**: 后端 API 失败时使用默认数据保证用户体验

## 未来优化方向

1. 缓存宜忌和详细信息（Redis）
2. 支持用户自定义宜忌数据
3. 添加农历到公历的转换 API
4. 支持时辰吉凶的动态计算
5. 添加二十四节气信息关联
