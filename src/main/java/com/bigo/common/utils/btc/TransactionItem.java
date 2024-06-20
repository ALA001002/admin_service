package com.bigo.common.utils.btc;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/7 1:43
 */
@Data
public class TransactionItem {
    private String address; //地址
    private String category;    //类型
    private BigDecimal amount;  // 数量
    private BigDecimal fee; //手续费
    private String txid;    // 事物ID
}
