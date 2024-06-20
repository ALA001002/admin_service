package com.bigo.project.bigo.wallet.entity;

import com.bigo.framework.web.domain.BaseEntity;
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
public class WalletEntity extends BaseEntity {
    /**
     * 主键id
     */
    private Long id;

    private Long[] ids;
    /**
     * 用户uid
     */
    private Long uid;
    private String uids;
    private String parentUid;

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
    private BigDecimal frozen;
    /**
     * 用户名
     */
    private String username;
    /**
     * 余额
     */
    private BigDecimal amount;
    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 操作类型
     */
    private Integer editType;

    private Integer editRelocateType;

    private Long googleCaptcha;

    private Long agentId;

    private Long productId;
}
