package com.wuxing.service.impl;

import com.wuxing.entity.CacheInfo;
import com.wuxing.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存服务实现
 */
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public List<String> getKeys(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            pattern = "*";
        }
        
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? new ArrayList<>(keys) : new ArrayList<>();
    }
    
    @Override
    public CacheInfo getCacheInfo(String key) {
        CacheInfo cacheInfo = new CacheInfo();
        cacheInfo.setKey(key);
        
        // 获取值
        Object value = redisTemplate.opsForValue().get(key);
        cacheInfo.setValue(value);
        
        // 获取过期时间
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        cacheInfo.setTtl(ttl);
        
        // 获取类型
        String type = redisTemplate.type(key).code();
        cacheInfo.setType(type);
        
        return cacheInfo;
    }
    
    @Override
    public Boolean deleteCache(String key) {
        return redisTemplate.delete(key);
    }
    
    @Override
    public Long deleteCaches(List<String> keys) {
        return redisTemplate.delete(keys);
    }
    
    @Override
    public void clearAll() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    
    @Override
    public Map<String, Object> getRedisInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // 获取所有键
        Set<String> allKeys = redisTemplate.keys("*");
        info.put("keyCount", allKeys != null ? allKeys.size() : 0);
        
        // 统计不同类型的键数量
        if (allKeys != null && !allKeys.isEmpty()) {
            Map<String, Long> typeCount = allKeys.stream()
                .collect(Collectors.groupingBy(
                    key -> redisTemplate.type(key).code(),
                    Collectors.counting()
                ));
            info.put("typeCount", typeCount);
        } else {
            info.put("typeCount", new HashMap<>());
        }
        
        return info;
    }
}
