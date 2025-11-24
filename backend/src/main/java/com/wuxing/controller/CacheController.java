package com.wuxing.controller;

import com.wuxing.annotation.OperationLog;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.CacheInfo;
import com.wuxing.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 缓存管理控制器（仅超级管理员可用）
 */
@Tag(name = "缓存管理")
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {
    
    private final CacheService cacheService;
    
    @Operation(summary = "获取缓存键列表")
    @GetMapping("/keys")
    @RequiresPermission("system:cache:list")
    @OperationLog(module = "缓存管理", operationType = "查询", description = "获取缓存键列表")
    public Result<List<String>> getKeys(@RequestParam(required = false) String pattern) {
        try {
            List<String> keys = cacheService.getKeys(pattern);
            return Result.success(keys);
        } catch (Exception e) {
            return Result.error("获取缓存键列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取缓存详情")
    @GetMapping("/{key}")
    @RequiresPermission("system:cache:query")
    @OperationLog(module = "缓存管理", operationType = "查询", description = "获取缓存详情")
    public Result<CacheInfo> getCacheInfo(@PathVariable String key) {
        try {
            CacheInfo cacheInfo = cacheService.getCacheInfo(key);
            return Result.success(cacheInfo);
        } catch (Exception e) {
            return Result.error("获取缓存详情失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "删除缓存")
    @DeleteMapping("/{key}")
    @RequiresPermission("system:cache:delete")
    @OperationLog(module = "缓存管理", operationType = "删除", description = "删除缓存")
    public Result<Boolean> deleteCache(@PathVariable String key) {
        try {
            Boolean result = cacheService.deleteCache(key);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("删除缓存失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "批量删除缓存")
    @DeleteMapping("/batch")
    @RequiresPermission("system:cache:delete")
    @OperationLog(module = "缓存管理", operationType = "删除", description = "批量删除缓存")
    public Result<Long> deleteCaches(@RequestBody List<String> keys) {
        try {
            Long count = cacheService.deleteCaches(keys);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("批量删除缓存失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "清空所有缓存")
    @DeleteMapping("/all")
    @RequiresPermission("system:cache:clear")
    @OperationLog(module = "缓存管理", operationType = "删除", description = "清空所有缓存")
    public Result<Void> clearAll() {
        try {
            cacheService.clearAll();
            return Result.success();
        } catch (Exception e) {
            return Result.error("清空缓存失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取Redis信息")
    @GetMapping("/info")
    @RequiresPermission("system:cache:info")
    @OperationLog(module = "缓存管理", operationType = "查询", description = "获取Redis信息")
    public Result<Map<String, Object>> getRedisInfo() {
        try {
            Map<String, Object> info = cacheService.getRedisInfo();
            return Result.success(info);
        } catch (Exception e) {
            return Result.error("获取Redis信息失败: " + e.getMessage());
        }
    }
}
