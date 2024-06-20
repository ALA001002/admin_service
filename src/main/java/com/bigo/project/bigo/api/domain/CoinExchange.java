package com.bigo.project.bigo.api.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description: 闪兑实体
 * @author: wenxm
 * @date: 2020/7/2 10:26
 */
@Data
public class CoinExchange {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 用来兑换的币种
     */
    @NotBlank(message = "form_currency_cannot_be_null")
    private String from;
    /**
     * 要兑换的币种
     */
    @NotBlank(message = "to_currency_cannot_be_null")
    private String to;
    /**
     * 兑换数量
     */
    @NotNull(message = "exchange_amount_cannot_be_null")
    private BigDecimal amount;
}
