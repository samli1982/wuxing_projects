package com.wuxing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.dto.request.PermissionRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.Permission;
import com.wuxing.entity.RolePermission;
import com.wuxing.exception.BusinessException;
import com.wuxing.mapper.RolePermissionMapper;
import com.wuxing.service.PermissionService;
import com.wuxing.utils.JwtUtil;
import com.wuxing.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限控制器
 * 
 * @author wuxing
 */
@Tag(name = "权限管理", description = "权限相关接口")
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;
    private final RolePermissionMapper rolePermissionMapper;
    private final JwtUtil jwtUtil;

    @Operation(summary = "权限列表")
    @GetMapping("/list")
    @RequiresPermission("system:permission:list")
    public Result<IPage<Permission>> list(PageRequest request) {
        Page<Permission> page = new Page<>(request.getCurrent(), request.getSize());
        
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSort, Permission::getId);
        
        IPage<Permission> result = permissionService.page(page, wrapper);
        return ResultUtil.success(result);
    }

    @Operation(summary = "权限树")
    @GetMapping("/tree")
    public Result<List<Permission>> tree() {
        List<Permission> allPermissions = permissionService.list();
        List<Permission> tree = permissionService.buildPermissionTree(allPermissions);
        return ResultUtil.success(tree);
    }

    @Operation(summary = "权限详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:permission:query")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return ResultUtil.success(permission);
    }

    @Operation(summary = "新增权限")
    @PostMapping
    @RequiresPermission("system:permission:add")
    public Result<Void> create(@Valid @RequestBody PermissionRequest request) {
        // 检查权限编码是否已存在
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, request.getPermissionCode());
        if (permissionService.count(wrapper) > 0) {
            throw new BusinessException("权限编码已存在");
        }
        
        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);
        
        // 设置默认值
        if (permission.getPermissionType() == null) {
            permission.setPermissionType(1); // 默认为菜单
        }
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        if (permission.getSort() == null) {
            permission.setSort(0);
        }
        
        permissionService.save(permission);
        return ResultUtil.success();
    }

    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    @RequiresPermission("system:permission:edit")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PermissionRequest request) {
        Permission existPermission = permissionService.getById(id);
        if (existPermission == null) {
            throw new BusinessException("权限不存在");
        }
        
        // 如果修改了权限编码，检查新编码是否已存在
        if (!existPermission.getPermissionCode().equals(request.getPermissionCode())) {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Permission::getPermissionCode, request.getPermissionCode());
            wrapper.ne(Permission::getId, id);
            if (permissionService.count(wrapper) > 0) {
                throw new BusinessException("权限编码已存在");
            }
        }
        
        Permission permission = new Permission();
        BeanUtils.copyProperties(request, permission);
        permission.setId(id);
        
        permissionService.updateById(permission);
        return ResultUtil.success();
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:permission:delete")
    public Result<Void> delete(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        
        // 检查是否有子权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        if (permissionService.count(wrapper) > 0) {
            throw new BusinessException("该权限下存在子权限，无法删除");
        }
        
        permissionService.removeById(id);
        return ResultUtil.success();
    }

    @Operation(summary = "获取角色的权限列表")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getByRoleId(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.findByRoleId(roleId);
        return ResultUtil.success(permissions);
    }

    @Operation(summary = "分配权限给角色")
    @PostMapping("/role/{roleId}")
    @RequiresPermission("system:role:assign")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        // 先删除该角色的所有权限
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 批量插入新的权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        RolePermission rp = new RolePermission();
                        rp.setRoleId(roleId);
                        rp.setPermissionId(permissionId);
                        return rp;
                    })
                    .collect(Collectors.toList());
            
            for (RolePermission rp : rolePermissions) {
                rolePermissionMapper.insert(rp);
            }
        }
        
        return ResultUtil.success();
    }

    @Operation(summary = "获取当前用户的权限列表")
    @GetMapping("/current")
    public Result<List<Permission>> getCurrentUserPermissions(HttpServletRequest request) {
        // 从Token中获取用户ID
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            return ResultUtil.success(List.of());
        }
        
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<Permission> permissions = permissionService.findByUserId(userId);
            return ResultUtil.success(permissions);
        } catch (Exception e) {
            return ResultUtil.success(List.of());
        }
    }
    
    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
