package com.bigo.project.bigo.marketsituation.domain;

import com.bigo.project.bigo.enums.CandlestickEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description K线
 * @Author wenxm
 * @Date 2020/6/19 10:18
 */
@Getter
@Setter
public class Kline {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 新加坡时间的时间戳，单位秒，并以此作为此K线柱的id
     */
    private Long timestamp;
    /**
     * 时间粒度
     * {@link CandlestickEnum}
     */
    private String period;
    /**
     * 交易对 btcusdt、ethusdt、...
     */
    private String symbol;
    /**
     * 以基础币种计量的交易量
     */
    private BigDecimal amount;
    /**
     * 交易次数
     */
    private Integer count;
    /**
     * 本阶段开盘价
     */
    private BigDecimal open;
    /**
     * 本阶段收盘价
     */
    private BigDecimal close;
    /**
     * 本阶段最低价
     */
    private BigDecimal low;
    /**
     * 本阶段最高价
     */
    private BigDecimal high;
    /**
     * 以报价币种计量的交易量
     */
    private BigDecimal vol;
    /**
     * 本阶段真实开盘价
     */
    private BigDecimal realOpen;
    /**
     * 本阶段真实收盘价
     */
    private BigDecimal realClose;
    /**
     * 本阶段真实最低价
     */
    private BigDecimal realLow;
    /**
     * 本阶段真实最高价
     */
    private BigDecimal realHigh;

    @JsonIgnore
    private Long size;

}
