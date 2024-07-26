package com.bigo.project.bigo.userinfo.entity;

import com.bigo.common.utils.DateUtils;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description 币高用户entity
 * @Author wenxm
 * @Date 2020/6/16 11:01
 */
@Getter
@Setter
public class BigoUserEntity extends BaseEntity {
    /**
     * UID 主键，唯一标识
     */
    @Excel(name = "用户ID")
    private Long uid;

    private Long[] uids;
    /**
     * 邮箱
     */
    @Excel(name = "邮箱账号")
    private String email;
    /**
     * 昵称
     */
    @Excel(name = "昵称")
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
    private Integer level;
    /**
     * 用户等级名称
     */
    private String levelName;
    /**
     * 推荐人账号
     */
    @Excel(name = "邀请人账号")
    private String parentName;
    /**
     * 上级uid
     */
    @Excel(name = "邀请人ID")
    private Long parentUid;

//    @JsonIgnore
    /**
     * 账户状态 0：正常，1：禁用
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=禁用")
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
    @Excel(name = "注册时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    /**
     * 用户名
     */
    private String username;
    /**
     * 身份认证标识
     */
    private Integer authFlag;
    /**
     * 身份认证地址列表
     */
    private List<String> imgList;
    /**
     * 未读消息条数
     */
    private Integer unreadNum;
    /**
     * 联系方式
     */
    private String contractInfo;
    /**
     * 代理商ID
     */
    private Long agentId;

    /**
     * 代理商IDList
     */
    private String agentIds;

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

    private Date lastMsgTime;

    private Long teamUid;


    @Transient
    private Map balances;

    @Transient
    private Map recharge;
    @Transient
    private Map withdraw;

    /**
     * 登录IP归属地
     */
    private String lastLoginArea;

    /**
     * 注册IP归属地
     */
    private String registerArea;

    private String whatsapp;

    /**
     * 限时合约订单控制
     */
    private Integer timeContractControl;

    public Long getTimestamp(){
        if(this.lastMsgTime != null){
            return this.lastMsgTime.getTime();
        }
        return null;
    }
}
