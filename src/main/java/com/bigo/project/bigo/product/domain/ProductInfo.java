package com.bigo.project.bigo.product.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 理财产品信息对象 bg_product_info
 * 
 * @author bigo
 * @date 2021-01-27
 */
@Data
public class ProductInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Long typeId;

    /** 收益率 */
    @Excel(name = "收益率")
    private BigDecimal profitRate;

    /** 收益时间 */
    @Excel(name = "收益时间")
    private Integer profitTime;

    /** 最低购买金额 */
    @Excel(name = "最低购买数量")
    private BigDecimal purchaseAmountMin;

    @Excel(name = "最高购买数量")
    private BigDecimal purchaseAmountMax;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;


    /** 总数量 */
    private Integer totalNumber;

    /** 剩余数量 */
    private Integer remainingNumber;

    /** 结算类型：1-日返，2-日返冻结，3-到期返 */
    @Excel(name = "结算类型")
    private Integer settlementType;


    /** 是否删除：0-否，1-是 */
    @Excel(name = "是否删除：0-否，1-是")
    private Long isDel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date countdownTime;

    @Excel(name = "是否热门：0-否，1-是")
    private Integer isTop;

    @Override
    public String toString() {
        return "ProductInfo{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", profitRate=" + profitRate +
                ", profitTime=" + profitTime +
                ", purchaseAmountMin=" + purchaseAmountMin +
                ", purchaseAmountMax=" + purchaseAmountMax +
                ", currency='" + currency + '\'' +
                ", totalNumber=" + totalNumber +
                ", remainingNumber=" + remainingNumber +
                ", settlementType=" + settlementType +
                ", isDel=" + isDel +
                '}';
    }
}
