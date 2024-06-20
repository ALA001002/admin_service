package com.bigo.project.bigo.loans.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 借款信息对象 bg_loans_info
 * 
 * @author bigo
 * @date 2022-01-12
 */
@Data
public class LoansInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 借款金额 */
    @Excel(name = "借款金额")
    private BigDecimal amount;

    /** 利息金额 */
    @Excel(name = "利息金额")
    private BigDecimal interestAmount;

    /** 总还款金额 */
    @Excel(name = "总还款金额")
    private BigDecimal totalAmount;

    /** 已偿还金额 */
    @Excel(name = "已偿还金额")
    private BigDecimal repaidAmount;

    @JsonIgnore
    private BigDecimal oldRepaidAmount;

    /** 借款天数 */
    @Excel(name = "借款天数")
    private Integer loansNumber;

    /** 1-申请借款，2-审核失败，3-待还款，4-还款成功 */
    @Excel(name = "1-申请借款，2-审核失败，3-待还款，4-还款成功")
    private Integer status;

    /** 还款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "还款时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paybackTime;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;

    /** 最低还款比例(%) */
    @Excel(name = "最低还款比例")
    private BigDecimal minRepaymentRate;

    @Excel(name = "充值金额")
    private BigDecimal rechargeAmount;

    @JsonIgnore
    private BigDecimal oldRechargeAmount;


    private List<Integer> statusList;

    private BigDecimal totalRecharge;

    private BigDecimal totalWithdraw;

    /**
     * 审核人id （系统用户id)
     */
    private Long operatorId;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("amount", getAmount())
            .append("interestAmount", getInterestAmount())
            .append("totalAmount", getTotalAmount())
            .append("repaidAmount", getRepaidAmount())
            .append("loansNumber", getLoansNumber())
            .append("status", getStatus())
            .append("paybackTime", getPaybackTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
