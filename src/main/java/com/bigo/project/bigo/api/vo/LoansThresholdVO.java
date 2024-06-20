package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LoansThresholdVO {

    private Long uid;

    /**
     * 借款门槛ID
     */
    private Long thresholdId;

    /** 最小金额 */
    private Long minAmount;

    /** 最大金额 */
    private BigDecimal maxAmount;

    /** 利息(%) */
    private BigDecimal interest;

    /*
     * 最低还款费率
     */
    private BigDecimal minRepaymentRate;

    /**

    /**
     * 借款期限
     */
    private List<Integer> numberList;


}
