package com.bigo.project.bigo.api.vo.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 灵活质押VO
 */
@Data
public class PledgeInfoVO {
    private Long id;

    /** 产品名称 */
    private String productName;


    /** 每日收益率 */
    private BigDecimal profitRate;

    /** 币种 */
    private String currency;

    /** 收益时间 */
    private Integer profitTime;

    /** 最低购买金额 */
    private BigDecimal purchaseAmountMin;

    /**
     * 最高购买数量
     */
    private BigDecimal purchaseAmountMax;

    /** 总数量 */
    private Integer totalNumber;

    /** 剩余数量 */
    private Integer remainingNumber;

    /**
     * 倒计时时间
     */
    private Date countdownTime;

//    /**
//     * 年化利率
//     */
//    private BigDecimal yearProfitRate;

    /**
     * 总收益
     */
    private BigDecimal totalRevenue;

    /**
     * 计息开始时间
     */
    private Date startTime;

    /**
     * 收益发放时间
     */
    private Date issueTime;





}
