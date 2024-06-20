package com.bigo.project.bigo.captcha.mapper;

import com.bigo.project.bigo.captcha.domain.Captcha;

/**
 * @description: 短信验证码mapper
 * @author: wenxm
 * @date: 2020/6/29 9:21
 */
public interface CaptchaMapper {

    /**
     * 插入
     * @param captcha
     * @return
     */
    int insert(Captcha captcha);

    /**
     * 更新使用状态为已使用
     * @param id
     * @return
     */
    int updateStatus(Long id);

    /**
     * 根据参数获取短信验证码
     * @param captcha
     * @return
     */
    Captcha getByParam(Captcha captcha);
}
