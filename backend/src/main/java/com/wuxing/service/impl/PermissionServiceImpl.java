package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.entity.Permission;
import com.wuxing.exception.BusinessException;
import com.wuxing.mapper.PermissionMapper;
import com.wuxing.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 * 
 * @author wuxing
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public Permission findByPermissionCode(String permissionCode) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionCode);
        Permission permission = this.getOne(wrapper);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return permission;
    }

    @Override
    public List<Permission> findByRoleId(Long roleId) {
        return baseMapper.findByRoleId(roleId);
    }

    @Override
    public List<Permission> findByUserId(Long userId) {
        return baseMapper.findByUserId(userId);
    }

    @Override
    public List<Permission> buildPermissionTree(List<Permission> permissions) {
        // 获取所有顶级权限（parentId = 0）
        List<Permission> rootPermissions = permissions.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .collect(Collectors.toList());
        
        // 递归构建树形结构
        for (Permission root : rootPermissions) {
            buildChildren(root, permissions);
        }
        
        return rootPermissions;
    }
    
    /**
     * 递归构建子权限
     */
    private void buildChildren(Permission parent, List<Permission> allPermissions) {
        List<Permission> children = allPermissions.stream()
                .filter(p -> parent.getId().equals(p.getParentId()))
                .collect(Collectors.toList());
        
        // 设置子权限列表
        parent.setChildren(children);
        
        // 递归构建子节点的子权限
        if (!children.isEmpty()) {
            for (Permission child : children) {
                buildChildren(child, allPermissions);
            }
        }
    }
}
