package com.bigo.project.bigo.agent.domain;

import lombok.Data;

import java.util.Date;

/**
 * @description: 代理商与用户关联关系
 * @author: wenxm
 * @date: 2020/8/4 16:54
 */
@Data
public class AgentRelation {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 代理商ID
     */
    private Long agentId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date createTime;
}
