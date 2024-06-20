package com.bigo.project.bigo.agent.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 代理商
 * @author: wenxm
 * @date: 2020/7/29 14:04
 */
@Data
public class Agent extends BaseEntity {
    /**
     * 代理商ID
     */
    private Long agentId;
    /**
     * 系统用户ID
     */
    private Long userId;
    /**
     * 邀请码
     */
    private Long agentCode;
    /**
     * 状态 0-正常 1-冻结
     */
    private Integer status;
    /**
     * 代理商名称
     */
    private String agentName;
    /**
     * 交易手续费分成比例
     */
    private BigDecimal feeShareRate;
    /**
     * 头寸分成比例
     */
    private BigDecimal profitShareRate;
    /**
     * 备付金
     */
    private BigDecimal cashDeposit;
    /**
     * usdt余额
     */
    private BigDecimal usdtBalance;
    /**
     * eth余额
     */
    private BigDecimal ethBalance;
    /**
     * libra余额
     */
    private BigDecimal bigoBalance;
    /**
     * 登录账号
     */
    private String loginName;


}
