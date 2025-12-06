package com.wuxing.utils;

import com.tyme.sixtycycle.SixtyCycle;
import com.tyme.lunar.LunarYear;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 五运六气计算工具类
 * 
 * 五运：根据天干推算（甲己之岁土运元，乙庚之岁金运先，...）
 * 六气：根据地支推算（北方之极盛于冬，南方之极盛于夏，...）
 */
public class WuyunUtils {

    /**
     * 天干列表
     */
    private static final String[] HEAVENLY_STEMS = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};

    /**
     * 地支列表
     */
    private static final String[] EARTHLY_BRANCHES = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    /**
     * 纳音五行
     */
    private static final String[] NAYIN = {
            "海中金", "炉中火", "大林木", "路旁土", "剑锋金",
            "山头火", "涧下水", "城头土", "白腊金", "杨柳木",
            "泉中水", "长流水", "沙中土", "中山木", "霹雳火",
            "松柏木", "大驿土", "细流水", "屋上土", "壁上土",
            "金箔金", "海上金", "砂中土", "大溪水", "覆灯火",
            "天河水", "大壮木", "平地木", "桑松木", "大溪水",
            "山下火", "山头土", "常流水", "泉中水", "长流水",
            "沙中土", "天上火", "炉中火", "城头土", "白腊金",
            "杨柳木", "泉中水", "长流水", "沙中土", "中山木",
            "霹雳火", "松柏木", "大驿土", "细流水", "屋上土",
            "壁上土", "金箔金", "海上金", "砂中土", "大溪水",
            "覆灯火", "天河水", "大壮木", "平地木", "桑松木"
    };

    /**
     * 五运对应的天干规律
     * 甲己->土, 乙庚->金, 丙辛->水, 丁壬->木, 戊癸->火
     */
    private static final String[][] FIVE_YUNS = {
            {"甲", "己", "土"}, // 甲己之岁土运元
            {"乙", "庚", "金"}, // 乙庚之岁金运先
            {"丙", "辛", "水"}, // 丙辛之岁水运周
            {"丁", "壬", "木"}, // 丁壬之岁木运临
            {"戊", "癸", "火"}  // 戊癸之岁火运行
    };

    /**
     * 六气对应的地支规律
     * 按地支的地支对应的六气
     */
    private static final String[] SIX_QI = {
            "太阴湿土", // 子
            "厥阴风木", // 丑
            "少阴君火", // 寅
            "太阴湿土", // 卯
            "少阳相火", // 辰
            "厥阴风木", // 巳
            "太阳寒水", // 午
            "厥阴风木", // 未
            "少阳相火", // 申
            "太阴湿土", // 酉
            "少阴君火", // 戌
            "太阳寒水"  // 亥
    };

    /**
     * 天干地支结合对应的司天和在泉
     * 根据古籍规律：司天在泉与天干地支有对应关系
     */
    private static final String[][] SITIAN_ZAIQUAN = {
            // 甲子 - 癸亥 的组合，按顺序
            {"厥阴风木", "少阳相火"}, // 甲子
            {"厥阴风木", "太阴湿土"}, // 乙丑
            {"少阴君火", "太阳寒水"}, // 丙寅
            {"太阴湿土", "厥阴风木"}, // 丁卯
            {"少阳相火", "少阴君火"}, // 戊辰
            {"厥阴风木", "太阴湿土"}, // 己巳
            {"太阳寒水", "少阳相火"}, // 庚午
            {"厥阴风木", "太阴湿土"}, // 辛未
            {"少阳相火", "少阴君火"}, // 壬申
            {"太阴湿土", "太阳寒水"}, // 癸酉
    };

    /**
     * 根据年份计算五运六气
     *
     * @param year 年份（公历）
     * @return 五运六气信息
     */
    public static WuyunInfo calculateWuyun(int year) {
        // 1. 获取天干地支
        String ganzhi = getGanzhi(year);
        String stem = String.valueOf(ganzhi.charAt(0));
        String branch = String.valueOf(ganzhi.charAt(1));

        // 2. 计算纳音
        String nayin = getNayin(year);

        // 3. 计算五运（根据天干）
        String wuyun = getWuyun(stem);

        // 4. 计算司天和在泉（根据地支）
        String[] siTianZaiQuan = getSiTianZaiQuan(year);
        String sitian = siTianZaiQuan[0];
        String zaiquan = siTianZaiQuan[1];

        return WuyunInfo.builder()
                .year(year)
                .ganzhi(ganzhi)
                .stem(stem)
                .branch(branch)
                .nayin(nayin)
                .wuyun(wuyun)
                .sitian(sitian)
                .zaiquan(zaiquan)
                .build();
    }

    /**
     * 根据公历年份获取天干地支
     * 使用 tyme4j 库中的 LunarYear 精确计算
     *
     * @param year 公历年份
     * @return 天干地支，如"甲子"
     */
    public static String getGanzhi(int year) {
        try {
            // 使用 tyme4j 的 LunarYear 计算
            LunarYear lunarYear = LunarYear.fromYear(year);
            SixtyCycle cycle = lunarYear.getSixtyCycle();
            return cycle.getHeavenStem().getName() + cycle.getEarthBranch().getName();
        } catch (Exception e) {
            // 降级处理
            LunarYear lunarYearPrev = LunarYear.fromYear(year - 1);
            SixtyCycle cyclePrev = lunarYearPrev.getSixtyCycle();
            String stem = cyclePrev.getHeavenStem().getName();
            String branch = cyclePrev.getEarthBranch().getName();
            return stem + branch;
        }
    }

    /**
     * 计算纳音五行
     * 根据天干地支的索引位置，从预设的纳音表中取值
     * 纳音表是一个 60 元的周期，根据天干地支的索引来查表
     *
     * @param year 公历年份
     * @return 纳音五行
     */
    public static String getNayin(int year) {
        // 使用 tyme4j 计算天干地支
        String ganzhi = getGanzhi(year);
        
        // 基准年份是 2001 年（乡巳年），对应 NAYIN[24]
        // 每个年份按照天干地支的 60 年周期顺序
        int offset = (year - 2001) % 60;
        if (offset < 0) offset += 60;
        return NAYIN[offset];
    }

    /**
     * 根据天干获取五运
     *
     * @param stem 天干（甲、乙、丙、丁、戊、己、庚、辛、壬、癸）
     * @return 五运（木、火、土、金、水）
     */
    public static String getWuyun(String stem) {
        for (String[] yun : FIVE_YUNS) {
            if (stem.equals(yun[0]) || stem.equals(yun[1])) {
                return yun[2];
            }
        }
        return "未知";
    }

    /**
     * 根据年份获取司天和在泉
     * 六气按 60 年周期循环，完整顺序：厥阴风木→少阴君火→太阴湿土→少阳相火→阳明燥金→太阳寒水
     * 司天与在泉相隔 3 个气的位置
     *
     * @param year 公历年份
     * @return [司天, 在泉]
     */
    public static String[] getSiTianZaiQuan(int year) {
        // 六气的标准循环顺序
        String[] sixQiCycle = {
            "厥阴风木",  // 0
            "少阴君火",  // 1
            "太阴湿土",  // 2
            "少阳相火",  // 3
            "阳明燥金",  // 4
            "太阳寒水"   // 5
        };

        // 基准年 1983（削上一个六气周期的开始年）对应司天为厥阴风木（索引 0）
        // 每个年份按丰支60年周期计算其在六气循环中的位置
        int offset = (year - 1983) % 60;
        if (offset < 0) offset += 60;
        
        // 司天的索引 = offset % 6
        int siTianIndex = offset % 6;
        String sitian = sixQiCycle[siTianIndex];
        
        // 在泉 = 司天后的第 3 个气（下半年，相隔半年）
        int zaiQuanIndex = (siTianIndex + 3) % 6;
        String zaiquan = sixQiCycle[zaiQuanIndex];

        return new String[]{sitian, zaiquan};
    }

    /**
     * 五运六气信息数据类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WuyunInfo {
        private int year;          // 年份
        private String ganzhi;     // 天干地支，如"甲子"
        private String stem;       // 天干
        private String branch;     // 地支
        private String nayin;      // 纳音五行
        private String wuyun;      // 五运（木、火、土、金、水）
        private String sitian;     // 司天（上半年主气）
        private String zaiquan;    // 在泉（下半年主气）
    }
}
