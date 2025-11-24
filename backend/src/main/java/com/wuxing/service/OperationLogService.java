package com.wuxing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.OperationLog;

/**
 * 操作日志服务接口
 */
public interface OperationLogService extends IService<OperationLog> {
    
    /**
     * 分页查询操作日志
     */
    IPage<OperationLog> page(PageRequest request, String module, String operator, String startTime, String endTime);
}
