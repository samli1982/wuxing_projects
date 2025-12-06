package com.wuxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxing.entity.Herb;
import org.apache.ibatis.annotations.Mapper;

/**
 * 药精数据访问层
 */
@Mapper
public interface HerbMapper extends BaseMapper<Herb> {
}
