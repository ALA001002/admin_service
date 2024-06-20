package com.bigo.project.bigo.otc.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @description: 支付方式
 * @author: wenxm
 * @date: 2020/7/20 14:46
 */
@Data
public class Payment {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long uid;
    /**
     * 法币
     */
    @NotBlank(message = "legal_currency_cannot_be_empty")
    @Length(max = 230)
    private String legalCurrency;
    /**
     * 银行
     */
    @NotBlank(message = "bank_name_cannot_be_empty")
    @Length(max = 230)
    private String bankName;
    /**
     * 开户支行
     */
    @Length(max = 230)
    private String bankBranch;
    /**
     * 收款账户
     */
    @NotBlank(message = "bank_account_cannot_be_empty")
    @Length(max = 230)
    private String bankAccount;
    /**
     * 收款人
     */
    @NotBlank(message = "payee_account_cannot_be_empty")
    @Length(max = 230)
    private String payee;
    /**
     * 是否已删除
     */
    private Integer deleted;
    /**
     * 创建时间
     */
    private Date createTime;
}
