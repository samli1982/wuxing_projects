package com.wuxing.controller;

import com.wuxing.dto.response.Result;
import com.wuxing.utils.ResultUtil;
import com.wuxing.utils.WuyunUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 五运六气控制器
 * 
 * @author wuxing
 */
@Tag(name = "五运六气", description = "五运六气计算相关接口")
@RestController
@RequestMapping("/api/wuyun")
@RequiredArgsConstructor
public class WuyunController {

    /**
     * 根据年份计算五运六气
     *
     * @param year 公历年份，如2025
     * @return 五运六气信息
     */
    @Operation(summary = "计算年份五运六气")
    @GetMapping("/{year}")
    public Result<WuyunUtils.WuyunInfo> calculateWuyun(@PathVariable int year) {
        WuyunUtils.WuyunInfo wuyunInfo = WuyunUtils.calculateWuyun(year);
        return ResultUtil.success(wuyunInfo);
    }

    /**
     * 批量计算多个年份的五运六气
     *
     * @param startYear 起始年份
     * @param endYear   结束年份
     * @return 多个年份的五运六气信息列表
     */
    @Operation(summary = "批量计算年份五运六气")
    @GetMapping("/range")
    public Result<Object> calculateWuyunRange(
            @RequestParam int startYear,
            @RequestParam int endYear) {
        
        if (startYear > endYear) {
            return ResultUtil.error("起始年份不能大于结束年份");
        }
        
        if (endYear - startYear > 100) {
            return ResultUtil.error("查询范围不能超过100年");
        }

        java.util.List<WuyunUtils.WuyunInfo> results = new java.util.ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            results.add(WuyunUtils.calculateWuyun(year));
        }

        return ResultUtil.success(results);
    }

    /**
     * 获取年份的天干地支
     *
     * @param year 公历年份
     * @return 天干地支信息
     */
    @Operation(summary = "获取年份天干地支")
    @GetMapping("/{year}/ganzhi")
    public Result<Map<String, Object>> getGanzhi(@PathVariable int year) {
        String ganzhi = WuyunUtils.getGanzhi(year);
        String nayin = WuyunUtils.getNayin(year);
        
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("year", year);
        data.put("ganzhi", ganzhi);
        data.put("nayin", nayin);
        
        return ResultUtil.success(data);
    }

    /**
     * 获取年份的五运
     *
     * @param year 公历年份
     * @return 五运信息
     */
    @Operation(summary = "获取年份五运")
    @GetMapping("/{year}/wuyun")
    public Result<Map<String, Object>> getWuyun(@PathVariable int year) {
        String ganzhi = WuyunUtils.getGanzhi(year);
        String stem = String.valueOf(ganzhi.charAt(0));
        String wuyun = WuyunUtils.getWuyun(stem);
        
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("year", year);
        data.put("ganzhi", ganzhi);
        data.put("stem", stem);
        data.put("wuyun", wuyun);
        
        return ResultUtil.success(data);
    }

    /**
     * 获取年份的司天和在泉
     *
     * @param year 公历年份
     * @return 司天和在泉信息
     */
    @Operation(summary = "获取年份司天和在泉")
    @GetMapping("/{year}/sitian-zaiquan")
    public Result<Map<String, Object>> getSiTianZaiQuan(@PathVariable int year) {
        String[] siTianZaiQuan = WuyunUtils.getSiTianZaiQuan(year);
        
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("year", year);
        data.put("sitian", siTianZaiQuan[0]);
        data.put("zaiquan", siTianZaiQuan[1]);
        
        return ResultUtil.success(data);
    }

    /**
     * 健康建议接口（基于五运六气）
     *
     * @param year 公历年份
     * @return 健康建议
     */
    @Operation(summary = "获取年份健康建议")
    @GetMapping("/{year}/health-tips")
    public Result<Map<String, Object>> getHealthTips(@PathVariable int year) {
        WuyunUtils.WuyunInfo wuyunInfo = WuyunUtils.calculateWuyun(year);
        
        java.util.List<String> tips = new java.util.ArrayList<>();
        
        // 根据五运生成健康建议
        String wuyun = wuyunInfo.getWuyun();
        switch (wuyun) {
            case "木":
                tips.add("肝木当令，易因七情所伤");
                tips.add("宜静坐调摄，避免过度思虑");
                tips.add("适宜食用酸味食物");
                break;
            case "火":
                tips.add("心火旺盛，易心烦失眠");
                tips.add("宜保持心态平和，避免过度兴奋");
                tips.add("适宜食用苦味食物");
                break;
            case "土":
                tips.add("脾土主运，易湿气困脾");
                tips.add("宜健脾祛湿，忌生冷油腻");
                tips.add("适宜食用甘味食物");
                break;
            case "金":
                tips.add("肺金偏旺，防呼吸道不适");
                tips.add("宜养阴润燥，忌辛辣燥热");
                tips.add("适宜食用辛味食物");
                break;
            case "水":
                tips.add("肾水虚弱，易腰膝酸软");
                tips.add("宜温阳补肾，避免过度劳累");
                tips.add("适宜食用咸味食物");
                break;
        }

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("year", year);
        data.put("wuyun", wuyunInfo.getWuyun());
        data.put("sitian", wuyunInfo.getSitian());
        data.put("zaiquan", wuyunInfo.getZaiquan());
        data.put("healthTips", tips);
        
        return ResultUtil.success(data);
    }
    
    /**
     * 获取完整的五运六气信息（包含气候特征和健康提示）
     * 用于首页展示
     *
     * @param year 公历年份
     * @return 完整的五运六气数据
     */
    @Operation(summary = "获取完整五运六气信息")
    @GetMapping("/{year}/complete")
    public Result<Map<String, Object>> getCompleteWuyun(@PathVariable int year) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", year);
        
        // 计算五运六气基础信息
        WuyunUtils.WuyunInfo wuyunInfo = WuyunUtils.calculateWuyun(year);
        
        // 转换五运为运气名称
        String transportation = convertYunToTransportation(wuyunInfo.getWuyun(), wuyunInfo.getStem());
        result.put("transportation", transportation);
        result.put("ganzhi", wuyunInfo.getGanzhi());
        result.put("nayin", wuyunInfo.getNayin());
        
        // 司天和在泉
        String tiansi = wuyunInfo.getSitian();
        String zaiquan = wuyunInfo.getZaiquan();
        result.put("sitian", tiansi);
        result.put("zaiquan", zaiquan);
        
        // 生成气候特征和健康提示
        String climate = generateClimate(tiansi);
        List<String> healthWarnings = generateHealthWarnings(tiansi, zaiquan);
        
        result.put("climate", climate);
        result.put("health_warnings", healthWarnings);
        
        return ResultUtil.success(result);
    }
    
    /**
     * 将五行转换为运气名称
     */
    private String convertYunToTransportation(String wuyun, String stem) {
        Map<String, String> stemToYun = new LinkedHashMap<>();
        stemToYun.put("甲", "土运太过");
        stemToYun.put("乙", "金运不及");
        stemToYun.put("丙", "水运太过");
        stemToYun.put("丁", "木运不及");
        stemToYun.put("戊", "火运太过");
        stemToYun.put("己", "土运不及");
        stemToYun.put("庚", "金运太过");
        stemToYun.put("辛", "金运不及");
        stemToYun.put("壬", "水运太过");
        stemToYun.put("癸", "火运不及");
        
        return stemToYun.getOrDefault(stem, "未知");
    }
    
    /**
     * 根据司天生成气候特征
     */
    private String generateClimate(String tiansi) {
        if (tiansi.contains("火")) {
            return "上半年火热偏盛，气候炎热；下半年阳气渐收，秋凉渐起";
        } else if (tiansi.contains("水")) {
            return "上半年寒凉潮湿，易伤阳气；下半年寒气加重，需温阳祛湿";
        } else if (tiansi.contains("木")) {
            return "上半年风气主令，易动肝阳；秋季金气肃降，易收敛过度";
        } else if (tiansi.contains("土")) {
            return "上半年湿土太过，易困脾胃；下半年湿气渐减，但仍需健脾";
        } else {
            return "上半年燥金之气，易伤肺津；下半年燥气转盛，需继续润肺";
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
}
