package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.Palmtree;
import com.wuxing.entity.PalmtreeDetail;
import com.wuxing.mapper.PalmtreeMapper;
import com.wuxing.mapper.PalmtreeDetailMapper;
import com.wuxing.service.PalmtreeService;
import com.wuxing.service.PalmtreeDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 命盘服务实现
 */
@Service
public class PalmtreeServiceImpl extends ServiceImpl<PalmtreeMapper, Palmtree> implements PalmtreeService {
    
    @Autowired
    private PalmtreeDetailMapper palmtreeDetailMapper;
    
    @Autowired
    private PalmtreeDetailService palmtreeDetailService;
    
    @Override
    @Transactional
    public Palmtree savePalmtreeWithDetail(Palmtree palmtree, PalmtreeDetail detail) {
        // 保存命盘基本信息
        this.save(palmtree);
        
        // 保存命盘详情
        if (detail != null) {
            detail.setPalmtreeId(palmtree.getId());
            palmtreeDetailService.save(detail);
        }
        
        return palmtree;
    }
    
    @Override
    public IPage<Palmtree> pagePalmtreesByMemberId(Long memberId, PageRequest request) {
        Page<Palmtree> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<Palmtree> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Palmtree::getMemberId, memberId)
               .eq(Palmtree::getStatus, 1)
               .orderByDesc(Palmtree::getId);
        
        return this.page(page, wrapper);
    }
    
    @Override
    public IPage<Palmtree> pagePalmtreesByMemberIdWithFilter(Long memberId, String keyword, String gender, PageRequest request) {
        Page<Palmtree> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<Palmtree> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Palmtree::getMemberId, memberId)
               .eq(Palmtree::getStatus, 1);
        
        // 按关键词搜索（是无词夹提不是横線）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Palmtree::getNickname, keyword)
                              .or().like(Palmtree::getRealName, keyword));
        }
        
        // 按性别筛选
        if (gender != null && !gender.trim().isEmpty()) {
            wrapper.eq(Palmtree::getGender, gender);
        }
        
        wrapper.orderByDesc(Palmtree::getId);
        
        return this.page(page, wrapper);
    }
    
    @Override
    public Palmtree getPalmtreeWithDetail(Long palmtreeId) {
        Palmtree palmtree = this.getById(palmtreeId);
        if (palmtree != null) {
            // 获取详情信息并关联
            PalmtreeDetail detail = palmtreeDetailService.getByPalmtreeId(palmtreeId);
            // 可以在这里将detail数据注入到palmtree中（需要添加对应字段）
        }
        return palmtree;
    }
    
    @Override
    @Transactional
    public boolean deletePalmtreeWithDetail(Long palmtreeId) {
        // 删除详情
        palmtreeDetailService.deleteByPalmtreeId(palmtreeId);
        
        // 删除命盘
        return this.removeById(palmtreeId);
    }
    
    @Override
    public List<Palmtree> listPalmtreesByMemberId(Long memberId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Palmtree>()
            .eq(Palmtree::getMemberId, memberId)
            .eq(Palmtree::getStatus, 1)
            .orderByDesc(Palmtree::getId)
        );
    }
}
