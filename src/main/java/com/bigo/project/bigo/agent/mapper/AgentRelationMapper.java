package com.bigo.project.bigo.agent.mapper;

import com.bigo.project.bigo.agent.domain.AgentAssetLog;
import com.bigo.project.bigo.agent.domain.AgentRelation;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/29 14:24
 */
public interface AgentRelationMapper {

    /**
     * 新增
     * @param relation
     * @return
     */
    int insert(AgentRelation relation);

    /**
     * 根据用户ID获取对应的代理商
     * @param userId
     * @return
     */
    Long getAgentIdByUserId(Long userId);

    /**
     * 获取代理商的下级列表
     * @param agentId
     * @return
     */
    List<AgentRelation> listByAgentId(Long agentId);


}
