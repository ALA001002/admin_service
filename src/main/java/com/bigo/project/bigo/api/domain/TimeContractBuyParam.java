package com.bigo.project.bigo.api.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Description 限时合约购买参数
 * @Author wenxm
 * @Date 2020/6/21 17:29
 */
@Data
public class TimeContractBuyParam {

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
     * 下单数量
     */
    @NotNull(message = "amount_cannot_be_empty")
    private BigDecimal amount;
    /**
     * 交易类型：1-做多 2-做空
     */
    @NotNull(message = "trade_type_cannot_be_empty")
    private Integer tradeType;
    /**
     * 合约周期
     */
    @NotNull(message = "period_cannot_be_empty")
    private Integer period;

}
