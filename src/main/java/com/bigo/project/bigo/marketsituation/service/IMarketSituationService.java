package com.bigo.project.bigo.marketsituation.service;

/**
 * @Description 行情service
 * @Author wenxm
 * @Date 2020/6/19 16:11
 */
public interface IMarketSituationService {

    /**
     * 从火币请求K线数据
     */
    void requestKlineFromHb() throws InterruptedException;

    /**
     * 从火币请求B线数据
     */
    void requestBlineFromHb();

}
