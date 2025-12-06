package com.wuxing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.Palmtree;
import com.wuxing.entity.PalmtreeDetail;

/**
 * 命盘服务接口
 */
public interface PalmtreeService extends IService<Palmtree> {
    
    /**
     * 保存命盘及详情
     */
    Palmtree savePalmtreeWithDetail(Palmtree palmtree, PalmtreeDetail detail);
    
    /**
     * 分页查询用户命盘
     */
    IPage<Palmtree> pagePalmtreesByMemberId(Long memberId, PageRequest request);
    
    /**
     * 按会员ID带搜索条件分页查询命盘
     */
    IPage<Palmtree> pagePalmtreesByMemberIdWithFilter(Long memberId, String keyword, String gender, PageRequest request);
    
    /**
     * 获取命盘及详情
     */
    Palmtree getPalmtreeWithDetail(Long palmtreeId);
    
    /**
     * 删除命盘（级联删除详情）
     */
    boolean deletePalmtreeWithDetail(Long palmtreeId);
    
    /**
     * 根据会员ID获取全部命盘
     */
    java.util.List<Palmtree> listPalmtreesByMemberId(Long memberId);
}
