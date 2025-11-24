package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.entity.Role;
import com.wuxing.exception.BusinessException;
import com.wuxing.mapper.RoleMapper;
import com.wuxing.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 * 
 * @author wuxing
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public Role findByRoleCode(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        Role role = this.getOne(wrapper);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }
    
    @Override
    public List<Role> findByUserId(Long userId) {
        return roleMapper.findByUserId(userId);
    }
}
