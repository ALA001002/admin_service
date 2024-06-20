package com.bigo.project.bigo.ico.domain;

import java.math.BigDecimal;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * ico交易记录对象 bg_ico_exchange_history
 * 
 * @author xx
 * @date 2023-01-15
 */
@Data
public class IcoExchangeHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户UID */
    @Excel(name = "用户UID")
    private Long uid;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;

    /** 成交数量 */
    @Excel(name = "成交数量")
    private BigDecimal exchangeNum;

    /** 成交价格 */
    @Excel(name = "成交价格")
    private BigDecimal exchangePrice;

    /** 交易类型：0-买，1-卖 */
    @Excel(name = "交易类型：0-买，1-卖")
    private Integer type;


    public IcoExchangeHistory() {

    }

    public IcoExchangeHistory(Long uid, String currency, Integer type) {
        this.uid = uid;
        this.currency = currency;
        this.type = type;
    }
}
