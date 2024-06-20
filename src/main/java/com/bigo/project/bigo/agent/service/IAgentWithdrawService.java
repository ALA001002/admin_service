package com.bigo.project.bigo.agent.service;

import com.bigo.project.bigo.agent.domain.AgentWithdraw;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/10 15:39
 */
public interface IAgentWithdrawService {

    /**
     * 新增
     * @param withdraw
     * @return
     */
    int insert(AgentWithdraw withdraw);

    /**
     * 获取符合条件的记录列表
     * @param param
     * @return
     */
    List<AgentWithdraw> listByEntity(AgentWithdraw param);

    /**
     * 更新
     * @param withdraw
     * @return
     */
    int update(AgentWithdraw withdraw);

    /**
     * 根据ID获取
     * @param id
     * @return
     */
    AgentWithdraw getById(Long id);

    /**
     * 提币审核
     * @param withdraw
     * @return
     */
    int checkWithdraw(AgentWithdraw withdraw);
}
