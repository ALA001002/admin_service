package com.bigo.project.bigo.api.vo;

import com.bigo.common.utils.PrivacyUtils;
import lombok.Data;
import java.util.Date;

/**
 * @Description 币高用户VO
 * @Author wenxm
 * @Date 2020/6/16 11:01
 */
@Data
public class BigoUserVO {
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
     * 头像地址
     */
    private String avatar;
    /**
     * 代理类型，0-非代理，1-一级代理
     */
    private Integer agentType;
    /**
     * 上级邀请码
     */
    private Long parentUid;
    /**
     * 上级电话
     */
    private String parentPhone;
    /**
     * 上级邮箱
     */
    private String parentEmail;
    /**
     * 账户状态 0：正常，1：禁用
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
     * 上次登录时间
     */
    private Date lastLoginTime;
    /**
     * 注册时间
     */
    private Date registerTime;
    /**
     * 联系方式
     */
    private String contractInfo;
    /**
     * 支付密码设置状态 0-未设置 1-已设置
     */
    private Integer payPwdStatus;
    //    昨日0点时分折合成usdt的数值记录下来，
//    和今天0点时分折合成usdt的数值做对比。形成一个百分比
    public Double percent;

    public String getPhone(){
        return PrivacyUtils.maskMobile(this.phone);
    }

    public String getParentName(){
        if(this.parentPhone != null) {
            return PrivacyUtils.maskMobile(this.parentPhone);
        }else {
            return PrivacyUtils.maskEmail(this.parentEmail);
        }
    }

    private String getParentEmail(){
        return PrivacyUtils.maskEmail(this.parentEmail);
    }

    private String getParentPhone(){
        return PrivacyUtils.maskMobile(this.parentPhone);
    }

    public String getEmail(){
        return PrivacyUtils.maskEmail(this.email);
    }

    public Integer getAuthType(){
        //实名认证类型 1-身份证 2-护照
        return this.getPassport() == null ? 1 : 2;
    }

}
