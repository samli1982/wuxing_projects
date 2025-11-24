package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuxing.entity.Permission;
import com.wuxing.mapper.PermissionMapper;
import com.wuxing.service.MenuService;
import com.wuxing.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    
    private final PermissionMapper permissionMapper;
    private final PermissionService permissionService;
    
    @Override
    public List<Permission> getMenuTree(Long userId) {
        // 获取用户的权限列表
        List<Permission> permissions = permissionService.findByUserId(userId);
        
        // 只保留菜单类型（permissionType = 1）
        List<Permission> menuPermissions = permissions.stream()
                .filter(p -> p.getPermissionType() == 1)
                .filter(p -> p.getStatus() == 1) // 只显示启用的
                .sorted((a, b) -> {
                    // 按sort排序，如果sort相同则按id排序
                    int sortCompare = Integer.compare(
                        a.getSort() != null ? a.getSort() : 0, 
                        b.getSort() != null ? b.getSort() : 0
                    );
                    return sortCompare != 0 ? sortCompare : Long.compare(a.getId(), b.getId());
                })
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildTree(menuPermissions);
    }
    
    @Override
    public List<Permission> getAllMenuTree() {
        // 获取所有菜单类型的权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionType, 1)
                .orderByAsc(Permission::getSort);
        
        List<Permission> menuPermissions = permissionMapper.selectList(wrapper);
        
        // 构建树形结构
        return buildTree(menuPermissions);
    }
    
    @Override
    public List<Permission> buildTree(List<Permission> permissions) {
        List<Permission> tree = new ArrayList<>();
        
        // 找出所有根节点（parentId = 0）
        List<Permission> roots = permissions.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .collect(Collectors.toList());
        
        // 为每个根节点构建子树
        for (Permission root : roots) {
            root.setChildren(getChildren(root.getId(), permissions));
            tree.add(root);
        }
        
        return tree;
    }
    
    /**
     * 递归获取子节点
     */
    private List<Permission> getChildren(Long parentId, List<Permission> all) {
        List<Permission> children = all.stream()
                .filter(p -> parentId.equals(p.getParentId()))
                .sorted((a, b) -> {
                    // 按sort排序，如果sort相同则按id排序
                    int sortCompare = Integer.compare(
                        a.getSort() != null ? a.getSort() : 0, 
                        b.getSort() != null ? b.getSort() : 0
                    );
                    return sortCompare != 0 ? sortCompare : Long.compare(a.getId(), b.getId());
                })
                .collect(Collectors.toList());
        
        // 递归设置子节点的children
        for (Permission child : children) {
            child.setChildren(getChildren(child.getId(), all));
        }
        
        return children;
    }
}
