package com.wuxing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.entity.Member;
import com.wuxing.mapper.MemberMapper;
import com.wuxing.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员服务实现类
 * 
 * @author wuxing
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
    
    private final MemberMapper memberMapper;
    
    @Override
    public Member findByOpenid(String openid) {
        return memberMapper.findByOpenid(openid);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveMember(Member member) {
        member.setStatus(1); // 默认正常状态
        return save(member);
    }
    
    @Override
    public Member findById(Long id) {
        return getById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMember(Member member) {
        return updateById(member);
    }
}