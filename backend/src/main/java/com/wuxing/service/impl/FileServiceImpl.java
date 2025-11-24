package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.FileInfo;
import com.wuxing.mapper.FileMapper;
import com.wuxing.service.FileService;
import com.wuxing.utils.OssUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务实现
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {
    
    private final OssUtil ossUtil;
    
    @Override
    public IPage<FileInfo> page(PageRequest request, String fileName, String category) {
        Page<FileInfo> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(fileName)) {
            wrapper.like(FileInfo::getFileName, fileName);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(FileInfo::getCategory, category);
        }
        
        wrapper.orderByDesc(FileInfo::getCreateTime);
        return this.page(page, wrapper);
    }
    
    @Override
    public FileInfo upload(MultipartFile file, String category) throws Exception {
        // 上传到OSS
        String fileUrl = ossUtil.upload(file, category != null ? category : "default");
        
        // 保存文件信息
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getOriginalFilename());
        fileInfo.setFileKey(fileUrl);
        fileInfo.setFileUrl(fileUrl);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setStorageType(2); // 2-OSS
        fileInfo.setCategory(category);
        
        this.save(fileInfo);
        return fileInfo;
    }
    
    @Override
    public void deleteFile(Long id) {
        FileInfo fileInfo = this.getById(id);
        if (fileInfo != null) {
            // 删除OSS文件
            if (fileInfo.getStorageType() == 2) {
                ossUtil.delete(fileInfo.getFileUrl());
            }
            // 删除数据库记录
            this.removeById(id);
        }
    }
}
