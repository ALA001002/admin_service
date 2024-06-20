package com.bigo.project.bigo.wallet.domain;

import java.math.BigDecimal;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * 充值奖励对象 bg_recharge_reward
 * 
 * @author xx
 * @date 2023-11-08
 */
@Data
public class RechargeReward extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 充值金额 */
    @Excel(name = "充值金额")
    private BigDecimal rechargeAmount;

    /** 奖励金额 */
    @Excel(name = "奖励金额")
    private BigDecimal userRewardNum;

    /** 上级奖励金额 */
    @Excel(name = "上级奖励金额")
    private BigDecimal parentRewardNum;

    private Long googleCaptcha;



}
