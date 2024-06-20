package com.bigo.project.bigo.api.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 设置合约止盈止损参数
 * @author: wenxm
 * @date: 2020/7/28 14:31
 */
@Data
public class ContractStopParam {
    /**
     * 合约ID
     */
    private Long contractId;
    /**
     * 计划委托ID
     */
    private Long contractPlanId;
    /**
     * 止盈类型 1-按价格 2-按百分比
     */
    private Integer stopSurplusType;
    /**
     * 止盈值
     */
    private BigDecimal stopSurplus;
    /**
     * 止损类型 1-按价格 2-按百分比
     */
    private Integer stopLossType;
    /**
     * 止损值
     */
    private BigDecimal stopLoss;
}
