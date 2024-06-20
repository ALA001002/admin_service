package com.bigo.project.bigo.otc.service.impl;

import com.bigo.project.bigo.otc.domain.LegalCurrency;
import com.bigo.project.bigo.otc.mapper.LegalCurrencyMapper;
import com.bigo.project.bigo.otc.service.ILegalCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/20 20:43
 */
@Service
public class LegalCurrencyServiceImpl implements ILegalCurrencyService {

    @Autowired
    private LegalCurrencyMapper legalCurrencyMapper;

    @Override
    public int insert(LegalCurrency legalCurrency) {
        return legalCurrencyMapper.insert(legalCurrency);
    }

    @Override
    public int update(LegalCurrency legalCurrency) {
        return legalCurrencyMapper.update(legalCurrency);
    }

    @Override
    public LegalCurrency getByCurrency(String currency) {
        return legalCurrencyMapper.getByCurrency(currency);
    }

    @Override
    public List<LegalCurrency> listAllLegalCurrency() {
        return legalCurrencyMapper.listAllLegalCurrency();
    }

    @Override
    public List<LegalCurrency> listByEntity(LegalCurrency entity) {
        return legalCurrencyMapper.listByEntity(entity);
    }

    @Override
    public int deleteById(Long id) {
        return legalCurrencyMapper.deleteById(id);
    }
}
