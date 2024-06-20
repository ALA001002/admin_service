package com.bigo.project.bigo.api.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @Description 合约查询参数
 * @Author wenxm
 * @Date 2020/6/21 17:29
 */
@Getter
@Setter
public class ContractQueryParam {

    /**
     * 用户id
     */
    private Long uid;
    /**
     * 合约id
     */
    private Long contractId;
    /**
     * 交易对
     */
    @NotBlank(message = "symbol_cannot_be_empty")
    private String symbol;
    /**
     * 合约类型，0-逐仓，1-全仓
     */
    private Integer contractType;
    /**
     * 合约状态 0-持仓 1-用户平仓 2-触发止盈止损 3-强制平仓
     */
    private Integer status;
    /*======计划委托参数======= */
    /**
     * 委托类型 0-市价委托 1-计划委托
     */
    //@NotEmpty(message = "trust_type_cannot_be_empty")
    private Integer trustType;

}
