package com.bigo.framework.websocket.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.constant.HttpStatus;
import com.bigo.common.enums.WebSocketRequestTypeEnum;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.project.bigo.chat.domain.DockInfo;
import com.bigo.project.bigo.chat.domain.Message;
import com.bigo.project.bigo.chat.domain.WsMessage;
import com.bigo.project.bigo.chat.service.IDockInfoService;
import com.bigo.project.bigo.chat.service.IMessageService;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/13 18:06
 */
@Slf4j
@Component
@ServerEndpoint(value = "/ws")
public class WebSocketServer {
    /**静态变量，用来记录当前在线连接数。*/
    private static volatile int onlineCount = 0;
    /**concurrent包的线程安全map，用来存放每个用户客户端对应的WebSocket对象。*/
    private static ConcurrentHashMap<Long,WebSocketServer> userWebSocketMap = new ConcurrentHashMap<>();
    /**concurrent包的线程安全map，用来存放每个客服客户端对应的WebSocket对象。*/
    private static ConcurrentHashMap<Long,WebSocketServer> sysUserWebSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**是否是用户*/
    private Boolean isUser = true;
    /**用户ID*/
    private Long userId;

    private static TokenService tokenService;

    private static IMessageService messageService;

    private static ISysUserService sysUserService;

    private static IBigoUserService bigoUserService;

    private static IDockInfoService dockInfoService;

    @Autowired
    public void setService(TokenService tokenService, IMessageService messageService, ISysUserService sysUserService, IBigoUserService bigoUserService, IDockInfoService dockInfoService){
        WebSocketServer.tokenService = tokenService;
        WebSocketServer.messageService = messageService;
        WebSocketServer.sysUserService = sysUserService;
        WebSocketServer.bigoUserService = bigoUserService;
        WebSocketServer.dockInfoService = dockInfoService;
    }

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if(userId != null) {
            if (isUser) {
                userWebSocketMap.remove(userId);
            } else {
                sysUserWebSocketMap.remove(userId);
            }
        }
        //log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //log.info("用户消息:"+userId+",报文:"+message);
        //消息保存到数据库、redis
        if(StringUtils.isNotBlank(message)){
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                WsMessage wsData = JSONObject.toJavaObject(jsonObject,WsMessage.class);
                wsData.setCode(HttpStatus.SUCCESS);
                if(wsData.getType() == null) {
                    return;
                }
                Boolean isLogin = checkLoginStatus(wsData);
                switch (wsData.getType()){
                    case PING:
                        //心跳检测
                        break;
                    case LOGIN:
                        doLogin(wsData);
                        break;
                    case MESSAGE:
                        if(!isLogin){
                            break;
                        }
                        //发消息
                        sendMessageToUser(wsData);
                        break;
                    case CONTACT_SERVICE:
                        if(!isLogin){
                            break;
                        }
                        //请求客服
                        requireCustomerService(wsData);
                        break;
                    case HISTORY:
                        if(!isLogin){
                            break;
                        }
                        //获取聊天历史
                        if(!isUser){
                            Long uid = Long.valueOf(wsData.getData().toString());
                            requireHistory(wsData, uid);
                        }else{
                            requireHistory(wsData, null);
                        }
                        break;
                    case CONTRACT_LIST:
                        if(!isLogin){
                            break;
                        }
                        wsData.setData(null);
                        //客服获取负责用户列表
                        if(!isUser){
                            List<BigoUserEntity> userList = bigoUserService.listDockUserByCustomerServiceId(userId);
                            wsData.setData(userList);
                            sendMessage(wsData);
                        }
                        break;
                    case POINTS_STOP:
                        if(!isLogin){
                            break;
                        }
                        sendPointsStop(wsData);
                        break;
                    default:
                        wsData.setCode(HttpStatus.ERROR);
                        wsData.setData(null);
                        wsData.setMessage("unknown_request_type");
                }
                //回复消息
                sendMessage(wsData);
            } catch (Exception e){
                log.error("用户:"+userId+"请求错误,报文："+message,e);
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        //log.error("用户错误:"+this.userId+",原因:"+error.getMessage(),error);
    }

    /**
     * 实现服务器主动推送
     */
    private void sendMessage(WsMessage message) throws IOException {
        this.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
    }

    /**
     * 发送状态变更通知
     * */
    public static void noticeStatusChange(String type,Long userId) {
        try {
            log.info("【发送状态变更通知】用户ID:" + userId + "，类型:" + type);
            if (userId != null && userWebSocketMap.containsKey(userId)) {
                WsMessage wsData = new WsMessage();
                wsData.setData(type);
                wsData.setCode(HttpStatus.SUCCESS);
                wsData.setNonce(System.currentTimeMillis());
                wsData.setType(WebSocketRequestTypeEnum. STATUS_CHANGE);
                userWebSocketMap.get(userId).sendMessage(wsData);
            } else {
                log.error("【发送状态变更通知失败】用户:" + userId + ",不在线！");
            }
        }catch (Exception ex){
            log.error("【发送状态变更通知异常】用户:{}，类型:{}, 异常信息:{}", userId, type, ex.getMessage(), ex);
        }
    }

    public static synchronized int getOnlineCount() {
        return userWebSocketMap.size();
    }

    public static synchronized int getOnlineCustomerServiceCount() {
        return sysUserWebSocketMap.size();
    }

    private void addToMap(){
        if(isUser) {
            userWebSocketMap.put(userId, this);
        }else {
            sysUserWebSocketMap.put(userId, this);
        }
    }

    /**
     * 发送点停消息
     */
    private void sendPointsStop(WsMessage wsData) throws IOException {
        Message message =JSONObject.toJavaObject(JSON.parseObject(wsData.getData().toString()),Message.class);
        Long toUserId = message.getReceiver();
        //传送给对应toUserId用户的websocket
        if(message.getReceiver() != null ){
            WebSocketServer socketServer = null;
            wsData.setCode(HttpStatus.SUCCESS);
            if(userWebSocketMap.containsKey(toUserId)){
                socketServer = userWebSocketMap.get(toUserId);
            }
            //插入聊天记录
            if(socketServer != null){
                message.setCreateTime(new Date());
                try {
                    //发给对方
                    wsData.setData(message);
                    socketServer.sendMessage(wsData);
                }catch (IOException e){
                    wsData.setCode(HttpStatus.ERROR);
                    wsData.setMessage("failed");
                    sendMessage(wsData);
                }
            }
            message.setStatus(0);
            wsData.setData(message);
            messageService.insert(message);
        }else{
            log.error("用户:"+userId+"发送点停消息失败，未指定接收人,报文："+message);
        }
    }

    /**
     * 发送消息
     * @param wsData 消息数据
     * @throws IOException
     */
    private void sendMessageToUser(WsMessage wsData) throws IOException {
        Message message =JSONObject.toJavaObject(JSON.parseObject(wsData.getData().toString()),Message.class);
        message.setSender(userId);
        Long toUserId = message.getReceiver();
        //传送给对应toUserId用户的websocket
        if(message.getReceiver() != null ){
            WebSocketServer socketServer = null;
            wsData.setCode(HttpStatus.SUCCESS);
            if(isUser){
                //用户发给客服
                if(sysUserWebSocketMap.containsKey(toUserId)){
                    socketServer = sysUserWebSocketMap.get(toUserId);
                }
            }else{
                //客服发给用户
                if(userWebSocketMap.containsKey(toUserId)){
                    socketServer = userWebSocketMap.get(toUserId);
                }
            }
            //插入聊天记录
            if(socketServer != null){
                message.setCreateTime(new Date());
                try {
                    //发给对方
                    wsData.setData(message);
                    socketServer.sendMessage(wsData);
                }catch (IOException e){
                    wsData.setCode(HttpStatus.ERROR);
                    wsData.setMessage("failed");
                    sendMessage(wsData);
                }
            }
            message.setStatus(0);
            wsData.setData(message);
            messageService.insert(message);
        }else{
            log.error("用户:"+userId+"发送消息失败，未指定接收人,报文："+message);
        }
    }

    /**
     * 请求客服
     */
    private void requireCustomerService(WsMessage wsData) throws IOException {
        wsData.setData(null);
        // 先从数据库直接获取，如果没获取到，再进行匹配
        Long csId = dockInfoService.getCustomerServiceIdByUid(userId);
        if(csId == null){
            //如果有在线客服，则优先匹配在线客服
            List<Long> csList = sysUserWebSocketMap.isEmpty() ? sysUserService.listCustomerService() : Collections.list(sysUserWebSocketMap.keys());
            if(!CollectionUtils.isEmpty(csList)) {
                List<DockInfo> dockInfoList = dockInfoService.listDockInfo(StringUtils.join(csList, ','));
                if (!CollectionUtils.isEmpty(dockInfoList)) {
                    //取负责用户最少的客服
                    dockInfoList.sort(Comparator.comparingInt(DockInfo::getUserNum));
                    csId = dockInfoList.get(0).getCustomerServiceId();
                }
            }
            DockInfo info = new DockInfo();
            info.setUid(userId);
            info.setCustomerServiceId(csId);
            dockInfoService.insert(info);
        }
        //是否匹配到客服
        if(csId == null){
            wsData.setMessage("customer_service_not_matched");
            wsData.setCode(HttpStatus.ERROR);
        }else{
            wsData.setData(csId);
        }
    }

    /**
     * 请求聊天历史
     * @throws IOException
     */
    private void requireHistory(WsMessage wsData,Long bigoUserId) throws IOException {
        Boolean isCs = true;
        if(bigoUserId == null){
            bigoUserId = userId;
            isCs = false;
        }
        Message param = new Message();
        param.setUid(bigoUserId);
        List<Message> recordList = messageService.listMessageByParam(param);
        //将聊天记录设为已读
        if(isCs){
            param.setSender(bigoUserId);
        }else{
            param.setReceiver(bigoUserId);
        }
        messageService.updateMsgToRead(param);
        wsData.setData(recordList);
    }

    /**
     * 检查登录状态
     * @param wsData
     * @return
     */
    private Boolean checkLoginStatus(WsMessage wsData) {
        if(userId == null){
            wsData.setMessage("not_logged_in");
            wsData.setCode(HttpStatus.ERROR);
            return false;
        }
        return true;
    }

    /**
     * 登录
     * @param wsData
     */
    private void doLogin(WsMessage wsData){
        String token = wsData.getData().toString();
        if(StringUtils.isEmpty(token)){
            wsData.setCode(HttpStatus.ERROR);
            wsData.setMessage("token_cannot_be_null");
        }else{
            LoginUser loginUser = tokenService.getLoginUserByToken(token);
            if(loginUser == null){
                wsData.setCode(HttpStatus.ERROR);
                wsData.setMessage("not_logged_in");
            }else{
                if(loginUser.getUser() != null){
                    isUser = false;
                    userId = loginUser.getUser().getUserId();
                }else{
                    isUser = true;
                    userId = loginUser.getBigoUser().getUid();
                }
                //添加到map
                addToMap();
                wsData.setMessage(null);
                wsData.setData(null);
                wsData.setCode(HttpStatus.SUCCESS);
                //log.info("用户连接:"+userId+",当前在线人数为用户:" + getOnlineCount() + "，客服："+getOnlineCustomerServiceCount());
            }
        }
    }

}
