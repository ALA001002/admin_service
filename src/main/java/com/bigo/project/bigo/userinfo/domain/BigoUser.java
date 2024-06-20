package com.bigo.project.bigo.userinfo.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 币高用户实体
 * @Author wenxm
 * @Date 2020/6/16 11:01
 */
@Getter
@Setter
public class BigoUser {
    /**
     * UID 主键，唯一标识
     */
    private Long uid;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 密码
     */
    private String password;
    /**
     * 支付密码
     */
    private String payPassword;
    /**
     * 头像地址
     */
    private String avatar;
    /**
     * 代理类型，0-非代理，1-一级代理
     */
    private Integer agentType;
    /**
     * 用户等级 1-lv1 2-lv2 3-lv3
     */
    private Long level;

    /**
     * 用户历史最高等级
     */
    private Long topLevel;

    /**
     * 上级uid
     */
    private Long parentUid;
    /**
     * 顶级ID
     */
    private Long topUid;
    /**
     * 上级电话
     */
    private String parentPhone;
    /**
     * 上级邮箱
     */
    private String parentEmail;
    /**
     * 账户状态 0：正常，1：禁用 2-内部账号
     */
    private Integer status;
    /**
     * 认证状态，0-未认证,1-审核中,2-已认证
     */
    private Integer authStatus;
    /**
     * 实名认证图片
     */
    private String authPhotos;

    /**
     * 认证审核理由
     */
    private String authRationale;
    /**
     * 姓名
     */
    private String realName;
    /**
     * 身份证
     */
    private String idNum;
    /**
     * 护照
     */
    private String passport;
    /**
     * 性别 0-未设置,1-男,2-女
     */
    private Integer sex;
    /**
     * 所在地区id
     */
    private Long areaId;
    /**
     * 所在地区
     */
    private String areaName;
    /**
     * 个人简介
     */
    private String profile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 联系方式
     */
    private String contractInfo;
    /**
     * 上次登录ip
     */
    private String lastLoginIp;
    /**
     * 上次登录时间
     */
    private Date lastLoginTime;
    /**
     * 注册ip
     */
    private String registerIp;
    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 邀请人uid，多个以‘,’隔开
     */
    private String childUids;

    /**
     * 期权合约下单状态
     */
    private Integer timeContractStatus;

    /**
     * 提现状态
     */
    private Integer withdrawStatus;

    /**
     * 国籍
     */
    private String country;

    /**
     * 驾驶证
     */
    private String driverLicense;

    private Integer authType;

    /**
     * 登录IP归属地
     */
    private String lastLoginArea;

    /**
     * 注册IP归属地
     */
    private String registerArea;

      /**
     * 谷歌秘钥绑定状态0-未绑定，1-已绑定
     */
    private Integer googleSecretStatus;
    /**
     * whatsapp
     */
    private String whatsapp;

    private String remark;

    private Date updateTime;

}
