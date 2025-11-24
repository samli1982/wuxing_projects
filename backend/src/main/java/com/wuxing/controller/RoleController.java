package com.wuxing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.request.RoleQueryRequest;
import com.wuxing.dto.request.RoleRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.Role;
import com.wuxing.exception.BusinessException;
import com.wuxing.service.RoleService;
import com.wuxing.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 角色控制器
 * 
 * @author wuxing
 */
@Tag(name = "角色管理", description = "角色相关接口")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "角色列表")
    @GetMapping("/list")
    @RequiresPermission("system:role:list")
    public Result<IPage<Role>> list(RoleQueryRequest request) {
        Page<Role> page = new Page<>(request.getCurrent(), request.getSize());
        
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        // 角色名称模糊查询
        if (StringUtils.hasText(request.getRoleName())) {
            wrapper.like(Role::getRoleName, request.getRoleName());
        }
        
        // 角色编码精确查询
        if (StringUtils.hasText(request.getRoleCode())) {
            wrapper.eq(Role::getRoleCode, request.getRoleCode());
        }
        
        // 状态查询
        if (request.getStatus() != null) {
            wrapper.eq(Role::getStatus, request.getStatus());
        }
        
        // 按创建时间降序
        wrapper.orderByDesc(Role::getCreateTime);
        
        IPage<Role> result = roleService.page(page, wrapper);
        return ResultUtil.success(result);
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:role:query")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return ResultUtil.success(role);
    }

    @Operation(summary = "新增角色")
    @PostMapping
    @RequiresPermission("system:role:add")
    public Result<Void> create(@Valid @RequestBody RoleRequest request) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, request.getRoleCode());
        if (roleService.count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }
        
        Role role = new Role();
        BeanUtils.copyProperties(request, role);
        
        // 设置默认值
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getSort() == null) {
            role.setSort(0);
        }
        
        roleService.save(role);
        return ResultUtil.success();
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @RequiresPermission("system:role:edit")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        Role existRole = roleService.getById(id);
        if (existRole == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 如果修改了角色编码，检查新编码是否已存在
        if (!existRole.getRoleCode().equals(request.getRoleCode())) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getRoleCode, request.getRoleCode());
            wrapper.ne(Role::getId, id);
            if (roleService.count(wrapper) > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }
        
        Role role = new Role();
        BeanUtils.copyProperties(request, role);
        role.setId(id);
        
        roleService.updateById(role);
        return ResultUtil.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:role:delete")
    public Result<Void> delete(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        roleService.removeById(id);
        return ResultUtil.success();
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody java.util.List<Long> ids) {
        roleService.removeByIds(ids);
        return ResultUtil.success();
    }
}
