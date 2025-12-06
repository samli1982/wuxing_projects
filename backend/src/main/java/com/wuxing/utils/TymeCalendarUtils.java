package com.wuxing.utils;

import com.tyme.lunar.LunarDay;
import com.tyme.lunar.LunarYear;
import com.tyme.sixtycycle.SixtyCycle;
import com.tyme.sixtycycle.SixtyCycleDay;
import com.tyme.sixtycycle.SixtyCycleHour;
import com.tyme.sixtycycle.HeavenStem;
import com.tyme.sixtycycle.EarthBranch;
import com.tyme.solar.SolarDay;
import com.tyme.solar.SolarTime;
import com.tyme.solar.SolarTerm;
import com.tyme.culture.Zodiac;
import com.tyme.culture.star.twentyeight.TwentyEightStar;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 中国农历和节气工具类（基于 Tyme4j）
 * 提供完整的干支纪年历信息计算
 * 
 * @author wuxing
 */
public class TymeCalendarUtils {

    /**
     * 农历信息
     */
    public static class LunarInfo {
        public String lunarYear;      // 农历年（如：乙巳年）
        public String lunarMonth;     // 农历月（如：十月）
        public String lunarDay;       // 农历日（如：廿五）
        public String lunarDate;      // 完整农历日期
        public String chineseZodiac;  // 生肖
        public String stem;           // 天干
        public String branch;         // 地支
        public String nayin;          // 纳音五行
        
        public LunarInfo(String lunarYear, String lunarMonth, String lunarDay, 
                        String lunarDate, String chineseZodiac, String stem, 
                        String branch, String nayin) {
            this.lunarYear = lunarYear;
            this.lunarMonth = lunarMonth;
            this.lunarDay = lunarDay;
            this.lunarDate = lunarDate;
            this.chineseZodiac = chineseZodiac;
            this.stem = stem;
            this.branch = branch;
            this.nayin = nayin;
        }
    }

    /**
     * 节气信息
     */
    public static class SolarTermInfo {
        public String name;           // 节气名称
        public int month;             // 月份
        public int day;               // 日期
        public int dayIndex;          // 节气内第几天
        public String description;    // 节气描述
        public String healthAdvice;   // 养生建议
        
        public SolarTermInfo(String name, int month, int day, int dayIndex, 
                            String description, String healthAdvice) {
            this.name = name;
            this.month = month;
            this.day = day;
            this.dayIndex = dayIndex;
            this.description = description;
            this.healthAdvice = healthAdvice;
        }
    }

    /**
     * 干支历（时辰）信息
     */
    public static class SixtyCycleInfo {
        public String solarDate;      // 公历日期（YYYY-MM-DD）
        public String solarTime;      // 公历时间（HH:mm）
        public String year;           // 干支年
        public String month;          // 干支月
        public String day;            // 干支日
        public String hour;           // 干支时
        public String sixtyCycleDate; // 完整干支历
        public String yearSound;      // 年纳音
        public String monthSound;     // 月纳音
        public String daySound;       // 日纳音
        public String hourSound;      // 时纳音
        
        public SixtyCycleInfo(String solarDate, String solarTime, String year, String month, 
                             String day, String hour, String sixtyCycleDate) {
            this.solarDate = solarDate;
            this.solarTime = solarTime;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.sixtyCycleDate = sixtyCycleDate;
        }
        
        public SixtyCycleInfo(String solarDate, String solarTime, String year, String month, 
                             String day, String hour, String sixtyCycleDate,
                             String yearSound, String monthSound, String daySound, String hourSound) {
            this(solarDate, solarTime, year, month, day, hour, sixtyCycleDate);
            this.yearSound = yearSound;
            this.monthSound = monthSound;
            this.daySound = daySound;
            this.hourSound = hourSound;
        }
    }

    /**
     * 干支日期详细信息
     */
    public static class SixtyCycleDayInfo {
        public String solarDate;      // 公历日期（YYYY-MM-DD）
        public String year;           // 干支年
        public String month;          // 干支月
        public String day;            // 干支日
        public String daySound;       // 日纳音
        public String duty;           // 建除十二值神
        public String twelveStar;     // 黄道黑道十二神
        public String twentyEightStar; // 二十八星宿
        
        public SixtyCycleDayInfo(String solarDate, String year, String month, String day,
                                String daySound, String duty, String twelveStar, String twentyEightStar) {
            this.solarDate = solarDate;
            this.year = year;
            this.month = month;
            this.day = day;
            this.daySound = daySound;
            this.duty = duty;
            this.twelveStar = twelveStar;
            this.twentyEightStar = twentyEightStar;
        }
    }

    /**
     * 获取指定日期的农历信息（使用 tyme4j 计算）
     */
    public static LunarInfo getLunarInfo(LocalDate date) {
        try {
            // 使用 Tyme4j 计算农历
            SolarDay solarDay = SolarDay.fromYmd(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            LunarDay lunarDay = solarDay.getLunarDay();
            
            // 获取农历年信息
            LunarYear lunarYear = LunarYear.fromYear(lunarDay.getYear());
            SixtyCycle yearCycle = lunarYear.getSixtyCycle();
            HeavenStem yearStem = yearCycle.getHeavenStem();
            EarthBranch yearBranch = yearCycle.getEarthBranch();
            
            // 农历月份（中文）
            String monthStr = lunarDay.getLunarMonth().getName();
            String dayStr = lunarDay.getName();
            
            String lunarYearStr = yearStem.getName() + yearBranch.getName() + "年";
            String lunarDateStr = lunarYearStr + monthStr + dayStr;
            
            // 生肖
            Zodiac zodiac = Zodiac.fromIndex(yearBranch.getIndex());
            String chineseZodiac = zodiac.getName();
            
            // 天干地支
            String stem = yearStem.getName();
            String branch = yearBranch.getName();
            
            // 纳音五行
            String nayin = yearCycle.getSound().getName();
            
            return new LunarInfo(lunarYearStr, monthStr, dayStr, lunarDateStr,
                    chineseZodiac, stem, branch, nayin);
        } catch (Exception e) {
            throw new RuntimeException("农历计算失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取指定日期的节气信息（使用 tyme4j 计算）
     */
    public static SolarTermInfo getSolarTermInfo(LocalDate date) {
        try {
            SolarDay solarDay = SolarDay.fromYmd(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            
            // 获取当前节气
            SolarTerm currentTerm = solarDay.getTerm();
            
            String termName = currentTerm == null ? "未知" : currentTerm.getName();
            String healthAdvice = getHealthAdvice(termName);
            
            // 计算节气内第几天
            int dayIndex = 1;
            if (currentTerm != null) {
                // 获取当前节气的开始日期
                SolarDay termStartDay = currentTerm.getSolarDay();
                // 计算从节气开始日到目标日期相隔多少天
                // 使用儒略日进行精确计算
                long startJD = Math.round(termStartDay.getJulianDay().getDay());
                long currentJD = Math.round(solarDay.getJulianDay().getDay());
                dayIndex = (int) (currentJD - startJD) + 1;
            }
            
            return new SolarTermInfo(
                    termName,
                    date.getMonthValue(),
                    date.getDayOfMonth(),
                    dayIndex,
                    termName + "：" + healthAdvice,
                    healthAdvice
            );
        } catch (Exception e) {
            throw new RuntimeException("节气计算失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据月份计算北斗方向
     */
    public static String calculateBeidouDirection(int month) {
        return switch (month) {
            case 1, 2 -> "斗柄东北 · 春风拂过，万物萌发";
            case 3, 4, 5 -> "斗柄东南 · 天地火气渐升";
            case 6, 7, 8 -> "斗柄西南 · 盛夏阳气当令";
            case 9, 10 -> "斗柄西北 · 秋高气爽，金气收敛";
            case 11, 12 -> "斗柄北方 · 寒冬水气主令";
            default -> "斗柄指向 · 天地四时流转";
        };
    }

    /**
     * 获取指定时间的干支历信息（包含纳音）- 使用 tyme4j 计算
     */
    public static SixtyCycleInfo getSixtyCycleInfo(LocalDateTime dateTime) {
        try {
            SolarDay solarDay = SolarDay.fromYmd(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
            SolarTime solarTime = SolarTime.fromYmdHms(
                    dateTime.getYear(),
                    dateTime.getMonthValue(),
                    dateTime.getDayOfMonth(),
                    dateTime.getHour(),
                    dateTime.getMinute(),
                    dateTime.getSecond()
            );
            
            // 干支日期信息
            String solarDateStr = String.format("%04d-%02d-%02d",
                    dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
            String solarTimeStr = String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
            
            // 获取干支时辰信息
            SixtyCycleHour sixtyCycleHour = solarTime.getSixtyCycleHour();
            
            // 干支年月日时
            SixtyCycle yearCycle = sixtyCycleHour.getYear();
            SixtyCycle monthCycle = sixtyCycleHour.getMonth();
            SixtyCycle dayCycle = sixtyCycleHour.getDay();
            SixtyCycle hourCycle = sixtyCycleHour.getSixtyCycle();
            
            String yearStr = yearCycle.getName();
            String monthStr = monthCycle.getName();
            String dayStr = dayCycle.getName();
            String hourStr = hourCycle.getName();
            
            // 获取纳音五行
            String yearSound = yearCycle.getSound().getName();
            String monthSound = monthCycle.getSound().getName();
            String daySound = dayCycle.getSound().getName();
            String hourSound = hourCycle.getSound().getName();
            
            String sixtyCycleDateStr = yearStr + " " + monthStr + " " + dayStr + " " + hourStr;
            
            return new SixtyCycleInfo(solarDateStr, solarTimeStr, yearStr, monthStr, dayStr, hourStr, sixtyCycleDateStr,
                    yearSound, monthSound, daySound, hourSound);
        } catch (Exception e) {
            throw new RuntimeException("干支历计算失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取指定公历日期的干支日详细信息 - 使用 tyme4j 计算
     * 包含建除十二值神、黄道黑道十二神、二十八星宿等信息
     */
    public static SixtyCycleDayInfo getSixtyCycleDayInfo(LocalDate date) {
        try {
            SolarDay solarDay = SolarDay.fromYmd(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            SixtyCycleDay sixtyCycleDay = new SixtyCycleDay(solarDay);
            
            // 干支日期
            SixtyCycle yearCycle = sixtyCycleDay.getYear();
            SixtyCycle monthCycle = sixtyCycleDay.getMonth();
            SixtyCycle dayCycle = sixtyCycleDay.getSixtyCycle();
            
            String solarDateStr = String.format("%04d-%02d-%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            
            return new SixtyCycleDayInfo(
                    solarDateStr,
                    yearCycle.getName(),
                    monthCycle.getName(),
                    dayCycle.getName(),
                    dayCycle.getSound().getName(),
                    sixtyCycleDay.getDuty().getName(),
                    sixtyCycleDay.getTwelveStar().getName(),
                    sixtyCycleDay.getTwentyEightStar().getName()
            );
        } catch (Exception e) {
            throw new RuntimeException("干支日计算失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据节气名称获取养生建议
     */
    private static String getHealthAdvice(String termName) {
        return switch (termName) {
            case "小寒" -> "寒冷如冰，宜温阳补气";
            case "大寒" -> "严寒将至，宜静养";
            case "立春" -> "春风拂过，万物萌发";
            case "雨水" -> "春雨初降，宜调理脾胃";
            case "惊蛰" -> "虫类惊醒，宜祛湿";
            case "春分" -> "昼夜均分，宜调和阴阳";
            case "清明" -> "清气上升，万物生长";
            case "谷雨" -> "雨水充足，宜养阴";
            case "立夏" -> "天地火气渐升，宜养阴润燥";
            case "小满" -> "物始充盈，宜健脾";
            case "芒种" -> "麦粒始熟，宜祛湿";
            case "夏至" -> "阳气最旺，宜养心";
            case "小暑" -> "暑气渐盛，宜清补";
            case "大暑" -> "暑气最盛，宜补阴";
            case "立秋" -> "秋气渐起，宜润肺";
            case "处暑" -> "暑气渐消，宜养阴";
            case "白露" -> "露珠初现，宜养肺";
            case "秋分" -> "昼夜均分，宜调理";
            case "寒露" -> "天气转寒，宜温阳";
            case "霜降" -> "初霜始降，宜进补";
            case "立冬" -> "冬季开始，宜温阳";
            case "小雪" -> "初雪始降，宜补肾";
            case "大雪" -> "降雪增多，宜温阳";
            case "冬至" -> "阳气始生，宜进补";
            default -> "宜调理身体";
        };
    }
}
