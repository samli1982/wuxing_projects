package com.wuxing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {
    
    /**
     * 访问密钥ID
     */
    private String accessKeyId;
    
    /**
     * 访问密钥Secret
     */
    private String accessKeySecret;
    
    /**
     * Bucket名称
     */
    private String bucketName;
    
    /**
     * 地域节点
     */
    private String endpoint;
    
    /**
     * 自定义域名（可选）
     */
    private String domain;
    
    /**
     * 文件存储目录前缀
     */
    private String prefix = "wuxing/";
}
