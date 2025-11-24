package com.wuxing.controller;

import com.wuxing.dto.response.Result;
import com.wuxing.entity.Permission;
import com.wuxing.service.MenuService;
import com.wuxing.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;
    private final JwtUtil jwtUtil;
    
    @Operation(summary = "获取当前用户菜单树")
    @GetMapping("/tree")
    public Result<List<Permission>> getMenuTree(HttpServletRequest request) {
        // 从token中获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            List<Permission> menuTree = menuService.getMenuTree(userId);
            return Result.success(menuTree);
        }
        return Result.error("未登录");
    }
    
    @Operation(summary = "获取所有菜单树（管理用）")
    @GetMapping("/tree/all")
    public Result<List<Permission>> getAllMenuTree() {
        List<Permission> menuTree = menuService.getAllMenuTree();
        return Result.success(menuTree);
    }
}
