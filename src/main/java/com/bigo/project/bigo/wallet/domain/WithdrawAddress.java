package com.bigo.project.bigo.wallet.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * 提现地址对象 bg_withdraw_address
 * 
 * @author bigo
 * @date 2022-03-27
 */
@Data
public class WithdrawAddress extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long uid;

    /** 钱包Id */
    @Excel(name = "钱包Id")
    private Long walletId;

    /** 币种 */
    @Excel(name = "币种")
    private String coin;

    /** 提现地址 */
    @Excel(name = "提现地址")
    private String address;

    private String username;
    @Override
    public String toString() {
        return "WithdrawAddress{" +
                "id=" + id +
                ", uid=" + uid +
                ", walletId=" + walletId +
                ", coin='" + coin + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
