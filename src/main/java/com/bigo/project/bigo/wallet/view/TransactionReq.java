package com.bigo.project.bigo.wallet.view;

import com.bigo.project.bigo.wallet.controller.request.PageReq;
import lombok.Data;

@Data
public class TransactionReq extends PageReq {
    String address;
    Long uid;
    Long topUid;
    Integer rowId;
    Boolean score;
    Integer status;
}
