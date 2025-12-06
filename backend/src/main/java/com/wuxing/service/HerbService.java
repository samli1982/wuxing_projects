package com.wuxing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.Herb;

import java.util.List;

/**
 * 药精服务接口
 */
public interface HerbService extends IService<Herb> {
    
    /**
     * 分页查询药精
     */
    IPage<Herb> page(PageRequest request, String element, String category, String keyword, Integer status);
    
    /**
     * 根据五行分类查询
     */
    List<Herb> listByElement(String element);
    
    /**
     * 根据子分类查询
     */
    List<Herb> listByCategory(String category);
    
    /**
     * 搜索药精
     */
    List<Herb> search(String keyword);
    
    /**
     * 根据ID获取详情
     */
    Herb getDetail(Long id);
}
