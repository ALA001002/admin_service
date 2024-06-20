package com.bigo.project.bigo.api.vo.ico;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class IcoSpotVO {

    /** id */
    private Long id;

    /** 订单号 */
    private String orderId;

    /** 用户id */
    private Long uid;

    /** 交易对 */
    private String symbol;

    /** 订单状态：NEW-进行中,FILLED-已成交,CANCELED-已取消 */
    private String status;

    private List<String> statusList;

    /** 报价币种 */
    private String baseCoin;

    /** 基础币种 */
    private String quoteCoin;

    /** 订单类型：LIMIT-限价单，MARKET-市价单 */
    private String orderType;

    /** 方向：BUY-买入，SELL-卖出 */
    private String side;

    /** 总数量 */
    private BigDecimal origQty;

    /** 成交数量 */
    private BigDecimal executedQty;

    /** 均价 */
    private BigDecimal executedPrice;

    /** 价格 */
    private BigDecimal price;

    /** 手续费 */
    private BigDecimal fee;

    /** 成交额 */
    private BigDecimal executedQuoteQty;

    /** 成交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workingTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonIgnore
    private Date startTime;

    @JsonIgnore
    private Date endTime;
}
