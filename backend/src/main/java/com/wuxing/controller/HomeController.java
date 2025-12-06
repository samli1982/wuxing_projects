package com.wuxing.controller;

import com.wuxing.dto.response.Result;
import com.wuxing.service.EmperorCalendarService;
import com.wuxing.utils.ResultUtil;
import com.wuxing.utils.TymeCalendarUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 首页控制器
 * 
 * @author wuxing
 */
@RestController
@RequestMapping("/api/home")
@Tag(name = "首页", description = "首页信息相关接口")
public class HomeController {

    @Autowired
    private EmperorCalendarService emperorCalendarService;

    /**
     * 获取首页完整信息
     */
    @Operation(summary = "获取首页完整信息")
    @GetMapping("/info")
    public Result<Object> getHomeInfo() {
        LocalDate today = LocalDate.now();
        
        return ResultUtil.success(new Object() {
            public final Object headerInfo = new Object() {
                public final String sunriseTime = "05:28";
                public final String lunarDate = "乙巳年四月初八";
                public final String solarTermName = "立夏";
                public final int solarTermDayIndex = 3;
                public final String beidouDirection = "斗柄东南 · 天地火气渐升";
            };
        });
    }

    /**
     * 获取日出日落时间
     *
     * @param date      日期，格式：yyyy-MM-dd，不提供则为今天
     * @param latitude  纬度
     * @param longitude 经度
     * @return 日出日落信息
     */
    @Operation(summary = "获取日出日落时间")
    @GetMapping("/sunrise-sunset")
    public Result<SunriseSunsetInfo> getSunriseSunset(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        
        // 这里是简化的实现，实际可以根据地理位置计算
        // 或调用外部服务（如 OpenWeather API）
        SunriseSunsetInfo info = SunriseSunsetInfo.builder()
                .date(targetDate.toString())
                .sunriseTime("05:28")
                .sunsetTime("18:45")
                .dayLength("13:17")
                .latitude(latitude != null ? latitude : 0.0)
                .longitude(longitude != null ? longitude : 0.0)
                .build();
        
        return ResultUtil.success(info);
    }

    /**
     * 获取节气信息
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 节气信息
     */
    @Operation(summary = "获取节气信息")
    @GetMapping("/solar-term")
    public Result<SolarTermInfo> getSolarTermInfo(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        
        // 使用 Tyme4j 计算节气信息
        TymeCalendarUtils.SolarTermInfo termInfo = TymeCalendarUtils.getSolarTermInfo(targetDate);
        
        SolarTermInfo info = SolarTermInfo.builder()
                .date(targetDate.toString())
                .name(termInfo.name)
                .index(termInfo.month * 2)
                .dayIndex(termInfo.dayIndex)
                .description(termInfo.description)
                .healthAdvice(termInfo.healthAdvice)
                .build();
        
        return ResultUtil.success(info);
    }

    /**
     * 获取农历信息
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 农历信息
     */
    @Operation(summary = "获取农历信息")
    @GetMapping("/lunar")
    public Result<LunarInfo> getLunarInfo(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        
        // 使用 Tyme4j 计算农历信息
        TymeCalendarUtils.LunarInfo lunarInfo = TymeCalendarUtils.getLunarInfo(targetDate);
        
        LunarInfo info = LunarInfo.builder()
                .solarDate(targetDate.toString())
                .lunarYear(lunarInfo.lunarYear)
                .lunarMonth(lunarInfo.lunarMonth)
                .lunarDay(lunarInfo.lunarDay)
                .lunarDate(lunarInfo.lunarDate)
                .chineseZodiac(lunarInfo.chineseZodiac)
                .stem(lunarInfo.stem)
                .branch(lunarInfo.branch)
                .nayin(lunarInfo.nayin)
                .build();
        
        return ResultUtil.success(info);
    }

    /**
     * 获取北斗信息
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 北斗信息
     */
    @Operation(summary = "获取北斗信息")
    @GetMapping("/beidou")
    public Result<BeidouInfo> getBeidouInfo(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        int month = targetDate.getMonthValue();
        
        // 根据月份计算斗柄方向
        String direction = TymeCalendarUtils.calculateBeidouDirection(month);
        
        BeidouInfo info = BeidouInfo.builder()
                .date(targetDate.toString())
                .month(month)
                .direction(direction)
                .meaning("象征季节流转，天地四时更替")
                .build();
        
        return ResultUtil.success(info);
    }

    /**
     * 获取今日养生建议
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 养生建议
     */
    @Operation(summary = "获取今日养生建议")
    @GetMapping("/health-advice")
    public Result<HealthAdviceInfo> getDailyHealthAdvice(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        
        HealthAdviceInfo info = HealthAdviceInfo.builder()
                .date(targetDate.toString())
                .advice("您四柱木弱，今值金运当令，建议佩戴绿植饰品调和。")
                .dietaryAdvice("宜食用黑色食物（黑米、黑豆），忌辛辣燥热")
                .exerciseAdvice("适宜静坐调摄，避免过度出汗")
                .emotionalAdvice("保持平和心态，避免过度思虑")
                .bedtimeAdvice("建议晚间11点前入睡，顺应阴气生长")
                .build();
        
        return ResultUtil.success(info);
    }

    /**
     * 获取干支历信息
     *
     * @param date 日期，格式：yyyy-MM-dd
     * @param dateTime 上下文时间，格式：yyyy-MM-dd HH:mm:ss，不提供则为当前时间。date 和 dateTime 至少提供一个
     * @return 干支历信息
     */
    @Operation(summary = "获取干支历信息")
    @GetMapping("/sixties-cycle")
    public Result<SixtiesCycleInfo> getSixtiesCycleInfo(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String dateTime) {
        LocalDateTime targetDateTime;
        
        if (dateTime != null) {
            // 使用完整的 dateTime 参数
            targetDateTime = LocalDateTime.parse(dateTime.replace(" ", "T"));
        } else if (date != null) {
            // 使用 date 参数，补充当前时间的时刻部分
            LocalDate targetDate = LocalDate.parse(date);
            LocalDateTime now = LocalDateTime.now();
            targetDateTime = LocalDateTime.of(targetDate, now.toLocalTime());
        } else {
            // 都不提供则使用当前时间
            targetDateTime = LocalDateTime.now();
        }
        
        TymeCalendarUtils.SixtyCycleInfo sixtyCycleInfo = TymeCalendarUtils.getSixtyCycleInfo(targetDateTime);
        
        SixtiesCycleInfo info = SixtiesCycleInfo.builder()
                .solarDate(sixtyCycleInfo.solarDate)
                .solarTime(sixtyCycleInfo.solarTime)
                .year(sixtyCycleInfo.year)
                .month(sixtyCycleInfo.month)
                .day(sixtyCycleInfo.day)
                .hour(sixtyCycleInfo.hour)
                .sixtyCycleDate(sixtyCycleInfo.sixtyCycleDate)
                .build();
        
        return ResultUtil.success(info);
    }

    /**
     * 获取干支纪年历信息入口
     */
    @Operation(summary = "干支纪年历入口")
    @GetMapping("/emperor-calendar")
    public Result<Object> getEmperorCalendarEntry() {
        return ResultUtil.success(new Object() {
            public final String entry = "/pages/emperor-calendar/index";
            public final String title = "干支纪年历";
            public final String description = "查看中国历代干支纪年历及其性质、拆分、一览";
        });
    }

    /**
     * 获取干支纪年历的宜忌信息
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 宜忌信息
     */
    @Operation(summary = "获取干支纪年历的宜忌信息")
    @GetMapping("/emperor-calendar/yi-ji")
    public Result<EmperorCalendarService.YiJiInfo> getEmperorCalendarYiJi(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        EmperorCalendarService.YiJiInfo info = emperorCalendarService.getYiJiInfo(targetDate);
        return ResultUtil.success(info);
    }

    /**
     * 获取干支纪年历的详细信息
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 详细信息
     */
    @Operation(summary = "获取干支纪年历的详细信息")
    @GetMapping("/emperor-calendar/detail")
    public Result<EmperorCalendarService.DetailInfo> getEmperorCalendarDetail(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        EmperorCalendarService.DetailInfo info = emperorCalendarService.getDetailInfo(targetDate);
        return ResultUtil.success(info);
    }

    /**
     * 获取干支日期详细信息（建除十二值神、黄道黑道十二神、二十八星宿等）
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 干支日期详细信息
     */
    @Operation(summary = "获取干支日期详细信息")
    @GetMapping("/sixties-cycle/day-info")
    public Result<TymeCalendarUtils.SixtyCycleDayInfo> getSixtyCycleDayInfo(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        TymeCalendarUtils.SixtyCycleDayInfo info = emperorCalendarService.getSixtyCycleDayInfo(targetDate);
        return ResultUtil.success(info);
    }

    /**
     * 获取时辰吉凶信息
     *
     * @param date 日期，格式：yyyy-MM-dd，不提供则为今天
     * @return 时辰吉凶信息
     */
    @Operation(summary = "获取时辰吉凶信息")
    @GetMapping("/emperor-calendar/shichen")
    public Result<EmperorCalendarService.ShiChenInfo> getShiChenInfo(@RequestParam(required = false) String date) {
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        EmperorCalendarService.ShiChenInfo info = emperorCalendarService.getShiChenInfo(targetDate);
        return ResultUtil.success(info);
    }

    /**
     * 日出日落信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SunriseSunsetInfo {
        private String date;          // 日期
        private String sunriseTime;   // 日出时间
        private String sunsetTime;    // 日落时间
        private String dayLength;     // 日长
        private Double latitude;      // 纬度
        private Double longitude;     // 经度
    }

    /**
     * 节气信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SolarTermInfo {
        private String date;          // 日期
        private String name;          // 节气名称
        private int index;            // 节气序号（1-24）
        private int dayIndex;         // 节气内第几天
        private String description;   // 节气描述
        private String healthAdvice;  // 养生建议
    }

    /**
     * 农历信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LunarInfo {
        private String solarDate;     // 公历日期
        private String lunarYear;     // 农历年
        private String lunarMonth;    // 农历月
        private String lunarDay;      // 农历日
        private String lunarDate;     // 完整农历日期
        private String chineseZodiac; // 生肖
        private String stem;          // 天干
        private String branch;        // 地支
        private String nayin;         // 纳音
    }

    /**
     * 北斗信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeidouInfo {
        private String date;          // 日期
        private int month;            // 月份
        private String direction;     // 斗柄方向
        private String meaning;       // 含义
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SixtiesCycleInfo {
        private String solarDate;      // 公历日期（YYYY-MM-DD）
        private String solarTime;      // 公历时间（HH:mm）
        private String year;           // 干支年
        private String month;          // 干支月
        private String day;            // 干支日
        private String hour;           // 干支时
        private String sixtyCycleDate; // 完整干支历
    }

    /**
     * 养生建议
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthAdviceInfo {
        private String date;          // 日期
        private String advice;        // 主要建议
        private String dietaryAdvice; // 饮食建议
        private String exerciseAdvice; // 运动建议
        private String emotionalAdvice; // 情志建议
        private String bedtimeAdvice;  // 睡眠建议
    }
}
