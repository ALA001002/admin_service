package com.bigo.project.bigo.marketsituation.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.DictUtils;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.project.bigo.enums.CandlestickEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.http.HttpClientUtil;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.marketsituation.domain.*;
import com.bigo.project.bigo.marketsituation.entity.SlipDot;
import com.bigo.project.bigo.marketsituation.mapper.BlineMapper;
import com.bigo.project.bigo.marketsituation.mapper.KlineMapper;
import com.bigo.project.bigo.marketsituation.service.IMarketSituationService;
import com.bigo.project.bigo.marketsituation.service.ISlipDotService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/19 16:12
 */
@Service
@Slf4j
public class MarketSituationService implements IMarketSituationService {

    @Autowired
    private KlineMapper klineMapper;

    @Autowired
    private BlineMapper blineMapper;

    @Autowired
    private ISlipDotService slipDotService;

    @Autowired
    private RedisCache redisCache;

    //国内网络访问
    //private static final String klineUrl = "https://api.huobi.de.com/market/history/kline?size=100&symbol=";
    //private static final String blineUrl = "https://api.huobi.de.com/market/history/trade?size=100&symbol=";

    // 国外网络访问
    private static final String klineUrl="https://api.huobi.pro/market/history/kline?size=200&symbol=";
   private static final String blineUrl="https://api.huobi.pro/market/history/trade?size=200&symbol=";


    @Override
    public void requestKlineFromHb() throws InterruptedException {
        int count = SymbolEnum.values().length;
        int periodCount = CandlestickEnum.values().length;
        int totalCount = periodCount * count;
        Long libraTimeOfIssue = Long.valueOf(CoinUtils.getTimeOfIssue());   // libra 币发行时间
        ExecutorService executors = Executors.newFixedThreadPool(totalCount);
        for(int m=0;m<periodCount;m++){
            CandlestickEnum period = CandlestickEnum.values()[m];
            //不同粒度间隔1.5秒请求，以防火币限频
            Thread.sleep(1500);
            for(SymbolEnum symbol : SymbolEnum.values()){
                String symbolCode = symbol.getCode();
                String url = klineUrl + symbolCode + "&period=" + period.getCode();
                // 启动线程抓取
                executors.execute(()->{
                    String resultJson = HttpClientUtil.get(url);
                    try {
                        HbKlineResult result = JSONObject.toJavaObject(JSON.parseObject(resultJson), HbKlineResult.class);
                        if (result == null || !"ok".equals(result.getStatus())) {
                            log.error("请求K线数据失败，url：{}，返回报文：{}", url, resultJson);
                            return;
                        }
                        List<Kline> list1 = result.getData();
                        if(list1 != null && list1.size() == 0) {
                            return;
                        }
                        List<Kline> list = new LinkedList<>();
                        for(Kline kline : list1){
                            kline.setTimestamp(kline.getId());
                            kline.setSymbol(symbolCode);
                            kline.setPeriod(period.getCode());
                            list.add(kline);
                        }
                        if(list != null && list.size() == 0) {
                            return;
                        }
                        Kline klineQuery = new Kline();
                        klineQuery.setSymbol(symbolCode);
                        klineQuery.setPeriod(period.getCode());
                        Long lastKid = getMaxTimestampBySymbolAndPeriod(klineQuery);
                        //记录今天的交易数据，最高价，最低价，交易量
                        if(period.getCode().equals(CandlestickEnum.DAY1.getCode())){
                            //天粒度，第一条数据就是今天的数据
                            Kline todayKline = list.get(0);
                            redisCache.setCacheObject(symbolCode+"_today_kline",todayKline);
                        }
                        //已经写入数据库的数据过滤掉
                        if(lastKid != null){
                            list = list.stream().filter(a->a.getTimestamp() > lastKid).collect(Collectors.toList());
                        }

                        if(list.size()>1){
                            //最新的一条数据不入库，因为收还没确定
                            Kline lastKline = list.get(0);
                            list.remove(0);
                            //火币返回的数据是根据时间戳倒序排列的，存入数据库需要重新排序
                            list.sort((a,b)-> (int) (a.getTimestamp() - b.getTimestamp()));
                            dealSlipDotBeforeInsert(list, period);
                            klineMapper.batchInsert(list);
                            KlineQuery params = new KlineQuery();
                            params.setSize(499);
                            params.setSymbol(symbolCode);
                            params.setPeriod(period.getCode());
                            //重新从数据库读出来，写入redis
                            list = klineMapper.listKlineForCache(params);
                            list.add(0,lastKline);
                            redisCache.deleteObject(symbolCode+period.getCode());
                            redisCache.setCacheObject(symbolCode+period.getCode(),list);
                            redisCache.setCacheObject(symbolCode + "_" + period.getCode() + "_max_ts", list.get(1).getTimestamp());
                        }
                    }catch (Exception ex){
                        log.error("请求K线数据失败，url：{}，错误信息：{}", url, ex.getMessage(), ex);
                    }
                });
            }
        }
        executors.shutdown();
    }

    @Override
    public void requestBlineFromHb() {
        ExecutorService executors = Executors.newFixedThreadPool(SymbolEnum.values().length);
        for(SymbolEnum symbol : SymbolEnum.values()) {
            String symbolCode = symbol.getCode();
            String url = blineUrl + symbolCode;
            // 启动线程抓取
            executors.execute(()->{
                String resultJson = HttpClientUtil.get(url);
                try {
                    HbBlineResult result = JSONObject.toJavaObject(JSON.parseObject(resultJson), HbBlineResult.class);
                    if (result == null || !"ok".equals(result.getStatus())) {
                        log.error("请求B线数据失败，url：{}，返回报文：{}", url, resultJson);
                        return;
                    }
                    List<HbBline> list = result.getData();
                    List<Bline> blineList = Lists.newArrayList();
                    if(list.size() == 0) {
                        return;
                    }
                    //如果有滑点，要加上滑点
                    BigDecimal slipPrice = redisCache.getCacheObject(symbolCode+"_slipdot");
                    for(HbBline hbBline : list){
                        for(Bline bline : hbBline.getData()){
                            bline.setBid(bline.getId());
                            bline.setSymbol(symbolCode);
                            if(slipPrice != null) {
                                bline.setRealPrice(bline.getPrice());
                                bline.setPrice(bline.getPrice().add(slipPrice));
                            }
                            blineList.add(bline);
                        }
                    }
                    //写入redis
                    redisCache.deleteObject(symbolCode);
                    redisCache.setCacheObject(symbolCode, blineList);
                    Long lastTradeId = getMaxTradeIdBySymbol(symbolCode);
                    if(lastTradeId != null){
                        blineList = blineList.stream().filter(a->a.getTradeId()>lastTradeId).collect(Collectors.toList());
                    }
                    if(blineList.size()>0) {
                        blineList.sort((a, b) -> (int) (a.getTradeId() - b.getTradeId()));
                        Bline bline = blineList.get(blineList.size()-1);
                        if(slipPrice != null) {
                            //有滑点的B线数据才插入数据库
                            blineMapper.batchInsert(blineList);
                        }
                        //计算合约信息
                        calContractInfo(symbolCode, bline);
                        //重新计算K线最后一个点
                        calKlineLastPoint(symbolCode, bline);
                    }
                }catch (Exception ex){
                    log.error("请求B线数据失败，url：{}，错误信息：{}", url, ex.getMessage(), ex);
                }
            });
        }
        executors.shutdown();
    }

    /**
     * 计算交合约信息
     * @param symbol 交易对
     * @param bline 最新价格
     */
    private void calContractInfo(String symbol, Bline bline){
        redisCache.setCacheObject(symbol+"_price", bline.getPrice());
        redisCache.setCacheObject(symbol+"_max_trade_id",bline.getTradeId());
    }

    private Long getMaxTimestampBySymbolAndPeriod(Kline klineQuery){
        String key = klineQuery.getSymbol() + "_" + klineQuery.getPeriod() + "_max_ts";
        Object cache = redisCache.getCacheObject(key);
        if(cache == null){
            return klineMapper.getMaxTimestampBySymbolAndPeriod(klineQuery);
        }else{
            return Long.valueOf(cache.toString());
        }
    }

    private Long getMaxTradeIdBySymbol(String symbol){
        Object cache = redisCache.getCacheObject(symbol+"_max_trade_id");
        if(cache == null){
            return blineMapper.getMaxTradeIdBySymbol(symbol);
        }else{
            return Long.valueOf(cache.toString());
        }
    }

    /**
     * 获取每天的第一笔交易信息
     * @param queryParam
     * @return
     */
    private Bline getPerDayFirstTrade(Map<String, Object> queryParam){
        String key = queryParam.get("symbol")+"_"+queryParam.get("zeroTime");
        Object cache = redisCache.getCacheObject(key);
        if(cache == null){
            Bline bline = blineMapper.getPerDayFirstTrade(queryParam);
            redisCache.setCacheObject(key,bline,1, TimeUnit.DAYS);
            return bline;
        }else{
            return (Bline) cache;
        }
    }

    /**
     * 根据最新交易信息计算K线的最后一个点
     * @param symbol
     * @param bline
     */
    private void calKlineLastPoint(String symbol, Bline bline){
        for(CandlestickEnum period : CandlestickEnum.values()){
            String klineKey = symbol + period.getCode();
            List<Kline> klineList = redisCache.getCacheObject(klineKey);
            if(CollectionUtils.isEmpty(klineList)){
                return;
            }
            Kline lastPoint = klineList.get(0);
            lastPoint.setClose(bline.getPrice());
            //如果b线的价格大于最后一个k线的高点，则将K线高点设为b线的价格
            if(bline.getPrice().compareTo(lastPoint.getHigh()) > 0){
                lastPoint.setHigh(bline.getPrice());
            }
            //如果b线的价格小于于最后一个k线的低点，则将K线低点设为b线的价格
            if(bline.getPrice().compareTo(lastPoint.getLow()) < 0){
                lastPoint.setLow(bline.getPrice());
            }
            /*if(symbol.contains(SymbolEnum.BIXUSDT.getCode().substring(0,3))) {
                // 发行时间
                Long timeOfIssue = Long.valueOf(CoinUtils.getTimeOfIssue());
                // 去除低于发行前的数据
                for (Kline kline : klineList) {
                    log.info("libra 发行时间：{}", timeOfIssue);
                    if(kline.getTimestamp() < timeOfIssue){
                        log.info("去除低于发行前的数据：{}", kline.getTimestamp());
                        klineList.remove(kline);
                    }
                }
            }*/
            redisCache.setCacheObject(klineKey, klineList);
        }
    }

    /**
     * 落库前根据滑点设置数据
     * @param klineList
     */
    private void dealSlipDotBeforeInsert(List<Kline> klineList, CandlestickEnum period){
        for(Kline kline : klineList){
            Date startTime = new Date(kline.getTimestamp() * 1000);
            Date endTime = getEndTimeByPeriod(startTime, period);
            List<SlipDot> slipDotList = listSlipDotByTime(startTime, endTime, kline.getSymbol(), period);
            if(CollectionUtils.isEmpty(slipDotList)){
                continue;
            }
            BigDecimal low = kline.getLow();
            BigDecimal high = kline.getHigh();
            for(SlipDot dot : slipDotList){
                //如果滑点开始时间早于k线开始时间，则开要加上滑点
                if(dot.getStartDotTime().before(startTime)){
                    kline.setRealOpen(kline.getOpen());
                    kline.setOpen(kline.getOpen().add(dot.getAdjustPrice()));
                }
                //如果滑点结束时间晚于于k线结束时间，则收要加上滑点
                if(dot.getStopDotTime() == null || dot.getStopDotTime().after(endTime)){
                    kline.setRealClose(kline.getClose());
                    kline.setClose(kline.getClose().add(dot.getAdjustPrice()));
                }
                List<Bline> blineList = blineMapper.listByTime(startTime.getTime(), endTime.getTime(), kline.getSymbol());
                Boolean upSlip = dot.getAdjustPrice().compareTo(BigDecimal.ZERO) >= 0;
                for(Bline bline : blineList){
                    if(upSlip){
                        if(bline.getPrice().compareTo(high) > 0){
                            high = bline.getPrice();
                        }
                        if(bline.getRealPrice().compareTo(low) < 1){
                            low = bline.getPrice();
                        }
                    }else {
                        if(bline.getRealPrice().compareTo(high) >= 0){
                            high = bline.getPrice();
                        }
                        if(bline.getPrice().compareTo(low) < 1){
                            low = bline.getPrice();
                        }
                    }
                }
            }
            kline.setRealHigh(kline.getHigh());
            kline.setHigh(high);
            kline.setRealLow(kline.getLow());
            kline.setLow(low);
        }
    }

    /**
     * 获取指定时间内的滑点
     * @param symbol 交易对
     * @return
     */
    private List<SlipDot> listSlipDotByTime(Date startTime, Date endTime, String symbol, CandlestickEnum period){
        Map<String, Object> params = new HashMap();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("symbol", symbol);
        return slipDotService.listSlipDotByDate(params);
    }

    /**
     * 根据时间粒度，获取结束时间
     * @param startTime
     * @param period
     * @return
     */
    private Date getEndTimeByPeriod(Date startTime, CandlestickEnum period){
        Date endTime ;
        switch (period){
            case MIN1:
                endTime = DateUtils.addMinutes(startTime, 1);
                break;
            case MIN5:
                endTime = DateUtils.addMinutes(startTime, 5);
                break;
            case MIN15:
                endTime = DateUtils.addMinutes(startTime, 15);
                break;
            case MIN30:
                endTime = DateUtils.addMinutes(startTime, 30);
                break;
            case MIN60:
                endTime = DateUtils.addMinutes(startTime, 60);
                break;
            case HOUR4:
                endTime = DateUtils.addHours(startTime, 4);
                break;
            case DAY1:
                endTime = DateUtils.addDays(startTime, 1);
                break;
            case WEEK1:
                endTime = DateUtils.addDays(startTime, 7);
                break;
            case MON1:
                endTime = DateUtils.addMonths(startTime, 1);
                break;
            default:
                endTime = startTime;
        }
        return endTime;
    }
}
