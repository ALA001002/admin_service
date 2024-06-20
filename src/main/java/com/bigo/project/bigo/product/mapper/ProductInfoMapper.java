package com.bigo.project.bigo.product.mapper;

import com.bigo.project.bigo.product.domain.ProductInfo;

import java.util.List;

/**
 * 理财产品信息Mapper接口
 * 
 * @author bigo
 * @date 2021-01-27
 */
public interface ProductInfoMapper 
{
    /**
     * 查询理财产品信息
     * 
     * @param id 理财产品信息ID
     * @return 理财产品信息
     */
    public ProductInfo selectProductInfoById(Long id);

    /**
     * 查询理财产品信息列表
     * 
     * @param productInfo 理财产品信息
     * @return 理财产品信息集合
     */
    public List<ProductInfo> selectProductInfoList(ProductInfo productInfo);

    /**
     * 新增理财产品信息
     * 
     * @param productInfo 理财产品信息
     * @return 结果
     */
    public int insertProductInfo(ProductInfo productInfo);

    /**
     * 修改理财产品信息
     * 
     * @param productInfo 理财产品信息
     * @return 结果
     */
    public int updateProductInfo(ProductInfo productInfo);

    /**
     * 删除理财产品信息
     * 
     * @param id 理财产品信息ID
     * @return 结果
     */
    public int deleteProductInfoById(Long id);

    /**
     * 批量删除理财产品信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteProductInfoByIds(Long[] ids);

    ProductInfo inStockForUpdate(Long id);

    int updateInStock(ProductInfo inStock);
}
