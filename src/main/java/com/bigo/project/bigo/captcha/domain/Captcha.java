package com.bigo.project.bigo.captcha.domain;

import lombok.Data;

import java.util.Date;

/**
 * @description: 短信验证码实体
 * @author: wenxm
 * @date: 2020/6/29 9:22
 */
@Data
public class Captcha {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 验证码
     */
    private String captcha;
    /**
     * 电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 使用状态 0-未使用 1-已使用
     */
    private Integer status;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * ip
     */
    private String ip;
    /**
     * 创建时间
     */
    private Date createTime;
}
