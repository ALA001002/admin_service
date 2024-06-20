package com.bigo.project.system.mapper;

import com.bigo.project.system.domain.SysNotice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 通知公告表 数据层
 * 
 * @author bigo
 */
public interface SysIndexMapper
{
    /**
     * 获取用户数量
     * 
     * @param params 查询参数
     * @return 结果
     */
    Long getUserNum(Map<String, Object> params);

    /**
     * 获取订单数量
     *
     * @param params 查询参数
     * @return 结果
     */
    Long getOrderNum(Map<String, Object> params);

    /**
     * 获取订单金额
     *
     * @param params 查询参数
     * @return 结果
     */
    BigDecimal getOrderMoney(Map<String, Object> params);

    /**
     * 获取订单手续费
     *
     * @param params 查询参数
     * @return 结果
     */
    BigDecimal getOrderFee(Map<String, Object> params);

    /**
     * 获取订单盈亏
     *
     * @param params 查询参数
     * @return 结果
     */
    BigDecimal getProfit(Map<String, Object> params);

    /**
     * 获取用户充提
     *
     * @param params 查询参数
     * @return 结果
     */
    BigDecimal getWithdrawInfo(Map<String, Object> params);

    /**
     * 获取用户充提手续费
     *
     * @param params 查询参数
     * @return 结果
     */
    BigDecimal getWithdrawFee(Map<String, Object> params);


    BigDecimal getTimeProfit(Map<String, Object> params);

    Long getTimeOrderNum(Map<String, Object> params);

    BigDecimal getTimeOrderMoney(Map<String, Object> params);

    BigDecimal getTimeOrderFee(Map<String, Object> params);

    List<Map> getWithdrawInfoList(Map<String, Object> params);
    Long getFirstDepositUserNum(Map<String, Object> params);

    List<Map> getManualList(Map<String, Object> params);

    BigDecimal sumUserBalance(Map params);

    Long getRechargeNum(Map params);
}