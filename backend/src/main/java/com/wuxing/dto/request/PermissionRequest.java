package com.wuxing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 权限请求
 * 
 * @author wuxing
 */
@Data
public class PermissionRequest {
    
    /**
     * 权限ID（编辑时需要）
     */
    private Long id;
    
    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;
    
    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;
    
    /**
     * 权限类型 1菜单 2按钮
     */
    private Integer permissionType;
    
    /**
     * 父权限ID
     */
    private Long parentId;
    
    /**
     * 路由地址
     */
    private String path;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 状态（0禁用 1正常）
     */
    private Integer status;
}
