package com.bigo.project.bigo.marketsituation.service.impl;

import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.enums.CandlestickEnum;
import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.domain.KlineQuery;
import com.bigo.project.bigo.marketsituation.entity.KlineConfig;
import com.bigo.project.bigo.marketsituation.mapper.KlineMapper;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/20 17:32
 */
@Service
public class KlineService implements IKlineService {

    @Resource
    private KlineMapper klineMapper;


    @Override
    public List<Kline> listKlineByTimestamp(KlineQuery queryParam) {
        return klineMapper.listKlineByTimestamp(queryParam);
    }

    @Override
    public List<Kline> findKlineList(Kline kline) {
        return klineMapper.findKlineList(kline);
    }

    @Override
    public Kline getKline(Kline entity) {
        return klineMapper.getKline(entity);
    }




}
