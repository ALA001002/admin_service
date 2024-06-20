package com.bigo.project.bigo.agent.service.impl;

import com.bigo.project.bigo.agent.domain.AgentAssetChange;
import com.bigo.project.bigo.agent.domain.AgentWithdraw;
import com.bigo.project.bigo.agent.mapper.AgentWithdrawMapper;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.agent.service.IAgentWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/10 15:40
 */
@Service
public class AgentWithdrawServiceImpl implements IAgentWithdrawService {

    @Autowired
    private AgentWithdrawMapper agentWithdrawMapper;

    @Autowired
    private IAgentService agentService;

    @Override
    public int insert(AgentWithdraw withdraw) {
        return agentWithdrawMapper.insert(withdraw);
    }

    @Override
    public List<AgentWithdraw> listByEntity(AgentWithdraw param) {
        return agentWithdrawMapper.listByEntity(param);
    }

    @Override
    public int update(AgentWithdraw withdraw) {
        return agentWithdrawMapper.update(withdraw);
    }

    @Override
    public AgentWithdraw getById(Long id) {
        return agentWithdrawMapper.getById(id);
    }

    @Override
    @Transactional
    public int checkWithdraw(AgentWithdraw withdraw) {
        if(withdraw.getStatus() == 2){
            //驳回提币申请需要返还扣除的金额
            AgentAssetChange change = AgentAssetChange.builder().agentId(withdraw.getAgentId())
                    .amount(withdraw.getAmount())
                    .coin(withdraw.getCoin())
                    .dim(0)
                    .type(2)
                    .build();
            agentService.changeAsset(change);
        }
        return agentWithdrawMapper.update(withdraw);
    }
}
