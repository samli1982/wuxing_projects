package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.entity.PalmtreeDetail;
import com.wuxing.mapper.PalmtreeDetailMapper;
import com.wuxing.service.PalmtreeDetailService;
import org.springframework.stereotype.Service;

/**
 * 命盘详情服务实现
 */
@Service
public class PalmtreeDetailServiceImpl extends ServiceImpl<PalmtreeDetailMapper, PalmtreeDetail> implements PalmtreeDetailService {
    
    @Override
    public PalmtreeDetail getByPalmtreeId(Long palmtreeId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<PalmtreeDetail>()
            .eq(PalmtreeDetail::getPalmtreeId, palmtreeId)
        );
    }
    
    @Override
    public boolean deleteByPalmtreeId(Long palmtreeId) {
        return baseMapper.delete(new LambdaQueryWrapper<PalmtreeDetail>()
            .eq(PalmtreeDetail::getPalmtreeId, palmtreeId)
        ) > 0;
    }
}
