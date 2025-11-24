package com.wuxing.entity;

import lombok.Data;

/**
 * 缓存信息
 */
@Data
public class CacheInfo {
    
    /**
     * 缓存键
     */
    private String key;
    
    /**
     * 缓存值
     */
    private Object value;
    
    /**
     * 过期时间(秒)，-1表示永不过期
     */
    private Long ttl;
    
    /**
     * 数据类型
     */
    private String type;
}
