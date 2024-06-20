package com.bigo.project.bigo.wallet.domain;

import com.bigo.project.bigo.enums.WalletTypeEnum;
import lombok.Data;

/**
 * @Description 币种实体
 * @Author wenxm
 * @Date 2020/6/21 11:20
 */
@Data
public class Currency {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 币种
     */
    private String currency;

    /**
     * 排序
     */
    private Integer order;

    /**
     * 账户可用状态 0-可用 1-不可用
     */
    private Integer status;

    /**
     * 是否模拟金账户 0-否 1-是
     */
    private Integer virtual;

    /**
     * 是否支持充值 0-否 1-是
     */
    private Integer supRecharge;

    /**
     * 是否支持提币 0-否 1-是
     */
    private Integer supWithdraw;

    /**
     * 是否支持闪兑 0-否 1-是
     */
    private Integer supExchange;

    /**
     * 是否支持限时合约 0-否 1-是
     */
    private Integer supTimeContract;

    /**
     * 是否支持普通合约 0-否 1-是
     */
    private Integer supNormalContract;

}
