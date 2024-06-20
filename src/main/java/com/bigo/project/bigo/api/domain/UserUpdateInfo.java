package com.bigo.project.bigo.api.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @Description 用户更新资料信息
 * @Author wenxm
 * @Date 2020/6/17 16:34
 */
@Getter
@Setter
public class UserUpdateInfo {
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 性别 0-未设置 1-男 2-女
     */
    private Integer sex;
    /**
     * 简介
     */
    private String profile;
    /**
     * 所在地
     */
    private Long areaId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 邀请人id
     */
    private String invitationCode;
    /**
     * 联系方式
     */
    private String contractInfo;
}
