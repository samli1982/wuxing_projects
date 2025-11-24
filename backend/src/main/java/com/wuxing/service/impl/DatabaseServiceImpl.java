package com.wuxing.service.impl;

import com.wuxing.entity.DatabaseQuery;
import com.wuxing.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 数据库查询服务实现
 */
@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {
    
    private final JdbcTemplate jdbcTemplate;
    
    // 危险SQL关键字检测
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile(
        "(?i)(drop|delete|truncate|update|insert|alter|create|grant|revoke|exec|execute)",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public List<String> getAllTables() {
        String sql = "SHOW TABLES";
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(sql);
        List<String> tableNames = new ArrayList<>();
        
        for (Map<String, Object> table : tables) {
            // MySQL返回的列名是动态的，获取第一个值
            tableNames.add(table.values().iterator().next().toString());
        }
        
        return tableNames;
    }
    
    @Override
    public List<Map<String, Object>> getTableStructure(String tableName) {
        // 验证表名，防止SQL注入
        validateTableName(tableName);
        
        String sql = "DESCRIBE " + tableName;
        return jdbcTemplate.queryForList(sql);
    }
    
    @Override
    public DatabaseQuery executeQuery(String sql, Integer pageNum, Integer pageSize) {
        // 安全检查：只允许SELECT查询
        if (!isSelectQuery(sql)) {
            throw new RuntimeException("只允许执行SELECT查询语句");
        }
        
        DatabaseQuery result = new DatabaseQuery();
        result.setSql(sql);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 执行查询
            List<Map<String, Object>> allRows = jdbcTemplate.queryForList(sql);
            
            if (allRows.isEmpty()) {
                result.setColumns(new ArrayList<>());
                result.setRows(new ArrayList<>());
                result.setTotal(0);
            } else {
                // 获取列名
                Set<String> columnSet = allRows.get(0).keySet();
                result.setColumns(new ArrayList<>(columnSet));
                
                // 分页处理
                int total = allRows.size();
                result.setTotal(total);
                
                if (pageNum != null && pageSize != null) {
                    int start = (pageNum - 1) * pageSize;
                    int end = Math.min(start + pageSize, total);
                    
                    if (start < total) {
                        result.setRows(allRows.subList(start, end));
                    } else {
                        result.setRows(new ArrayList<>());
                    }
                } else {
                    // 不分页，返回所有数据（限制最多1000条）
                    result.setRows(allRows.subList(0, Math.min(1000, total)));
                }
            }
            
            long endTime = System.currentTimeMillis();
            result.setExecutionTime(endTime - startTime);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("SQL执行失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getTableStats(String tableName) {
        validateTableName(tableName);
        
        Map<String, Object> stats = new HashMap<>();
        
        // 获取表行数
        String countSql = "SELECT COUNT(*) as count FROM " + tableName;
        Integer rowCount = jdbcTemplate.queryForObject(countSql, Integer.class);
        stats.put("rowCount", rowCount);
        
        // 获取表信息
        String infoSql = "SHOW TABLE STATUS LIKE '" + tableName + "'";
        List<Map<String, Object>> tableInfo = jdbcTemplate.queryForList(infoSql);
        
        if (!tableInfo.isEmpty()) {
            Map<String, Object> info = tableInfo.get(0);
            stats.put("engine", info.get("Engine"));
            stats.put("rowFormat", info.get("Row_format"));
            stats.put("dataLength", info.get("Data_length"));
            stats.put("createTime", info.get("Create_time"));
            stats.put("updateTime", info.get("Update_time"));
            stats.put("collation", info.get("Collation"));
            stats.put("comment", info.get("Comment"));
        }
        
        return stats;
    }
    
    /**
     * 验证表名是否合法
     */
    private void validateTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new RuntimeException("表名不能为空");
        }
        
        // 只允许字母、数字、下划线
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new RuntimeException("非法的表名");
        }
    }
    
    /**
     * 检查是否是SELECT查询
     */
    private boolean isSelectQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        
        String trimmedSql = sql.trim().toLowerCase();
        
        // 必须以SELECT开头
        if (!trimmedSql.startsWith("select")) {
            return false;
        }
        
        // 检查是否包含危险关键字
        if (DANGEROUS_PATTERN.matcher(sql).find()) {
            return false;
        }
        
        return true;
    }
}
