package com.bigo.project.bigo.product.service.impl;

import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.product.domain.ProductInfo;
import com.bigo.project.bigo.product.mapper.ProductInfoMapper;
import com.bigo.project.bigo.product.service.IProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 理财产品信息Service业务层处理
 * 
 * @author bigo
 * @date 2021-01-27
 */
@Service
public class ProductInfoServiceImpl implements IProductInfoService 
{
    @Autowired
    private ProductInfoMapper productInfoMapper;

    /**
     * 查询理财产品信息
     * 
     * @param id 理财产品信息ID
     * @return 理财产品信息
     */
    @Override
    public ProductInfo selectProductInfoById(Long id)
    {
        return productInfoMapper.selectProductInfoById(id);
    }

    /**
     * 查询理财产品信息列表
     * 
     * @param productInfo 理财产品信息
     * @return 理财产品信息
     */
    @Override
    public List<ProductInfo> selectProductInfoList(ProductInfo productInfo)
    {
        return productInfoMapper.selectProductInfoList(productInfo);
    }

    /**
     * 新增理财产品信息
     * 
     * @param productInfo 理财产品信息
     * @return 结果
     */
    @Override
    public int insertProductInfo(ProductInfo productInfo)
    {
//        productInfo.setCurrency(CurrencyEnum.USDT.getCode());
        productInfo.setRemainingNumber(productInfo.getTotalNumber());
        productInfo.setCreateTime(DateUtils.getNowDate());
        return productInfoMapper.insertProductInfo(productInfo);
    }

    /**
     * 修改理财产品信息
     * 
     * @param productInfo 理财产品信息
     * @return 结果
     */
    @Override
    public int updateProductInfo(ProductInfo productInfo)
    {
        productInfo.setUpdateTime(DateUtils.getNowDate());
        return productInfoMapper.updateProductInfo(productInfo);
    }

    /**
     * 批量删除理财产品信息
     * 
     * @param ids 需要删除的理财产品信息ID
     * @return 结果
     */
    @Override
    public int deleteProductInfoByIds(Long[] ids)
    {
        return productInfoMapper.deleteProductInfoByIds(ids);
    }

    /**
     * 删除理财产品信息信息
     * 
     * @param id 理财产品信息ID
     * @return 结果
     */
    @Override
    public int deleteProductInfoById(Long id)
    {
        return productInfoMapper.deleteProductInfoById(id);
    }

    public ProductInfo inStockForUpdate(Long id) {
        return productInfoMapper.inStockForUpdate(id);
    }

    @Transactional
    public int updateInStock(ProductInfo inStock) {
        return productInfoMapper.updateInStock(inStock);
    }
}
