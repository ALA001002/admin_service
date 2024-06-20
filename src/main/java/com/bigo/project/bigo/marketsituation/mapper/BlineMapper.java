package com.bigo.project.bigo.marketsituation.mapper;

import com.bigo.project.bigo.marketsituation.domain.Bline;
import com.bigo.project.bigo.marketsituation.domain.Kline;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description B线mapper
 * @Author wenxm
 * @Date 2020/6/19 15:31
 */
public interface BlineMapper {

    /**
     * 新增B线数据
     * @param bline
     * @return
     */
    Long insertBline(Bline bline);

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<Bline> list);

    /**
     * 获取最新的交易id
     * @return
     */
    Long getMaxTradeIdBySymbol(String symbol);

    /**
     * 获取每天第一笔交易
     * @param param zeroTime:每天0点的时间 symbol:交易对
     * @return
     */
    Bline getPerDayFirstTrade(Map<String,Object> param);

    /**
     * 根据开始结束时间和交易对获取B线信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param symbol 交易对
     * @return
     */
    List<Bline> listByTime(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("symbol") String symbol);
    
}
