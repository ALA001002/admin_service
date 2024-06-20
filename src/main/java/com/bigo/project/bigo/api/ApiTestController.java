package com.bigo.project.bigo.api;

import com.bigo.framework.websocket.server.WebSocketNotifyServer;
import com.bigo.project.bigo.api.domain.PushReq;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message/")
public class ApiTestController {

    @RequestMapping(value="push")
    public String push(PushReq req){
        WebSocketNotifyServer.sendMessage(WebSocketNotifyServer.NotifyType.get(req.getType()));
        return "success";
    }
}
