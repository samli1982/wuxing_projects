package com.wuxing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.entity.Member;

/**
 * 会员服务接口
 * 
 * @author wuxing
 */
public interface MemberService extends IService<Member> {
    
    /**
     * 根据openid查找会员
     * 
     * @param openid 微信openid
     * @return 会员信息
     */
    Member findByOpenid(String openid);
    
    /**
     * 保存会员信息
     * 
     * @param member 会员信息
     * @return 是否保存成功
     */
    boolean saveMember(Member member);
    
    /**
     * 根据ID查找会员
     * 
     * @param id 会员ID
     * @return 会员信息
     */
    Member findById(Long id);
    
    /**
     * 更新会员信息
     * 
     * @param member 会员信息
     * @return 是否更新成功
     */
    boolean updateMember(Member member);
}