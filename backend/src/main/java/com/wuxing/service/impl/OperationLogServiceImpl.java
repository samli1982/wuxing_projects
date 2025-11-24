package com.wuxing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuxing.dto.request.PageRequest;
import com.wuxing.entity.OperationLog;
import com.wuxing.mapper.OperationLogMapper;
import com.wuxing.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志服务实现
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    
    @Override
    public IPage<OperationLog> page(PageRequest request, String module, String operator, String startTime, String endTime) {
        Page<OperationLog> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(module)) {
            wrapper.like(OperationLog::getModule, module);
        }
        if (StringUtils.hasText(operator)) {
            wrapper.like(OperationLog::getOperator, operator);
        }
        if (StringUtils.hasText(startTime)) {
            wrapper.ge(OperationLog::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StringUtils.hasText(endTime)) {
            wrapper.le(OperationLog::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return this.page(page, wrapper);
    }
}
