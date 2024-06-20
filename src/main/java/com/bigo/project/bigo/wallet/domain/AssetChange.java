package com.bigo.project.bigo.wallet.domain;

import com.bigo.project.bigo.enums.AmountTypeEnum;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Description 用户资金变更实体
 * @Author wenxm
 * @Date 2020/6/18 15:53
 */
@Getter
@Setter
@Builder
public class AssetChange {
    /**
     * 钱包id
     */
    private Long walletId;
    /**
     * 变更金额
     */
    private BigDecimal amount;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 币种
     */
    private String currency;
    /**
     * 账户类型
     */
    private Integer walletType;
    /**
     * 变更维度，0-增 1-减
     */
    private Integer dim;
    /**
     * 资产变更类型
     */
    private AssetLogTypeEnum type;
    /**
     * 资产变更子类型
     */
    private AssetLogSubTypeEnum subType;
    /**
     * 金额类型：0-正常，1-冻结
     */
    private Integer amountType;
}
