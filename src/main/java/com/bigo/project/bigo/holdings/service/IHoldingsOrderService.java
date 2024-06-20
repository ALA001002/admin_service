package com.bigo.project.bigo.holdings.service;

import java.util.List;
import com.bigo.project.bigo.holdings.domain.HoldingsOrder;

/**
 * 智能持仓订单Service接口
 * 
 * @author bigo
 * @date 2023-12-28
 */
public interface IHoldingsOrderService 
{
    /**
     * 查询智能持仓订单
     * 
     * @param id 智能持仓订单ID
     * @return 智能持仓订单
     */
    public HoldingsOrder selectHoldingsOrderById(Long id);

    /**
     * 查询智能持仓订单列表
     * 
     * @param holdingsOrder 智能持仓订单
     * @return 智能持仓订单集合
     */
    public List<HoldingsOrder> selectHoldingsOrderList(HoldingsOrder holdingsOrder);

    /**
     * 新增智能持仓订单
     * 
     * @param holdingsOrder 智能持仓订单
     * @return 结果
     */
    public int insertHoldingsOrder(HoldingsOrder holdingsOrder);

    /**
     * 修改智能持仓订单
     * 
     * @param holdingsOrder 智能持仓订单
     * @return 结果
     */
    public int updateHoldingsOrder(HoldingsOrder holdingsOrder);

    /**
     * 批量删除智能持仓订单
     * 
     * @param ids 需要删除的智能持仓订单ID
     * @return 结果
     */
    public int deleteHoldingsOrderByIds(Long[] ids);

    /**
     * 删除智能持仓订单信息
     * 
     * @param id 智能持仓订单ID
     * @return 结果
     */
    public int deleteHoldingsOrderById(Long id);
}
