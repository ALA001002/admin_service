package com.bigo.project.bigo.wallet.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/21 15:38
 */
@Getter
@Setter
public class AssetLog {

    /**
     * 主键id
     */
    private Long id;
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
     * 变更前数量
     */
    private BigDecimal before;
    /**
     * 变更金额
     */
    private BigDecimal amount;
    /**
     * 变更后金额
     */
    private BigDecimal after;
    /**
     * 钱包id
     */
    private Long walletId;
    /**
     * 变更时间
     */
    private Date operateTime;
    /**
     * 释放时间
     */
    private Date releaseTime;
    /**
     * 是否已释放
     */
    private Integer isRelease;
    /**
     * 获取分佣的合约id
     */
    private Long contractId;
    /**
     * 币种
     */
    private String coin;
    /**
     * 用户id
     */
    private Long uid;


}
