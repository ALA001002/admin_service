package com.bigo.project.bigo.chat.service.impl;

import com.bigo.project.bigo.chat.domain.Message;
import com.bigo.project.bigo.chat.mapper.MessageMapper;
import com.bigo.project.bigo.chat.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/14 16:29
 */
@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public int insert(Message message) {
        return messageMapper.insert(message);
    }

    @Override
    public int update(Message message) {
        return messageMapper.update(message);
    }

    @Override
    public List<Message> listMessageByParam(Message message) {
        return messageMapper.listMessageByParam(message);
    }

    @Override
    public int updateMsgToRead(Message message) {
        return messageMapper.updateMsgToRead(message);
    }
}
