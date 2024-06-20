package com.bigo.project.bigo.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoansDTO {

    private Long thresholdId;

    /**
     * 借款金额
     */
    private BigDecimal loansAmount;

    /**
     * 借款期限
     */
    private Integer loansNumber;


}
