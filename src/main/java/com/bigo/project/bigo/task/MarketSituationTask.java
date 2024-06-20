package com.bigo.project.bigo.task;

import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.enums.SymbolCoinEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import com.bigo.project.bigo.marketsituation.service.impl.MarketSituationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wenxm
 * @Description: 火币网定时任务获取时时行情
 * @date 2020/6/17 下午9:50
 */
@Component("marketSituationTask")
@Slf4j
public class MarketSituationTask {

    @Autowired
    private MarketSituationService marketSituationService;

    @Autowired
    private IKlineService klineService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 请求K线数据
     */
//    @Scheduled(cron = "*/20 * * * * ?")
    public void requestKlineFromHb() {
        try {
            marketSituationService.requestKlineFromHb();
        } catch (Exception e) {
            log.error("请求K线数据失败, error.", e);
        }
    }

    /**
     * 请求B线数据
     */
    //@Scheduled(cron = "*/1 * * * * ?")
    public void requestBlineFromHb() {
        try {
            marketSituationService.requestBlineFromHb();
        } catch (Exception e) {
            log.error("请求B线数据失败, error.", e);
        }
    }

    /**
     * 刷新k线数据
     */
//    @Scheduled(fixedDelay = 100*1000)
    public void refreshKline() {
        try {
            String period = "5min";
            for (SymbolCoinEnum value : SymbolCoinEnum.values()) {
                log.info("refreshKline symbol={}",value.getCode());
                String symbol = value.getCode();
                Kline kline = new Kline();
                kline.setSymbol(symbol);
                kline.setPeriod(period);
                kline.setSize(50L);
                List<Kline> klineList = klineService.findKlineList(kline);
                if (klineList != null && klineList.size() > 0) {
                    String key = "home"+symbol+period;
                    redisCache.deleteObject(key);
                    redisCache.setCacheObject(key, klineList);
                }
            }
        } catch (Exception e) {
            log.error("请求B线数据失败, error.", e);
        }
    }

}
