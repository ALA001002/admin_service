package com.bigo.project.bigo.contract.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 限时合约周期
 * @author: wenxm
 * @date: 2020/9/1 15:47
 */
@Data
public class TimePeriod extends BaseEntity {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * symbol
     */
    private String symbol;
    /**
     * 结算周期（单位：秒）
     */
    private Integer period;
    /**
     * 收益率
     */
    private BigDecimal yieldRate;
    /**
     * 亏损率
     */
    private BigDecimal lossRate;
    /**
     * 状态 0-可用 1-不可用
     */
    private Integer status;
    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 最低下单金额
     */
    private BigDecimal minMoney;
}
