package com.wuxing.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色查询请求
 * 
 * @author wuxing
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryRequest extends PageRequest {
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色编码
     */
    private String roleCode;
    
    /**
     * 状态
     */
    private Integer status;
}
