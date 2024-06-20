package com.bigo.project.bigo.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class IcoSpotDTO {

    /**
     * 限价-价格
     */
    private BigDecimal price;
    /**
     * 限价,市价-数量
     */
    private BigDecimal quantity;
    /**
     * BUY买-SELL卖
     */
    private String side;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 下单类型
     */
    private String orderType;
    /**
     * 市价-成交额
     */
    private BigDecimal quoteOrderQty;

    /**
     * 状态
     */
    private String status;

    /**
     * 订单id
     */
    private String[] orderIds;

    private Date startTime;

    private Date endTime;
}
