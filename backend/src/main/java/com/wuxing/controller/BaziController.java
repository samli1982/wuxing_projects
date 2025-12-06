package com.wuxing.controller;

import com.tyme.eightchar.EightChar;
import com.tyme.lunar.LunarDay;
import com.tyme.sixtycycle.EarthBranch;
import com.tyme.sixtycycle.HeavenStem;
import com.tyme.solar.SolarDay;
import com.tyme.solar.SolarTime;
import com.wuxing.dto.response.Result;
import com.wuxing.utils.WuyunUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 八字命盘计算控制器
 */
@Slf4j
@Tag(name = "八字命盘计算")
@RestController
@RequestMapping("/api/bazi")
public class BaziController {
    
    private static final double BEIJING_LONGITUDE = 120.0;
    
    /**
     * 计算完整命盘
     */
    @Operation(summary = "计算完整命盘（四柱八字+五行+五运六气+体质）")
    @PostMapping("/calculate")
    public Result<Map<String, Object>> calculate(@RequestBody Map<String, Object> params) {
        try {
            // 解析参数
            int year = ((Number) params.get("year")).intValue();
            int month = ((Number) params.get("month")).intValue();
            int day = ((Number) params.get("day")).intValue();
            int hour = ((Number) params.get("hour")).intValue();
            int minute = ((Number) params.getOrDefault("minute", 0)).intValue();
            double longitude = ((Number) params.getOrDefault("longitude", BEIJING_LONGITUDE)).doubleValue();
            String calendarType = (String) params.getOrDefault("calendar_type", "gregorian");
            String gender = (String) params.getOrDefault("gender", "male");
            
            // 计算真太阳时
            SolarTime trueSolarTime = calculateTrueSolarTime(year, month, day, hour, minute, longitude, calendarType);
            
            log.info("计算八字 - 真太阳时: {}年{}月{}日{}时{}分", 
                trueSolarTime.getYear(), trueSolarTime.getMonth(), trueSolarTime.getDay(), 
                trueSolarTime.getHour(), trueSolarTime.getMinute());
            
            // 生成八字
            EightChar eightChar = trueSolarTime.getLunarHour().getEightChar();
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            
            // 1. 四柱八字
            result.put("eight", buildEightChar(eightChar));
            
            // 1.1 计算空亡和胎元
            result.put("kongwang", calculateKongwang(eightChar));
            result.put("taiyuan", calculateTaiyuan(eightChar));
            
            // 2. 五行强弱分布
            Map<String, Integer> wuxingData = calculateWuxing(eightChar);
            result.put("wuxing", wuxingData);
            
            // 2.1 计算喜用神
            Map<String, Object> usefulGodsData = calculateUsefulGods(wuxingData);
            result.put("useful_gods", usefulGodsData);
            
            // 3. 五运六气
            result.put("wuyunliuqi", calculateWuyunliuqi(trueSolarTime.getYear()));
            
            // 4. 体质判定
            result.put("constitution", determineConstitution(eightChar, gender));
            
            // 5. 调理建议
            result.put("analysis", generateAdvice(eightChar, gender));
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("计算命盘失败", e);
            return Result.error("计算失败：" + e.getMessage());
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
        double longitudeDiff = longitude - BEIJING_LONGITUDE;
        int timeDiffSeconds = (int) Math.round(longitudeDiff * 4 * 60);
        
        return originalTime.next(timeDiffSeconds);
    }
    
    /**
     * 构建八字数据
     */
    private Map<String, Object> buildEightChar(EightChar eightChar) {
        Map<String, Object> eight = new LinkedHashMap<>();
        
        // 年柱
        Map<String, String> yearPillar = new LinkedHashMap<>();
        yearPillar.put("heavenly_stem", eightChar.getYear().getHeavenStem().getName());
        yearPillar.put("earthly_branch", eightChar.getYear().getEarthBranch().getName());
        yearPillar.put("nayin", eightChar.getYear().getSound().getName());
        eight.put("year", yearPillar);
        
        // 月柱
        Map<String, String> monthPillar = new LinkedHashMap<>();
        monthPillar.put("heavenly_stem", eightChar.getMonth().getHeavenStem().getName());
        monthPillar.put("earthly_branch", eightChar.getMonth().getEarthBranch().getName());
        monthPillar.put("nayin", eightChar.getMonth().getSound().getName());
        eight.put("month", monthPillar);
        
        // 日柱
        Map<String, String> dayPillar = new LinkedHashMap<>();
        dayPillar.put("heavenly_stem", eightChar.getDay().getHeavenStem().getName());
        dayPillar.put("earthly_branch", eightChar.getDay().getEarthBranch().getName());
        dayPillar.put("nayin", eightChar.getDay().getSound().getName());
        eight.put("day", dayPillar);
        
        // 时柱
        Map<String, String> hourPillar = new LinkedHashMap<>();
        hourPillar.put("heavenly_stem", eightChar.getHour().getHeavenStem().getName());
        hourPillar.put("earthly_branch", eightChar.getHour().getEarthBranch().getName());
        hourPillar.put("nayin", eightChar.getHour().getSound().getName());
        eight.put("hour", hourPillar);
        
        return eight;
    }
    
    /**
     * 计算五行强弱分布
     */
    private Map<String, Integer> calculateWuxing(EightChar eightChar) {
        Map<String, Integer> wuxing = new LinkedHashMap<>();
        wuxing.put("木", 0);
        wuxing.put("火", 0);
        wuxing.put("土", 0);
        wuxing.put("金", 0);
        wuxing.put("水", 0);
        
        // 统计天干五行
        addWuxing(wuxing, eightChar.getYear().getHeavenStem().getElement().getName());
        addWuxing(wuxing, eightChar.getMonth().getHeavenStem().getElement().getName());
        addWuxing(wuxing, eightChar.getDay().getHeavenStem().getElement().getName());
        addWuxing(wuxing, eightChar.getHour().getHeavenStem().getElement().getName());
        
        // 统计地支五行
        addWuxing(wuxing, eightChar.getYear().getEarthBranch().getElement().getName());
        addWuxing(wuxing, eightChar.getMonth().getEarthBranch().getElement().getName());
        addWuxing(wuxing, eightChar.getDay().getEarthBranch().getElement().getName());
        addWuxing(wuxing, eightChar.getHour().getEarthBranch().getElement().getName());
        
        return wuxing;
    }
    
    /**
     * 计算喜用神（补弱抑强原则）
     */
    private Map<String, Object> calculateUsefulGods(Map<String, Integer> wuxing) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // 找出最弱的五行
        int minCount = Collections.min(wuxing.values());
        List<String> weakElements = wuxing.entrySet().stream()
            .filter(e -> e.getValue() == minCount)
            .map(Map.Entry::getKey)
            .toList();
        
        // 五行相生反向：谁生我
        Map<String, String> beShengMap = new LinkedHashMap<>();
        beShengMap.put("木", "水");
        beShengMap.put("火", "木");
        beShengMap.put("土", "火");
        beShengMap.put("金", "土");
        beShengMap.put("水", "金");
        
        // 喜用神策略：补充最弱的五行及其生我的五行
        List<String> usefulGodsList = new ArrayList<>();
        for (String weak : weakElements) {
            if (!usefulGodsList.contains(weak)) {
                usefulGodsList.add(weak);
            }
            String parent = beShengMap.get(weak);
            if (parent != null && !usefulGodsList.contains(parent)) {
                usefulGodsList.add(parent);
            }
        }
        
        result.put("names", usefulGodsList);
        result.put("count", usefulGodsList.size());
        
        return result;
    }
    
    /**
     * 计算五运六气
     * 使用 WuyunUtils 进行动态计算，确保司天和在泉正确
     */
    private Map<String, Object> calculateWuyunliuqi(int year) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", year);
        
        // 使用 WuyunUtils 计算五运六气信息
        com.wuxing.utils.WuyunUtils.WuyunInfo wuyunInfo = com.wuxing.utils.WuyunUtils.calculateWuyun(year);
        
        // 转换五运为运气名称
        String transportation = convertYunToTransportation(wuyunInfo.getWuyun(), wuyunInfo.getStem());
        result.put("transportation", transportation);
        
        // 司天和在泉
        String tiansi = wuyunInfo.getSitian();
        String zaiquan = wuyunInfo.getZaiquan();
        result.put("tiansi", tiansi + "司天");
        result.put("zaiquan", zaiquan + "在泉");
        
        // 生成气候特征和健康提示
        String climate = generateClimate(tiansi);
        List<String> healthWarnings = generateHealthWarnings(tiansi, zaiquan);
        
        result.put("climate", climate);
        result.put("health_warnings", healthWarnings);
        
        return result;
    }
    
    /**
     * 将五行转换为运气名称
     */
    private String convertYunToTransportation(String wuyun, String stem) {
        // 根据干支五子运理论，每个天干对应的运气
        Map<String, String> stemToYun = new LinkedHashMap<>();
        stemToYun.put("甲", "木运太过");
        stemToYun.put("乙", "木运不及");
        stemToYun.put("丙", "火运太过");
        stemToYun.put("丁", "火运不及");
        stemToYun.put("戊", "土运太过");
        stemToYun.put("己", "土运不及");
        stemToYun.put("庚", "金运太过");
        stemToYun.put("辛", "金运不及");
        stemToYun.put("壬", "水运太过");
        stemToYun.put("癸", "水运不及");
        
        return stemToYun.getOrDefault(stem, "未知");
    }
    
    /**
     * 根据司天生成气候特征
     */
    private String generateClimate(String tiansi) {
        if (tiansi.contains("火")) {
            return "上半年火热偏盛，气候炎热";
        } else if (tiansi.contains("水")) {
            return "上半年寒凉潮湿，易伤阳气";
        } else if (tiansi.contains("木")) {
            return "上半年风气主令，易动肝阳";
        } else if (tiansi.contains("土")) {
            return "上半年湿土太过，易困脾胃";
        } else {
            return "上半年燥金之气，易伤肺津";
        }
    }
    
    /**
     * 根据司天和在泉生成健康提示
     */
    private List<String> generateHealthWarnings(String tiansi, String zaiquan) {
        List<String> warnings = new ArrayList<>();
        
        // 上半年提示（司天）
        if (tiansi.contains("火")) {
            warnings.add("上半年应防暑降温，多饮水滋阴");
            warnings.add("避免过度曝晒，防范热伤元气");
        } else if (tiansi.contains("水")) {
            warnings.add("上半年注意保暖，防寒邪侵袭");
            warnings.add("勿过食生冷，适当温阳补阳");
        } else if (tiansi.contains("木")) {
            warnings.add("上半年应顺时调理，防范肝阳上亢");
            warnings.add("保持情绪平和，适当户外活动");
        } else if (tiansi.contains("土")) {
            warnings.add("上半年健脾祛湿，少食甘肥厚腻");
            warnings.add("适当运动，促进脾阳化湿");
        } else {
            warnings.add("上半年滋阴润肺，防范咽喉干燥");
            warnings.add("少食辛辣刺激，多食白色滋润食物");
        }
        
        // 下半年提示（在泉）
        if (zaiquan.contains("火")) {
            warnings.add("下半年炎热继续，需继续防暑");
        } else if (zaiquan.contains("水")) {
            warnings.add("下半年寒湿加重，需温阳祛湿");
        } else if (zaiquan.contains("木")) {
            warnings.add("下半年风邪仍盛，需继续调肝");
        } else if (zaiquan.contains("土")) {
            warnings.add("下半年湿气困脾，需健脾益气");
        } else {
            warnings.add("下半年燥气转盛，需继续润肺");
        }
        
        return warnings;
    }
    
    /**
     * 体质判定
     */
    private String determineConstitution(EightChar eightChar, String gender) {
        Map<String, Integer> wuxing = calculateWuxing(eightChar);
        
        // 找出最强和最弱的五行
        int maxCount = Collections.max(wuxing.values());
        int minCount = Collections.min(wuxing.values());
        
        String maxElement = wuxing.entrySet().stream()
            .filter(e -> e.getValue() == maxCount)
            .map(Map.Entry::getKey)
            .findFirst().orElse("");
        
        String minElement = wuxing.entrySet().stream()
            .filter(e -> e.getValue() == minCount)
            .map(Map.Entry::getKey)
            .findFirst().orElse("");
        
        // 体质判定逻辑（简化版）
        if (minElement.equals("水")) {
            return "阴虚质";
        } else if (minElement.equals("火")) {
            return "阳虚质";
        } else if (maxElement.equals("水")) {
            return "痰湿质";
        } else if (maxElement.equals("木")) {
            return "气郁质";
        } else if (maxElement.equals("火")) {
            return "湿热质";
        } else {
            return "平和质";
        }
    }
    
    /**
     * 生成调理建议
     */
    private Map<String, Object> generateAdvice(EightChar eightChar, String gender) {
        String constitution = determineConstitution(eightChar, gender);
        Map<String, Integer> wuxing = calculateWuxing(eightChar);
        return com.wuxing.util.AdviceGenerator.generateAdvice(eightChar, constitution, wuxing);
    }
    
    /**
     * 添加五行统计
     */
    private void addWuxing(Map<String, Integer> wuxing, String element) {
        if (element == null) return;
        wuxing.put(element, wuxing.getOrDefault(element, 0) + 1);
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
