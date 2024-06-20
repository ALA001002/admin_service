package com.bigo.project.bigo.ico.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.ico.mapper.IcoExchangeHistoryMapper;
import com.bigo.project.bigo.ico.domain.IcoExchangeHistory;
import com.bigo.project.bigo.ico.service.IIcoExchangeHistoryService;

/**
 * ico交易记录Service业务层处理
 * 
 * @author xx
 * @date 2023-01-15
 */
@Service
public class IcoExchangeHistoryServiceImpl implements IIcoExchangeHistoryService 
{
    @Autowired
    private IcoExchangeHistoryMapper icoExchangeHistoryMapper;

    /**
     * 查询ico交易记录
     * 
     * @param id ico交易记录ID
     * @return ico交易记录
     */
    @Override
    public IcoExchangeHistory selectIcoExchangeHistoryById(Long id)
    {
        return icoExchangeHistoryMapper.selectIcoExchangeHistoryById(id);
    }

    /**
     * 查询ico交易记录列表
     * 
     * @param icoExchangeHistory ico交易记录
     * @return ico交易记录
     */
    @Override
    public List<IcoExchangeHistory> selectIcoExchangeHistoryList(IcoExchangeHistory icoExchangeHistory)
    {
        return icoExchangeHistoryMapper.selectIcoExchangeHistoryList(icoExchangeHistory);
    }

    /**
     * 新增ico交易记录
     * 
     * @param icoExchangeHistory ico交易记录
     * @return 结果
     */
    @Override
    public int insertIcoExchangeHistory(IcoExchangeHistory icoExchangeHistory)
    {
        icoExchangeHistory.setCreateTime(DateUtils.getNowDate());
        return icoExchangeHistoryMapper.insertIcoExchangeHistory(icoExchangeHistory);
    }

    /**
     * 修改ico交易记录
     * 
     * @param icoExchangeHistory ico交易记录
     * @return 结果
     */
    @Override
    public int updateIcoExchangeHistory(IcoExchangeHistory icoExchangeHistory)
    {
        return icoExchangeHistoryMapper.updateIcoExchangeHistory(icoExchangeHistory);
    }

    /**
     * 批量删除ico交易记录
     * 
     * @param ids 需要删除的ico交易记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoExchangeHistoryByIds(Long[] ids)
    {
        return icoExchangeHistoryMapper.deleteIcoExchangeHistoryByIds(ids);
    }

    /**
     * 删除ico交易记录信息
     * 
     * @param id ico交易记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoExchangeHistoryById(Long id)
    {
        return icoExchangeHistoryMapper.deleteIcoExchangeHistoryById(id);
    }

    @Override
    public List<Long> statisticTradeNumber(List<Long> uidList, int num) {
        Map map = new HashMap();
        map.put("uidList", uidList);
        map.put("num", num);
        return icoExchangeHistoryMapper.statisticTradeNumber(map);
    }
}
