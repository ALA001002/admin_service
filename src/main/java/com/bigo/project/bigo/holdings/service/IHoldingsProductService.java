package com.bigo.project.bigo.holdings.service;

import java.util.List;
import com.bigo.project.bigo.holdings.domain.HoldingsProduct;

/**
 * 智能持仓产品Service接口
 * 
 * @author bigo
 * @date 2023-12-26
 */
public interface IHoldingsProductService 
{
    /**
     * 查询智能持仓产品
     * 
     * @param id 智能持仓产品ID
     * @return 智能持仓产品
     */
    public HoldingsProduct selectHoldingsProductById(Long id);

    /**
     * 查询智能持仓产品列表
     * 
     * @param holdingsProduct 智能持仓产品
     * @return 智能持仓产品集合
     */
    public List<HoldingsProduct> selectHoldingsProductList(HoldingsProduct holdingsProduct);

    /**
     * 新增智能持仓产品
     * 
     * @param holdingsProduct 智能持仓产品
     * @return 结果
     */
    public int insertHoldingsProduct(HoldingsProduct holdingsProduct);

    /**
     * 修改智能持仓产品
     * 
     * @param holdingsProduct 智能持仓产品
     * @return 结果
     */
    public int updateHoldingsProduct(HoldingsProduct holdingsProduct);

    /**
     * 批量删除智能持仓产品
     * 
     * @param ids 需要删除的智能持仓产品ID
     * @return 结果
     */
    public int deleteHoldingsProductByIds(Long[] ids);

    /**
     * 删除智能持仓产品信息
     * 
     * @param id 智能持仓产品ID
     * @return 结果
     */
    public int deleteHoldingsProductById(Long id);
}
