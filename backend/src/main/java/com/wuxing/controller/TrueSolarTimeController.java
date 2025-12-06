package com.wuxing.controller;

import com.wuxing.dto.response.Result;
import com.tyme.solar.SolarTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 真太阳时计算控制器
 */
@Slf4j
@Tag(name = "真太阳时计算")
@RestController
@RequestMapping("/api/true-solar-time")
public class TrueSolarTimeController {
    
    // 标准时区东经120°（北京时间基准）
    private static final double BEIJING_LONGITUDE = 120.0;
    
    /**
     * 计算真太阳时
     * @param params 包含year, month, day, hour, minute, longitude, city, calendar_type(可选)
     * @return 真太阳时数据
     */
    @Operation(summary = "计算真太阳时")
    @PostMapping("/calculate")
    public Result<Map<String, Object>> calculate(@RequestBody Map<String, Object> params) {
        try {
            int year = ((Number) params.get("year")).intValue();
            int month = ((Number) params.get("month")).intValue();
            int day = ((Number) params.get("day")).intValue();
            int hour = ((Number) params.get("hour")).intValue();
            int minute = ((Number) params.getOrDefault("minute", 0)).intValue();
            double longitude = ((Number) params.get("longitude")).doubleValue();
            String city = (String) params.getOrDefault("city", "未知");
            String calendarType = (String) params.getOrDefault("calendar_type", "gregorian"); // 默认公历
            
            // 如果是农历，先转换成公历
            SolarTime originalTime;
            if ("lunar".equals(calendarType)) {
                // 使用tyme4j将农历转换为公历
                com.tyme.lunar.LunarDay lunarDay = com.tyme.lunar.LunarDay.fromYmd(year, month, day);
                com.tyme.solar.SolarDay solarDay = lunarDay.getSolarDay();
                originalTime = SolarTime.fromYmdHms(
                    solarDay.getYear(), 
                    solarDay.getMonth(), 
                    solarDay.getDay(), 
                    hour, 
                    minute, 
                    0
                );
                log.info("农历{}\u5e74{}\u6708{}\u65e5 -> \u516c\u5386{}\u5e74{}\u6708{}\u65e5", 
                    year, month, day,
                    solarDay.getYear(), solarDay.getMonth(), solarDay.getDay());
            } else {
                // 公历直接创建
                originalTime = SolarTime.fromYmdHms(year, month, day, hour, minute, 0);
            }
            
            // 计算经度差
            double longitudeDiff = longitude - BEIJING_LONGITUDE;
            
            // 经度时差（分钟）：每15度1小时，即每度4分钟
            int timeDiffMinutes = (int) Math.round(longitudeDiff * 4);
            
            // 计算真太阳时（推移时差秒数）
            SolarTime trueSolarTime = originalTime.next(timeDiffMinutes * 60);
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            
            // 原始时间
            Map<String, Object> original = new HashMap<>();
            original.put("year", year);
            original.put("month", month);
            original.put("day", day);
            original.put("hour", hour);
            original.put("minute", minute);
            original.put("formatted", String.format("%04d年%02d月%02d日%02d时%02d分", year, month, day, hour, minute));
            result.put("original", original);
            
            // 真太阳时
            Map<String, Object> trueSolar = new HashMap<>();
            trueSolar.put("year", trueSolarTime.getYear());
            trueSolar.put("month", trueSolarTime.getMonth());
            trueSolar.put("day", trueSolarTime.getDay());
            trueSolar.put("hour", trueSolarTime.getHour());
            trueSolar.put("minute", trueSolarTime.getMinute());
            trueSolar.put("formatted", String.format("%04d年%02d月%02d日%02d时%02d分（%s）", 
                trueSolarTime.getYear(), 
                trueSolarTime.getMonth(), 
                trueSolarTime.getDay(), 
                trueSolarTime.getHour(), 
                trueSolarTime.getMinute(),
                city));
            result.put("trueSolar", trueSolar);
            
            // 时差信息
            result.put("timeDiffMinutes", timeDiffMinutes);
            result.put("city", city);
            result.put("longitude", longitude);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("计算真太阳时失败", e);
            return Result.error("计算失败：" + e.getMessage());
        }
    }
}
