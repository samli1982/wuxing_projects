package com.wuxing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuxing.annotation.RequiresPermission;
import com.wuxing.dto.request.LoginRequest;
import com.wuxing.dto.request.RegisterRequest;
import com.wuxing.dto.request.UserQueryRequest;
import com.wuxing.dto.response.Result;
import com.wuxing.entity.Role;
import com.wuxing.entity.User;
import com.wuxing.entity.UserRole;
import com.wuxing.exception.BusinessException;
import com.wuxing.mapper.UserRoleMapper;
import com.wuxing.service.RoleService;
import com.wuxing.service.UserService;
import com.wuxing.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 
 * @author wuxing
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleMapper userRoleMapper;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    @RequiresPermission("system:user:add")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        userService.register(user);
        return ResultUtil.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        
        return ResultUtil.success(data);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        User user = userService.getById(userId);
        return ResultUtil.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    @RequiresPermission("system:user:edit")
    public Result<Void> updateUser(@Valid @RequestBody User user) {
        userService.updateById(user);
        return ResultUtil.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @RequiresPermission("system:user:delete")
    public Result<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userService.removeById(id);
        return ResultUtil.success();
    }

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    @RequiresPermission("system:user:list")
    public Result<IPage<User>> list(UserQueryRequest request) {
        Page<User> page = new Page<>(request.getCurrent(), request.getSize());
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 用户名模糊查询
        if (StringUtils.hasText(request.getUsername())) {
            wrapper.like(User::getUsername, request.getUsername());
        }
        
        // 昵称模糊查询
        if (StringUtils.hasText(request.getNickname())) {
            wrapper.like(User::getNickname, request.getNickname());
        }
        
        // 邮箱精确查询
        if (StringUtils.hasText(request.getEmail())) {
            wrapper.eq(User::getEmail, request.getEmail());
        }
        
        // 手机号精确查询
        if (StringUtils.hasText(request.getPhone())) {
            wrapper.eq(User::getPhone, request.getPhone());
        }
        
        // 状态查询
        if (request.getStatus() != null) {
            wrapper.eq(User::getStatus, request.getStatus());
        }
        
        // 按创建时间降序
        wrapper.orderByDesc(User::getCreateTime);
        
        IPage<User> result = userService.page(page, wrapper);
        
        // 清空密码字段
        result.getRecords().forEach(user -> user.setPassword(null));
        
        return ResultUtil.success(result);
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    @RequiresPermission("system:user:query")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清空密码
        user.setPassword(null);
        return ResultUtil.success(user);
    }
    
    @Operation(summary = "获取用户的角色列表")
    @GetMapping("/role/{userId}")
    @RequiresPermission("system:user:query")
    public Result<List<Role>> getUserRoles(@PathVariable Long userId) {
        List<Role> roles = roleService.findByUserId(userId);
        return ResultUtil.success(roles);
    }
    
    @Operation(summary = "为用户分配角色")
    @PostMapping("/role/{userId}")
    @RequiresPermission("system:user:edit")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        // 检查用户是否存在
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 先删除该用户的所有角色
        userRoleMapper.deleteByUserId(userId);
        
        // 批量插入新的角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                // 验证角色是否存在
                Role role = roleService.getById(roleId);
                if (role == null) {
                    throw new BusinessException("角色ID " + roleId + " 不存在");
                }
                
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
        
        return ResultUtil.success();
    }
}
