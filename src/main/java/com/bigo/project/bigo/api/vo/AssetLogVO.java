package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/2 16:10
 */
@Data
public class AssetLogVO {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long uid;
    /**
    用户账号
     */
    private String username;
    /**
     * 操作类型
     */
    private Integer type;
    /**
     * 操作子类型
     */
    private Integer subType;
    /**
     * 操作维度：0-增加，1-减少
     */
    private Integer dim;
    /**
     * 变更金额
     */
    private BigDecimal amount;
    /**
     * 变更时间
     */
    private Date operateTime;
    /**
     * 释放时间
     */
    private Date releaseTime;
    /**
     * 币种
     */
    private String coin;
}
