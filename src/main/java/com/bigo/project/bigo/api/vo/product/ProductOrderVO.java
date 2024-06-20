package com.bigo.project.bigo.api.vo.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/28 14:42
 */
@Data
public class ProductOrderVO {

    /** $column.columnComment */
    private Long id;

    /** 用户ID */
    private Long uid;


    /** 产品名称 */
    private String productName;

    /** 购买金额 */
    private BigDecimal purchaseAmount;

    /** 币种 */
    private String currency;

    /** 订单状态 */
    private Integer status;


    private Date beginReleaseTime;

    /** 释放时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endReleaseTime;

}
