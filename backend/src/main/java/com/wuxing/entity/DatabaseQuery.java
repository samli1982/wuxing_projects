package com.wuxing.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据库查询结果
 */
@Data
public class DatabaseQuery {
    
    /**
     * 查询的SQL语句
     */
    private String sql;
    
    /**
     * 列名列表
     */
    private List<String> columns;
    
    /**
     * 数据行列表
     */
    private List<Map<String, Object>> rows;
    
    /**
     * 总行数
     */
    private Integer total;
    
    /**
     * 执行时间(毫秒)
     */
    private Long executionTime;
}
