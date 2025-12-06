package com.wuxing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final MemberTokenInterceptor memberTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册会员Token拦截器，拦截所有API请求
        registry.addInterceptor(memberTokenInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/app/auth/login",
                        "/api/health",
                        "/api/home/**"
                );
    }
}