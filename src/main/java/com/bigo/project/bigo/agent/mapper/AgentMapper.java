package com.bigo.project.bigo.agent.mapper;

import com.bigo.project.bigo.agent.domain.Agent;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/29 14:24
 */
public interface AgentMapper {

    /**
     * 插入
     * @param agent
     * @return
     */
    int insert(Agent agent);

    /**
     * 更新
     * @param agent
     * @return
     */
    int update(Agent agent);

    /**
     * 增加余额
     * @param agent
     * @return
     */
    int addBalance(Agent agent);

    /**
     * 变更状态
     * @param agent
     * @return
     */
    int updateStatus(Agent agent);

    /**
     * 根据代理商ID获取代理商信息
     * @param agentId
     * @return
     */
    Agent getByAgentId(Long agentId);

    /**
     * 根据系统用户ID获取代理商
     * @param userId
     * @return
     */
    Agent getByUserId(Long userId);

    /**
     * 根据代理商ID获取代理商信息(更新使用)
     * @param agentId
     * @return
     */
    Agent getAgentForUpdate(Long agentId);

    /**
     * 获取代理商列表
     * @param agent
     * @return
     */
    List<Agent> listByEntity(Agent agent);

    /**
     * 根据邀请码获取代理商ID
     * @param agentCode
     * @return
     */
    Long getAgentIdByAgentCode(Long agentCode);


}
