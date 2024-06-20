package com.bigo.project.bigo.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IcoProductDTO {

    /**
     * 产品id
     */
    private Long icoProductId;
    /**
     * 购买数量
     */
    private BigDecimal buyNum;
    /**
     * 卖出数量
     */
    private BigDecimal sellNum;

    /**
     * 交易对
     */
    private String symbol;
//    /**
//     * 来源
//     */
//    private String from;
//    /**
//     * 目的
//     */
//    private String to;
}
