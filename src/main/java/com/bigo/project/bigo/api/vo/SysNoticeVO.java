package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description: 系统公告VO
 * @author: wenxm
 * @date: 2020/6/28 14:30
 */
@Data
public class SysNoticeVO {

    /**
     * 公告ID
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 标题
     */
    private String content;
    /**
     * 封面图片
     */
    private String coverImage;
    /**
     * 文章来源
     */
    private String source;
    /**
     * 公告时间
     */
    private Date createTime;

}
