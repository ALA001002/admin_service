package com.bigo.project.bigo.api.vo;

import lombok.Data;

/**
 * @description: 轮播图实体
 * @author: wenxm
 * @date: 2020/6/28 14:11
 */
@Data
public class RotationPictureVO {

    private Integer order;

    private String url;

    private String noticeId;

    public RotationPictureVO() {

    }

    public RotationPictureVO(Integer order, String url) {
        this.order = order;
        this.url = url;
    }
}
