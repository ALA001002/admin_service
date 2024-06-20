package com.bigo.project.bigo.agent.service.impl;

import com.bigo.project.bigo.agent.domain.AgentAssetLog;
import com.bigo.project.bigo.agent.mapper.AgentAssetLogMapper;
import com.bigo.project.bigo.agent.service.IAgentAssetLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/4 16:27
 */
@Service
public class AgentAssetLogServiceImpl implements IAgentAssetLogService {

    @Autowired
    private AgentAssetLogMapper agentAssetLogMapper;

    @Override
    public int insertLog(AgentAssetLog log) {
        return agentAssetLogMapper.insertLog(log);
    }

    @Override
    public List<AgentAssetLog> listByEntity(AgentAssetLog param) {
        return agentAssetLogMapper.listByEntity(param);
    }
}
