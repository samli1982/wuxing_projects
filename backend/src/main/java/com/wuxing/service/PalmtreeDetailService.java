package com.wuxing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.entity.PalmtreeDetail;

/**
 * 命盘详情服务接口
 */
public interface PalmtreeDetailService extends IService<PalmtreeDetail> {
    
    /**
     * 根据命盘ID获取详情
     */
    PalmtreeDetail getByPalmtreeId(Long palmtreeId);
    
    /**
     * 删除命盘详情
     */
    boolean deleteByPalmtreeId(Long palmtreeId);
}
