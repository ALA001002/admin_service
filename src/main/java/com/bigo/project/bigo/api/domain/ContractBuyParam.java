package com.bigo.project.bigo.api.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Description 购买合约参数
 * @Author wenxm
 * @Date 2020/6/21 17:29
 */
@Getter
@Setter
public class ContractBuyParam {

    /**
     * 用户id
     */
    private Long uid;
    /**
     * 要购买的合约币种
     */
    @NotBlank(message = "contract_currency_cannot_be_empty")
    private String symbolCode;
    /**
     * 用来购买合约的币种
     */
    @NotBlank(message = "purchase_currency_cannot_be_empty")
    private String currency;
    /**
     * 合约类型，0-逐仓，1-全仓
     */
    //@NotEmpty(message = "contract_type_cannot_be_empty")
    private Integer contractType;
    /**
     * 下单金额(即保证金)
     */
    @NotNull(message = "amount_type_cannot_be_empty")
    private BigDecimal amount;
    /**
     * 合约杠杆倍数
     */
    @NotNull(message = "contract_multiple_type_cannot_be_empty")
    private Integer contractMultiple;
    /**
     * 止损价格
     */
    private BigDecimal stopLoss;
    /**
     * 止盈价格
     */
    private BigDecimal stopSurplus;
    /**
     * 交易类型：1-做多 2-做空
     */
    @NotNull(message = "trade_type_cannot_be_empty")
    private Integer tradeType;

    /*======计划委托参数======= */
    /**
     * 委托类型 0-市价委托 1-计划委托
     */
    @NotNull(message = "trust_type_cannot_be_empty")
    private Integer trustType;
    /**
     * 触发价
     */
    private BigDecimal triggerPrice;
    /**
     * 委托成交价
     */
    private BigDecimal trustPrice;
    /**
     * 补仓费
     */
    private BigDecimal replenish;
    /**
     * 合约ID
     */
    private Long contractId;
    /**
     * 止盈类型 1-按价格 2-按百分比
     */
    private Integer stopSurplusType;
    /**
     * 止损类型 1-按价格 2-按百分比
     */
    private Integer stopLossType;

    private ContractStopParam stopInfo;

}
