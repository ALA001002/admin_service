package com.bigo.project.bigo.api.vo.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductTypeVO {


    private Long typeId;
    /**
     * 产品名称
     */
    private String typeName;

    /**
     * 购买金额
     */
    private BigDecimal purchaseAmount;

    /**
     * 收益金额
     */
    private BigDecimal profitAmount;
}
