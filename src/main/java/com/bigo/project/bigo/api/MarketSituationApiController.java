package com.bigo.project.bigo.api;


import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.vo.ContractInfoVO;
import com.bigo.project.bigo.marketsituation.domain.Bline;
import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.domain.KlineQuery;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/17 16:29
 */
@Slf4j
@RestController
@RequestMapping("/api/marketsituation/")
public class MarketSituationApiController {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IKlineService klineService;

    /**
     * K线数据
     */
    @GetMapping("/kline")
    public AjaxResult kline(@RequestParam("symbol") String symbol,
                            @RequestParam("period") String period,
                            @RequestParam(value="timestamp", required = false) Long timestamp,
                            @RequestParam(value="size", required = false) Integer size){
        if(StringUtils.isEmpty(symbol)){
            return AjaxResult.error("symbol_cannot_be_null");
        }
        if(StringUtils.isEmpty(period)){
            return AjaxResult.error("period_cannot_be_null");
        }
        String key = symbol + period;
        List<Kline> klineList;
        //如果有传时间戳，则从数据库查询数据
        if(timestamp != null){
            KlineQuery param = new KlineQuery();
            param.setSymbol(symbol);
            param.setPeriod(period);
            param.setTimestamp(timestamp);
            param.setSize(size == null ? 100 : size);
            klineList = klineService.listKlineByTimestamp(param);
        }else {
            klineList = redisCache.getCacheObject(key);

            if(klineList == null) {
                Kline kline = new Kline();
                kline.setSymbol(symbol);
                kline.setPeriod(period);
                klineList = klineService.findKlineList(kline);
                if(klineList != null && klineList.size() > 0) {
                    redisCache.setCacheObject(symbol + period, klineList);
                }
            }

            if(size != null && klineList != null && !klineList.isEmpty()){
                if(klineList.size() < size) size = klineList.size();
                klineList = klineList.subList(0, size-1);
            }
        }
        for (Kline kline : klineList) {
            if(String.valueOf(kline.getTimestamp()).length() > 10) {
                kline.setTimestamp(kline.getTimestamp() / 1000);
            }
        }
//        if(klineList != null && !klineList.isEmpty() && symbol.contains(SymbolEnum.BIXUSDT.getCode().substring(0,3))) {   //如果是diem，根据发行时间来
//            // diem币 根据发行时间过滤
//            String timeOfIssue = CoinUtils.getTimeOfIssue();
//            for (int i=0; i<klineList.size(); i++) {
//                System.out.println(333);
//                if(klineList.get(i).getTimestamp() < Long.valueOf(timeOfIssue)) {
//                    System.out.println(222);
//                    klineList.remove(i);
//                }
//            }
//        }

        return AjaxResult.success(klineList);
    }




    /**
     * B线数据
     */
    @GetMapping("/bline")
    public AjaxResult bline(@RequestParam("symbol") String symbol){
        if(StringUtils.isEmpty(symbol)){
            return AjaxResult.error("symbol_cannot_be_null");
        }
        List<Bline> blineList = redisCache.getCacheObject(symbol);
        return AjaxResult.success(blineList);
    }

    /**
     * 合约列表
     */
    @GetMapping("/contractInfo")
    public AjaxResult contractInfo(){
        return AjaxResult.success(MarketSituationUtils.getContractInfo(0));
    }

    @GetMapping("/homeKline")
    public AjaxResult homeKline(@RequestParam("symbol") String symbol){
        if(StringUtils.isEmpty(symbol)){
            return AjaxResult.error("symbol_cannot_be_null");
        }
        String key = "home"+symbol+"5min";
        List<Kline> klineList = redisCache.getCacheObject(key);
        return AjaxResult.success(klineList);
    }

}
