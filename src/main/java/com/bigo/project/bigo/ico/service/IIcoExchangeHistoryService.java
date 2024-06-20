package com.bigo.project.bigo.ico.service;

import java.util.List;
import com.bigo.project.bigo.ico.domain.IcoExchangeHistory;

/**
 * ico交易记录Service接口
 * 
 * @author xx
 * @date 2023-01-15
 */
public interface IIcoExchangeHistoryService 
{
    /**
     * 查询ico交易记录
     * 
     * @param id ico交易记录ID
     * @return ico交易记录
     */
    public IcoExchangeHistory selectIcoExchangeHistoryById(Long id);

    /**
     * 查询ico交易记录列表
     * 
     * @param icoExchangeHistory ico交易记录
     * @return ico交易记录集合
     */
    public List<IcoExchangeHistory> selectIcoExchangeHistoryList(IcoExchangeHistory icoExchangeHistory);

    /**
     * 新增ico交易记录
     * 
     * @param icoExchangeHistory ico交易记录
     * @return 结果
     */
    public int insertIcoExchangeHistory(IcoExchangeHistory icoExchangeHistory);

    /**
     * 修改ico交易记录
     * 
     * @param icoExchangeHistory ico交易记录
     * @return 结果
     */
    public int updateIcoExchangeHistory(IcoExchangeHistory icoExchangeHistory);

    /**
     * 批量删除ico交易记录
     * 
     * @param ids 需要删除的ico交易记录ID
     * @return 结果
     */
    public int deleteIcoExchangeHistoryByIds(Long[] ids);

    /**
     * 删除ico交易记录信息
     * 
     * @param id ico交易记录ID
     * @return 结果
     */
    public int deleteIcoExchangeHistoryById(Long id);

    List<Long> statisticTradeNumber(List<Long> uidList, int i);
}
