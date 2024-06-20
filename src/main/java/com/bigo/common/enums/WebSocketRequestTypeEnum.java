package com.bigo.common.enums;

/**
 * @description: WebSocket请求类型枚举
 * @author: wenxm
 * @date: 2020/7/15 16:39
 */
public enum WebSocketRequestTypeEnum {
    /**
     * 消息
     */
    MESSAGE,
    /**
     * 连接客服
     */
    CONTACT_SERVICE,
    /**
     * 聊天记录
     */
    HISTORY,
    /**
     * 联系人列表（客服使用）
     */
    CONTRACT_LIST,
    /**
     * 心跳检测
     */
    PING,
    /**
     * 登录
     */
    LOGIN,
    /**
     * 状态变更
     */
    STATUS_CHANGE,
    /**
     * 点停
     */
    POINTS_STOP,
    ;

}
