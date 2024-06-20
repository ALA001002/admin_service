package com.bigo.project.bigo.wallet.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Trx充提对象 tron_transaction
 * 
 * @author bigo
 * @date 2021-11-23
 */
@Setter
@Getter
public class TronTransaction extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    private Long uid;

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    private String username;

    /**
     * 上级ID
     */
    @Excel(name = "上级ID")
    private Long parentUid;

    @Excel(name = "业务员ID")
    private Long salesmanId;


    /**
     * 顶级ID
     */
    @Excel(name = "顶级ID")
    private Long topUid;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 确认区块数 */
    @Excel(name = "确认区块数")
    private Long blockNum;

    /** 归集错误信息 */
    @Excel(name = "归集错误信息")
    private String collectErrors;

    /** 归集时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "归集时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date collectTime;

    /** 归集id */
    @Excel(name = "归集id")
    private String collectTxId;

    /** 确认区块数 */
    @Excel(name = "确认区块数")
    private Long confirmBlock;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmTime;

    /** 合约地址 */
    @Excel(name = "合约地址")
    private String contractAddress;

    /** created_at */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "created_at", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 错误信息 */
    @Excel(name = "错误信息")
    private String errors;

    /** 手续费id */
    @Excel(name = "手续费id")
    private String feeTxId;

    /** 来源地址 */
    @Excel(name = "来源地址")
    private String fromAddress;

    /** 初始数量 */
    @Excel(name = "初始数量")
    private Long originAmount;

    /** 父级交易id */
    @Excel(name = "父级交易id")
    private String parentTxId;

    /** 是否上分标志 */
    @Excel(name = "是否上分标志,1表示已处理")
    private Integer score;

    /** 0 待转手续费 1表示转账手续费中 2表示手续费成功 3表示手续费失败 4表示归集成功 5表示归集失败 6 手续费转账交易', */
    @Excel(name = "0 待转手续费 1表示转账手续费中 2表示手续费成功 3表示手续费失败 4表示归集成功 5表示归集失败 6 手续费转账交易',")
    private Integer status;

    /** 代币 */
    @Excel(name = "代币")
    private String symbol;

    /** 转向地址 */
    @Excel(name = "转向地址")
    private String toAddress;

    /** txid */
    @Excel(name = "txid")
    private String txid;

    /** 0提现 1充值 */
    @Excel(name = "0提现 1充值")
    private Integer type;

    /** updated_at */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "updated_at", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;


    private String lowerUids;




    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("amount", getAmount())
            .append("blockNum", getBlockNum())
            .append("collectErrors", getCollectErrors())
            .append("collectTime", getCollectTime())
            .append("collectTxId", getCollectTxId())
            .append("confirmBlock", getConfirmBlock())
            .append("confirmTime", getConfirmTime())
            .append("contractAddress", getContractAddress())
            .append("createdAt", getCreatedAt())
            .append("errors", getErrors())
            .append("feeTxId", getFeeTxId())
            .append("fromAddress", getFromAddress())
            .append("originAmount", getOriginAmount())
            .append("parentTxId", getParentTxId())
            .append("score", getScore())
            .append("status", getStatus())
            .append("symbol", getSymbol())
            .append("toAddress", getToAddress())
            .append("txid", getTxid())
            .append("type", getType())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
