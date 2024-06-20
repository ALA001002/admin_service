package com.bigo.project.bigo.api.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/20 19:02
 */
@Data
public class OrderParam {
    /**
     * 1-买 2-卖
     */
    @NotNull(message = "type_cannot_be_empty")
    private Integer type;
    /**
     * 币种
     */
    @NotBlank(message = "coin_cannot_be_empty")
    private String coin;
    /**
     * 法币
     */
    @NotBlank(message = "legal_currency_cannot_be_empty")
    private String legal;
    /**
     * 数量
     */
    @NotNull(message = "trade_quantity_cannot_be_empty")
    private BigDecimal num;
    /**
     * 用户ID
     */
    private Long uid;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 申诉内容
     */
    private String appealContent;
}
