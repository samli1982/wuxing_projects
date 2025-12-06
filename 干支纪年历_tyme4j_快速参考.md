# 干支纪年历 - 快速参考

## 🎯 核心 tyme4j 类使用

### 从公历日期获取干支信息

```java
import java.time.LocalDate;
import com.tyme.solar.SolarDay;
import com.tyme.sixtycycle.SixtyCycleDay;

LocalDate date = LocalDate.of(2025, 11, 25);

// 创建公历日对象
SolarDay solarDay = SolarDay.fromYmd(date.getYear(), date.getMonthValue(), date.getDayOfMonth());

// 创建干支日对象（自动计算干支年月日和各种信息）
SixtyCycleDay sixtyCycleDay = new SixtyCycleDay(solarDay);

// 获取干支
String year = sixtyCycleDay.getYear().getName();        // 乙巳
String month = sixtyCycleDay.getMonth().getName();      // 丁亥
String day = sixtyCycleDay.getSixtyCycle().getName();   // 戊戌

// 获取值神和星宿
String duty = sixtyCycleDay.getDuty().getName();        // 闭
String star = sixtyCycleDay.getTwelveStar().getName();  // 吉
String star28 = sixtyCycleDay.getTwentyEightStar().getName(); // 室火猪
```

### 从时间获取干支时辰及纳音

```java
import java.time.LocalDateTime;
import com.tyme.solar.SolarTime;
import com.tyme.sixtycycle.SixtyCycleHour;
import com.tyme.sixtycycle.SixtyCycle;

LocalDateTime dateTime = LocalDateTime.of(2025, 11, 25, 14, 30, 0);

// 创建公历时间对象
SolarTime solarTime = SolarTime.fromYmdHms(
    dateTime.getYear(),
    dateTime.getMonthValue(),
    dateTime.getDayOfMonth(),
    dateTime.getHour(),
    dateTime.getMinute(),
    dateTime.getSecond()
);

// 获取干支时辰
SixtyCycleHour sixtyCycleHour = solarTime.getSixtyCycleHour();

SixtyCycle yearCycle = sixtyCycleHour.getYear();
SixtyCycle monthCycle = sixtyCycleHour.getMonth();
SixtyCycle dayCycle = sixtyCycleHour.getDay();
SixtyCycle hourCycle = sixtyCycleHour.getSixtyCycle();

// 获取干支
String yearStr = yearCycle.getName();   // 乙巳
String monthStr = monthCycle.getName(); // 丁亥
String dayStr = dayCycle.getName();     // 戊戌
String hourStr = hourCycle.getName();   // 未时

// 获取纳音（五行）
String yearSound = yearCycle.getSound().getName();   // 平地木
String monthSound = monthCycle.getSound().getName(); // 屋上土
String daySound = dayCycle.getSound().getName();     // 平地木
String hourSound = hourCycle.getSound().getName();   // 天河水
```

### 从公历获取农历

```java
import com.tyme.lunar.LunarDay;
import com.tyme.lunar.LunarYear;
import com.tyme.sixtycycle.HeavenStem;
import com.tyme.sixtycycle.EarthBranch;

SolarDay solarDay = SolarDay.fromYmd(2025, 11, 25);

// 获取农历日
LunarDay lunarDay = solarDay.getLunarDay();

// 农历月日名称（中文）
String lunarMonth = lunarDay.getLunarMonth().getName(); // 十月
String lunarDay = lunarDay.getName();                   // 廿五

// 获取农历年的干支信息
LunarYear lunarYear = LunarYear.fromYear(lunarDay.getYear());
SixtyCycle yearCycle = lunarYear.getSixtyCycle();
HeavenStem yearStem = yearCycle.getHeavenStem();
EarthBranch yearBranch = yearCycle.getEarthBranch();

String lunarYearStr = yearStem.getName() + yearBranch.getName() + "年"; // 乙巳年
String lunarDateStr = lunarYearStr + lunarMonth + lunarDay;            // 乙巳年十月廿五
```

### 获取生肖

```java
import com.tyme.culture.Zodiac;

// 从地支获取生肖
EarthBranch earthBranch = sixtyCycleDay.getYear().getEarthBranch();
Zodiac zodiac = Zodiac.fromIndex(earthBranch.getIndex());
String animalSign = zodiac.getName(); // 蛇
```

### 获取节气

```java
import com.tyme.solar.SolarTerm;

SolarDay solarDay = SolarDay.fromYmd(2025, 11, 25);

// 获取当前节气
SolarTerm currentTerm = solarDay.getTerm();
String termName = currentTerm.getName(); // 小雪、大雪等

// 获取节气的开始日期
SolarDay termStartDay = currentTerm.getSolarDay();
```

## 📦 常用导入

```java
// 公历
import com.tyme.solar.SolarDay;
import com.tyme.solar.SolarTime;
import com.tyme.solar.SolarTerm;

// 农历
import com.tyme.lunar.LunarDay;
import com.tyme.lunar.LunarMonth;
import com.tyme.lunar.LunarYear;

// 干支相关
import com.tyme.sixtycycle.SixtyCycle;
import com.tyme.sixtycycle.SixtyCycleDay;
import com.tyme.sixtycycle.SixtyCycleHour;
import com.tyme.sixtycycle.HeavenStem;
import com.tyme.sixtycycle.EarthBranch;

// 文化属性
import com.tyme.culture.Zodiac;      // 生肖
import com.tyme.culture.Sound;        // 纳音五行
import com.tyme.culture.Duty;         // 建除十二值神
import com.tyme.culture.star.twelve.TwelveStar;  // 黄道黑道十二神
import com.tyme.culture.star.twentyeight.TwentyEightStar;  // 二十八星宿
```

## 🔑 关键方法对照表

| 需求 | 使用类 | 方法 |
|-----|-------|------|
| 创建公历日 | `SolarDay` | `fromYmd(year, month, day)` |
| 创建公历时间 | `SolarTime` | `fromYmdHms(year, month, day, hour, minute, second)` |
| 获取农历日 | `SolarDay` | `getLunarDay()` |
| 获取农历月 | `LunarDay` | `getLunarMonth()` |
| 获取干支日 | `SolarDay` | `new SixtyCycleDay(solarDay)` |
| 获取干支时辰 | `SolarTime` | `getSixtyCycleHour()` |
| 获取年的干支 | `SixtyCycleDay` | `getYear()` |
| 获取月的干支 | `SixtyCycleDay` | `getMonth()` |
| 获取日的干支 | `SixtyCycleDay` | `getSixtyCycle()` |
| 获取值神 | `SixtyCycleDay` | `getDuty()` |
| 获取十二神 | `SixtyCycleDay` | `getTwelveStar()` |
| 获取二十八星宿 | `SixtyCycleDay` | `getTwentyEightStar()` |
| 获取纳音 | `SixtyCycle` | `getSound()` |
| 获取生肖 | `EarthBranch` | `getZodiac()` |
| 获取节气 | `SolarDay` | `getTerm()` |
| 获取天干名称 | `HeavenStem` | `getName()` |
| 获取地支名称 | `EarthBranch` | `getName()` |

## 💡 实际例子

```java
// 完整示例：获取 2025-11-25 的所有信息
LocalDate date = LocalDate.of(2025, 11, 25);
LocalDateTime dateTime = LocalDateTime.of(2025, 11, 25, 14, 30, 0);

// 1. 公历到干支日期
SolarDay solarDay = SolarDay.fromYmd(2025, 11, 25);
SixtyCycleDay sixtyCycleDay = new SixtyCycleDay(solarDay);

// 2. 公历到农历
LunarDay lunarDay = solarDay.getLunarDay();

// 3. 公历时间到干支时辰及纳音
SolarTime solarTime = SolarTime.fromYmdHms(2025, 11, 25, 14, 30, 0);
SixtyCycleHour sixtyCycleHour = solarTime.getSixtyCycleHour();

// 4. 完整信息输出
System.out.println("公历日期: 2025-11-25 星期二");
System.out.println("农历日期: " + lunarDay.getLunarMonth().getName() + lunarDay.getName());
System.out.println("干支纪年: " + sixtyCycleDay.getYear().getName() + 
                   " " + sixtyCycleDay.getMonth().getName() + 
                   " " + sixtyCycleDay.getSixtyCycle().getName());
System.out.println("纳音五行: " + sixtyCycleDay.getSixtyCycle().getSound().getName());
System.out.println("值神: " + sixtyCycleDay.getDuty().getName());
System.out.println("十二神: " + sixtyCycleDay.getTwelveStar().getName());
System.out.println("星宿: " + sixtyCycleDay.getTwentyEightStar().getName());
System.out.println("时辰干支: " + sixtyCycleHour.getSixtyCycle().getName());
System.out.println("生肖: " + sixtyCycleDay.getYear().getEarthBranch().getZodiac().getName());
```

## ✨ 项目集成

在项目中使用 `TymeCalendarUtils` 工具类调用这些功能：

```java
// 获取干支日信息
TymeCalendarUtils.SixtyCycleDayInfo dayInfo = TymeCalendarUtils.getSixtyCycleDayInfo(date);

// 获取干支时辰及纳音
TymeCalendarUtils.SixtyCycleInfo cycleInfo = TymeCalendarUtils.getSixtyCycleInfo(dateTime);

// 获取农历信息
TymeCalendarUtils.LunarInfo lunarInfo = TymeCalendarUtils.getLunarInfo(date);
```

所有的复杂计算都已通过 tyme4j 的专业算法实现！
