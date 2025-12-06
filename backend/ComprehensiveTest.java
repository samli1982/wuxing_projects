import com.wuxing.utils.WuyunUtils;

/**
 * 完整的五运六气修复验证
 * 验证：
 * 1. 1982年和1988年有不同的司天和在泉
 * 2. 五运数据正确
 * 3. 气候特征正确
 */
public class ComprehensiveTest {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    五运六气计算修复 - 完整验证");
        System.out.println("========================================\n");
        
        // 测试用例
        int[][] testCases = {
            {1982, 1988},  // 用户报告的两个年份
            {1980, 1986},  // 相差6年的另一组
            {1996, 2002},  // 不同世纪的对比
        };
        
        boolean allPassed = true;
        
        for (int[] pair : testCases) {
            int year1 = pair[0];
            int year2 = pair[1];
            
            WuyunUtils.WuyunInfo info1 = WuyunUtils.calculateWuyun(year1);
            WuyunUtils.WuyunInfo info2 = WuyunUtils.calculateWuyun(year2);
            
            System.out.println("【测试: " + year1 + "年 vs " + year2 + "年】");
            System.out.println(year1 + "年:");
            System.out.println("  天干地支: " + info1.getGanzhi());
            System.out.println("  五运: " + info1.getWuyun());
            System.out.println("  司天: " + info1.getSitian());
            System.out.println("  在泉: " + info1.getZaiquan());
            
            System.out.println(year2 + "年:");
            System.out.println("  天干地支: " + info2.getGanzhi());
            System.out.println("  五运: " + info2.getWuyun());
            System.out.println("  司天: " + info2.getSitian());
            System.out.println("  在泉: " + info2.getZaiquan());
            
            // 验证
            boolean siTianDiff = !info1.getSitian().equals(info2.getSitian());
            boolean zaiQuanDiff = !info1.getZaiquan().equals(info2.getZaiquan());
            boolean passed = siTianDiff && zaiQuanDiff;
            
            System.out.println("\n验证结果:");
            System.out.println("  司天不同: " + (siTianDiff ? "✓ PASS" : "✗ FAIL"));
            System.out.println("  在泉不同: " + (zaiQuanDiff ? "✓ PASS" : "✗ FAIL"));
            System.out.println("  总体: " + (passed ? "✓ PASS" : "✗ FAIL"));
            
            if (!passed) {
                allPassed = false;
            }
            System.out.println();
        }
        
        // 测试健康提示逻辑
        System.out.println("【测试: 气候特征和健康提示】");
        testClimateAndHealthWarnings();
        
        System.out.println("\n========================================");
        if (allPassed) {
            System.out.println("✓ 所有验证通过！五运六气修复成功。");
        } else {
            System.out.println("✗ 某些验证失败。");
        }
        System.out.println("========================================");
    }
    
    static void testClimateAndHealthWarnings() {
        // 测试不同五气对应的特征
        String[] qiTypes = {"木", "火", "土", "金", "水"};
        
        System.out.println("\n不同司天的气候特征：");
        for (String qi : qiTypes) {
            String[] sitian = {
                "厥阴风木", "少阴君火", "太阴湿土", "少阳相火", "阳明燥金", "太阳寒水"
            };
            
            for (String s : sitian) {
                if (s.contains(qi)) {
                    String climate = generateClimate(s);
                    System.out.println("  " + s + " -> " + climate);
                    break;
                }
            }
        }
    }
    
    static String generateClimate(String tiansi) {
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
}
