package com.bigo.project.bigo.agent.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 代理商提现
 * @author: wenxm
 * @date: 2020/8/5 11:06
 */
@Data
public class AgentWithdraw extends BaseEntity {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 代理商
     */
    private Long agentId;
    /**
     * 代理商名称
     */
    private String agentName;
    /**
     * 币种
     */
    private String coin;
    /**
     * 提现数量
     */
    private BigDecimal amount;
    /**
     * 状态 0-待审核 1-审核通过 2-审核失败
     */
    private Integer status;
    /**
     * 交易哈希
     */
    private String hash;
    /**
     * 提币地址
     */
    private String toAddress;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 审核人ID
     */
    private Long operatorId;
    /**
     * 审核人
     */
    private String operatorName;
    /**
     * 审核时间
     */
    private Date operateTime;
}
