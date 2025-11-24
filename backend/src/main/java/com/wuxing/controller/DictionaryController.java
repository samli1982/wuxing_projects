package com.wuxing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.Dictionary;
import com.wuxing.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 字典管理控制器
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/api/dictionary")
@RequiredArgsConstructor
public class DictionaryController {
    
    private final DictionaryService dictionaryService;
    
    @Operation(summary = "字典分页列表")
    @GetMapping("/list")
    @RequiresPermission("system:dict:list")
    public Result<IPage<Dictionary>> list(PageRequest request,
                                           @RequestParam(required = false) String dictType,
                                           @RequestParam(required = false) String dictKey) {
        IPage<Dictionary> page = dictionaryService.page(request, dictType, dictKey);
        return Result.success(page);
    }
    
    @Operation(summary = "根据类型获取字典")
    @GetMapping("/type/{dictType}")
    public Result<List<Dictionary>> getByType(@PathVariable String dictType) {
        List<Dictionary> list = dictionaryService.listByType(dictType);
        return Result.success(list);
    }
    
    @Operation(summary = "获取所有字典类型")
    @GetMapping("/types")
    public Result<List<Dictionary>> getTypes() {
        List<Dictionary> types = dictionaryService.listTypes();
        return Result.success(types);
    }
    
    @Operation(summary = "获取字典Map")
    @GetMapping("/map")
    public Result<Map<String, List<Dictionary>>> getDictMap() {
        Map<String, List<Dictionary>> map = dictionaryService.getDictMap();
        return Result.success(map);
    }
    
    @Operation(summary = "字典详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:dict:query")
    public Result<Dictionary> getById(@PathVariable Long id) {
        Dictionary dict = dictionaryService.getById(id);
        return Result.success(dict);
    }
    
    @Operation(summary = "新增字典")
    @PostMapping
    @RequiresPermission("system:dict:add")
    @com.wuxing.annotation.OperationLog(module = "字典管理", operationType = "新增", description = "新增字典")
    public Result<Void> add(@RequestBody Dictionary dictionary) {
        dictionaryService.save(dictionary);
        return Result.success();
    }
    
    @Operation(summary = "更新字典")
    @PutMapping("/{id}")
    @RequiresPermission("system:dict:edit")
    @com.wuxing.annotation.OperationLog(module = "字典管理", operationType = "编辑", description = "更新字典")
    public Result<Void> update(@PathVariable Long id, @RequestBody Dictionary dictionary) {
        dictionary.setId(id);
        dictionaryService.updateById(dictionary);
        return Result.success();
    }
    
    @Operation(summary = "删除字典")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:dict:delete")
    @com.wuxing.annotation.OperationLog(module = "字典管理", operationType = "删除", description = "删除字典")
    public Result<Void> delete(@PathVariable Long id) {
        dictionaryService.removeById(id);
        return Result.success();
    }
    
    @Operation(summary = "批量删除字典")
    @DeleteMapping("/batch")
    @RequiresPermission("system:dict:delete")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        dictionaryService.removeByIds(ids);
        return Result.success();
    }
}
