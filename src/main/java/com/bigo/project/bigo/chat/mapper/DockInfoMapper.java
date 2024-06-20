package com.bigo.project.bigo.chat.mapper;

import com.bigo.project.bigo.chat.domain.DockInfo;
import com.bigo.project.bigo.chat.domain.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 客服对接mapper
 * @Author wenxm
 * @Date 2020/7/13 15:31
 */
public interface DockInfoMapper {

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
    List<DockInfo> listDockInfo(@Param("csIds") String csIds);


}
