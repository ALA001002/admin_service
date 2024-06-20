package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @description: 我的邀请-交易VO
 * @author: wenxm
 * @date: 2020/7/1 17:28
 */
@Data
public class ChildTradeVO {
    /**
     * 新增邀请人数
     */
    private Integer inviteNum;
    /**
     * 交易人数
     */
    private Integer tradeNum;
    /**
     * 交易信息
     */
    private Map<String, BigDecimal> tradeInfo;

    public ChildTradeVO() {
    }

    public ChildTradeVO(Integer inviteNum, Integer tradeNum, Map<String, BigDecimal> tradeInfo) {
        this.inviteNum = inviteNum;
        this.tradeNum = tradeNum;
        this.tradeInfo = tradeInfo;
    }
}
