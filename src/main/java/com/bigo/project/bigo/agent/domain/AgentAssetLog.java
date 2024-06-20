package com.bigo.project.bigo.agent.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 代理商资产记录
 * @author: wenxm
 * @date: 2020/7/29 14:17
 */
@Data
public class AgentAssetLog extends BaseEntity {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 代理商ID
     */
    private Long agentId;
    /**
     * 资产变更类型 0-分成 1-提现 2-提现失败
     */
    private Integer type;
    /**
     * 操作维度：0-增加，1-减少
     */
    private Integer dim;
    /**
     * 币种
     */
    private String coin;
    /**
     * 变更前金额
     */
    private BigDecimal before;
    /**
     * 头寸分成（备付金额）
     */
    private BigDecimal amount;
    /**
     * 手续费分成
     */
    private BigDecimal fee;
    /**
     * 变更后金额
     */
    private BigDecimal after;
    /**
     * 关联合约ID
     */
    private Long contractId;
    /**
     * 代理商名称
     */
    private String agentName;
}
