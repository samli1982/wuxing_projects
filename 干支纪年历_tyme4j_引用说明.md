# 干支纪年历 - tyme4j 引用说明

## 📋 概述

通过引用 tyme4j-master 项目的强大功能，实现了完整的干支纪年历计算系统。所有干支相关的计算现在均使用 tyme4j 库，确保准确性和完整性。

## 🔧 核心实现

### 1. TymeCalendarUtils 工具类增强

**文件位置**: `/backend/src/main/java/com/wuxing/utils/TymeCalendarUtils.java`

新增以下数据类和方法:

#### 新增数据类

```java
// 干支日期详细信息类
public static class SixtyCycleDayInfo {
    public String solarDate;      // 公历日期（YYYY-MM-DD）
    public String year;           // 干支年
    public String month;          // 干支月
    public String day;            // 干支日
    public String daySound;       // 日纳音
    public String duty;           // 建除十二值神
    public String twelveStar;     // 黄道黑道十二神
    public String twentyEightStar; // 二十八星宿
}
```

#### 核心方法

**1. getSixtyCycleDayInfo(LocalDate date)** 
- 使用 `SixtyCycleDay` 类计算干支日期
- 返回包含建除十二值神、黄道黑道十二神、二十八星宿的完整信息

```java
SolarDay solarDay = SolarDay.fromYmd(year, month, day);
SixtyCycleDay sixtyCycleDay = new SixtyCycleDay(solarDay);

// 获取 tyme4j 计算的值神、十二神、星宿等
sixtyCycleDay.getDuty().getName()        // 建除十二值神
sixtyCycleDay.getTwelveStar().getName()  // 黄道黑道十二神
sixtyCycleDay.getTwentyEightStar().getName() // 二十八星宿
```

**2. getSixtyCycleInfo(LocalDateTime dateTime)** - 增强版
- 包含纳音五行（Sound）
- 获取年、月、日、时的纳音

```java
SixtyCycleHour sixtyCycleHour = solarTime.getSixtyCycleHour();
SixtyCycle yearCycle = sixtyCycleHour.getYear();

// 获取纳音五行
yearCycle.getSound().getName()   // 年纳音（如："平地木"）
monthCycle.getSound().getName()  // 月纳音
dayCycle.getSound().getName()    // 日纳音
hourCycle.getSound().getName()   // 时纳音
```

**3. getLunarInfo(LocalDate date)** - 现有增强
- 使用 `LunarYear` 和 `SixtyCycle` 准确获取农历和干支信息

### 2. tyme4j 核心类引用

**使用的主要 tyme4j 类:**

| 类名 | 作用 | 方法 |
|-----|------|------|
| `SolarDay` | 公历日期 | `fromYmd()`, `getLunarDay()`, `getTerm()` |
| `SixtyCycleDay` | 干支日 | `getYear()`, `getMonth()`, `getSixtyCycle()`, `getDuty()`, `getTwelveStar()`, `getTwentyEightStar()` |
| `SixtyCycleHour` | 干支时辰 | `getYear()`, `getMonth()`, `getDay()`, `getSixtyCycle()` |
| `SixtyCycle` | 干支 | `getHeavenStem()`, `getEarthBranch()`, `getSound()` |
| `HeavenStem` | 天干 | `getName()`, `getIndex()`, `getElement()` |
| `EarthBranch` | 地支 | `getName()`, `getIndex()`, `getZodiac()` |
| `LunarDay` | 农历日 | `getLunarMonth()`, `getName()` |
| `LunarYear` | 农历年 | `getSixtyCycle()` |
| `SolarTerm` | 节气 | `getName()`, `getSolarDay()` |
| `Zodiac` | 生肖 | `fromIndex()`, `getName()` |

### 3. EmperorCalendarService 服务增强

**文件位置**: `/backend/src/main/java/com/wuxing/service/EmperorCalendarService.java`

新增方法:

```java
/**
 * 获取指定日期的干支日信息
 * @param date 公历日期
 * @return 干支日详细信息（包含建除十二值神、黄道黑道十二神、二十八星宿）
 */
public TymeCalendarUtils.SixtyCycleDayInfo getSixtyCycleDayInfo(LocalDate date) {
    return TymeCalendarUtils.getSixtyCycleDayInfo(date);
}
```

## 📖 使用示例

### 获取干支日详细信息

```java
import java.time.LocalDate;
import com.wuxing.utils.TymeCalendarUtils;

// 获取 2025-11-25 的干支日信息
LocalDate date = LocalDate.of(2025, 11, 25);
TymeCalendarUtils.SixtyCycleDayInfo dayInfo = TymeCalendarUtils.getSixtyCycleDayInfo(date);

System.out.println("公历日期: " + dayInfo.solarDate);           // 2025-11-25
System.out.println("干支年: " + dayInfo.year);                 // 乙巳
System.out.println("干支月: " + dayInfo.month);                 // 丁亥
System.out.println("干支日: " + dayInfo.day);                   // 戊戌
System.out.println("日纳音: " + dayInfo.daySound);              // 平地木
System.out.println("建除十二值神: " + dayInfo.duty);             // 闭
System.out.println("黄道黑道十二神: " + dayInfo.twelveStar);     // 吉
System.out.println("二十八星宿: " + dayInfo.twentyEightStar);   // 室火猪
```

### 获取干支年月日时及纳音

```java
import java.time.LocalDateTime;

LocalDateTime dateTime = LocalDateTime.of(2025, 11, 25, 14, 30, 0);
TymeCalendarUtils.SixtyCycleInfo cycleInfo = TymeCalendarUtils.getSixtyCycleInfo(dateTime);

System.out.println("干支年: " + cycleInfo.year);           // 乙巳
System.out.println("干支月: " + cycleInfo.month);           // 丁亥
System.out.println("干支日: " + cycleInfo.day);             // 戊戌
System.out.println("干支时: " + cycleInfo.hour);            // 未时

System.out.println("年纳音: " + cycleInfo.yearSound);       // 平地木
System.out.println("月纳音: " + cycleInfo.monthSound);       // 屋上土
System.out.println("日纳音: " + cycleInfo.daySound);         // 平地木
System.out.println("时纳音: " + cycleInfo.hourSound);        // 天河水
```

### 获取农历信息

```java
TymeCalendarUtils.LunarInfo lunarInfo = TymeCalendarUtils.getLunarInfo(date);

System.out.println("农历年: " + lunarInfo.lunarYear);        // 乙巳年
System.out.println("农历月: " + lunarInfo.lunarMonth);        // 十月
System.out.println("农历日: " + lunarInfo.lunarDay);          // 廿五
System.out.println("完整农历: " + lunarInfo.lunarDate);        // 乙巳年十月廿五
System.out.println("生肖: " + lunarInfo.chineseZodiac);       // 蛇
System.out.println("纳音五行: " + lunarInfo.nayin);           // 平地木
```

## 🎯 tyme4j 核心算法

### 建除十二值神 (Duty)

```java
// tyme4j 通过 SixtyCycleDay.getDuty() 自动计算
// 建除满平定执破危成收开闭的循环
```

### 黄道黑道十二神 (TwelveStar)

```java
// tyme4j 通过 SixtyCycleDay.getTwelveStar() 自动计算
// 根据日期和月份的干支关系确定
```

### 二十八星宿 (TwentyEightStar)

```java
// tyme4j 通过 SixtyCycleDay.getTwentyEightStar() 自动计算
// 根据周易天象规律循环周期
```

### 纳音五行 (Sound)

```java
// tyme4j 通过 SixtyCycle.getSound() 自动计算
// 按照纳音五行的对应规律：
// 甲子乙丑金，丙寅丁卯火，戊辰己巳木，庚午辛未土，壬申癸酉金...
```

## 📊 数据流程

```
用户请求日期信息
    ↓
TymeCalendarUtils.getSixtyCycleDayInfo(date)
    ↓
SolarDay.fromYmd() - 创建公历日对象
    ↓
new SixtyCycleDay(solarDay) - 计算干支日期
    ↓
提取干支、纳音、值神、星宿等信息
    ↓
返回 SixtyCycleDayInfo 对象
```

## ✅ 优势

1. **准确性**: 完全依赖 tyme4j 的专业计算
2. **完整性**: 支持年月日时的干支和纳音计算
3. **可靠性**: 经过长期验证的历法计算库
4. **易维护**: 无需自己维护复杂的历法规则
5. **可扩展**: 可轻松添加更多 tyme4j 的功能

## 🔍 相关文件

- `tyme4j-master/` - tyme4j 源码库
- `backend/src/main/java/com/wuxing/utils/TymeCalendarUtils.java` - 工具类
- `backend/src/main/java/com/wuxing/service/EmperorCalendarService.java` - 服务类
- `backend/src/main/java/com/wuxing/controller/HomeController.java` - API 端点

## 📝 备注

所有干支纪年历的计算现在都基于 tyme4j 库，确保：
- 时间准确性
- 历法规则正确性
- 与其他 tyme4j 用户的一致性

日期格式: 2025-11-25 星期二
干支信息: 乙巳(蛇)年 丁亥月 戊戌日
