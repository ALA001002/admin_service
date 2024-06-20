package com.bigo.project.bigo.wallet.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 钱包交易状态对象 wallet_transaction_status
 * 
 * @author bigo
 * @date 2021-01-25
 */
@Data
public class WalletTransactionStatus extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;

    /** 区块高度 */
    @Excel(name = "区块高度")
    private Long blockCount;

    /** hash值 */
    @Excel(name = "hash值")
    private String hash;

    /** 处理状态：1 -成功，0-失败 */
    @Excel(name = "处理状态：1 -成功，0-失败")
    private Integer status;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("currency", getCurrency())
            .append("blockCount", getBlockCount())
            .append("hash", getHash())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
