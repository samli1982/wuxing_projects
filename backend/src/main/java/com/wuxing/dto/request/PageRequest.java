package com.wuxing.dto.request;

import lombok.Data;

/**
 * 分页请求基类
 * 
 * @author wuxing
 */
@Data
public class PageRequest {
    
    /**
     * 当前页码
     */
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
}
