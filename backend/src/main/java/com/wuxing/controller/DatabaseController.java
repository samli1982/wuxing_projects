package com.wuxing.controller;

import com.wuxing.annotation.OperationLog;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.DatabaseQuery;
import com.wuxing.service.DatabaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据库查询控制器（仅超级管理员可用）
 */
@Tag(name = "数据库查询管理")
@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {
    
    private final DatabaseService databaseService;
    
    @Operation(summary = "获取所有表名")
    @GetMapping("/tables")
    @RequiresPermission("system:database:tables")
    @OperationLog(module = "数据库查询", operationType = "查询", description = "获取所有表名")
    public Result<List<String>> getAllTables() {
        try {
            List<String> tables = databaseService.getAllTables();
            return Result.success(tables);
        } catch (Exception e) {
            return Result.error("获取表列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取表结构")
    @GetMapping("/tables/{tableName}/structure")
    @RequiresPermission("system:database:structure")
    @OperationLog(module = "数据库查询", operationType = "查询", description = "获取表结构")
    public Result<List<Map<String, Object>>> getTableStructure(@PathVariable String tableName) {
        try {
            List<Map<String, Object>> structure = databaseService.getTableStructure(tableName);
            return Result.success(structure);
        } catch (Exception e) {
            return Result.error("获取表结构失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取表统计信息")
    @GetMapping("/tables/{tableName}/stats")
    @RequiresPermission("system:database:stats")
    @OperationLog(module = "数据库查询", operationType = "查询", description = "获取表统计信息")
    public Result<Map<String, Object>> getTableStats(@PathVariable String tableName) {
        try {
            Map<String, Object> stats = databaseService.getTableStats(tableName);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取表统计信息失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "执行SQL查询")
    @PostMapping("/query")
    @RequiresPermission("system:database:query")
    @OperationLog(module = "数据库查询", operationType = "查询", description = "执行SQL查询")
    public Result<DatabaseQuery> executeQuery(
            @RequestParam String sql,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        try {
            DatabaseQuery result = databaseService.executeQuery(sql, pageNum, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("SQL执行失败: " + e.getMessage());
        }
    }
}
