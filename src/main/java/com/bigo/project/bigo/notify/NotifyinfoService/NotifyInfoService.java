package com.bigo.project.bigo.notify.NotifyinfoService;

import com.bigo.framework.websocket.server.WebSocketNotifyServer;
import com.bigo.project.bigo.notify.domain.NotifyInfo;
import com.bigo.project.bigo.notify.mapper.NotifyInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotifyInfoService {


    @Resource
    NotifyInfoMapper notifyInfoMapper;

    public int saveNotify(NotifyInfo notifyInfo,int addNum){
        List<NotifyInfo> notifyInfoList = notifyInfoMapper.queryNotify(notifyInfo);
        int num = Optional.ofNullable(notifyInfo.getNum()).orElse(0);
        WebSocketNotifyServer.NotifyType type = notifyInfo.getType();
        Long userId = notifyInfo.getUserId();
        if(!CollectionUtils.isEmpty(notifyInfoList)) {
            notifyInfo = null;
            for (NotifyInfo temp : notifyInfoList) {
                if (temp.getType() == type) {
                    notifyInfo = temp;
                    num = Optional.ofNullable(temp.getNum()).orElse(0);
                    break;
                }
            }
        }
        if(notifyInfo!=null && notifyInfo.getId()!=null){
            num += addNum;
            notifyInfo.setNum(num);
            notifyInfo.setUserId(userId);
            notifyInfoMapper.updateNotify(notifyInfo);
        }else{
            notifyInfo = new NotifyInfo();
            notifyInfo.setNum(1);
            notifyInfo.setUserId(userId);
            notifyInfo.setType(type);
            notifyInfoMapper.insertNotify(notifyInfo);
        }
        return num;
    }
    public void saveNotify(NotifyInfo notifyInfo){
        List<NotifyInfo> notifyInfoList = notifyInfoMapper.queryNotify(notifyInfo);
        WebSocketNotifyServer.NotifyType type = notifyInfo.getType();
        Long userId = notifyInfo.getUserId();
        int num = Optional.ofNullable(notifyInfo.getNum()).orElse(0);
        if(!CollectionUtils.isEmpty(notifyInfoList)) {
            notifyInfo = null;
            for (NotifyInfo temp : notifyInfoList) {
                if (temp.getType() == type) {
                    notifyInfo = temp;
                    num = Optional.ofNullable(notifyInfo.getNum()).orElse(0);
                    break;
                }
            }
        }
        if(notifyInfo!=null && notifyInfo.getId()!=null){
            notifyInfo.setNum(0);
            notifyInfo.setUserId(userId);
            notifyInfoMapper.updateNotify(notifyInfo);
        }else{
            notifyInfo = new NotifyInfo();
            notifyInfo.setNum(1);
            notifyInfo.setUserId(userId);
            notifyInfo.setType(type);
            notifyInfoMapper.insertNotify(notifyInfo);
        }
    }

    public Map<WebSocketNotifyServer.NotifyType,Integer> queryNotify(NotifyInfo notifyInfo){
        List<NotifyInfo> notifyInfoList = notifyInfoMapper.queryNotify(notifyInfo);
        Map<WebSocketNotifyServer.NotifyType,Integer> result = new HashMap<>();
        if(!CollectionUtils.isEmpty(notifyInfoList)){
            result = notifyInfoList.stream().collect(Collectors.toMap(NotifyInfo::getType,NotifyInfo::getNum,(t1,t2)->t1));
        }
        return result;
    }

}
