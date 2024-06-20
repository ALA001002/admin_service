package com.bigo.project.bigo.notify.mapper;

import com.bigo.project.bigo.notify.domain.NotifyInfo;

import java.util.List;

public interface NotifyInfoMapper {

    int insertNotify(NotifyInfo notifyInfo);
    void updateNotify(NotifyInfo notifyInfo);
    List<NotifyInfo> queryNotify(NotifyInfo notifyInfo);
}
