package com.bigo.project.bigo.product.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 理财产品订单对象 bg_product_order
 * 
 * @author bigo
 * @date 2021-01-27
 */
@Data
public class ProductOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    private Long[] ids;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long uid;

    /** 用户账号 */
    private String username;

    /** 用户账号 */
    private String userRemark;

    /**
     * 产品ID
     */
    private Long productId;

    /** 产品名称 */
    private Long typeId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 购买金额 */
    @Excel(name = "购买金额")
    private BigDecimal purchaseAmount;

    /** 收益率 */
    @Excel(name = "收益率")
    private BigDecimal profitRate;

    /** 收益金额 */
    @Excel(name = "收益金额")
    private BigDecimal profitAmount;

    /** 收益时间 */
    @Excel(name = "收益时间")
    private Integer profitTime;

    private String profitTimeType;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;

    /** 订单状态：1-冻结，2-已释放 */
    @Excel(name = "订单状态")
    private Integer status;

    /** 释放次数 */
    @Excel(name = "释放次数")
    private Integer releaseCount;

    /** 今日是否已释放：0-否，1-是 */
    @Excel(name = "今日是否已释放")
    private Integer isTodayRelease;

    /** 结算类型：1-日返，2-日返冻结，3-到期返 */
    @Excel(name = "结算类型")
    private Integer settlementType;

    /** 开始释放时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始释放时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date beginReleaseTime;

    /** 结束释放时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束释放时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endReleaseTime;

    private int isOld;

    private Date lastReleaseTime;

    private Long agentId;

    private Long googleCaptcha;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("productName", getProductName())
            .append("purchaseAmount", getPurchaseAmount())
            .append("profitRate", getProfitRate())
            .append("profitAmount", getProfitAmount())
            .append("currency", getCurrency())
            .append("status", getStatus())
            .append("beginReleaseTime", getBeginReleaseTime())
            .append("endReleaseTime", getEndReleaseTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
