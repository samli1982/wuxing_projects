package com.wuxing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员实体类
 * 
 * @author wuxing
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("member_user")
public class Member extends BaseEntity {

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别（0未知 1男 2女）
     */
    private Integer gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

    /**
     * 语言
     */
    private String language;

    /**
     * 会员等级（1普通会员 2VIP 3SVIP）
     */
    private Integer memberLevel;

    /**
     * 会员过期时间
     */
    private String memberExpireTime;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 状态（0禁用 1正常）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 最后登录时间
     */
    private String lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;
}