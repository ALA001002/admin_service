package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/9/3 17:18
 */
@Data
public class TimePeriodVO {
    /**
     * 结算周期（单位：秒）
     */
    private Integer period;
    /**
     * 收益率
     */
    private BigDecimal yieldRate;
    /**
     * 手续费
     */
    private BigDecimal feeRate;
    /**
     * 最低下单金额
     */
    private BigDecimal minMoney;
}
