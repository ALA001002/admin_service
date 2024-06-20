package com.bigo.project.bigo.api.vo;

import com.bigo.project.bigo.marketsituation.domain.Kline;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 合约详情
 * @Author wenxm
 * @Date 2020/6/20 13:22
 */
@Getter
@Setter
public class ContractInfoVO {

    /**
     * 交易对
     */
    private String symbolCode;
    /**
     * 交易对名称
     */
    private String symbolName;
    /**
     * 最新价格
     */
    private String price;
    /**
     * 相比于今天第一笔交易的涨跌幅
     */
    private BigDecimal change;
    /**
     * 当天最高价
     */
    private BigDecimal highPrice;
    /**
     * 当天最低价
     */
    private BigDecimal lowPrice;
    /**
     * 当天交易量
     */
    private BigDecimal vol;
    /**
     * 滑点
     */
    private String slipPoint;
    /**
     * 资金费率
     */
    private String capitalRate;
    /**
     * 资金费率周期(每隔n小时)
     */
    private String capitalRatePeriod;
    /**
     * 交易对支持的杠杆倍数列表
     */
    private List<Integer> multipleList;
    /**
     * 币图标地址
     */
    private String icon;
    /**
     * 是否支持限合约 0-否 1-是
     */
    private Integer supTimeContract;

    /** 报价币种 */
    private String baseCoin;

    /** 基础币种 */
    private String quoteCoin;

}
