package com.wuxing.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuxing.annotation.OperationLog;
import com.wuxing.service.OperationLogService;
import com.wuxing.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final OperationLogService operationLogService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    
    @Around("@annotation(com.wuxing.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        
        // 创建日志对象
        com.wuxing.entity.OperationLog operationLog = new com.wuxing.entity.OperationLog();
        operationLog.setModule(annotation.module());
        operationLog.setOperationType(annotation.operationType());
        operationLog.setDescription(annotation.description());
        operationLog.setMethod(method.getName());
        
        // 获取用户信息
        if (request != null) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    String username = jwtUtil.getUsernameFromToken(token);
                    operationLog.setOperator(username);
                    // 这里简化处理，实际应该从数据库获取用户ID
                    operationLog.setOperatorId(1L);
                } catch (Exception e) {
                    operationLog.setOperator("未知用户");
                }
            }
            
            // 获取IP
            operationLog.setIp(getIpAddress(request));
        }
        
        // 获取请求参数
        try {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                operationLog.setParams(objectMapper.writeValueAsString(args));
            }
        } catch (Exception e) {
            operationLog.setParams("参数序列化失败");
        }
        
        // 执行方法
        Object result = null;
        try {
            result = point.proceed();
            operationLog.setStatus(1);
            
            // 记录返回结果
            try {
                String resultStr = objectMapper.writeValueAsString(result);
                if (resultStr.length() > 2000) {
                    resultStr = resultStr.substring(0, 2000) + "...";
                }
                operationLog.setResult(resultStr);
            } catch (Exception e) {
                operationLog.setResult("结果序列化失败");
            }
        } catch (Exception e) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时长
            long duration = System.currentTimeMillis() - startTime;
            operationLog.setDuration(duration);
            
            // 异步保存日志
            try {
                operationLogService.save(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
        
        return result;
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
