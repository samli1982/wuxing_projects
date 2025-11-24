package com.wuxing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuxing.annotation.OperationLog;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.FileInfo;
import com.wuxing.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理控制器
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    @Operation(summary = "文件分页列表")
    @GetMapping("/list")
    @RequiresPermission("system:file:list")
    public Result<IPage<FileInfo>> list(PageRequest request,
                                         @RequestParam(required = false) String fileName,
                                         @RequestParam(required = false) String category) {
        IPage<FileInfo> page = fileService.page(request, fileName, category);
        return Result.success(page);
    }
    
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @RequiresPermission("system:file:upload")
    @OperationLog(module = "文件管理", operationType = "上传", description = "上传文件")
    public Result<FileInfo> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(required = false) String category) {
        try {
            FileInfo fileInfo = fileService.upload(file, category);
            return Result.success(fileInfo);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "文件详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:file:query")
    public Result<FileInfo> getById(@PathVariable Long id) {
        FileInfo fileInfo = fileService.getById(id);
        return Result.success(fileInfo);
    }
    
    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:file:delete")
    @OperationLog(module = "文件管理", operationType = "删除", description = "删除文件")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.success();
    }
}
