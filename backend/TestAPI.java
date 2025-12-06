import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测试命盘详情API - 验证五运六气修复
 */
public class TestAPI {
    public static void main(String[] args) {
        try {
            System.out.println("======= 测试命盘详情API - 验证五运六气修复 =======\n");
            
            // 测试命盘26（1988年10月26日）
            testPalmtree(26, "1988年 - 抱抱");
            
            // 测试命盘25（1982年12月12日）
            testPalmtree(25, "1982年 - SA");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static void testPalmtree(int id, String label) throws Exception {
        String url = "http://localhost:8080/api/palmtree/" + id;
        
        System.out.println("【" + label + "】");
        System.out.println("URL: " + url);
        
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int status = conn.getResponseCode();
            System.out.println("状态码: " + status);
            
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                String json = response.toString();
                
                // 提取五运六气数据
                String tiansi = extractField(json, "\"tiansi\"");
                String zaiquan = extractField(json, "\"zaiquan\"");
                String climate = extractField(json, "\"climate\"");
                
                System.out.println("司天: " + tiansi);
                System.out.println("在泉: " + zaiquan);
                System.out.println("气候: " + climate);
            } else {
                System.out.println("错误: 状态码 " + status);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
                in.close();
            }
        } catch (Exception e) {
            System.out.println("连接失败: " + e.getMessage());
            System.out.println("（这可能是由于API认证或服务未完全启动）");
        }
        
        System.out.println();
    }
    
    static String extractField(String json, String fieldName) {
        String pattern = fieldName + ":\"([^\"]+)\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "未获取";
    }
}
