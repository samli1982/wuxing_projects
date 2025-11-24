package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.Dictionary;
import com.wuxing.mapper.DictionaryMapper;
import com.wuxing.service.DictionaryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典服务实现
 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {
    
    @Override
    public IPage<Dictionary> page(PageRequest request, String dictType, String dictKey) {
        Page<Dictionary> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(dictType)) {
            wrapper.eq(Dictionary::getDictType, dictType);
        }
        if (StringUtils.hasText(dictKey)) {
            wrapper.like(Dictionary::getDictKey, dictKey);
        }
        
        wrapper.orderByAsc(Dictionary::getDictType, Dictionary::getSort);
        return this.page(page, wrapper);
    }
    
    @Override
    public List<Dictionary> listByType(String dictType) {
        return baseMapper.selectByDictType(dictType);
    }
    
    @Override
    public List<Dictionary> listTypes() {
        return baseMapper.selectDictTypes();
    }
    
    @Override
    public Map<String, List<Dictionary>> getDictMap() {
        List<Dictionary> all = this.list(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getStatus, 1)
                .orderByAsc(Dictionary::getDictType, Dictionary::getSort));
        
        return all.stream().collect(Collectors.groupingBy(Dictionary::getDictType));
    }
}
