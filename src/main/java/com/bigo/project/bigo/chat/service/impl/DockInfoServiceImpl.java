package com.bigo.project.bigo.chat.service.impl;

import com.bigo.project.bigo.chat.domain.DockInfo;
import com.bigo.project.bigo.chat.mapper.DockInfoMapper;
import com.bigo.project.bigo.chat.service.IDockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/15 16:17
 */
@Service
public class DockInfoServiceImpl implements IDockInfoService {

    @Autowired
    private DockInfoMapper dockInfoMapper;

    @Override
    public int insert(DockInfo dockInfo) {
        return dockInfoMapper.insert(dockInfo);
    }

    @Override
    public int update(DockInfo dockInfo) {
        return dockInfoMapper.update(dockInfo);
    }

    @Override
    public Long getCustomerServiceIdByUid(Long uid) {
        return dockInfoMapper.getCustomerServiceIdByUid(uid);
    }

    @Override
    public List<DockInfo> listDockInfo(String csIds) {
        return dockInfoMapper.listDockInfo(csIds);
    }


}
