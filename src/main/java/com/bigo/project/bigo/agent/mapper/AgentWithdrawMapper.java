package com.bigo.project.bigo.agent.mapper;

import com.bigo.project.bigo.agent.domain.AgentWithdraw;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/29 14:24
 */
public interface AgentWithdrawMapper {

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

}
