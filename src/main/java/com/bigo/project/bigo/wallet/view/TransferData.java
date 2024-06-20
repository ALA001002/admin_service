package com.bigo.project.bigo.wallet.view;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferData {
    BigDecimal price;
    String symbol;
    String address;
    Long withdrawId;
}
