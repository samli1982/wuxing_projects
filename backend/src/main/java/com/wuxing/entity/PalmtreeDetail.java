package com.wuxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 命盘详情实体类（八字、五行、体质等详细信息）
 * 
 * @author wuxing
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("palmtree_detail")
public class PalmtreeDetail extends BaseEntity {

    /**
     * 命盘ID
     */
    private Long palmtreeId;

    /**
     * 年柱天干
     */
    private String yearHeavenlyStem;

    /**
     * 年柱地支
     */
    private String yearEarthlyBranch;

    /**
     * 月柱天干
     */
    private String monthHeavenlyStem;

    /**
     * 月柱地支
     */
    private String monthEarthlyBranch;

    /**
     * 日柱天干
     */
    private String dayHeavenlyStem;

    /**
     * 日柱地支
     */
    private String dayEarthlyBranch;

    /**
     * 时柱天干
     */
    private String hourHeavenlyStem;

    /**
     * 时柱地支
     */
    private String hourEarthlyBranch;

    /**
     * 纳音
     */
    private String nayin;

    /**
     * 空亡
     */
    private String kongwang;

    /**
     * 胎元
     */
    private String taiyuan;

    /**
     * 五行数据（JSON格式）
     */
    private String wuxingData;

    /**
     * 体质类型
     */
    private String constitutionType;

    /**
     * 体质分析（JSON格式）
     */
    private String constitutionAnalysis;

    /**
     * 五运六气数据（JSON格式）
     */
    private String wuyunliuqiData;

    /**
     * 喜用神（JSON数组格式）
     */
    private String usefulGods;

    /**
     * 调理建议（JSON格式）
     */
    private String adjustmentSuggestions;

    /**
     * 备注
     */
    private String remark;
}
