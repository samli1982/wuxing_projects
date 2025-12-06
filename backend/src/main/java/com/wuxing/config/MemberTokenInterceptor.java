package com.wuxing.config;

import com.wuxing.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 会员Token拦截器
 * 用于验证小程序端的会员请求
 */
@Component
@RequiredArgsConstructor
public class MemberTokenInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 如果是公开接口，直接放行
        if (isPublicEndpoint(requestURI)) {
            return true;
        }
        
        // 从请求头获取Token
        String token = getTokenFromRequest(request);
        
        // 验证Token
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            // Token有效，放行请求
            return true;
        }
        
        // Token无效，返回401错误
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"code\":401,\"message\":\"未授权访问\",\"data\":null}");
        return false;
    }

    /**
     * 判断是否为公开接口
     */
    private boolean isPublicEndpoint(String requestURI) {
        // 公开接口列表
        String[] publicEndpoints = {
            "/",
            "/api/user/login",
            "/api/user/register",
            "/api/app/auth/login",
            "/api/health",
            "/api/home/"
        };
        
        // Swagger相关接口
        String[] swaggerEndpoints = {
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/swagger-resources/",
            "/webjars/",
            "/favicon.ico",
            "/error"
        };
        
        // 检查是否为公开接口
        for (String endpoint : publicEndpoints) {
            if (requestURI.equals(endpoint) || requestURI.startsWith(endpoint)) {
                return true;
            }
        }
        
        // 检查是否为Swagger接口
        for (String endpoint : swaggerEndpoints) {
            if (requestURI.startsWith(endpoint)) {
                return true;
            }
        }
        
        return false;
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