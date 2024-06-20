package com.bigo.project.bigo.wallet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushData {
    String txId;
    String method;
    Long uid;
    BigDecimal balance;
    Byte type;
    String symbol;
    String address;
    String fromAddress;
    Boolean test;
}
