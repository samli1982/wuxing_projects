package com.wuxing.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 城市经纬度工具类
 */
public class CityLocationUtil {
    
    // 中国主要城市经纬度数据（东经，北纬）
    private static final Map<String, double[]> CITY_COORDINATES = new HashMap<>();
    
    static {
        // 直辖市
        CITY_COORDINATES.put("北京", new double[]{116.4074, 39.9042});
        CITY_COORDINATES.put("上海", new double[]{121.4737, 31.2304});
        CITY_COORDINATES.put("天津", new double[]{117.2008, 39.0842});
        CITY_COORDINATES.put("重庆", new double[]{106.5516, 29.5630});
        
        // 省会城市
        CITY_COORDINATES.put("广州", new double[]{113.2644, 23.1291});
        CITY_COORDINATES.put("深圳", new double[]{114.0579, 22.5431});
        CITY_COORDINATES.put("杭州", new double[]{120.1551, 30.2741});
        CITY_COORDINATES.put("南京", new double[]{118.7969, 32.0603});
        CITY_COORDINATES.put("武汉", new double[]{114.3055, 30.5931});
        CITY_COORDINATES.put("成都", new double[]{104.0665, 30.5723});
        CITY_COORDINATES.put("西安", new double[]{108.9398, 34.3416});
        CITY_COORDINATES.put("郑州", new double[]{113.6254, 34.7466});
        CITY_COORDINATES.put("济南", new double[]{117.1205, 36.6519});
        CITY_COORDINATES.put("沈阳", new double[]{123.4328, 41.8045});
        CITY_COORDINATES.put("长春", new double[]{125.3235, 43.8171});
        CITY_COORDINATES.put("哈尔滨", new double[]{126.5349, 45.8038});
        CITY_COORDINATES.put("太原", new double[]{112.5489, 37.8706});
        CITY_COORDINATES.put("石家庄", new double[]{114.5149, 38.0428});
        CITY_COORDINATES.put("南昌", new double[]{115.8581, 28.6832});
        CITY_COORDINATES.put("长沙", new double[]{112.9388, 28.2282});
        CITY_COORDINATES.put("福州", new double[]{119.2965, 26.0745});
        CITY_COORDINATES.put("厦门", new double[]{118.0894, 24.4798});
        CITY_COORDINATES.put("南宁", new double[]{108.3661, 22.8172});
        CITY_COORDINATES.put("昆明", new double[]{102.8329, 24.8801});
        CITY_COORDINATES.put("贵阳", new double[]{106.7135, 26.5783});
        CITY_COORDINATES.put("兰州", new double[]{103.8343, 36.0611});
        CITY_COORDINATES.put("西宁", new double[]{101.7782, 36.6171});
        CITY_COORDINATES.put("银川", new double[]{106.2586, 38.4681});
        CITY_COORDINATES.put("乌鲁木齐", new double[]{87.6177, 43.8256});
        CITY_COORDINATES.put("拉萨", new double[]{91.1145, 29.6444});
        CITY_COORDINATES.put("海口", new double[]{110.1999, 20.0444});
        CITY_COORDINATES.put("合肥", new double[]{117.2272, 31.8206});
        CITY_COORDINATES.put("呼和浩特", new double[]{111.6708, 40.8183});
        
        // 其他重要城市
        CITY_COORDINATES.put("苏州", new double[]{120.5954, 31.2989});
        CITY_COORDINATES.put("宁波", new double[]{121.5440, 29.8683});
        CITY_COORDINATES.put("温州", new double[]{120.6994, 28.0006});
        CITY_COORDINATES.put("青岛", new double[]{120.3826, 36.0671});
        CITY_COORDINATES.put("大连", new double[]{121.6147, 38.9140});
        CITY_COORDINATES.put("东莞", new double[]{113.7518, 23.0205});
        CITY_COORDINATES.put("佛山", new double[]{113.1220, 23.0212});
        CITY_COORDINATES.put("珠海", new double[]{113.5765, 22.2707});
        CITY_COORDINATES.put("龙岩", new double[]{117.0297, 25.0918}); // 福建省龙岩市
    }
    
    /**
     * 根据城市名称获取经纬度
     * @param cityName 城市名称
     * @return 经纬度数组 [经度, 纬度]，如果未找到返回北京坐标
     */
    public static double[] getCoordinates(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return CITY_COORDINATES.get("北京"); // 默认北京
        }
        
        // 去除"市"、"省"等后缀
        String cleanName = cityName.replaceAll("[市省自治区特别行政区]", "");
        
        // 精确匹配
        if (CITY_COORDINATES.containsKey(cleanName)) {
            return CITY_COORDINATES.get(cleanName);
        }
        
        // 模糊匹配（检查是否包含）
        for (Map.Entry<String, double[]> entry : CITY_COORDINATES.entrySet()) {
            if (cleanName.contains(entry.getKey()) || entry.getKey().contains(cleanName)) {
                return entry.getValue();
            }
        }
        
        // 未找到，返回北京坐标
        return CITY_COORDINATES.get("北京");
    }
    
    /**
     * 获取经度
     */
    public static double getLongitude(String cityName) {
        return getCoordinates(cityName)[0];
    }
    
    /**
     * 获取纬度
     */
    public static double getLatitude(String cityName) {
        return getCoordinates(cityName)[1];
    }
}
