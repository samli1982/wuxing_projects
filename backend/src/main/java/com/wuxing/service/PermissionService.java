package com.wuxing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 * 
 * @author wuxing
 */
public interface PermissionService extends IService<Permission> {
    
    /**
     * 根据权限编码查询权限
     */
    Permission findByPermissionCode(String permissionCode);
    
    /**
     * 根据角色ID查询权限列表
     */
    List<Permission> findByRoleId(Long roleId);
    
    /**
     * 根据用户ID查询权限列表
     */
    List<Permission> findByUserId(Long userId);
    
    /**
     * 构建权限树
     */
    List<Permission> buildPermissionTree(List<Permission> permissions);
}
