package com.bigo.project.bigo.wallet.domain;

import lombok.Data;

@Data
public class TrxResponse<T> {
    String retMsg;
    int retCode;
    Object data;
}
