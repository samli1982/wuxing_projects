package com.wuxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operation_log")
@Schema(description = "操作日志")
public class OperationLog extends BaseEntity {
    
    @Schema(description = "操作模块")
    private String module;
    
    @Schema(description = "操作类型")
    private String operationType;
    
    @Schema(description = "操作描述")
    private String description;
    
    @Schema(description = "请求方法")
    private String method;
    
    @Schema(description = "请求参数")
    private String params;
    
    @Schema(description = "返回结果")
    private String result;
    
    @Schema(description = "操作IP")
    private String ip;
    
    @Schema(description = "操作地址")
    private String location;
    
    @Schema(description = "操作人")
    private String operator;
    
    @Schema(description = "操作人ID")
    private Long operatorId;
    
    @Schema(description = "执行时长(毫秒)")
    private Long duration;
    
    @Schema(description = "状态 0失败 1成功")
    private Integer status;
    
    @Schema(description = "错误信息")
    private String errorMsg;
}
