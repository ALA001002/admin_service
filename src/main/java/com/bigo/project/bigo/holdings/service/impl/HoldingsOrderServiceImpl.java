package com.bigo.project.bigo.holdings.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.holdings.mapper.HoldingsOrderMapper;
import com.bigo.project.bigo.holdings.domain.HoldingsOrder;
import com.bigo.project.bigo.holdings.service.IHoldingsOrderService;

/**
 * 智能持仓订单Service业务层处理
 * 
 * @author bigo
 * @date 2023-12-28
 */
@Service
public class HoldingsOrderServiceImpl implements IHoldingsOrderService 
{
    @Autowired
    private HoldingsOrderMapper holdingsOrderMapper;

    /**
     * 查询智能持仓订单
     * 
     * @param id 智能持仓订单ID
     * @return 智能持仓订单
     */
    @Override
    public HoldingsOrder selectHoldingsOrderById(Long id)
    {
        return holdingsOrderMapper.selectHoldingsOrderById(id);
    }

    /**
     * 查询智能持仓订单列表
     * 
     * @param holdingsOrder 智能持仓订单
     * @return 智能持仓订单
     */
    @Override
    public List<HoldingsOrder> selectHoldingsOrderList(HoldingsOrder holdingsOrder)
    {
        return holdingsOrderMapper.selectHoldingsOrderList(holdingsOrder);
    }

    /**
     * 新增智能持仓订单
     * 
     * @param holdingsOrder 智能持仓订单
     * @return 结果
     */
    @Override
    public int insertHoldingsOrder(HoldingsOrder holdingsOrder)
    {
        holdingsOrder.setCreateTime(DateUtils.getNowDate());
        return holdingsOrderMapper.insertHoldingsOrder(holdingsOrder);
    }

    /**
     * 修改智能持仓订单
     * 
     * @param holdingsOrder 智能持仓订单
     * @return 结果
     */
    @Override
    public int updateHoldingsOrder(HoldingsOrder holdingsOrder)
    {
        holdingsOrder.setUpdateTime(DateUtils.getNowDate());
        return holdingsOrderMapper.updateHoldingsOrder(holdingsOrder);
    }

    /**
     * 批量删除智能持仓订单
     * 
     * @param ids 需要删除的智能持仓订单ID
     * @return 结果
     */
    @Override
    public int deleteHoldingsOrderByIds(Long[] ids)
    {
        return holdingsOrderMapper.deleteHoldingsOrderByIds(ids);
    }

    /**
     * 删除智能持仓订单信息
     * 
     * @param id 智能持仓订单ID
     * @return 结果
     */
    @Override
    public int deleteHoldingsOrderById(Long id)
    {
        return holdingsOrderMapper.deleteHoldingsOrderById(id);
    }
}
