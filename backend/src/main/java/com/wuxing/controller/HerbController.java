package com.wuxing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.Herb;
import com.wuxing.service.HerbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药精管理控制器
 */
@Tag(name = "药精管理")
@RestController
@RequestMapping("/api/herb")
@RequiredArgsConstructor
public class HerbController {
    
    private final HerbService herbService;
    
    @Operation(summary = "分页查询药精")
    @GetMapping("/list")
    public Result<IPage<Herb>> list(PageRequest request,
                                    @RequestParam(required = false) String element,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Integer status) {
        IPage<Herb> page = herbService.page(request, element, category, keyword, status);
        return Result.success(page);
    }
    
    @Operation(summary = "根据五行分类查询药精")
    @GetMapping("/element/{element}")
    public Result<List<Herb>> listByElement(@PathVariable String element) {
        List<Herb> list = herbService.listByElement(element);
        return Result.success(list);
    }
    
    @Operation(summary = "根据子分类查询药精")
    @GetMapping("/category/{category}")
    public Result<List<Herb>> listByCategory(@PathVariable String category) {
        List<Herb> list = herbService.listByCategory(category);
        return Result.success(list);
    }
    
    @Operation(summary = "搜索药精")
    @GetMapping("/search")
    public Result<List<Herb>> search(@RequestParam String keyword) {
        List<Herb> list = herbService.search(keyword);
        return Result.success(list);
    }
    
    @Operation(summary = "获取药精详情")
    @GetMapping("/{id}")
    public Result<Herb> getById(@PathVariable Long id) {
        Herb herb = herbService.getDetail(id);
        return Result.success(herb);
    }
    
    @Operation(summary = "新增药精")
    @PostMapping
    public Result<Void> add(@RequestBody Herb herb) {
        herbService.save(herb);
        return Result.success();
    }
    
    @Operation(summary = "编辑药精")
    @PutMapping("/{id}")
    public Result<Void> edit(@PathVariable Long id, @RequestBody Herb herb) {
        herb.setId(id);
        herbService.updateById(herb);
        return Result.success();
    }
    
    @Operation(summary = "删除药精")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        herbService.removeById(id);
        return Result.success();
    }
    
    @Operation(summary = "批量删除药精")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        herbService.removeByIds(ids);
        return Result.success();
    }
}
