package com.wuxing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author wuxing
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 根据角色编码查询角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Role findByRoleCode(String roleCode);
    
    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> findByUserId(Long userId);
}
