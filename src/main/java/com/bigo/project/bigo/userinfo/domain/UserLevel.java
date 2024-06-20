package com.bigo.project.bigo.userinfo.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 用户等级实体
 * @author: wenxm
 * @date: 2020/6/29 17:58
 */
@Data
public class UserLevel {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 等级名称
     */
    private String name;
    /**
     * 达成此等级需要推荐的用户
     */
    private Integer requireUser;
    /**
     * 达成此等级需要缴纳的合约手续费
     */
    private BigDecimal requireFee;
    /**
     * 需要达成的手续费（usdt）
     */
    private BigDecimal fee;
    /**
     * 一级分佣比例
     */
    private BigDecimal firstRate;
    /**
     * 二级分佣比例
     */
    private BigDecimal secondRate;
    /**
     * 每日提币上限（已实名认证）
     */
    private BigDecimal limitAuth;
    /**
     * 每日提币上限（未实名认证）
     */
    private BigDecimal limitNoAuth;
    /**
     * 单日累计提币多少需要审核
     */
    private BigDecimal limitExamine;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 操作人id
     */
    private Long operatorId;
}
