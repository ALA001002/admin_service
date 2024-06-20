package com.bigo.common.utils;
import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.spring.SpringUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.OperateEnum;
import com.bigo.project.bigo.wallet.domain.Currency;
import com.bigo.project.bigo.wallet.service.ICurrencyService;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * 数字货币工具类
 * @author wenxm
 */
@Component
public class CurrencyUtils {

    private static CurrencyUtils util;

    private CurrencyUtils(){}

    @Autowired
    private ICurrencyService currencyService;

    @Autowired
    private RedisCache redisService;

    @PostConstruct
    public void init() {
        util = this;
        util.currencyService = this.currencyService;
        util.redisService = this.redisService;
    }

    /**
     * 校验币种是否支持操作
     * @param code
     * @param operate
     */
    public static void validateCurrency(String code, OperateEnum operate){
        String key = code + "_INFO";
        Currency currency = SpringUtils.getBean(RedisCache.class).getCacheObject(key);
        if(currency == null){
            synchronized(CurrencyUtils.class){
                currency = util.currencyService.getByCode(code);
                //缓存5
                util.redisService.setCacheObject(key, currency, 5, TimeUnit.SECONDS);
            }
            if (currency == null) {
                throw new CustomException("unsupported_coin");
            }
        }
        if (!currency.getStatus().equals(0)) {
            throw new CustomException("unsupported_coin");
        }
        switch (operate){
            case EXCHANGE:
                if(currency.getSupExchange().equals(0)){
                    throw new CustomException("unsupported_coin");
                }
                break;
            case WITHDRAW:
                if(currency.getSupWithdraw().equals(0)){
                    throw new CustomException("unsupported_coin");
                }
                break;
            case RECHARGE:
                if(currency.getSupRecharge().equals(0)){
                    throw new CustomException("unsupported_coin");
                }
                break;
            case BUY_NORMAL_CONTRACT:
                if(currency.getSupNormalContract().equals(0)){
                    throw new CustomException("unsupported_coin");
                }
                break;
            case BUY_TIME_CONTRACT:
                if(currency.getSupTimeContract().equals(0)){
                    throw new CustomException("unsupported_coin");
                }
                break;
            default:
                throw new CustomException("unsupported_coin");
        }
    }

}
