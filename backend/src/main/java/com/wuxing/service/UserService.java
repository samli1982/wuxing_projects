package com.wuxing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.entity.User;

/**
 * 用户服务接口
 * 
 * @author wuxing
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 用户注册
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(User user);
    
    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT Token
     */
    String login(String username, String password);
}
