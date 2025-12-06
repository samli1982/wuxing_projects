package com.wuxing.util;

import com.tyme.eightchar.EightChar;

import java.util.*;

/**
 * 体质分析与调理建议生成工具类
 */
public class AdviceGenerator {
    
    /**
     * 生成完整的调理建议
     */
    public static Map<String, Object> generateAdvice(EightChar eightChar, String constitution, Map<String, Integer> wuxing) {
        Map<String, Object> result = new LinkedHashMap<>();
        
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
        
        // 体质分析
        Map<String, String> analysis = generateAnalysis(constitution, maxElement, minElement);
        result.put("cause", analysis.get("cause"));
        result.put("symptom", analysis.get("symptom"));
        result.put("tune", analysis.get("tune"));
        
        // 调理建议
        List<Map<String, String>> dietTips = new ArrayList<>();
        List<Map<String, String>> lifestyleTips = new ArrayList<>();
        List<Map<String, String>> emotionTips = new ArrayList<>();
        List<Map<String, String>> herbTips = new ArrayList<>();
        
        generateTips(constitution, dietTips, lifestyleTips, emotionTips, herbTips);
        
        result.put("diet", dietTips);
        result.put("lifestyle", lifestyleTips);
        result.put("emotion", emotionTips);
        result.put("herb", herbTips);
        
        return result;
    }
    
    private static Map<String, String> generateAnalysis(String constitution, String maxElement, String minElement) {
        Map<String, String> analysis = new LinkedHashMap<>();
        
        switch (constitution) {
            case "阴虚质":
                analysis.put("cause", minElement + "行不足，" + maxElement + "行太盛，阴液亏虚");
                analysis.put("symptom", "口干舌燥、失眠多梦、手足心热、盗汗");
                analysis.put("tune", "滋阴降火，养阴润燥、清热安神");
                break;
            case "阳虚质":
                analysis.put("cause", minElement + "行不足，阳气亏虚，温照功能低下");
                analysis.put("symptom", "畏寒怕冷、手足不温、精神萎靡、大便溏薄");
                analysis.put("tune", "温阳散寒，补益阳气、温照脏腑");
                break;
            case "痰湿质":
                analysis.put("cause", "脾运不健，水湿内停，聚湿生痰");
                analysis.put("symptom", "形体肥胖、肚腹肥满、口中粘腻、痰多胸闷");
                analysis.put("tune", "健脾化痰，祛湿化浊、理气宽中");
                break;
            case "气郁质":
                analysis.put("cause", "肝气郁结，气机郁滞，情志不畅");
                analysis.put("symptom", "情绪低落、胸闷叹息、失眠健忘、嘴唇发紫");
                analysis.put("tune", "疏肝理气，调畅气机、愤郁解凝");
                break;
            case "湿热质":
                analysis.put("cause", maxElement + "行太盛，湿热互结，蕴积体内");
                analysis.put("symptom", "面垢油光、口苦口臭、大便粘滞、小便短赤");
                analysis.put("tune", "清热化湿，苦寒通泄、利湿解毒");
                break;
            default:
                analysis.put("cause", "五行相对平衡，阴阳调和，体质平和");
                analysis.put("symptom", "精力充沛、面色红润、睡眠良好、胃口良好");
                analysis.put("tune", "保持现有生活方式，顺应四时，适度调养");
        }
        
        return analysis;
    }
    
    private static void generateTips(String constitution, 
                                    List<Map<String, String>> dietTips,
                                    List<Map<String, String>> lifestyleTips,
                                    List<Map<String, String>> emotionTips,
                                    List<Map<String, String>> herbTips) {
        switch (constitution) {
            case "阴虚质":
                dietTips.add(createTip("宜滋阴清热，多食百合、枸杞、银耳、鸭肉"));
                dietTips.add(createTip("忌辛辣燥热，少食花椒、生姜、大蒜、胡椒"));
                lifestyleTips.add(createTip("避免熬夜，晚10点前入睡，保证充足睡眠"));
                lifestyleTips.add(createTip("避免剧烈运动，适合太极、瑜伽等柔和锻炼"));
                emotionTips.add(createTip("保持心态平和，避免焦虑急躁"));
                emotionTips.add(createTip("可听安静音乐，练习冥想放松"));
                herbTips.add(createTip("六味地黄丸：滋补肾阴的基础方剂"));
                herbTips.add(createTip("知柏地黄丸：阴虚火旺者加用"));
                break;
            case "阳虚质":
                dietTips.add(createTip("宜温阳散寒，多食羊肉、韭菜、生姜、桂圆"));
                dietTips.add(createTip("忌生冷寒凉，少食苦瓜、西瓜、蛏蟹、绿豆"));
                lifestyleTips.add(createTip("注意保暖，尤其是背部、腹部、足部"));
                lifestyleTips.add(createTip("适度运动，多晒太阳，增强阳气"));
                emotionTips.add(createTip("保持乐观情绪，多参加社交活动"));
                emotionTips.add(createTip("避免过度悲伤，多听欢快音乐"));
                herbTips.add(createTip("金匮肾气丸：温补肾阳，蓄气化水"));
                herbTips.add(createTip("右归丸：阳虚较重者适用"));
                break;
            case "痰湿质":
                dietTips.add(createTip("宜健脾化痰，多食薏米、山药、冬瓜、陈皮"));
                dietTips.add(createTip("忌甘肥厚腻，少食糖果、油炸、甜食、肥肉"));
                lifestyleTips.add(createTip("适度运动，避免久坐，促进代谢"));
                lifestyleTips.add(createTip("控制体重，避免肥胖加重痰湿"));
                emotionTips.add(createTip("保持心情舒畅，避免思虑过度"));
                emotionTips.add(createTip("多参加户外活动，活跃气机"));
                herbTips.add(createTip("二陈汤：燥湿化痰，理气和中"));
                herbTips.add(createTip("六君子汤：脾虚痰湿者加用"));
                break;
            case "气郁质":
                dietTips.add(createTip("宜疏肝理气，多食玫瑰、佛手、葡萄、金橘"));
                dietTips.add(createTip("忌过食生冷，少食收敛食物"));
                lifestyleTips.add(createTip("规律作息，避免熬夜损伤肝血"));
                lifestyleTips.add(createTip("适当运动，可选择慢跑、散步等"));
                emotionTips.add(createTip("保持心情愉悦，避免郁闷生气"));
                emotionTips.add(createTip("多与人交流，排解负面情绪"));
                herbTips.add(createTip("逍遥散：疏肝解郁，理气宽胸"));
                herbTips.add(createTip("柴胡疏肝丸：肝郁气滞者适用"));
                break;
            case "湿热质":
                dietTips.add(createTip("宜清热化湿，多食苦瓜、绿豆、冬瓜、薄荷"));
                dietTips.add(createTip("忌辛辣甘肥，少食烧烤、油炸、甜食、辣椒"));
                lifestyleTips.add(createTip("保持环境干燥通风，避免潮湿"));
                lifestyleTips.add(createTip("适当运动，出汗排湿，但勿过度"));
                emotionTips.add(createTip("保持心境平和，避免燥怒伤肝"));
                emotionTips.add(createTip("可练习瑜伽、太极，平复心情"));
                herbTips.add(createTip("龙胆泻肝丸：清肝胆湿热"));
                herbTips.add(createTip("茵陈术散：湿热偏重者加用"));
                break;
            default:
                dietTips.add(createTip("均衡饮食，五谷杂粮，应季蔬果"));
                dietTips.add(createTip("定时定量，不过饱过饥，细嚼慢咽"));
                lifestyleTips.add(createTip("规律作息，适度运动，劳逸结合"));
                lifestyleTips.add(createTip("顺应四时，春夏养阳，秋冬养阴"));
                emotionTips.add(createTip("心态平和，乐观向上，顺应自然"));
                emotionTips.add(createTip("适当放松，避免过度精神紧张"));
                herbTips.add(createTip("根据季节调理，春生夏长秋收冬藏"));
                herbTips.add(createTip("可适当饮用药茶，如枸杞、菊花"));
        }
    }
    
    private static Map<String, String> createTip(String text) {
        Map<String, String> tip = new HashMap<>();
        tip.put("text", text);
        return tip;
    }
}
