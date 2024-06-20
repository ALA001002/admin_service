package com.bigo.project.bigo.marketsituation.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: youzi
 * @date: 2021/6/15 18:28
 */
@Data
public class KlineConfig {

    private Long id;

    /**
     * 时间粒度
     */
    private String period;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 引线比例
     */
    private BigDecimal leadWire;

    /**
     *跌幅
     */
    private BigDecimal decline;
    /**
     * 日涨幅
     */
    private BigDecimal updateRate;
    /**
     * 下引线长度
     */
    private BigDecimal lowDeg;

    /**
     * bar长度1.0表示默认值，倍数为默认值备注
     */
    private BigDecimal barRate;
}
