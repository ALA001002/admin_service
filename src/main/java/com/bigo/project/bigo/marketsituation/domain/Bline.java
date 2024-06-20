package com.bigo.project.bigo.marketsituation.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description B线
 * @Author wenxm
 * @Date 2020/6/19 15:21
 */
@Getter
@Setter
public class Bline {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 唯一交易id（将被废弃）
     */
    private Long bid;
    /**
     * 唯一成交ID
     */
    private Long tradeId;
    /**
     * 交易对 btcusdt、ethusdt、...
     */
    private String symbol;
    /**
     * 以基础币种计量的交易量
     */
    private BigDecimal amount;
    /**
     * 以报价币种为单位的成交价格
     */
    private BigDecimal price;
    /**
     * 真实价格，未滑点的价格
     */
    private BigDecimal realPrice;
    /**
     * 新加坡时间的时间戳，单位毫秒
     */
    private Long ts;
    /**
     * 交易方向：“buy” 或 “sell”, “buy” 即买，“sell” 即卖
     */
    private String direction;
}
