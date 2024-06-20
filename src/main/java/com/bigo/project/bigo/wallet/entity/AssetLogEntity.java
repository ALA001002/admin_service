package com.bigo.project.bigo.wallet.entity;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/21 15:38
 */
@Getter
@Setter
public class AssetLogEntity extends BaseEntity {

    /**
     * 主键id
     */
    @Excel(name = "id")
    private Long id;
    /**
     * 用户id
     */
    @Excel(name = "用户ID")
    private Long uid;
    /**
     * 用户名
     */
    @Excel(name = "用户账号")
    private String username;

    /**
     * 用户状态
     */
    @Excel(name = "账号类型", readConverterExp = "0=普通玩家,1=普通玩家,2=内部账号")
    private Integer userStatus;
    /**
     * 币种
     */
    @Excel(name = "币种")
    private String coin;
    /**
     * 操作类型
     */
    @Excel(name = "变更类型", readConverterExp = "1=资金划转,2=合约开仓,3=合约平仓,4=充币,5=提币,6=下级返佣,7=币币兑换," +
            "8=补仓,9=提币失败-返还金额,10=内部充币,11=OTC,12=购买期权,13=期权结算,14=抽奖奖励,15=冻结释放,16=内部扣除")
    private Integer type;
    /**
     * 操作子类型
     */
    private Integer subType;
    /**
     * 操作维度：0-增加，1-减少
     */
    @Excel(name = "操作维度", readConverterExp = "0=增加,1=减少")
    private Integer dim;
    /**
     * 变更前数量
     */
    private BigDecimal before;
    /**
     * 变更金额
     */
    @Excel(name = "变更金额")
    private BigDecimal amount;
    /**
     * 变更后金额
     */
    private BigDecimal after;
    /**
     * 钱包id
     */
    private Long walletId;
    /**
     * 变更时间
     */
    @Excel(name = "变更时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
    /**
     * 获取分佣的合约id
     */
    private Long contractId;


    /**
     * 释放时间
     */
    private Date releaseTime;

    /**
     * 是否已释放
     */
    private Integer isRelease;

    private String uids;

    private List<Long> uidList;

    private Long agentId;


    /**
     * 登录IP归属地
     */
    private String lastLoginArea;

    /**
     * 上次登录ip
     */
    private String lastLoginIp;



}
