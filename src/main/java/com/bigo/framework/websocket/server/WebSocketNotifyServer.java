package com.bigo.framework.websocket.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.spring.SpringUtils;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.project.bigo.chat.service.IDockInfoService;
import com.bigo.project.bigo.chat.service.IMessageService;
import com.bigo.project.bigo.notify.NotifyinfoService.NotifyInfoService;
import com.bigo.project.bigo.notify.domain.NotifyInfo;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.system.service.ISysUserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/13 18:06
 */
@Slf4j
@Component
@ServerEndpoint(value = "/admin/{token}")
public class WebSocketNotifyServer {
    private static ConcurrentHashMap<String, UserSession> sysUserWebSocketMap = new ConcurrentHashMap<>();

    private static TokenService tokenService;

    private static IMessageService messageService;

    private static ISysUserService sysUserService;

    private static IBigoUserService bigoUserService;

    private static IDockInfoService dockInfoService;

    @Autowired
    public void setService(TokenService tokenService, IMessageService messageService, ISysUserService sysUserService, IBigoUserService bigoUserService, IDockInfoService dockInfoService){
        WebSocketNotifyServer.tokenService = tokenService;
        WebSocketNotifyServer.messageService = messageService;
        WebSocketNotifyServer.sysUserService = sysUserService;
        WebSocketNotifyServer.bigoUserService = bigoUserService;
        WebSocketNotifyServer.dockInfoService = dockInfoService;
    }

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("token") String token) {
        LoginUser user = tokenService.getLoginUserByToken(token);
        if(user!=null){
            Long userId = user.getUser().getUserId();
            if(userId!=null) {
                sysUserWebSocketMap.put(token, new UserSession(user,session));
                NotifyInfo notifyInfo = new NotifyInfo();
                notifyInfo.setUserId(userId);
                NotifyInfoService notifyInfoService = SpringUtils.getBean("notifyInfoService");
                Map<NotifyType, Integer> notifyTypeMap = notifyInfoService.queryNotify(notifyInfo);
                try {
                    session.getBasicRemote().sendText(JSON.toJSONString(new NotifyMsg(NotifyType.FULL,notifyTypeMap)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("token") String token) {
        if(token != null) {
            sysUserWebSocketMap.remove(token);
        }
        //log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session,@PathParam("token")String token) {
        NotifyMsg notifyMsg = JSON. parseObject(message,NotifyMsg.class);
        if(notifyMsg!=null){
            if(NotifyType.PING != notifyMsg.type) {
                if("RESET".equals(notifyMsg.data)){
                    NotifyInfo notifyInfo = new NotifyInfo();
                    UserSession userSession = sysUserWebSocketMap.get(token);
                    if(userSession!=null) {
                        notifyInfo.setUserId(userSession.getUserId());
                    }
                    notifyInfo.setType(notifyMsg.type);
                    notifyInfo.setNum(0);
                    NotifyInfoService notifyInfoService = SpringUtils.getBean("notifyInfoService");
                    notifyInfoService.saveNotify(notifyInfo);
                }else {
                    try {
                        session.getBasicRemote().sendText(JSON.toJSONString(new NotifyMsg(NotifyType.PING)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error,@PathParam("token")String token) {
        //log.error("用户错误:"+this.userId+",原因:"+error.getMessage(),error);
        error.printStackTrace();
        if(token!=null){
            sysUserWebSocketMap.remove(token);
        }
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendMessage(NotifyType type) {
        Iterator<Map.Entry<String,UserSession>> iterator = sysUserWebSocketMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,UserSession> entry = iterator.next();
            UserSession userSession = entry.getValue();
            Session session = userSession.getSession();
            Long userId = userSession.getUserId();
            NotifyMsg notifyMsg = new NotifyMsg(type);

            NotifyInfoService notifyInfoService = SpringUtils.getBean("notifyInfoService");
            NotifyInfo notifyInfo = new NotifyInfo();
            notifyInfo.setUserId(userId);
            notifyInfo.setType(type);
            notifyInfo.setNum(1);
            int num = notifyInfoService.saveNotify(notifyInfo,1);
            notifyMsg.data = num;
            String msg = JSONObject.toJSONString(notifyMsg);
            log.debug("sendMsg={},userId={}",msg,userId);
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Data
    private static class UserSession {
        LoginUser user;
        Session session;

        public UserSession(LoginUser user, Session session) {
            this.user = user;
            this.session = session;
        }

        public Long getUserId(){
            return user.getUser().getUserId();
        }
    }

    @Data
    private static class NotifyMsg {
        NotifyType type;
        Object data;
        public NotifyMsg(){}
        public NotifyMsg(NotifyType type) {
            this.type = type;
        }
        public NotifyMsg(NotifyType type,Object data) {
            this.type = type;
            this.data = data;
        }
    }

    public enum NotifyType{
        RECHARGE,WITHDRAW,PRODUCT,CONTRACT,AUTH,ORDER,SUGGEST,PING,FULL;

        public static NotifyType get(String type) {
            if(StringUtils.isEmpty(type)){
                return WITHDRAW;
            }
            NotifyType[] notifyTypes = NotifyType.values();
            for(NotifyType notifyType:notifyTypes){
                if(notifyType.name().equals(type.toUpperCase(Locale.ROOT))){
                    return notifyType;
                }
            }
            return WITHDRAW;
        }
    }
}
