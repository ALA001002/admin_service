package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 我的收益VO
 * @author: wenxm
 * @date: 2020/7/1 13:57
 */
@Data
public class ProfitVO {

    /**
     * 币种
     */
    private String coin;
    /**
     * 昨日分佣
     */
    private BigDecimal yesterdayProfit;
    /**
     * 累计分佣
     */
    private BigDecimal totalProfit;
}
