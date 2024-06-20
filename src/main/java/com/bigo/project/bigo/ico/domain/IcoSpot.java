package com.bigo.project.bigo.ico.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 现货交易记录对象 bg_ico_spot
 * 
 * @author bigo
 * @date 2023-03-14
 */
@Data
public class IcoSpot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderId;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    private String username;

    /** 交易对 */
    @Excel(name = "交易对")
    private String symbol;

    /** 订单状态：NEW-进行中,FILLED-已成交,CANCELED-已取消 */
    @Excel(name = "订单状态：NEW-进行中,FILLED-已成交,CANCELED-已取消")
    private String status;

    /** 报价币种 */
    @Excel(name = "报价币种")
    private String baseCoin;

    /** 基础币种 */
    @Excel(name = "基础币种")
    private String quoteCoin;

    /** 订单类型：LIMIT-限价单，MARKET-市价单 */
    @Excel(name = "订单类型：LIMIT-限价单，MARKET-市价单")
    private String orderType;

    /** 方向：BUY-买入，SELL-卖出 */
    @Excel(name = "方向：BUY-买入，SELL-卖出")
    private String side;

    /** 总数量 */
    @Excel(name = "总数量")
    private BigDecimal origQty;

    /** 成交数量 */
    @Excel(name = "成交数量")
    private BigDecimal executedQty;

    /** 均价 */
    @Excel(name = "均价")
    private BigDecimal executedPrice;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 手续费 */
    @Excel(name = "手续费")
    private BigDecimal fee;

    /** 成交额 */
    @Excel(name = "成交额")
    private BigDecimal executedQuoteQty;

    /** 成交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "成交时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date workingTime;

    @JsonIgnore
    private String oldStatus;


    public IcoSpot() {
    }

    public IcoSpot(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
}
