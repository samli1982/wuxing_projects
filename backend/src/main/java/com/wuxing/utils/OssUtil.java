package com.wuxing.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.wuxing.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssUtil {
    
    private final OssConfig ossConfig;
    
    /**
     * 上传文件到OSS
     *
     * @param file 文件
     * @param folder 文件夹路径
     * @return 文件URL
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String objectName = ossConfig.getPrefix() + folder + "/" + fileName;
        
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
        
        InputStream inputStream = null;
        try {
            // 获取文件流
            inputStream = file.getInputStream();
            
            // 直接上传，不使用PutObjectRequest构造器
            ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream);
            
            // 返回文件URL
            if (ossConfig.getDomain() != null && !ossConfig.getDomain().isEmpty()) {
                return ossConfig.getDomain() + "/" + objectName;
            } else {
                return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectName;
            }
        } finally {
            // 关闭流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
            ossClient.shutdown();
        }
    }
    
    /**
     * 删除OSS文件
     *
     * @param fileUrl 文件URL
     */
    public void delete(String fileUrl) {
        try {
            // 从URL中提取objectName
            String objectName;
            if (ossConfig.getDomain() != null && !ossConfig.getDomain().isEmpty()) {
                objectName = fileUrl.replace(ossConfig.getDomain() + "/", "");
            } else {
                String prefix = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/";
                objectName = fileUrl.replace(prefix, "");
            }
            
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(
                    ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
            );
            
            try {
                ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            } finally {
                ossClient.shutdown();
            }
        } catch (Exception e) {
            log.error("删除OSS文件失败: {}", fileUrl, e);
        }
    }
}
