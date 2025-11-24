package com.wuxing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.Dictionary;

import java.util.List;
import java.util.Map;

/**
 * 字典服务接口
 */
public interface DictionaryService extends IService<Dictionary> {
    
    /**
     * 分页查询字典
     */
    IPage<Dictionary> page(PageRequest request, String dictType, String dictKey);
    
    /**
     * 根据类型获取字典列表
     */
    List<Dictionary> listByType(String dictType);
    
    /**
     * 获取所有字典类型
     */
    List<Dictionary> listTypes();
    
    /**
     * 获取字典Map(按类型分组)
     */
    Map<String, List<Dictionary>> getDictMap();
}
