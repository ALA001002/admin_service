package com.bigo.common.utils.btc;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/8 14:45
 */
@Data
public class Transaction {
    private  List<TransactionItem> transactions;
}
