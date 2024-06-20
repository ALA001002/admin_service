package com.bigo.project.bigo.wallet.service.impl;

import com.bigo.project.bigo.wallet.domain.Currency;
import com.bigo.project.bigo.wallet.mapper.CurrencyMapper;
import com.bigo.project.bigo.wallet.service.ICurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/9/21 16:07
 */
@Service
public class CurrencyServiceImpl implements ICurrencyService {

    @Autowired
    private CurrencyMapper currencyMapper;

    @Override
    public int insert(Currency currency) {
        return currencyMapper.insert(currency);
    }

    @Override
    public int update(Currency currency) {
        return currencyMapper.update(currency);
    }

    @Override
    public Currency getByCode(String code) {
        return currencyMapper.getByCode(code);
    }
}
