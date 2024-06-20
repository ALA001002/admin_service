package com.bigo.project.bigo.api.vo;

import lombok.Data;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 16:38
 */
@Data
public class PayPassageVO {

    /** id */
    private Long id;

    /** 通道名称 */
    private String passageName;
    /**
     * 单笔最低金额
     */
    private Long minEveryAmount;
    /**
     * 单笔最大金额
     */
    private Long maxEveryAmount;
}
