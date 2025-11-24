package com.wuxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxing.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件Mapper
 */
@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
}
