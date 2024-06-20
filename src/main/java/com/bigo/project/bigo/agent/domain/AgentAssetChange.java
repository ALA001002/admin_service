package com.bigo.project.bigo.agent.domain;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * @Description 代理商资金变更实体
 * @Author wenxm
 * @Date 2020/6/18 15:53
 */
@Data
@Builder
public class AgentAssetChange {
    /**
     * 钱包id
     */
    private Long agentId;
    /**
     * 变更金额
     */
    private BigDecimal amount;
    /**
     * 变更手续费
     */
    private BigDecimal fee;
    /**
     * 币种
     */
    private String coin;
    /**
     * 变更维度，0-增 1-减
     */
    private Integer dim;
    /**
     * 资产变更类型 0-分成 1-提现 2-提现失败
     */
    private Integer type;
    /**
     * 合约ID
     */
    private Long contractId;
}
