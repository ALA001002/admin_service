package com.bigo.project.bigo.api.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Description 资金划转参数
 * @Author wenxm
 * @Date 2020/6/21 10:58
 */
@Getter
@Setter
public class AssetTransferParam {

    /**
     * 用户id
     */
    private Long uid;
    /**
     * 资金划出账户
     */
    @NotNull(message = "from_account_cannot_be_null")
    private Integer fromType;
    /**
     * 资金划入账户
     */
    @NotNull(message = "to_account_cannot_be_null")
    private Integer toType;
    /**
     * 币种
     */
    @NotNull(message = "currency_account_cannot_be_null")
    private String currency;
    /**
     * 划转数量
     */
    @NotBlank(message = "amount_account_cannot_be_null")
    private BigDecimal amount;


}
