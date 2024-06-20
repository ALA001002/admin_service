package com.bigo.project.bigo.marketsituation.service;

import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.domain.KlineQuery;
import com.bigo.project.bigo.marketsituation.entity.KlineConfig;

import java.util.List;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/20 17:23
 */
public interface IKlineService {

    /**
     * 查询k线数据
     * @param queryParam
     * @return
     */
    List<Kline> listKlineByTimestamp(KlineQuery queryParam);

    List<Kline> findKlineList(Kline kline);

    Kline getKline(Kline entity);

}
