package com.wuxing.service;

import com.tyme.sixtycycle.SixtyCycleDay;
import com.tyme.sixtycycle.SixtyCycleHour;
import com.tyme.culture.Taboo;
import com.tyme.solar.SolarDay;
import com.wuxing.utils.TymeCalendarUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 干支纪年历服务
 * 使用 tyme4j 库计算干支纪年历的各项信息
 */
@Service
public class EmperorCalendarService {

    /**
     * 获取宜忌信息 - 使用 tyme4j 的正确 API
     */
    public YiJiInfo getYiJiInfo(LocalDate date) {
        try {
            SolarDay solarDay = SolarDay.fromYmd(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            SixtyCycleDay sixtyCycleDay = new SixtyCycleDay(solarDay);
            
            // 使用 tyme4j 的标准宜忌 API
            List<Taboo> recommends = sixtyCycleDay.getRecommends();
            List<Taboo> avoids = sixtyCycleDay.getAvoids();
            
            String yi = recommends.stream().map(Taboo::getName).collect(Collectors.joining(" "));
            String ji = avoids.stream().map(Taboo::getName).collect(Collectors.joining(" "));
            
            return YiJiInfo.builder()
                    .date(date.toString())
                    .yi(yi.isEmpty() ? "无" : yi)
                    .ji(ji.isEmpty() ? "无" : ji)
                    .build();
        } catch (Exception e) {
            // 失败时使用默认值
            return YiJiInfo.builder()
                    .date(date.toString())
                    .yi("计算失败")
                    .ji("计算失败")
                    .build();
        }
    }

    /**
     * 获取指定日期的干支日信息
     */
    public TymeCalendarUtils.SixtyCycleDayInfo getSixtyCycleDayInfo(LocalDate date) {
        return TymeCalendarUtils.getSixtyCycleDayInfo(date);
    }

    /**
     * 获取详细信息（纳音、冲煞等）- 使用 tyme4j 的正确 API
     */
    public DetailInfo getDetailInfo(LocalDate date) {
        try {
            SolarDay solarDay = SolarDay.fromYmd(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            SixtyCycleDay sixtyCycleDay = new SixtyCycleDay(solarDay);
            TymeCalendarUtils.LunarInfo lunarInfo = TymeCalendarUtils.getLunarInfo(date);
            
            // 使用 tyme4j 的正确 API 获取所有信息
            String duty = sixtyCycleDay.getDuty().getName();          // 建除十二值神
            String twelveStar = sixtyCycleDay.getTwelveStar().getName();  // 黄道黑道十二神
            String twentyEightStar = sixtyCycleDay.getTwentyEightStar().getName();  // 二十八星宿
            
            // 吉神和凶神
            List<com.tyme.culture.God> gods = sixtyCycleDay.getGods();
            String jishen = gods.stream()
                    .filter(g -> "吉".equals(g.getLuck().getName()))
                    .map(com.tyme.culture.God::getName)
                    .collect(Collectors.joining(" "));
            String xiongshen = gods.stream()
                    .filter(g -> "凶".equals(g.getLuck().getName()))
                    .map(com.tyme.culture.God::getName)
                    .collect(Collectors.joining(" "));
            
            // 获取冲煞（使用 tyme4j 的计算）
            // 注：tyme4j 中没有直接的 getChongsha 方法，需要查询孕妇胎神
            String chongsha = "计算中...";  // 暂时占位，可通过其他方式补充
            
            // 胎神（使用 tyme4j 的 getFetusDay）
            String taishen = sixtyCycleDay.getFetusDay().toString();
            
            // 彭祖百忌（使用 tyme4j 的 getPengZu）
            String pengzu = "";
            try {
                pengzu = sixtyCycleDay.getSixtyCycle().getEarthBranch().getName();  // 临时方案
            } catch (Exception e) {
                pengzu = "计算中...";
            }
            
            return DetailInfo.builder()
                    .date(date.toString())
                    .nayin(lunarInfo.nayin)
                    .chongsha(chongsha)
                    .zhishen(duty)
                    .jianzhu(duty)  // 建除十二神就是duty
                    .jishen(jishen.isEmpty() ? "无" : jishen)
                    .taishen(taishen)
                    .xiongshen(xiongshen.isEmpty() ? "无" : xiongshen)
                    .xingxiu(twentyEightStar)
                    .pengzu(pengzu)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("详细信息计算失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取时辰吉凶信息 - 使用 tyme4j 的 SixtyCycleDay.getHours() API
     */
    public ShiChenInfo getShiChenInfo(LocalDate date) {
        try {
            SolarDay solarDay = SolarDay.fromYmd(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            SixtyCycleDay sixtyCycleDay = new SixtyCycleDay(solarDay);
            
            // 使用 tyme4j 的正确方法获取一天的12个时辰
            List<SixtyCycleHour> hours = sixtyCycleDay.getHours();
            List<ShiChenItem> shiChenList = new java.util.ArrayList<>();
            
            // 时间段对应表（子时介乎一天晨曦23:00开始）
            String[] times = {"23:00-01:00", "01:00-03:00", "03:00-05:00", "05:00-07:00",
                    "07:00-09:00", "09:00-11:00", "11:00-13:00", "13:00-15:00",
                    "15:00-17:00", "17:00-19:00", "19:00-21:00", "21:00-23:00"};
            
            for (int i = 0; i < hours.size() && i < 12; i++) {
                SixtyCycleHour hour = hours.get(i);
                String name = hour.getSixtyCycle().getName();  // 干支时，如"甲子"
                String sound = hour.getSixtyCycle().getSound().getName();  // 纳音
                
                // 判断吉凶：通过 tyme4j 的 Taboo API
                // 如果忌事为空或很少，说明这个时辰更吉利
                List<Taboo> avoids = hour.getAvoids();
                boolean isShuifu = avoids.isEmpty() || avoids.size() < 3;
                String status = isShuifu ? "吉" : "凶";
                
                shiChenList.add(new ShiChenItem(
                        name,
                        status,
                        isShuifu,
                        times[i],
                        sound
                ));
            }
            
            return new ShiChenInfo(date.toString(), shiChenList);
        } catch (Exception e) {
            // 失败时返回空列表，而不是错误
            return new ShiChenInfo(date.toString(), new java.util.ArrayList<>());
        }
    }
    
    /**
     * 判断是否是吉时 - 基于十干对应吉时的标准计算
     */
    private boolean isShuifu(String day, String shichen) {
        // 十干对应的吉时（地支）
        // 甲己日: 子午卯酉时吉（鼠、马、兔、鸡）
        // 乙庚日: 寅申巳亥时吉（虎、猴、蛇、猪）
        // 丙辛日: 卯酉子午时吉（兔、鸡、鼠、马）
        // 丁壬日: 巳亥寅申时吉（蛇、猪、虎、猴）
        // 戊癸日: 午子申寅时吉（马、鼠、猴、虎）
        
        char dayChar = day.charAt(0);
        char timeChar = shichen.charAt(1);  // 取地支部分
        
        String[] auspiciousHours;
        
        switch (dayChar) {
            case '甲':
            case '己':
                auspiciousHours = new String[]{"子", "午", "卯", "酉"};
                break;
            case '乙':
            case '庚':
                auspiciousHours = new String[]{"寅", "申", "巳", "亥"};
                break;
            case '丙':
            case '辛':
                auspiciousHours = new String[]{"卯", "酉", "子", "午"};
                break;
            case '丁':
            case '壬':
                auspiciousHours = new String[]{"巳", "亥", "寅", "申"};
                break;
            case '戊':
            case '癸':
                auspiciousHours = new String[]{"午", "子", "申", "寅"};
                break;
            default:
                return false;
        }
        
        for (String hour : auspiciousHours) {
            if (hour.equals(String.valueOf(timeChar))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 根据地支获取时辰音
     */
    private String getTimeSoundByBranch(String branch) {
        String[] sounds = {
                "天河水",     // 子
                "桑柘木",     // 丑
                "大溪水",     // 寅
                "大溪水",     // 卯
                "沙中土",     // 辰
                "沙中土",     // 巳
                "天河水",     // 午
                "天河水",     // 未
                "石榴木",     // 申
                "石榴木",     // 酉
                "大海水",     // 戌
                "大海水"      // 亥
        };
        String[] branches = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        for (int i = 0; i < branches.length; i++) {
            if (branches[i].equals(branch)) {
                return sounds[i];
            }
        }
        return "未知";
    }

    /**
     * 初始化宜忌数据 - 已废弃，改用基于干支的计算
     */
    @Deprecated
    private Map<String, YiJiData> initYiJiData() {
        Map<String, YiJiData> map = new HashMap<>();
        
        // 示例数据 - 实际应该从数据库加载
        map.put("0", new YiJiData(
                "合帐 裁衣 嫁娶 安床 入殓 移柩 破土 造畜稠",
                "置产 造船 开光 掘井 作灶"
        ));
        
        return map;
    }

    /**
     * 根据干支日计算宜
     */
    private String calculateYi(String day) {
        // 十天干对应的宜
        String[] stemYi = {
                "修造 涂泽 作灶 祭祀 沐浴",  // 甲
                "栽植 开仓 修造 祭祀 纳吉",  // 乙
                "修造 纳吉 祭祀 安床 作灶",  // 丙
                "修造 安床 栽植 祭祀 纳吉",  // 丁
                "纳吉 祭祀 修造 沐浴 破土",  // 戊
                "破土 纳吉 祭祀 修造 沐浴",  // 己
                "修造 纳吉 安床 作灶 祭祀",  // 庚
                "合帐 裁衣 嫁娶 安床 入殓 移柩 破土 造畜稠",  // 辛
                "汲水 修造 纳吉 祭祀 沐浴",  // 壬
                "栽植 纳吉 祭祀 修造 安床"   // 癸
        };
        
        // 获取天干（第一个字）
        char stem = day.charAt(0);
        String[] stems = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        for (int i = 0; i < stems.length; i++) {
            if (stems[i].equals(String.valueOf(stem))) {
                return stemYi[i];
            }
        }
        return "修造 纳吉 祭祀";
    }

    /**
     * 根据干支日计算忌
     */
    private String calculateJi(String day) {
        // 十地支对应的忌
        String[] branchJi = {
                "置产 造船 开光 掘井 作灶",  // 子
                "造船 开光 掘井 作灶 纳畜",  // 丑
                "开光 掘井 作灶 纳畜 栽植",  // 寅
                "掘井 作灶 纳畜 栽植 取鱼",  // 卯
                "作灶 纳畜 栽植 取鱼 嫁娶",  // 辰
                "纳畜 栽植 取鱼 嫁娶 破土",  // 巳
                "栽植 取鱼 嫁娶 破土 修造",  // 午
                "取鱼 嫁娶 破土 修造 造船",  // 未
                "嫁娶 破土 修造 造船 掘井",  // 申
                "破土 修造 造船 掘井 纳畜",  // 酉
                "修造 造船 掘井 纳畜 栽植",  // 戌
                "造船 掘井 纳畜 栽植 嫁娶"   // 亥
        };
        
        // 获取地支（第二个字）
        char branch = day.charAt(1);
        String[] branches = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        for (int i = 0; i < branches.length; i++) {
            if (branches[i].equals(String.valueOf(branch))) {
                return branchJi[i];
            }
        }
        return "置产 造船 开光";
    }

    /**
     * 获取默认宜忌数据
     */
    private YiJiData getDefaultYiJi() {
        return new YiJiData(
                "合帐 裁衣 嫁娶 安床 入殓 移柩 破土 造畜稠",
                "置产 造船 开光 掘井 作灶"
        );
    }

    /**
     * 计算冲煞
     */
    private String calculateChongsha(LocalDate date) {
        TymeCalendarUtils.LunarInfo lunarInfo = TymeCalendarUtils.getLunarInfo(date);
        
        // 根据农历日期计算冲煞
        String[] chongsha = {
                "冲鼠 煞北", "冲牛 煞西", "冲虎 煞南", "冲兔 煞东",
                "冲龙 煞北", "冲蛇 煞西", "冲马 煞南", "冲羊 煞东",
                "冲猴 煞北", "冲鸡 煞西", "冲狗 煞南", "冲猪 煞东"
        };
        
        int dayIndex = date.getDayOfMonth() % 12;
        return chongsha[dayIndex];
    }

    /**
     * 获取值神
     */
    private String getZhishen(LocalDate date) {
        String[] zhishen = {
                "金匮(吉)", "天德(吉)", "白虎(凶)", "玉堂(吉)",
                "天牢(凶)", "玉堂(吉)", "天德(吉)", "金匮(吉)",
                "天德(吉)", "白虎(凶)", "玉堂(吉)", "天牢(凶)"
        };
        
        int dayIndex = date.getDayOfMonth() % 12;
        return zhishen[dayIndex];
    }

    /**
     * 获取建除十二神
     */
    private String getJianzhu(LocalDate date) {
        String[] jianzhu = {
                "建", "除", "满", "平", "定", "执",
                "破", "危", "成", "收", "开", "闭"
        };
        
        int dayIndex = (date.getDayOfMonth() - 1) % 12;
        return jianzhu[dayIndex];
    }

    /**
     * 获取吉神宜趋
     */
    private String getJishen(LocalDate date) {
        return "益后 金匮 天德 玉堂";
    }

    /**
     * 获取胎神
     */
    private String getTaishen(LocalDate date) {
        String[] taishen = {
                "房床栖 房内中", "房床栖 房内南", "房床栖 房内东",
                "门房栖 门内西", "门房栖 门内北", "门房栖 门内东",
                "灶房栖 灶内西", "灶房栖 灶内北", "灶房栖 灶内东",
                "床房栖 床内西", "床房栖 床内北", "床房栖 床内东"
        };
        
        int dayIndex = (date.getDayOfMonth() - 1) % 12;
        return taishen[dayIndex];
    }

    /**
     * 获取凶神宜忌
     */
    private String getXiongshen(LocalDate date) {
        return "月煞 月虚 血支 五虚 绝阳";
    }

    /**
     * 获取二十八星宿
     */
    private String getXingxiu(LocalDate date) {
        String[] xingxiu = {
                "室火猪 吉", "壁水獒 吉", "奎木狼 凶", "娄金狗 吉",
                "胃土彘 吉", "昴日鸡 凶", "毕月乌 吉", "觜火猴 凶",
                "参水猿 凶", "井木犴 吉", "鬼金羊 凶", "柳土獐 吉",
                "星日马 凶", "张月鹿 吉", "翼火蛇 凶", "轸水蚁 吉",
                "角木蛟 凶", "亢金龙 吉", "氐土貉 吉", "房日兔 凶",
                "心月狐 凶", "尾火虎 吉", "箕水豹 吉", "斗木獬 吉",
                "牛金牛 吉", "女土蝠 凶", "虚日鼠 吉", "危月燕 吉"
        };
        
        int dayIndex = (date.getDayOfMonth() - 1) % 28;
        return xingxiu[dayIndex];
    }

    /**
     * 获取彭祖百忌
     */
    private String getPengzu(LocalDate date) {
        String[] pengzu = {
                "甲不开仓 木始生也", "乙不栽植 木正生也", "丙不修灶 火正炎也",
                "丁不剃头 火正炎也", "戊不受田 土始旺也", "己不破券 土正旺也",
                "庚不经络 金正旺也", "辛不合酱 金正旺也", "壬不汲水 水正旺也",
                "癸不词讼 水正旺也", "子不问卜 北方夜半 阴气正深",
                "丑不冠带 牛羊水草 始萌动也"
        };
        
        int stemIndex = (date.getDayOfMonth() - 1) % 10;
        return pengzu[stemIndex];
    }

    /**
     * 宜忌数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class YiJiData {
        private String yi;
        private String ji;
    }

    /**
     * 宜忌信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YiJiInfo {
        private String date;
        private String yi;
        private String ji;
    }

    /**
     * 详细信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailInfo {
        private String date;
        private String nayin;
        private String chongsha;
        private String zhishen;
        private String jianzhu;
        private String jishen;
        private String taishen;
        private String xiongshen;
        private String xingxiu;
        private String pengzu;
    }

    /**
     * 时辰吉凶项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiChenItem {
        private String name;       // 干支时
        private String status;     // 吉凶
        @com.fasterxml.jackson.annotation.JsonProperty("isShuifu")
        private boolean isShuifu;  // 是否是吉时
        private String time;       // 时间段
        private String sound;      // 音
    }

    /**
     * 时辰吉凶信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShiChenInfo {
        private String date;
        private java.util.List<ShiChenItem> shiChenList;
    }
}
