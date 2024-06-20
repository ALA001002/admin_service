package com.bigo.project.bigo.wallet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description 用户钱包
 * @Author wenxm
 * @Date 2020/6/18 15:53
 */
@Getter
@Setter
public class Wallet {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 用户uid
     */
    private Long uid;
    /**
     * 钱包类型
     */
    private Integer type;
    /**
     * 币种
     */
    private String currency;
    /**
     * 余额
     */
    private BigDecimal balance;
    /**
     * 冻结金额
     */
//    @JsonIgnore
    private BigDecimal frozen;
    /**
     * 钱包顺序
     */
    private Integer order;

    //扩展字段
    /**
     * 变更金额
     */
    private BigDecimal changeAmount;

    public Wallet() {
    }

    public Wallet(Long uid, String currency, Integer type) {
        this.uid = uid;
        this.currency = currency;
        this.type = type;
    }
}
