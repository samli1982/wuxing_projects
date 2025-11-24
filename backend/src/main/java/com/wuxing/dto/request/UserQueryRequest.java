package com.wuxing.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 * 
 * @author wuxing
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 状态
     */
    private Integer status;
}
