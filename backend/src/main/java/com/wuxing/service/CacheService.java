package com.wuxing.service;

import com.wuxing.entity.CacheInfo;

import java.util.List;
import java.util.Map;

/**
 * 缓存服务
 */
public interface CacheService {
    
    /**
     * 获取所有缓存键（支持模糊匹配）
     */
    List<String> getKeys(String pattern);
    
    /**
     * 获取缓存详情
     */
    CacheInfo getCacheInfo(String key);
    
    /**
     * 删除缓存
     */
    Boolean deleteCache(String key);
    
    /**
     * 批量删除缓存
     */
    Long deleteCaches(List<String> keys);
    
    /**
     * 清空所有缓存
     */
    void clearAll();
    
    /**
     * 获取Redis信息
     */
    Map<String, Object> getRedisInfo();
}
