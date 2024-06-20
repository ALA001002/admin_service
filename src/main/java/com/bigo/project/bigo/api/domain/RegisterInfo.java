package com.bigo.project.bigo.api.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Description 用户注册信息
 * @Author wenxm
 * @Date 2020/6/17 16:34
 */
@Getter
@Setter
public class RegisterInfo {
    /**
     * 手机号
     */
    private String phone;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 密码
     */
    @NotBlank(message = "password_cannot_be_null")
    @Length(min = 6, max = 20, message = "password_must_be_between_6_and_20_characters")
    private String password;
    /**
     * 短信验证码
     */
//    @NotBlank(message = "captcha_cannot_be_null")
    private String captcha;
    /**
     * 推荐人邀请码
     */
    private String invitationCode;
    /**
     * 区号
     */
    private Long areaId;
    /**
     * 注册方式 0-手机注册 1-邮箱注册
     */
    private Integer regType;
    /**
     * 支付密码
     */
    private String payPassword;

    private Integer type;
}
