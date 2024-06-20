package com.bigo.project.bigo.marketsituation.mapper;

import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.domain.KlineQuery;

import java.util.List;


/**
 * @Description K线mapper
 * @Author wenxm
 * @Date 2020/6/19 15:31
 */
public interface KlineMapper {

    /**
     * 新增k线数据
     * @param kline
     * @return
     */
    Long insertKline(Kline kline);

    /**
     * 根据交易对和粒度获取最大的kid
     * @param kline
     * @return
     */
    Long getMaxTimestampBySymbolAndPeriod(Kline kline);

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<Kline> list);

    /**
     * 查询k线数据
     * @param queryParam
     * @return
     */
    List<Kline> listKlineByTimestamp(KlineQuery queryParam);

    /**
     * 查询k线数据
     * @param queryParam
     * @return
     */
    List<Kline> listKlineForCache(KlineQuery queryParam);

    List<Kline> findKlineList(Kline kline);

    Kline getKline(Kline entity);
}
