package com.bigo.project.bigo.notify.domain;

import com.bigo.framework.websocket.server.WebSocketNotifyServer;
import lombok.Data;

@Data
public class NotifyInfo {
    Long id;
    WebSocketNotifyServer.NotifyType type;
    Integer num;
    Long userId;

}
