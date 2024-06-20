package com.bigo.project.bigo.api.vo;

import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.wallet.domain.Wallet;
import lombok.Data;

import java.util.Comparator;

/**
 * @Description 钱包VO
 * @Author wenxm
 * @Date 2020/6/21 11:20
 */
@Data
public class AccountVO extends Wallet  {

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

    /**
     * 折合美元
     * @return
     */
    private String usdPrice;

    private String ico;

/*    @Override
    public int compare(AccountVO o1, AccountVO o2) {
        if (o1.getOrder().compareTo(o2.getOrder()) == 1) {
            return -1;
        } else if (o1.getOrder().compareTo(o2.getOrder()) == -1) {
            return 1;
        } else {
            return 0;
        }
    }*/


//    public String getAccountName() {
//        return WalletTypeEnum.getNameByType(super.getType());
//    }
}
