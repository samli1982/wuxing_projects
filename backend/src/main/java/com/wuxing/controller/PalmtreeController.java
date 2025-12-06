package com.wuxing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tyme.eightchar.EightChar;
import com.tyme.lunar.LunarDay;
import com.tyme.sixtycycle.EarthBranch;
import com.tyme.sixtycycle.HeavenStem;
import com.tyme.solar.SolarDay;
import com.tyme.solar.SolarTime;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.Palmtree;
import com.wuxing.entity.PalmtreeDetail;
import com.wuxing.service.PalmtreeService;
import com.wuxing.service.PalmtreeDetailService;
import com.wuxing.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命盘管理控制器（小程序和后台通用API）
 */
@Slf4j
@Tag(name = "命盘管理")
@RestController
@RequestMapping("/api/palmtree")
@RequiredArgsConstructor
public class PalmtreeController {
    
    private final PalmtreeService palmtreeService;
    private final PalmtreeDetailService palmtreeDetailService;
    private final JwtUtil jwtUtil;
    
    @Operation(summary = "获取当前用户的命盘列表")
    @GetMapping("/list")
    public Result<List<Palmtree>> list(@RequestHeader("Authorization") String authorization) {
        // 从JWT token中获取当前用户ID
        String token = authorization.replace("Bearer ", "");
        Long memberId = jwtUtil.getUserIdFromToken(token);
        
        List<Palmtree> list = palmtreeService.lambdaQuery()
            .eq(Palmtree::getMemberId, memberId)
            .eq(Palmtree::getDeleted, 0)
            .orderByDesc(Palmtree::getCreateTime)
            .list();
        return Result.success(list);
    }
    
    @Operation(summary = "获取当前用户的命盘分页列表")
    @GetMapping("/page")
    public Result<IPage<Palmtree>> page(PageRequest request) {
        // 这里需要从当前登录用户的信息中获取memberId
        // 临时实现：使用hardcoded的memberId（后期需要添加权限验证）
        Long memberId = 1L; // TODO: 从SecurityContext获取当前用户ID
        IPage<Palmtree> page = palmtreeService.pagePalmtreesByMemberId(memberId, request);
        return Result.success(page);
    }
    
    @Operation(summary = "按会员ID分页查询命盘（后台管理用）")
    @GetMapping("/member/{memberId}/page")
    public Result<IPage<Palmtree>> pageByMemberId(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String gender) {
        PageRequest request = new PageRequest();
        request.setCurrent(current);
        request.setSize(size);
        IPage<Palmtree> page = palmtreeService.pagePalmtreesByMemberIdWithFilter(memberId, keyword, gender, request);
        return Result.success(page);
    }
    
    @Operation(summary = "获取命盘详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        Palmtree palmtree = palmtreeService.getById(id);
        PalmtreeDetail detail = palmtreeDetailService.getByPalmtreeId(id);
        
        // 如果kongwang和taiyuan为null，动态计算
        if (detail != null && palmtree != null) {
            if (detail.getKongwang() == null || detail.getKongwang().isEmpty()) {
                // 从年月日时数据创建八字对象并计算
                try {
                    int year = palmtree.getBirthYear();
                    int month = palmtree.getBirthMonth();
                    int day = palmtree.getBirthDay();
                    int hour = palmtree.getBirthHour() != null ? palmtree.getBirthHour() : 0;
                    int minute = palmtree.getBirthMinute() != null ? palmtree.getBirthMinute() : 0;
                    double longitude = palmtree.getBirthLng() != null ? palmtree.getBirthLng() : 120.0;
                    
                    String calendarType = palmtree.getCalendarType();
                    
                    // 计算真太阳时，然后获取八字
                    SolarTime trueSolarTime = calculateTrueSolarTime(year, month, day, hour, minute, longitude, calendarType);
                    EightChar eightChar = trueSolarTime.getLunarHour().getEightChar();
                    
                    String kongwang = calculateKongwang(eightChar);
                    String taiyuan = calculateTaiyuan(eightChar);
                    
                    detail.setKongwang(kongwang);
                    detail.setTaiyuan(taiyuan);
                } catch (Exception e) {
                    log.warn("动态计算空亡和胎元失败", e);
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("palmtree", palmtree);
        result.put("detail", detail);
        
        return Result.success(result);
    }
    
    @Operation(summary = "保存命盘（小程序提交）")
    @PostMapping("/save")
    public Result<Palmtree> save(@RequestHeader("Authorization") String authorization,
                                  @RequestBody Map<String, Object> data) {
        try {
            // 从JWT token中获取当前用户ID
            String token = authorization.replace("Bearer ", "");
            Long memberId = jwtUtil.getUserIdFromToken(token);
            
            log.info("开始保存命盘，会员ID: {}, 数据: {}", memberId, data);
            log.info("birth_hour: {}, birth_minute: {}", data.get("birth_hour"), data.get("birth_minute"));
            
            // 构建Palmtree对象
            Palmtree palmtree = new Palmtree();
            palmtree.setMemberId(memberId);
            palmtree.setNickname((String) data.get("nickname"));
            palmtree.setRealName((String) data.get("name"));
            palmtree.setBirthYear(((Number) data.getOrDefault("birth_year", 0)).intValue());
            palmtree.setBirthMonth(((Number) data.getOrDefault("birth_month", 0)).intValue());
            palmtree.setBirthDay(((Number) data.getOrDefault("birth_day", 0)).intValue());
            
            // 处理时间：优先使用新的birth_hour/birth_minute，向下兼容hour_index
            if (data.containsKey("birth_hour")) {
                palmtree.setBirthHour(((Number) data.get("birth_hour")).intValue());
                palmtree.setBirthMinute(((Number) data.getOrDefault("birth_minute", 0)).intValue());
                // 根据小时计算时辰索引（兼容旧逻辑）
                int hour = palmtree.getBirthHour();
                int hourIndex = (hour == 23) ? 0 : (hour + 1) / 2; // 23点为子时，0-1点为丑时，依此类推
                palmtree.setBirthHourIndex(hourIndex);
            } else if (data.containsKey("hour_index")) {
                // 向下兼容旧的hour_index
                int hourIndex = ((Number) data.get("hour_index")).intValue();
                palmtree.setBirthHourIndex(hourIndex);
                // 时辰转小时（取时辰起始时间）
                int hour = (hourIndex == 0) ? 23 : (hourIndex - 1) * 2 + 1;
                palmtree.setBirthHour(hour);
                palmtree.setBirthMinute(0);
            }
            palmtree.setBirthCity((String) data.getOrDefault("location_city", ""));
            
            // 处理经纬度：如果前端没传，根据城市名称自动获取
            Double lng = data.get("location_lng") != null ? ((Number) data.get("location_lng")).doubleValue() : null;
            Double lat = data.get("location_lat") != null ? ((Number) data.get("location_lat")).doubleValue() : null;
            
            String city = palmtree.getBirthCity();
            if ((lng == null || lat == null) && city != null && !city.isEmpty()) {
                // 根据城市名称获取经纬度
                double[] coords = com.wuxing.util.CityLocationUtil.getCoordinates(city);
                lng = coords[0];
                lat = coords[1];
                log.info("根据城市名称 {} 自动获取经纬度: [{}, {}]", city, lng, lat);
            }
            
            palmtree.setBirthLng(lng);
            palmtree.setBirthLat(lat);
            palmtree.setCalendarType((String) data.getOrDefault("calendar_type", "gregorian"));
            palmtree.setGender((String) data.getOrDefault("gender", "male"));
            palmtree.setForHealthAnalysis(data.get("for_health_analysis") != null ? (Integer) data.get("for_health_analysis") : 1);
            palmtree.setStatus(1);
            
            // 调用八字计算API生成命盘详情
            PalmtreeDetail detail = null;
            try {
                Map<String, Object> baziParams = new HashMap<>();
                baziParams.put("year", palmtree.getBirthYear());
                baziParams.put("month", palmtree.getBirthMonth());
                baziParams.put("day", palmtree.getBirthDay());
                baziParams.put("hour", palmtree.getBirthHour());
                baziParams.put("minute", palmtree.getBirthMinute());
                // 确保经度不为null，否则使用默认值
                baziParams.put("longitude", lng != null ? lng : 120.0);
                baziParams.put("calendar_type", palmtree.getCalendarType());
                baziParams.put("gender", palmtree.getGender());
                
                // 调用BaziController计算
                BaziController baziController = new BaziController();
                Result<Map<String, Object>> baziResult = baziController.calculate(baziParams);
                log.info("八字计算结果: code={}, data={}", baziResult.getCode(), baziResult.getData() != null);
                
                if (baziResult.getCode() == 200 && baziResult.getData() != null) {
                    Map<String, Object> baziData = baziResult.getData();
                    detail = new PalmtreeDetail();
                    
                    // 设置八字数据
                    @SuppressWarnings("unchecked")
                    Map<String, Object> eightData = (Map<String, Object>) baziData.get("eight");
                    if (eightData != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> year = (Map<String, String>) eightData.get("year");
                        @SuppressWarnings("unchecked")
                        Map<String, String> month = (Map<String, String>) eightData.get("month");
                        @SuppressWarnings("unchecked")
                        Map<String, String> day = (Map<String, String>) eightData.get("day");
                        @SuppressWarnings("unchecked")
                        Map<String, String> hour = (Map<String, String>) eightData.get("hour");
                        
                        detail.setYearHeavenlyStem(year.get("heavenly_stem"));
                        detail.setYearEarthlyBranch(year.get("earthly_branch"));
                        detail.setMonthHeavenlyStem(month.get("heavenly_stem"));
                        detail.setMonthEarthlyBranch(month.get("earthly_branch"));
                        detail.setDayHeavenlyStem(day.get("heavenly_stem"));
                        detail.setDayEarthlyBranch(day.get("earthly_branch"));
                        detail.setHourHeavenlyStem(hour.get("heavenly_stem"));
                        detail.setHourEarthlyBranch(hour.get("earthly_branch"));
                        detail.setNayin(year.get("nayin"));
                    }
                    
                    // 设置空亡和胎元
                    detail.setKongwang((String) baziData.get("kongwang"));
                    detail.setTaiyuan((String) baziData.get("taiyuan"));
                    
                    // 设置五行数据
                    detail.setWuxingData(convertToJson(baziData.get("wuxing")));
                    
                    // 设置喜用神数据
                    detail.setUsefulGods(convertToJson(baziData.get("useful_gods")));
                    
                    // 设置体质
                    detail.setConstitutionType((String) baziData.get("constitution"));
                    
                    // 设置五运六气
                    detail.setWuyunliuqiData(convertToJson(baziData.get("wuyunliuqi")));
                    
                    // 设置调理建议
                    detail.setAdjustmentSuggestions(convertToJson(baziData.get("analysis")));
                }
            } catch (Exception e) {
                log.error("计算八字失败，将保存基本信息", e);
                // 不抛出异常，继续保存基本的命盘信息
                log.warn("命盘 {} 的计算详情缺失，仅保存基本信息", palmtree.getNickname());
            }
            
            // 保存命盘及详情
            Palmtree saved = palmtreeService.savePalmtreeWithDetail(palmtree, detail);
            log.info("命盘保存成功，ID: {}, 经纬度: [{}, {}]", saved.getId(), saved.getBirthLng(), saved.getBirthLat());
            return Result.success(saved);
        } catch (Exception e) {
            log.error("保存命盘失败", e);
            return Result.error("保存失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "编辑命盘")
    @PutMapping("/{id}")
    public Result<Void> edit(@PathVariable Long id, @RequestBody Palmtree palmtree) {
        palmtree.setId(id);
        palmtreeService.updateById(palmtree);
        return Result.success();
    }
    
    @Operation(summary = "删除命盘")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        palmtreeService.deletePalmtreeWithDetail(id);
        return Result.success();
    }
    
    @Operation(summary = "批量删除命盘")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            palmtreeService.deletePalmtreeWithDetail(id);
        }
        return Result.success();
    }
    
    @Operation(summary = "批量重新计算命盘")
    @PostMapping("/recalculate")
    public Result<Map<String, Object>> recalculatePalmtrees(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> ids = (List<Object>) params.get("ids");
            
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择至少一个命盘");
            }
            
            // 转换所有ID为Long类型
            List<Long> palmtreeIds = new ArrayList<>();
            for (Object id : ids) {
                if (id instanceof Number) {
                    palmtreeIds.add(((Number) id).longValue());
                } else if (id instanceof String) {
                    palmtreeIds.add(Long.parseLong((String) id));
                }
            }
            
            int successCount = 0;
            int failureCount = 0;
            List<String> errors = new ArrayList<>();
            
            for (Long palmtreeId : palmtreeIds) {
                try {
                    Palmtree palmtree = palmtreeService.getById(palmtreeId);
                    if (palmtree == null) {
                        failureCount++;
                        errors.add("命盘ID " + palmtreeId + " 不存在");
                        continue;
                    }
                    
                    // 调用八字计算API重新计算
                    Map<String, Object> baziParams = new HashMap<>();
                    baziParams.put("year", palmtree.getBirthYear());
                    baziParams.put("month", palmtree.getBirthMonth());
                    baziParams.put("day", palmtree.getBirthDay());
                    baziParams.put("hour", palmtree.getBirthHour() != null ? palmtree.getBirthHour() : 0);
                    baziParams.put("minute", palmtree.getBirthMinute() != null ? palmtree.getBirthMinute() : 0);
                    baziParams.put("longitude", palmtree.getBirthLng() != null ? palmtree.getBirthLng() : 120.0);
                    baziParams.put("calendar_type", palmtree.getCalendarType());
                    baziParams.put("gender", palmtree.getGender());
                    
                    // 调用BaziController的calculate接口
                    com.wuxing.dto.response.Result<?> baziResult = callBaziCalculate(baziParams);
                    
                    if (baziResult != null && baziResult.getCode() == 200 && baziResult.getData() != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> baziData = (Map<String, Object>) baziResult.getData();
                        
                        // 删除旧的详情数据
                        palmtreeDetailService.lambdaUpdate()
                            .eq(com.wuxing.entity.PalmtreeDetail::getPalmtreeId, palmtreeId)
                            .remove();
                        
                        // 创建新的详情记录
                        PalmtreeDetail detail = new PalmtreeDetail();
                        detail.setPalmtreeId(palmtreeId);
                        
                        // 设置八字数据
                        @SuppressWarnings("unchecked")
                        Map<String, Object> eightData = (Map<String, Object>) baziData.get("eight");
                        if (eightData != null) {
                            @SuppressWarnings("unchecked")
                            Map<String, String> year = (Map<String, String>) eightData.get("year");
                            @SuppressWarnings("unchecked")
                            Map<String, String> month = (Map<String, String>) eightData.get("month");
                            @SuppressWarnings("unchecked")
                            Map<String, String> day = (Map<String, String>) eightData.get("day");
                            @SuppressWarnings("unchecked")
                            Map<String, String> hour = (Map<String, String>) eightData.get("hour");
                            
                            detail.setYearHeavenlyStem(year.get("heavenly_stem"));
                            detail.setYearEarthlyBranch(year.get("earthly_branch"));
                            detail.setMonthHeavenlyStem(month.get("heavenly_stem"));
                            detail.setMonthEarthlyBranch(month.get("earthly_branch"));
                            detail.setDayHeavenlyStem(day.get("heavenly_stem"));
                            detail.setDayEarthlyBranch(day.get("earthly_branch"));
                            detail.setHourHeavenlyStem(hour.get("heavenly_stem"));
                            detail.setHourEarthlyBranch(hour.get("earthly_branch"));
                            detail.setNayin(year.get("nayin"));
                        }
                        
                        // 设置空亡和胎元
                        detail.setKongwang((String) baziData.get("kongwang"));
                        detail.setTaiyuan((String) baziData.get("taiyuan"));
                        
                        // 设置五行数据
                        detail.setWuxingData(convertToJson(baziData.get("wuxing")));
                        
                        // 设置喜用神数据
                        detail.setUsefulGods(convertToJson(baziData.get("useful_gods")));
                        
                        // 设置体质
                        detail.setConstitutionType((String) baziData.get("constitution"));
                        
                        // 设置五运六气
                        detail.setWuyunliuqiData(convertToJson(baziData.get("wuyunliuqi")));
                        
                        // 设置调理建议
                        detail.setAdjustmentSuggestions(convertToJson(baziData.get("analysis")));
                        
                        // 保存详情
                        palmtreeDetailService.save(detail);
                        successCount++;
                        log.info("命盘 {} 重新计算成功", palmtreeId);
                    } else {
                        failureCount++;
                        errors.add("命盘ID " + palmtreeId + " 计算失败");
                        log.error("命盘 {} 计算失败", palmtreeId);
                    }
                } catch (Exception e) {
                    failureCount++;
                    errors.add("命盘ID " + palmtreeId + " 异常: " + e.getMessage());
                    log.error("命盘 {} 重新计算异常", palmtreeId, e);
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failureCount", failureCount);
            result.put("totalCount", palmtreeIds.size());
            result.put("errors", errors);
            
            log.info("批量重新计算完成: 成功 {}, 失败 {}", successCount, failureCount);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量重新计算命盘失败", e);
            return Result.error("批量重新计算失败：" + e.getMessage());
        }
    }
    
    /**
     * 调用八字计算API
     */
    @SuppressWarnings("unchecked")
    private com.wuxing.dto.response.Result<?> callBaziCalculate(Map<String, Object> params) {
        try {
            // 直接新建 BaziController 並调用 calculate 方法
            BaziController baziController = new BaziController();
            return baziController.calculate(params);
        } catch (Exception e) {
            log.error("调用八字计算API失败", e);
            return null;
        }
    }
    
    /**
     * 将对象转为JSON字符串
     */
    private String convertToJson(Object obj) {
        if (obj == null) return null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON转换失败", e);
            return null;
        }
    }
    
    /**
     * 计算真太阳时
     */
    private SolarTime calculateTrueSolarTime(int year, int month, int day, int hour, int minute,
                                           double longitude, String calendarType) {
        SolarTime originalTime;
        
        // 处理农历
        if ("lunar".equals(calendarType)) {
            LunarDay lunarDay = LunarDay.fromYmd(year, month, day);
            SolarDay solarDay = lunarDay.getSolarDay();
            originalTime = SolarTime.fromYmdHms(
                solarDay.getYear(), solarDay.getMonth(), solarDay.getDay(),
                hour, minute, 0
            );
        } else {
            originalTime = SolarTime.fromYmdHms(year, month, day, hour, minute, 0);
        }
        
        // 计算经度时差
        double BEIJING_LONGITUDE = 120.0;
        double longitudeDiff = longitude - BEIJING_LONGITUDE;
        int timeDiffSeconds = (int) Math.round(longitudeDiff * 4 * 60);
        
        return originalTime.next(timeDiffSeconds);
    }
    
    /**
     * 计算空亡（孤虚）
     * 根据日干，计算该命主的空亡日期
     * 甲己之日子午空，乙庚之日丑未空，丙辛之日寅申空，丁壬之日卯酉空，戊癸之日辰戌空
     */
    private String calculateKongwang(EightChar eightChar) {
        try {
            HeavenStem dayStem = eightChar.getDay().getHeavenStem();
            int stemIndex = dayStem.getIndex();
            
            // 根据天干索引计算对应的空亡地支
            String[] kongwangMap = {
                "子午空",  // 0-甲
                "丑未空",  // 1-乙
                "寅申空",  // 2-丙
                "卯酉空",  // 3-丁
                "辰戌空",  // 4-戊
                "辰戌空",  // 5-己
                "子午空",  // 6-庚
                "丑未空",  // 7-辛
                "寅申空",  // 8-壬
                "卯酉空"   // 9-癸
            };
            
            return kongwangMap[stemIndex];
        } catch (Exception e) {
            log.warn("计算空亡失败", e);
            return "-";
        }
    }
    
    /**
     * 计算胎元
     * 胎元 = 月天干顺推一位 + 月地支顺推两位
     * 用于判断怀孕期间的忌讳
     */
    private String calculateTaiyuan(EightChar eightChar) {
        try {
            HeavenStem monthStem = eightChar.getMonth().getHeavenStem();
            EarthBranch monthBranch = eightChar.getMonth().getEarthBranch();
            
            // 胎元天干：月干顺推一位
            HeavenStem taiyuanStem = HeavenStem.fromIndex((monthStem.getIndex() + 1) % 10);
            
            // 胎元地支：月支顺推两位
            EarthBranch taiyuanBranch = EarthBranch.fromIndex((monthBranch.getIndex() + 2) % 12);
            
            return taiyuanStem.getName() + taiyuanBranch.getName();
        } catch (Exception e) {
            log.warn("计算胎元失败", e);
            return "-";
        }
    }
}
