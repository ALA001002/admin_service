package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.util.List;

/**
 * @description: 我的邀请VO
 * @author: wenxm
 * @date: 2020/7/1 17:24
 */
@Data
public class MyInvitationVO {
    /**
     * 总邀请人数(含间接邀请)
     */
    private Integer totalInviteNum;
    /**
     * 邀请记录
     */
    private List<ChildVO> childList;
    /**
     * 当日交易信息
     */
    private ChildTradeVO todayTrade;
    /**
     * 昨日交易信息
     */
    private ChildTradeVO yesterdayTrade;
}
