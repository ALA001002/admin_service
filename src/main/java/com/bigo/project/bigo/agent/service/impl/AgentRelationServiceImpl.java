package com.bigo.project.bigo.agent.service.impl;


import com.bigo.project.bigo.agent.domain.AgentRelation;
import com.bigo.project.bigo.agent.mapper.AgentRelationMapper;
import com.bigo.project.bigo.agent.service.IAgentRelationService;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/4 17:05
 */
@Service
public class AgentRelationServiceImpl implements IAgentRelationService {

    @Autowired
    private AgentRelationMapper agentRelationMapper;

    @Autowired
    private IAgentService agentService;

    @Override
    public Boolean insert(BigoUser user) {
        if(user.getParentUid() != null){
            Long agentId = agentRelationMapper.getAgentIdByUserId(user.getParentUid());
            agentId = agentId == null ? agentService.getAgentIdByAgentCode(user.getParentUid()) : agentId;
            if(agentId != null){
                AgentRelation relation = new AgentRelation();
                relation.setAgentId(agentId);
                relation.setUserId(user.getUid());
                agentRelationMapper.insert(relation);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Long getAgentIdByUserId(Long userId) {
        return agentRelationMapper.getAgentIdByUserId(userId);
    }

    @Override
    public List<AgentRelation> listByAgentId(Long agentId) {
        return agentRelationMapper.listByAgentId(agentId);
    }
}
