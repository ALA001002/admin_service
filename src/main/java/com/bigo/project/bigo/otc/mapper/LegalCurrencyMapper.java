package com.bigo.project.bigo.otc.mapper;

import com.bigo.project.bigo.otc.domain.LegalCurrency;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 法币mapper
 * @author: wenxm
 * @date: 2020/7/20 15:28
 */
public interface LegalCurrencyMapper {

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
     * 根据ID删除
     * @param id
     * @return
     */
    int deleteById(Long id);

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

}
