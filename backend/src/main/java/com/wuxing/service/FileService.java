package com.wuxing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService extends IService<FileInfo> {
    
    /**
     * 分页查询文件
     */
    IPage<FileInfo> page(PageRequest request, String fileName, String category);
    
    /**
     * 上传文件
     */
    FileInfo upload(MultipartFile file, String category) throws Exception;
    
    /**
     * 删除文件
     */
    void deleteFile(Long id);
}
