package com.wuxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 命盘实体类
 * 
 * @author wuxing
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("palmtree")
public class Palmtree extends BaseEntity {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名（可选）
     */
    private String realName;

    /**
     * 出生年份
     */
    private Integer birthYear;

    /**
     * 出生月份
     */
    private Integer birthMonth;

    /**
     * 出生日期
     */
    private Integer birthDay;

    /**
     * 出生小时（0-23）
     */
    private Integer birthHour = 0;

    /**
     * 出生分钟（0-59）
     */
    private Integer birthMinute = 0;

    /**
     * 出生时辰索引（0-12：不详、子、丑...亥）
     * @deprecated 已废弃，使用birthHour和birthMinute
     */
    @Deprecated
    private Integer birthHourIndex;

    /**
     * 出生时辰类型（precise精确 unknown不详）
     * @deprecated 已废弃
     */
    @Deprecated
    private String birthHourType;

    /**
     * 出生地城市
     */
    private String birthCity;

    /**
     * 出生地经度
     */
    private Double birthLng;

    /**
     * 出生地纬度
     */
    private Double birthLat;

    /**
     * 历法类型（gregorian公历 lunar农历）
     */
    private String calendarType;

    /**
     * 性别（male男 female女 other其他）
     */
    private String gender;

    /**
     * 是否用于健康分析（0否 1是）
     */
    private Integer forHealthAnalysis;

    /**
     * 状态（0禁用 1正常）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
