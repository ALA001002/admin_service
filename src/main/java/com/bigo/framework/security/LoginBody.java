package com.bigo.framework.security;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录对象
 * 
 * @author bigo
 */
@Getter
@Setter
public class LoginBody
{
        /**
     * 用户名
     */
    private String username;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 短信验证码
     */
    private String captcha;
    /**
     * 图像验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid = "";

    /**
     * 版本号
     */
    private String versionCode;


    private Long googleCaptcha;

}
