package com.bigo.project.bigo.chat.service;

import com.bigo.project.bigo.chat.domain.DockInfo;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/15 16:16
 */
public interface IDockInfoService {

    /**
     * 插入
     * @param dockInfo
     * @return
     */
    int insert(DockInfo dockInfo);

    /**
     * 更新
     * @param dockInfo
     * @return
     */
    int update(DockInfo dockInfo);

    /**
     * 获取负责用户的客服
     * @param uid
     * @return
             */
    Long getCustomerServiceIdByUid(Long uid);

    /**
     * 获取客服的对接信息
     * @param csIds
     * @return
     */
    List<DockInfo> listDockInfo(String csIds);
}
