package com.bigo.project.bigo.loans.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 借款门槛对象 bg_loans_threshold
 * 
 * @author bigo
 * @date 2022-01-12
 */
@Data
public class LoansThreshold  extends BaseEntity implements Comparable<LoansThreshold>
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 充值金额 */
    @Excel(name = "充值金额")
    private Long rechargeAmount;

    /** 最小金额 */
    @Excel(name = "最小金额")
    private Long minAmount;


    /** 利息(%) */
    @Excel(name = "利息(%)")
    private BigDecimal interest;


    /** 额度倍数 */
    @Excel(name = "额度倍数")
    private BigDecimal quotaMultiplier;

    /** 最低还款比例(%) */
    @Excel(name = "最低还款比例(%)")
    private BigDecimal minRepaymentRate;


    @Override
    public String toString() {
        return "LoansThreshold{" +
                "id=" + id +
                ", rechargeAmount=" + rechargeAmount +
                ", minAmount=" + minAmount +
                ", interest=" + interest +
                ", quotaMultiplier=" + quotaMultiplier +
                ", minRepaymentRate=" + minRepaymentRate +
                '}';
    }

    @Override
    public int compareTo(LoansThreshold o) {
        return this.rechargeAmount.compareTo(o.getRechargeAmount());
    }
}
