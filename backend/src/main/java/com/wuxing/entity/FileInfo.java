package com.wuxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
@Schema(description = "文件信息")
public class FileInfo extends BaseEntity {
    
    @Schema(description = "文件原始名称")
    private String fileName;
    
    @Schema(description = "文件存储名称")
    private String fileKey;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "文件大小(字节)")
    private Long fileSize;
    
    @Schema(description = "文件类型")
    private String fileType;
    
    @Schema(description = "存储类型 1-本地 2-OSS")
    private Integer storageType;
    
    @Schema(description = "上传人")
    private String uploader;
    
    @Schema(description = "上传人ID")
    private Long uploaderId;
    
    @Schema(description = "文件分类")
    private String category;
    
    @Schema(description = "备注")
    private String remark;
}
