package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.Herb;
import com.wuxing.mapper.HerbMapper;
import com.wuxing.service.HerbService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 药精服务实现
 */
@Service
public class HerbServiceImpl extends ServiceImpl<HerbMapper, Herb> implements HerbService {
    
    @Override
    public IPage<Herb> page(PageRequest request, String element, String category, String keyword, Integer status) {
        Page<Herb> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<Herb> wrapper = new LambdaQueryWrapper<>();
        
        // 控制是否需要仅显示启用的，不仅限于不是是否指定了何种状态
        if (status != null) {
            wrapper.eq(Herb::getStatus, status);
        } else {
            wrapper.eq(Herb::getStatus, 1);
        }
        
        if (StringUtils.hasText(element)) {
            wrapper.eq(Herb::getElement, element);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Herb::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(Herb::getName, keyword)
                .or().like(Herb::getAlias, keyword)
                .or().like(Herb::getEffects, keyword)
                .or().like(Herb::getTaste, keyword)
                .or().like(Herb::getProperties, keyword)
            );
        }
        
        wrapper.orderByAsc(Herb::getSort, Herb::getId);
        return this.page(page, wrapper);
    }
    
    @Override
    public List<Herb> listByElement(String element) {
        return baseMapper.selectList(new LambdaQueryWrapper<Herb>()
            .eq(Herb::getElement, element)
            .eq(Herb::getStatus, 1)
            .orderByAsc(Herb::getSort, Herb::getId)
        );
    }
    
    @Override
    public List<Herb> listByCategory(String category) {
        return baseMapper.selectList(new LambdaQueryWrapper<Herb>()
            .eq(Herb::getCategory, category)
            .eq(Herb::getStatus, 1)
            .orderByAsc(Herb::getSort, Herb::getId)
        );
    }
    
    @Override
    public List<Herb> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        
        return baseMapper.selectList(new LambdaQueryWrapper<Herb>()
            .eq(Herb::getStatus, 1)
            .and(w -> w
                .like(Herb::getName, keyword)
                .or().like(Herb::getAlias, keyword)
                .or().like(Herb::getEffects, keyword)
                .or().like(Herb::getTaste, keyword)
                .or().like(Herb::getProperties, keyword)
            )
            .orderByAsc(Herb::getSort, Herb::getId)
        );
    }
    
    @Override
    public Herb getDetail(Long id) {
        return baseMapper.selectById(id);
    }
}
