package com.bigo.project.bigo.config.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;
import java.math.BigDecimal;

@Setter
@Getter
public class ConfigSetting {
    /**
     *每日提现次数
     */
    private Integer withdrawCount;
    /**
     *提现手续费(%)
     */
    private BigDecimal withdrawFee;
    /**
     *USDT最低充值
     */
    private BigDecimal usdtRechargeMin;
    /**
     *USDT最低提现
     */
    private BigDecimal usdtWithdrawMin;
    /**
     *外部提现状态：0-false，1-true
     */
    private Integer externalWithdrawStatus;
    /**
     *邮箱注册状态：0-false，1-true
     */
    private Integer emailRegisterStatus;
    /**
     *短信注册状态：0-false，1-true
     */
    private Integer smsRegisterStatus;
    /**
     * 返利开关
     */
    private Integer rebateStatus;
    /**
     * 一级分销
     */
    private BigDecimal firstLevelRebate;
    /**
     *二级分销
     */
    private BigDecimal twoLevelRebate;
    /**
     *三级分销
     */
    private BigDecimal threeLevelRebate;
    /**
     *额外层级分销
     */
    private BigDecimal extraLevelRebate;
    /**
     *最高返利层级
     */
    private Integer mostRebateLevel;
    /**
     *usdt充值状态：0-false，1-true
     */
    private Integer usdtRecharge;
    private Integer ethRecharge;
    private Integer btcRecharge;


    /**
     *usdt人工充值状态：0-false，1-true
     */
    private Integer usdtManualRecharge;
    private Integer ethManualRecharge;
    private Integer btcManualRecharge;
    /**
     * 谷歌验证码
     */
    private Long googleCaptcha;

    /**
     * 延迟上分状态
     */
    private Integer delayedScoreStatus;
    /**
     * 延迟上分时间
     */
    private Integer delayedTime;
    /**
     * 借款状态
     */
    private Integer loanStatus;

    /**
     * 期权下单状态
     */
    private Integer timeContractStatus;

    private Integer tradeFeeRebate;

    private BigDecimal probabilityRebate;


    /**
     * 实名认证奖励状态：0-false，1-true
     */
    private Integer authRewardStatus;

    /**
     * 实名认证奖励数量
     */
    private BigDecimal authRewardNum;

    /**
     * 多地址绑定状态：0-false，1-true
     */
    private Integer multiAddressStatus;

    /**
     * 首充奖励开关
     */
    private Integer firstRechargeStatus;
    /**
     * 首充达标奖励金额
     */
    private BigDecimal firstRechargeComplyAmount;
    /**
     * 首充奖励金额
     */
    private BigDecimal firstRechargeRewards;
    /**
     * 首充上级奖励金额
     */
    private BigDecimal firstRechargeSuperRewards;

    /**
     * ip注册限制次数
     */
    private Long ipRegisterCount;

    private BigDecimal registerGiveLockAmount;

}
