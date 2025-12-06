package com.wuxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxing.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员Mapper接口
 * 
 * @author wuxing
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
    
    /**
     * 根据openid查找会员
     * 
     * @param openid 微信openid
     * @return 会员信息
     */
    Member findByOpenid(String openid);
}