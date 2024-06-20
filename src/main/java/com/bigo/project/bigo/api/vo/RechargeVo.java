package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeVo {
    private String email;
    private BigDecimal amount;
    private String dateTime;
}
