package com.bigo.project.bigo.holdings.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.holdings.mapper.HoldingsProductMapper;
import com.bigo.project.bigo.holdings.domain.HoldingsProduct;
import com.bigo.project.bigo.holdings.service.IHoldingsProductService;

/**
 * 智能持仓产品Service业务层处理
 * 
 * @author bigo
 * @date 2023-12-26
 */
@Service
public class HoldingsProductServiceImpl implements IHoldingsProductService 
{
    @Autowired
    private HoldingsProductMapper holdingsProductMapper;

    /**
     * 查询智能持仓产品
     * 
     * @param id 智能持仓产品ID
     * @return 智能持仓产品
     */
    @Override
    public HoldingsProduct selectHoldingsProductById(Long id)
    {
        return holdingsProductMapper.selectHoldingsProductById(id);
    }

    /**
     * 查询智能持仓产品列表
     * 
     * @param holdingsProduct 智能持仓产品
     * @return 智能持仓产品
     */
    @Override
    public List<HoldingsProduct> selectHoldingsProductList(HoldingsProduct holdingsProduct)
    {
        return holdingsProductMapper.selectHoldingsProductList(holdingsProduct);
    }

    /**
     * 新增智能持仓产品
     * 
     * @param holdingsProduct 智能持仓产品
     * @return 结果
     */
    @Override
    public int insertHoldingsProduct(HoldingsProduct holdingsProduct)
    {
        holdingsProduct.setCreateTime(DateUtils.getNowDate());
        return holdingsProductMapper.insertHoldingsProduct(holdingsProduct);
    }

    /**
     * 修改智能持仓产品
     * 
     * @param holdingsProduct 智能持仓产品
     * @return 结果
     */
    @Override
    public int updateHoldingsProduct(HoldingsProduct holdingsProduct)
    {
        holdingsProduct.setUpdateTime(DateUtils.getNowDate());
        return holdingsProductMapper.updateHoldingsProduct(holdingsProduct);
    }

    /**
     * 批量删除智能持仓产品
     * 
     * @param ids 需要删除的智能持仓产品ID
     * @return 结果
     */
    @Override
    public int deleteHoldingsProductByIds(Long[] ids)
    {
        return holdingsProductMapper.deleteHoldingsProductByIds(ids);
    }

    /**
     * 删除智能持仓产品信息
     * 
     * @param id 智能持仓产品ID
     * @return 结果
     */
    @Override
    public int deleteHoldingsProductById(Long id)
    {
        return holdingsProductMapper.deleteHoldingsProductById(id);
    }
}
