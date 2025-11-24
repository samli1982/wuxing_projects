package com.wuxing.service;

import com.wuxing.entity.Permission;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService {
    
    /**
     * 获取菜单树（根据用户权限）
     */
    List<Permission> getMenuTree(Long userId);
    
    /**
     * 获取所有菜单树（管理用）
     */
    List<Permission> getAllMenuTree();
    
    /**
     * 构建树形结构
     */
    List<Permission> buildTree(List<Permission> permissions);
}
