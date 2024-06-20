package com.bigo.project.bigo.userinfo.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @date: 2021/4/28 16:05
 */
@Data
public class UserDayWalletEntity {
    /**
     * uid
     */
    private Long uid;

    /**
     * 币种
     */
    private String currency;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 折合USDT金额
     */
    private BigDecimal convertAmount;

}
