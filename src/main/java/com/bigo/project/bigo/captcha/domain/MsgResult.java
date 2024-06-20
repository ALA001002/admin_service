package com.bigo.project.bigo.captcha.domain;

import lombok.Data;

/**
 * @description: 傲众云短信接口返回实体
 * @author: wenxm
 * @date: 2020/6/28 21:38
 */
@Data
public class MsgResult {

    /**
     * 状态
     */
    private String status;
    /**
     * 状态码
     */
    private String code;
    /**
     * 状态码
     */
    private String respCode;
    /**
     * 消息
     */
    private String respMsg;
    /**
     * 状态码
     */
    private String msgid;
}
