package com.wuxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dictionary")
@Schema(description = "系统字典")
public class Dictionary extends BaseEntity {
    
    @Schema(description = "字典类型")
    private String dictType;
    
    @Schema(description = "字典类型名称")
    private String dictTypeName;
    
    @Schema(description = "字典键")
    private String dictKey;
    
    @Schema(description = "字典值")
    private String dictValue;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "状态 0-禁用 1-启用")
    private Integer status;
    
    @Schema(description = "备注")
    private String remark;
}
