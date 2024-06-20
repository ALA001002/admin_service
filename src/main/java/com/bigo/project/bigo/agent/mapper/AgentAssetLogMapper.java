package com.bigo.project.bigo.agent.mapper;

import com.bigo.project.bigo.agent.domain.AgentAssetLog;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/29 14:24
 */
public interface AgentAssetLogMapper {

    /**
     * 新增资产变更记录
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
