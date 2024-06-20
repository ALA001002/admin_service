package com.bigo.project.bigo.chat.mapper;

import com.bigo.project.bigo.chat.domain.Message;

import java.util.List;

/**
 * @Description 消息mapper
 * @Author wenxm
 * @Date 2020/7/13 15:31
 */
public interface MessageMapper {

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
