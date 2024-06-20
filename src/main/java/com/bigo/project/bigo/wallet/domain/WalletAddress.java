package com.bigo.project.bigo.wallet.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description 用户钱包和地址对应关系实体
 * @Author wenxm
 * @Date 2020/6/18 15:53
 */
@Getter
@Setter
public class WalletAddress {
    /**
     * 主键id
     */
    @Excel(name = "id")
    private Long id;
    /**
     * 用户id
     */
    @Excel(name = "用户ID")
    private Long uid;
    /**
     * 用户名
     */
    @Excel(name = "用户账号")
    private String username;
    /**
     * 钱包地址
     */
    @Excel(name = "钱包地址")
    private String address;

    /**
     * 币种
     */
    @Excel(name = "币种")
    private String coin;

    /**
     * 余额
     */
    @Excel(name = "余额")
    private BigDecimal balance;

    /**
     * 错误信息
     */
    @Excel(name = "错误信息")
    private String error;

}
