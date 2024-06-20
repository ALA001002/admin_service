package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @description: 法币VO
 * @author: wenxm
 * @date: 2020/7/20 20:29
 */
@Data
public class LegalCurrencyVO {
    /**
     * 法币
     */
    private String legalCurrency;
    /**
     * 名称
     */
    private String name;
    /**
     * 买入汇率
     */
    private BigDecimal buyRate;
    /**
     * 卖出汇率
     */
    private BigDecimal sellRate;

    public String getBuyRate(){
        if(this.buyRate != null){
            return this.buyRate.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return null;
    }

    public String getSellRate(){
        if(this.sellRate != null){
            return this.sellRate.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return null;
    }
}
