package com.wuxing.aspect;

import com.wuxing.annotation.RequiresPermission;
import com.wuxing.entity.Permission;
import com.wuxing.exception.BusinessException;
import com.wuxing.service.PermissionService;
import com.wuxing.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限验证切面
 * 
 * @author wuxing
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final JwtUtil jwtUtil;
    private final PermissionService permissionService;

    @Before("@annotation(requiresPermission)")
    public void checkPermission(JoinPoint joinPoint, RequiresPermission requiresPermission) {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException("无法获取请求信息");
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 从Token中获取用户ID
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("未登录或token已过期");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        // 获取用户的所有权限
        List<Permission> userPermissions = permissionService.findByUserId(userId);
        Set<String> permissionCodes = userPermissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
        
        // 获取注解中要求的权限
        String[] requiredPermissions = requiresPermission.value();
        RequiresPermission.Logical logical = requiresPermission.logical();
        
        // 验证权限
        boolean hasPermission;
        if (logical == RequiresPermission.Logical.AND) {
            // 需要拥有所有权限
            hasPermission = permissionCodes.containsAll(Arrays.asList(requiredPermissions));
        } else {
            // 拥有任一权限即可
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(permissionCodes::contains);
        }
        
        if (!hasPermission) {
            log.warn("用户 {} 缺少权限: {}", userId, Arrays.toString(requiredPermissions));
            throw new BusinessException("权限不足，无法执行此操作");
        }
        
        log.debug("用户 {} 权限验证通过: {}", userId, Arrays.toString(requiredPermissions));
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
