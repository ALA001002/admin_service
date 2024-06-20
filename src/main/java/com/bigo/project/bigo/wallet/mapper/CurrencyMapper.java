package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.wallet.domain.Currency;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 币种信息mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface CurrencyMapper {

    /**
     * 插入记录
     * @param currency
     * @return
     */
    int insert(Currency currency);


    /**
     * 更新
     * @param currency
     * @return
     */
    int update(Currency currency);

    /**
     * 根据币名称获取币种信息
     * @param code
     * @return
     */
    Currency getByCode(String code);




}
