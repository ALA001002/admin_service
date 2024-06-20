package com.bigo.project.bigo.holdings.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * 智能持仓订单对象 bg_holdings_order
 * 
 * @author bigo
 * @date 2023-12-28
 */
@Data
public class HoldingsOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long uid;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 类型：1-常规，2-信号，3-随机 */
    @Excel(name = "类型：1-常规，2-信号，3-随机")
    private Long type;

    /** 购买金额 */
    @Excel(name = "购买金额")
    private BigDecimal buyAmount;

    /** 收益率 */
    @Excel(name = "收益率")
    private BigDecimal gainRate;

    /** 收益金额 */
    @Excel(name = "收益金额")
    private BigDecimal gainAmount;

    /** 收益時間(分钟) */
    @Excel(name = "收益時間(分钟)")
    private Long timeNum;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;

    /** 交易对参数 */
    @Excel(name = "交易对参数")
    private String symbolJson;

    /** 订单状态：1-冻结，2-已释放 */
    @Excel(name = "订单状态：1-冻结，2-已释放")
    private Long status;

    /** 释放时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "释放时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date releaseTime;


}
