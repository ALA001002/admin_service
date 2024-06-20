package com.bigo.project.bigo.userinfo.entity;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.math.BigDecimal;
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
public class ExportBigoUserEntity extends BaseEntity {
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
     * 注册时间
     */
    @Excel(name = "注册时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;


    @Excel(name = "余额")
    private BigDecimal balance;


    @Excel(name = "充值金额")
    private BigDecimal rechargeAmount;

    @Excel(name = "提现金额")
    private BigDecimal withdrawAmount;

    @Transient
    private Map balances;

    @Transient
    private Map recharge;
    @Transient
    private Map withdraw;


}
