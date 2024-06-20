package com.bigo.project.bigo.contract.entity;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 币高合约entity
 * @Author wenxm
 * @Date 2020/6/21 17:00
 */
@Getter
@Setter
public class ContractEntity extends BaseEntity {
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
     * 合约标题
     */
    private String title;
    /**
     * 下单金额（即保证金）
     */
    private BigDecimal money;
    /**
     * 补仓费
     */
    private BigDecimal replenish;
    /**
     * 手续费
     */
    private BigDecimal fee;
    /**
     * 资金费用
     */
    private BigDecimal capitalFee;
    /**
     * 合约状态 0-持仓 1-用户平仓 2-触发止盈止损 3-强制平仓
     */
    private Integer status;
    /**
     * 关联计划委托id
     */
    private Long contractPlanId;
    /**
     * 交易类型：1-做多 2-做空
     */
    private Integer tradeType;
    /**
     * 合约周期
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
     * 平仓时间
     */
    private Date sellTime;
    /**
     * 平仓价
     */
    private BigDecimal sellPrice;
    /**
     * 收益率
     */
    private BigDecimal yieldRate;
    /**
     * 盈亏金额
     */
    private BigDecimal profit;
    /**
     * 收益类型：1盈 2亏 3平
     */
    private Integer profitType;
    /**
     * 建仓后余额
     */
    private BigDecimal oriBalance;
    /**
     * 平仓后的余额
     */
    private BigDecimal newBalance;
    /**
     * 是否用券
     */
    private Integer coupon;
    /**
     * 止损价格
     */
    private BigDecimal stopLoss;
    /**
     * 止盈价格
     */
    private BigDecimal stopSurplus;
    /**
     * 预估强制平仓价格
     */
    private BigDecimal predictPrice;
    /**
     * 平仓备注
     */
    private String remark;
    /**
     * 合约类型，0-逐仓，1-全仓
     */
    private Integer contractType;
    /**
     * 合约杠杆倍数
     */
    private Integer contractMultiple;
    /**
     * 赔偿时间
     */
    private Date compensateTime;
    /**
     * 用户名
     */
    private String username;
    /**
     * 代理商ID
     */
    private Long agentId;
    /**
     * 用户状态
     */
    private Integer userStatus;

}
