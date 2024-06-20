package com.bigo.project.bigo.chat.domain;

import com.bigo.common.enums.WebSocketRequestTypeEnum;
import lombok.Data;

/**
 * @description: WebSocket消息
 * @author: wenxm
 * @date: 2020/7/15 16:49
 */
@Data
public class WsMessage {
    /**
     * 请求类型
     */
    private WebSocketRequestTypeEnum type;
    /**
     * 状态码 参考http
     */
    private int code;
    /**
     * 用于失败消息描述
     */
    private String message;
    /**
     * 返回的数据
     */
    private Object data;
    /**
     * 幂等字段
     */
    private Long nonce;

}
