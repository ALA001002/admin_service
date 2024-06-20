package com.bigo.project.bigo.agent.service;

import com.bigo.project.bigo.agent.domain.AgentAssetLog;

import java.util.List;

/**
 * @description: 代理商
 * @author: wenxm
 * @date: 2020/8/4 16:22
 */
public interface IAgentAssetLogService {

    /**
     * 插入记录
     * @param log
     * @return
     */
    int insertLog(AgentAssetLog log);

    /**
     * 获取符合条件的记录列表
     * @param param
     * @return
     */
    List<AgentAssetLog> listByEntity(AgentAssetLog param);
}
