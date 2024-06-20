package com.bigo.project.bigo.holdings.mapper;

import java.util.List;
import com.bigo.project.bigo.holdings.domain.HoldingsProduct;

/**
 * 智能持仓产品Mapper接口
 * 
 * @author bigo
 * @date 2023-12-26
 */
public interface HoldingsProductMapper 
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
     * 删除智能持仓产品
     * 
     * @param id 智能持仓产品ID
     * @return 结果
     */
    public int deleteHoldingsProductById(Long id);

    /**
     * 批量删除智能持仓产品
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteHoldingsProductByIds(Long[] ids);
}
