package com.bigo.project.bigo.wallet.service;

import com.bigo.project.bigo.wallet.domain.Currency;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;

import java.util.List;

/**
 * @Description 币种service
 * @Author wenxm
 * @Date 2020/6/20 10:02
 */
public interface ICurrencyService {


    /**
     * 插入
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
