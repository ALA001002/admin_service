package com.bigo.project.system.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/18 20:05
 */
@Data
public class IndexInfoVO {

    private Long userNum;

    private Long orderNum;

    private BigDecimal orderMoney;

    private BigDecimal orderFee;

    private BigDecimal profit;

    private BigDecimal loss;

    private BigDecimal recharge;


    private BigDecimal withdraw;

    /**
     * 总充值
     */
    private BigDecimal usdtRecharge;
    /**
     * 线上充值
     */
    private BigDecimal usdtOnlineRecharge;
    /**
     * 线下充值
     */
    private BigDecimal usdtOfflineRecharge;
    /**
     * 人工存入
     */
    private BigDecimal usdtManualRecharge;
/*    private BigDecimal usdtTrc20Recharge;
    private BigDecimal ethRecharge;
    private BigDecimal btcRecharge;*/

    /**
     * 总提现
     */
    private BigDecimal usdtWithdraw;
    /**
     * 线上提现
     */
    private BigDecimal usdtOnlineWithdraw;
    /**
     * 线下提现
     */
    private BigDecimal usdtOfflineWithdraw;

    /**
     * 三方通道提现
     */
    private BigDecimal usdtPassageWithdraw;
    /**
     * 人工存入
     */
    private BigDecimal usdtTrc20Recharge;
    private BigDecimal ethRecharge;
    private BigDecimal btcRecharge;

    private BigDecimal usdtTrc20Withdraw;
    private Long firstDepositNum;
    private BigDecimal ethWithdraw;
    private BigDecimal btcWithdraw;


    private BigDecimal usdtWithdrawFee;
    private BigDecimal usdtTrc20WithdrawFee;
    private BigDecimal ethWithdrawFee;
    private BigDecimal btcWithdrawFee;

    // 期权订单
    private Long timeOrderNum;

    //期权交易额
    private BigDecimal timeOrderMoney;

    //期权手续费
    private BigDecimal timeOrderFee;

    //期权盈
    private BigDecimal timeLoss;

    //期权亏
    private BigDecimal timeProfit;


}
