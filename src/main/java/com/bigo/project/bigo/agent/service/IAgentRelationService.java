package com.bigo.project.bigo.agent.service;

import com.bigo.project.bigo.agent.domain.AgentRelation;
import com.bigo.project.bigo.userinfo.domain.BigoUser;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/29 14:24
 */
public interface IAgentRelationService {

    /**
     * 新增关联关系
     * @param user
     * @return
     */
    Boolean insert(BigoUser user);

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
