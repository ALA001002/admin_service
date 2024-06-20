package com.bigo.project.bigo.userinfo.domain;

import com.bigo.common.utils.StringUtils;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户谷歌秘钥对象 bg_user_google_secret
 * 
 * @author bigo
 * @date 2024-03-06
 */
@Setter
@Getter
public class UserGoogleSecret extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 谷歌秘钥 */
    @Excel(name = "谷歌秘钥")
    private String googleSecretKey;

    /**
     * 用户名
     */
    private String username;

    /**
     * 谷歌秘钥
     */
    private Long googleCaptcha;

    public Long getBindStatus() {
        Long status = 0L;
        if(StringUtils.isNotEmpty(this.getGoogleSecretKey())) {
            return 1L;
        }
        return status;
    }
}
