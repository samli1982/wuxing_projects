import com.wuxing.utils.WuyunUtils;

/**
 * 验证五运六气修复
 * 测试1982年和1988年应该有不同的司天和在泉
 */
public class TestWuyunFix {
    public static void main(String[] args) {
        System.out.println("======= 五运六气修复验证 =======\n");
        
        // 测试1982年
        System.out.println("【1982年数据】");
        WuyunUtils.WuyunInfo info1982 = WuyunUtils.calculateWuyun(1982);
        System.out.println("  年份: " + info1982.getYear());
        System.out.println("  天干地支: " + info1982.getGanzhi());
        System.out.println("  纳音: " + info1982.getNayin());
        System.out.println("  五运: " + info1982.getWuyun());
        System.out.println("  司天: " + info1982.getSitian());
        System.out.println("  在泉: " + info1982.getZaiquan());
        System.out.println();
        
        // 测试1988年
        System.out.println("【1988年数据】");
        WuyunUtils.WuyunInfo info1988 = WuyunUtils.calculateWuyun(1988);
        System.out.println("  年份: " + info1988.getYear());
        System.out.println("  天干地支: " + info1988.getGanzhi());
        System.out.println("  纳音: " + info1988.getNayin());
        System.out.println("  五运: " + info1988.getWuyun());
        System.out.println("  司天: " + info1988.getSitian());
        System.out.println("  在泉: " + info1988.getZaiquan());
        System.out.println();
        
        // 验证结果
        System.out.println("【验证结果】");
        boolean siTianDifferent = !info1982.getSitian().equals(info1988.getSitian());
        boolean zaiQuanDifferent = !info1982.getZaiquan().equals(info1988.getZaiquan());
        
        System.out.println("司天是否不同? " + (siTianDifferent ? "✓ 是" : "✗ 否（错误）"));
        System.out.println("在泉是否不同? " + (zaiQuanDifferent ? "✓ 是" : "✗ 否（错误）"));
        
        if (siTianDifferent && zaiQuanDifferent) {
            System.out.println("\n✓ 修复成功！1982年和1988年现在有不同的司天和在泉数据。");
        } else {
            System.out.println("\n✗ 修复失败！1982年和1988年司天和在泉仍然相同。");
        }
        
        // 测试更多年份，验证周期性
        System.out.println("\n【周期性验证 - 查看相邻6年的变化】");
        for (int year = 1980; year <= 1992; year++) {
            WuyunUtils.WuyunInfo info = WuyunUtils.calculateWuyun(year);
            System.out.printf("%d年: 司天=%s, 在泉=%s%n", year, info.getSitian(), info.getZaiquan());
        }
    }
}
