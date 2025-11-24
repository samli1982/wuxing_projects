package com.wuxing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.OperationLog;
import com.wuxing.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/api/log/operation")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    @Operation(summary = "操作日志分页列表")
    @GetMapping("/list")
    @RequiresPermission("system:log:list")
    public Result<IPage<OperationLog>> list(PageRequest request,
                                              @RequestParam(required = false) String module,
                                              @RequestParam(required = false) String operator,
                                              @RequestParam(required = false) String startTime,
                                              @RequestParam(required = false) String endTime) {
        IPage<OperationLog> page = operationLogService.page(request, module, operator, startTime, endTime);
        return Result.success(page);
    }
    
    @Operation(summary = "操作日志详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:log:query")
    public Result<OperationLog> getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return Result.success(log);
    }
    
    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:log:delete")
    public Result<Void> delete(@PathVariable Long id) {
        operationLogService.removeById(id);
        return Result.success();
    }
}
