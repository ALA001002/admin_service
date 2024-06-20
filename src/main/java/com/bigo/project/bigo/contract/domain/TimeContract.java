package com.bigo.project.bigo.contract.domain;

import com.bigo.project.bigo.enums.SymbolEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 限时合约实体
 * @Author wenxm
 * @Date 2020/6/21 17:00
 */
@Data
public class TimeContract {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 交易对
     */
    private String symbol;
    /**
     * 购买合约使用的币种
     */
    private String currency;
    /**
     * 下单金额
     */
    private BigDecimal money;
    /**
     * 手续费
     */
    private BigDecimal fee;
    /**
     * 合约状态 0-持仓 1-平仓
     */
    private Integer status;
    /**
     * 交易类型：1-做多 2-做空
     */
    private Integer tradeType;
    /**
     * 结算周期
     */
    private Integer period;
    /**
     * 建仓时间
     */
    private Date buyTime;
    /**
     * 建仓价
     */
    private BigDecimal buyPrice;
    /**
     * 结算时间
     */
    private Date settlementTime;
    /**
     * 结算价
     */
    private BigDecimal settlementPrice;
    /**
     * 收益率
     */
    private BigDecimal yieldRate;
    /**
     * 亏损率
     */
    private BigDecimal lossRate;
    /**
     * 盈亏金额
     */
    private BigDecimal profit;
    /**
     * 收益类型：1盈 2亏 3平
     */
    private Integer profitType;

    /**
     * 结算类型 1-盈，2-亏，3-随行情
     */
    private Integer settlementType;

    /**
     * ip地址
     */
    public String ipAddress;

    /**
     * 位置
     */
    public String position;

    public String getSymbolName(){
        return SymbolEnum.getNameByCode(this.symbol);
    }

    public Long getRemainTime(){
        if(this.status.equals(0)) {
            return (this.settlementTime.getTime() - System.currentTimeMillis()) / 1000;
        }else{
            return 0L;
        }
    }


}
