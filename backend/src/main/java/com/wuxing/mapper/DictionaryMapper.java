package com.wuxing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuxing.entity.Dictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 字典Mapper
 */
@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {
    
    /**
     * 获取所有字典类型
     */
    @Select("SELECT DISTINCT dict_type, dict_type_name FROM sys_dictionary WHERE status = 1 ORDER BY dict_type")
    List<Dictionary> selectDictTypes();
    
    /**
     * 根据字典类型获取字典数据
     */
    @Select("SELECT * FROM sys_dictionary WHERE dict_type = #{dictType} AND status = 1 ORDER BY sort ASC")
    List<Dictionary> selectByDictType(String dictType);
}
