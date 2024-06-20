package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/1 17:25
 */
@Data
public class ChildVO {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 邀请时间
     */
    private Date inviteTime;
}
