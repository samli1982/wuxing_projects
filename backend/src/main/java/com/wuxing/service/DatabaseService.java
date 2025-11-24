package com.wuxing.service;

import com.wuxing.entity.DatabaseQuery;

import java.util.List;
import java.util.Map;

/**
 * 数据库查询服务
 */
public interface DatabaseService {
    
    /**
     * 获取所有表名
     */
    List<String> getAllTables();
    
    /**
     * 获取表结构信息
     */
    List<Map<String, Object>> getTableStructure(String tableName);
    
    /**
     * 执行查询SQL
     */
    DatabaseQuery executeQuery(String sql, Integer pageNum, Integer pageSize);
    
    /**
     * 获取表数据统计
     */
    Map<String, Object> getTableStats(String tableName);
}
