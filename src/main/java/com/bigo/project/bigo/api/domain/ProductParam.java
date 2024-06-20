package com.bigo.project.bigo.api.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/28 1:04
 */
@Getter
@Setter
public class ProductParam {
    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 购买数量
     */
    private BigDecimal buyProducts;


}
