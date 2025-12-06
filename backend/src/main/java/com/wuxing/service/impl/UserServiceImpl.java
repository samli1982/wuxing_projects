package com.wuxing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.entity.User;
import com.wuxing.exception.BusinessException;
import com.wuxing.mapper.UserMapper;
import com.wuxing.service.UserService;
import com.wuxing.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 * 
 * @author wuxing
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public User findByOpenid(String openid) {
        return userMapper.findByOpenid(openid);
    }
    
    @Override
    public String generateToken(Long userId, String username) {
        return jwtUtil.generateToken(userId, username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(User user) {
        // 检查用户名是否存在
        User existUser = findByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        
        return save(user);
    }

    @Override
    public String login(String username, String password) {
        // 查询用户
        System.out.println("Debug: 开始登录 - 用户名: " + username);
        User user = findByUsername(username);
        System.out.println("Debug: 查询到用户: " + (user != null));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        System.out.println("Debug: 输入密码: " + password);
        System.out.println("Debug: 存储密码: " + user.getPassword());
        
        // 验证密码
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("Debug: 密码匹配结果: " + matches);
        if (!matches) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成JWT Token
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }
}
