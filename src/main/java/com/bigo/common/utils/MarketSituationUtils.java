package com.bigo.common.utils;

import com.bigo.common.constant.Constants;
import com.bigo.framework.config.RuoYiConfig;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.api.vo.ContractInfoVO;
import com.bigo.project.bigo.enums.CandlestickEnum;
import com.bigo.project.bigo.enums.SymbolCoinEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.project.bigo.ico.domain.SymbolCoin;
import com.bigo.project.bigo.marketsituation.domain.Deep;
import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 行情工具类
 * @Author wenxm
 * @Date 2020/6/24 14:01
 */
@Component
@Slf4j
public class MarketSituationUtils {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IKlineService klineService;

    private static MarketSituationUtils util;

    private MarketSituationUtils(){}

    @PostConstruct
    public void init() {
        util = this;
        util.redisCache = this.redisCache;
    }

    /**
     * 获取交易对当前的价格
     * @param symbol 交易对
     * @return 价格
     */
    public static BigDecimal getCurrentPriceBySymbol(String symbol){
        BigDecimal price = util.redisCache.getCacheObject(symbol+"_price");
        if(price == null){
            log.error("获取{}最新价格失败",symbol);
            throw new RuntimeException("failed_to_get_transaction_price");
        }
        return price;
    }

    public static Deep getDeepPriceBySymbol(String symbol){
        Deep deep = util.redisCache.getCacheObject(symbol+"_deep");
        if(deep == null){
            log.error("获取{}最行情深度失败",symbol);
            throw new RuntimeException("failed_to_get_transaction_price");
        }
        return deep;
    }



    /**
     * 获取实时合约信息
     * @return
     */
    public static List<ContractInfoVO> getContractInfo(Integer type){
        List<ContractInfoVO> list = Lists.newArrayList();
        if(type == 1) {
            for (SymbolEnum symbol : SymbolEnum.values()) {
                String code = symbol.getCode();
                ContractInfoVO info = new ContractInfoVO();
                BigDecimal price = util.redisCache.getCacheObject(code + "_price") == null ? BigDecimal.ZERO : util.redisCache.getCacheObject(code + "_price");
                BigDecimal change = new BigDecimal("0.00");
                Kline kline = util.redisCache.getCacheObject(code + "_today_kline");
                if (kline == null) {
                    info.setHighPrice(BigDecimal.ZERO);
                    info.setLowPrice(BigDecimal.ZERO);
                    info.setVol(BigDecimal.ZERO);
                } else {
                    info.setHighPrice(kline.getHigh());
                    info.setLowPrice(kline.getLow());
                    info.setVol(kline.getVol());
                    change = price.subtract(kline.getOpen()).divide(kline.getOpen(), 4, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal("100"));
                }
                info.setSymbolCode(symbol.getCode());
                info.setSymbolName(symbol.getName());
                info.setSlipPoint(transferToPercent(CoinUtils.getSlipPoint(symbol.getCode())));
                info.setIcon(getIconBySymbolName(symbol.getName()));
                info.setMultipleList(transferLever(DictUtils.getDictValue("bigo_rate_config", "lever_" + symbol.getCode(), "1,5,10,20,50,100,125")));
                info.setPrice(price.toPlainString());
                info.setChange(change);
                info.setCapitalRate(transferToPercent(CoinUtils.getCapitalRate()));
                info.setCapitalRatePeriod(CoinUtils.getCapitalPeriod());
                info.setSupTimeContract(symbol.getSupTimeContract());
                String[] coinArray = symbol.getName().split("/");
                info.setBaseCoin(coinArray[0]);
                info.setQuoteCoin(coinArray[1]);
                list.add(info);
            }
        }else {
            for (SymbolCoinEnum  symbol: SymbolCoinEnum.values()) {
                String code = symbol.getCode();
                ContractInfoVO info = new ContractInfoVO();
                BigDecimal price = util.redisCache.getCacheObject(code + "_price") == null ? BigDecimal.ZERO : util.redisCache.getCacheObject(code + "_price");
                BigDecimal change = new BigDecimal("0.00");
                Kline kline = util.redisCache.getCacheObject(code + "_today_kline");
                if (kline == null) {
                    info.setHighPrice(BigDecimal.ZERO);
                    info.setLowPrice(BigDecimal.ZERO);
                    info.setVol(BigDecimal.ZERO);
                } else {
                    info.setHighPrice(kline.getHigh());
                    info.setLowPrice(kline.getLow());
                    info.setVol(kline.getVol());
                    change = price.subtract(kline.getOpen()).divide(kline.getOpen(), 4, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal("100"));
                }
                info.setSymbolCode(symbol.getCode());
                info.setSymbolName(symbol.getName());
                info.setSlipPoint(transferToPercent(CoinUtils.getSlipPoint(symbol.getCode())));
                info.setIcon(getIconBySymbolName(symbol.getName()));
                info.setMultipleList(transferLever(DictUtils.getDictValue("bigo_rate_config", "lever_" + symbol.getCode(), "1,5,10,20,50,100,125")));
                info.setPrice(price.toPlainString());
                info.setChange(change);
                info.setCapitalRate(transferToPercent(CoinUtils.getCapitalRate()));
                info.setCapitalRatePeriod(CoinUtils.getCapitalPeriod());
                info.setSupTimeContract(symbol.getSupTimeContract());
                list.add(info);
            }
        }
        return list;
    }

/*    private static List<Kline> getList(String symbol) {
        String period = CandlestickEnum.MIN5.getCode();;
        String key = symbol + period;
        List<Kline> klineList = util.redisCache.getCacheObject(key);

        if( klineList != null && klineList.size() > 0){
            klineList = klineList.subList(0, 9);
        }

        return klineList;
    }*/

    private List<Kline> getKlineList(String symbol) {
        String period = CandlestickEnum.MIN5.getCode();;
        String key = symbol + period;
        List<Kline> klineList = redisCache.getCacheObject(key);

        if(klineList == null) {
            Kline kline = new Kline();
            kline.setSymbol(symbol);
            kline.setPeriod(period);
            klineList = klineService.findKlineList(kline);
            if(klineList != null && klineList.size() > 0) {
                redisCache.setCacheObject(symbol + period, klineList);
            }
        }

        if( klineList != null && klineList.size() > 0){
            klineList = klineList.subList(0, 9);
        }

        return klineList;
    }

    /**
     * 将数字字符串转换为list
     * @param value
     * @return
     */
    private static List<Integer> transferLever(String value){
        List<String> arrList = Arrays.asList(value.split(","));
        return arrList.stream().map(Integer::valueOf).collect(Collectors.toList());
    }

    private static String transferToPercent(BigDecimal value){
        BigDecimal capitalRate = value.multiply(new BigDecimal("100"));
        return capitalRate.toString()+"%";
    }

    public static String getIconBySymbolName(String symbolName){
        String iconName = symbolName.split("/")[0]+".png";
        return RuoYiConfig.getFileUrl()+Constants.RESOURCE_PREFIX + "/icon/" + iconName;
    }


}
