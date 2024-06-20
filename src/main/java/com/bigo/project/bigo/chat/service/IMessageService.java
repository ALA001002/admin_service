package com.bigo.project.bigo.chat.service;

import com.bigo.project.bigo.chat.domain.Message;

import java.util.List;

/**
 * @description: 消息service
 * @author: wenxm
 * @date: 2020/7/14 16:08
 */
public interface IMessageService {

    /**
     * 插入
     * @param message
     * @return
     */
    int insert(Message message);

    /**
     * 更新
     * @param message
     * @return
     */
    int update(Message message);

    /**
     * 获取聊天记录列表
     * @param message
     * @return
     */
    List<Message> listMessageByParam(Message message);

    /**
     * 将消息设为已读
     * @param message
     * @return
     */
    int updateMsgToRead(Message message);
}
