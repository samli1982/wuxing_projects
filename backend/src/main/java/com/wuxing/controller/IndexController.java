package com.wuxing.controller;

import com.wuxing.dto.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * 
 * @author wuxing
 */
@RestController
public class IndexController {

    /**
     * 欢迎页面
     */
    @GetMapping("/")
    public Result<Map<String, Object>> index() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "五行项目API服务");
        data.put("version", "1.0.0");
        data.put("status", "running");
        data.put("time", LocalDateTime.now());
        data.put("documentation", "http://localhost:8080/doc.html");
        return Result.success(data);
    }
}
