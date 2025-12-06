package com.wuxing.controller;

import com.tyme.solar.SolarDay;
import com.tyme.lunar.LunarDay;
import com.wuxing.dto.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 农历日历控制器
 */
@Slf4j
@Tag(name = "农历日历")
@RestController
@RequestMapping("/api/lunar-calendar")
public class LunarCalendarController {
    
    @Operation(summary = "获取指定年份的农历月份列表")
    @GetMapping("/months/{year}")
    public Result<List<Map<String, Object>>> getMonths(@PathVariable Integer year) {
        try {
            String[] lunarMonthNames = {"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"};
            List<Map<String, Object>> months = new ArrayList<>();
            
            // 遍历该年的所有月份
            // 农历有闰月，最多13个月
            for (int month = 1; month <= 12; month++) {
                // 尝试正常月
                try {
                    LunarDay.fromYmd(year, month, 1);
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("monthValue", month);
                    monthData.put("isLeap", false);
                    monthData.put("name", lunarMonthNames[month - 1]);
                    months.add(monthData);
                } catch (Exception e) {
                    // 跳过无效月份
                }
                
                // 尝试闰月（负数表示闰月）
                try {
                    LunarDay.fromYmd(year, -month, 1);
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("monthValue", month);
                    monthData.put("isLeap", true);
                    monthData.put("name", "闰" + lunarMonthNames[month - 1]);
                    months.add(monthData);
                } catch (Exception e) {
                    // 跳过无效闰月
                }
            }
            
            return Result.success(months);
        } catch (Exception e) {
            log.error("获取农历月份失败: year={}", year, e);
            return Result.error("获取农历月份失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取指定年月的农历日期列表")
    @GetMapping("/days/{year}/{monthValue}/{isLeap}")
    public Result<List<Map<String, Object>>> getDays(
            @PathVariable Integer year,
            @PathVariable Integer monthValue,
            @PathVariable Boolean isLeap) {
        try {
            // 农历月份：负数表示闰月
            int month = isLeap ? -monthValue : monthValue;
            
            List<Map<String, Object>> days = new ArrayList<>();
            
            // 尝试获取该月的天数（29或30）
            int dayCount = 30;
            for (int i = 1; i <= dayCount; i++) {
                try {
                    LunarDay lunarDay = LunarDay.fromYmd(year, month, i);
                    Map<String, Object> dayData = new HashMap<>();
                    dayData.put("day", i); // 日期数字（1-29或30）
                    dayData.put("name", lunarDay.getName()); // 显示名称，如"初一"、"十五"
                    days.add(dayData);
                } catch (Exception e) {
                    // 到达月末，停止遍历
                    break;
                }
            }
            
            return Result.success(days);
        } catch (Exception e) {
            log.error("获取农历日期失败: year={}, monthValue={}, isLeap={}", year, monthValue, isLeap, e);
            return Result.error("获取农历日期失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "农历转公历")
    @GetMapping("/to-solar")
    public Result<Map<String, Object>> lunarToSolar(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam Integer day,
            @RequestParam(defaultValue = "false") Boolean isLeap) {
        try {
            // 农历月份：负数表示闰月
            int lunarMonth = isLeap ? -month : month;
            
            LunarDay lunarDay = LunarDay.fromYmd(year, lunarMonth, day);
            SolarDay solarDay = lunarDay.getSolarDay();
            
            Map<String, Object> result = new HashMap<>();
            result.put("solarYear", solarDay.getYear());
            result.put("solarMonth", solarDay.getMonth());
            result.put("solarDay", solarDay.getDay());
            result.put("lunarYear", year);
            result.put("lunarMonth", month);
            result.put("lunarDay", day);
            result.put("isLeap", isLeap);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("农历转公历失败: year={}, month={}, day={}, isLeap={}", 
                    year, month, day, isLeap, e);
            return Result.error("农历转公历失败: " + e.getMessage());
        }
    }
}
