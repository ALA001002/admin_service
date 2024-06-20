package com.bigo.project.bigo.chat.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.util.Date;


/**
 * @description: 聊天消息
 * @author: wenxm
 * @date: 2020/7/14 14:03
 */
@Data
public class Message extends BaseEntity {
    /**
     * 主键
     */
    private Long id;
    /**
     * 发送人ID
     */
    private Long sender;
    /**
     * 接收人ID
     */
    private Long receiver;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息类型 0-文字 1-图片
     */
    private Integer type;
    /**
     * 消息状态 0-未读，1-已读
     */
    private Integer status;
    /**
     * 请求的用户ID
     */
    private Long uid;
    /**
     * 请求类型
     */
    private String requestType;

    private Date createTime;

    public Long getTimestamp(){
        if(this.createTime != null){
            return this.createTime.getTime();
        }
        return null;
    }
}
