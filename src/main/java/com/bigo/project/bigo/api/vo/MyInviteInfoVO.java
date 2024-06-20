package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 我的邀请VO
 * @author: wenxm
 * @date: 2020/7/1 17:24
 */
@Data
public class MyInviteInfoVO {
    /**
     * 总邀请人数(含间接邀请)
     */
    private Integer inviteNum;
    /**
     * 邀请记录
     */
    private List<ChildVO> childList;
    /**
     * USDT收益
     */
    private BigDecimal usdtProfit;
    /**
     * ETH收益
     */
    private BigDecimal ethProfit;
    /**
     * 邀请链接
     */
    private String inviteUrl;
}
