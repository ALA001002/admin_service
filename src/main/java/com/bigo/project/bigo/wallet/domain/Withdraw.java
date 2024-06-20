package com.bigo.project.bigo.wallet.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/6/27 12:51
 */
@Data
public class Withdraw {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 币种
     */
    @NotBlank(message = "currency_cannot_be_null")
    private String coin;
    /**
     * 提币金额
     */
    @NotNull(message = "withdraw_amount_cannot_be_null")
    private BigDecimal money;
    /**
     * 提币手续费
     */
    private BigDecimal fee;
    /**
     * 交易hash值
     */
    private String hash;
    /**
     * 转入地址
     */
    private String from;
    /**
     * 提币到此地址
     */
    @NotBlank(message = "address_cannot_be_null")
    private String toAddress;
    /**
     * 备注
     */
    private String remark;
    /**
     * 提现状态 0-待处理 1-成功 2-失败 3-提币中
     */
    private Integer status;
    /**
     * 提币类型 1：手动代付 2：线上提现 3：线下提现 4：线上充值 5：线下充值 6：人工存入
     */
    private Integer type;
    /**
     * 审核人id （系统用户id)
     */
    private Long operatorId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 审核时间
     */
    private Date verifyTime;
    /**
     * 关联wallet_transaction提币记录id
     */
    private Long transactionId;
    /**
     * 提现状态 0-待审核 1-已通过 2-已驳回
     */
    private Integer checkStatus;

    /**
     * 支付密码
     */
    private String payPassword;

    private String versionCode;

    /**
     * 失败原因
     */
    private String error;

    private Long withdrawAddressId;

    /**
     * 充值凭证
     */
    private String photo;


    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 位置
     */
    private String position;

    private List<Long> uidList;

}
