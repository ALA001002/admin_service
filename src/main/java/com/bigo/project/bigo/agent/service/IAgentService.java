package com.bigo.project.bigo.agent.service;

import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.domain.AgentAssetChange;
import com.bigo.project.bigo.agent.domain.AgentRegisterParam;
import com.bigo.project.bigo.agent.domain.AgentWithdraw;
import com.bigo.project.bigo.agent.vo.UserChildrenVo;
import com.bigo.project.bigo.agent.vo.UserSumVo;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.wallet.entity.WalletEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/29 15:22
 */
public interface IAgentService {

    /**
     * 新增代理商
     * @param param
     * @return
     */
    int addAgent(AgentRegisterParam param) throws IOException;

    /**
     * 更新代理商
     * @param agent
     * @return
     */
    int updateAgent(Agent agent);

    /**
     * 变更状态
     * @param agent
     * @return
     */
    int updateStatus(Agent agent);

     /**
     * 获取代理商信息
     * @param agentId
     * @return
     */
    Agent getByAgentId(Long agentId);

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

    /**
     * 计算代理商分成
     * @param contractId 合约ID
     * @param isTimeContract 是否限时合约
     */
    void calAgentShare(Long contractId, Boolean isTimeContract);

    /**
     * 代理商资产变更
     * @param change
     */
    void changeAsset(AgentAssetChange change);

    /**
     * 代理商提现
     * @param withdraw
     * @return
     */
    Boolean withdraw(AgentWithdraw withdraw);

    /**
     * 根据系统用户ID获取代理商
     * @param userId
     * @return
     */
    Agent getByUserId(Long userId);

    /**
     * 统计
     */
    List<UserChildrenVo> statistics(WalletEntity entity);

    Map totalStatistics(Map params);
}
