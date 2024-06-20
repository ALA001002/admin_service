package com.bigo.project.bigo.ico.mapper;

import java.util.List;
import java.util.Map;

import com.bigo.project.bigo.ico.domain.IcoExchangeHistory;

/**
 * ico交易记录Mapper接口
 * 
 * @author xx
 * @date 2023-01-15
 */
public interface IcoExchangeHistoryMapper 
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
     * 删除ico交易记录
     * 
     * @param id ico交易记录ID
     * @return 结果
     */
    public int deleteIcoExchangeHistoryById(Long id);

    /**
     * 批量删除ico交易记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteIcoExchangeHistoryByIds(Long[] ids);

    List<Long> statisticTradeNumber(Map map);
}
