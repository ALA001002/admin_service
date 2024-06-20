package com.bigo.project.bigo.api.vo.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/28 14:29
 */
@Data
public class ProductInfoVO {
    /** id */
    private Long id;

    /** 产品名称 */
    private String productName;

    /** 收益率 */
    private BigDecimal profitRate;

    /** 收益时间 */
    private Integer profitTime;

    /** 最低购买金额 */
    private BigDecimal purchaseAmountMin;

    /** 币种 */
    private String currency;
    /** 总数量 */
    private Integer totalNumber;

    /** 剩余数量 */
    private Integer remainingNumber;

    private String typeName;

//    /**
//     * 年化利率
//     */
//    private BigDecimal yearProfitRate;
    /**
     * 总收益
     */
    private BigDecimal totalRevenue;
}
