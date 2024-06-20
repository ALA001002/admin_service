package com.bigo.project.bigo.otc.service;

import com.bigo.project.bigo.otc.domain.LegalCurrency;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/20 20:42
 */
public interface ILegalCurrencyService {

    /**
     * 插入
     * @param legalCurrency
     * @return
     */
    int insert(LegalCurrency legalCurrency);

    /**
     * 变更状态
     * @param legalCurrency
     * @return
     */
    int update(LegalCurrency legalCurrency);

    /**
     * 根据法币获取法币信息
     * @param currency
     * @return
     */
    LegalCurrency getByCurrency(String currency);

    /**
     * 获取所有可用法币
     * @return
     */
    List<LegalCurrency> listAllLegalCurrency();

    /**
     * 根据参数查询
     * @param entity
     * @return
     */
    List<LegalCurrency> listByEntity(LegalCurrency entity);

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    int deleteById(Long id);
}
