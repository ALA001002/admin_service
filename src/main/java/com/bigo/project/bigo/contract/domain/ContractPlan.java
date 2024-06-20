package com.bigo.project.bigo.contract.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 计划委托实体
 * @Author wenxm
 * @Date 2020/6/23 15:21
 */
@Getter
@Setter
public class ContractPlan {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 合约标题
     */
    private String title;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 合约标识(交易对)
     */
    private String symbol;
    /**
     * 购买合约使用的币种
     */
    private String currency;
    /**
     * 委托状态 0-委托中，1-已成功，2-撤销撤销，3-委托失败（余额不足）
     */
    private Integer status;
    /**
     * 交易类型：1-做多 2-做空
     */
    private Integer tradeType;
    /**
     * 价格
     */
    private BigDecimal money;
    /**
     * 触发价
     */
    private BigDecimal triggerPrice;
    /**
     * 合约杠杆倍数
     */
    private Integer contractMultiple;
    /**
     * 委托时的价格
     */
    private BigDecimal trustPrice;
    /**
     * 最终成交价
     */
    private BigDecimal finalPrice;
    /**
     * 止损价格
     */
    private BigDecimal stopLoss;
    /**
     * 止盈价格
     */
    private BigDecimal stopSurplus;
    /**
     * 委托时间
     */
    private Date trustTime;
    /**
     * 成交时间
     */
    private Date dealTime;
    /**
     * 撤销时间
     */
    private Date revokeTime;
}
