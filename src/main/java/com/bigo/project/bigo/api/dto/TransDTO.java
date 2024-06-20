package com.bigo.project.bigo.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransDTO {

    private Long uid;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 收款银行户名
     */
    private String accountName;

    /**
     * 收款银行账号
     */
    private String accountNo;

    /**
     * 收款人手机号
     */
    private String receiverPhone;

    /**
     * 收款银行代码ID
     */
    private Long bankCodeId;

    /**
     * 支付密码
     */
    private String payPassword;

    /**
     * 币种
     */
    private String coin;

}
