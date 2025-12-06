package com.wuxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 药精实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("herb_info")
@Schema(description = "药精信息")
public class Herb extends BaseEntity {
    
    @Schema(description = "药精编号 例如: 1-①")
    private String number;
    
    @Schema(description = "药精名称")
    private String name;
    
    @Schema(description = "别名，多个别名用逗号分隔")
    private String alias;
    
    @Schema(description = "五行分类（木/火/土/金/水）")
    private String element;
    
    @Schema(description = "子分类 例如: 木中木、木中火等")
    private String category;
    
    @Schema(description = "分类图标emoji")
    private String categoryIcon;
    
    @Schema(description = "性味（寒/温/热/凉/平），多个用逗号分隔")
    private String properties;
    
    @Schema(description = "五味（酸/苦/甘/辛/咸），多个用逗号分隔")
    private String taste;
    
    @Schema(description = "性质分类（hot/warm/neutral/cool/cold）")
    private String natureClass;
    
    @Schema(description = "功效，多个用逗号分隔")
    private String effects;
    
    @Schema(description = "详细描述")
    private String description;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "状态 0-禁用 1-启用")
    private Integer status;
    
    @Schema(description = "备注")
    private String remark;
}
