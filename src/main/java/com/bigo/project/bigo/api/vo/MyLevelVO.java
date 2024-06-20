package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 我的等级VO
 * @author: wenxm
 * @date: 2020/7/1 16:33
 */
@Data
public class MyLevelVO {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 当前等級
     */
    private Integer level;
    /**
     * 一级分佣比例
     */
    private BigDecimal firstRate;
    /**
     * 二级分佣比例
     */
    private BigDecimal secondRate;
    /**
     * 下一级等级
     */
    private String nextLevel;
    /**
     * 下一级一级分佣比例
     */
    private BigDecimal nextFirstRate;
    /**
     * 下一级二级分佣比例
     */
    private BigDecimal nextSecondRate;



}
