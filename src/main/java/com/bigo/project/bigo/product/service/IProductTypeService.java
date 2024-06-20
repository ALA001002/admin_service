package com.bigo.project.bigo.product.service;

import com.bigo.project.bigo.api.vo.product.ProductTypeVO;
import com.bigo.project.bigo.product.domain.ProductType;

import java.util.List;

/**
 * 理财产品类型Service接口
 * 
 * @author bigo
 * @date 2022-03-22
 */
public interface IProductTypeService 
{
    /**
     * 查询理财产品类型
     * 
     * @param id 理财产品类型ID
     * @return 理财产品类型
     */
    public ProductType selectProductTypeById(Long id);

    /**
     * 查询理财产品类型列表
     * 
     * @param productType 理财产品类型
     * @return 理财产品类型集合
     */
    public List<ProductType> selectProductTypeList(ProductType productType);

    /**
     * 新增理财产品类型
     * 
     * @param productType 理财产品类型
     * @return 结果
     */
    public int insertProductType(ProductType productType);

    /**
     * 修改理财产品类型
     * 
     * @param productType 理财产品类型
     * @return 结果
     */
    public int updateProductType(ProductType productType);

    /**
     * 批量删除理财产品类型
     * 
     * @param ids 需要删除的理财产品类型ID
     * @return 结果
     */
    public int deleteProductTypeByIds(Long[] ids);

    /**
     * 删除理财产品类型信息
     * 
     * @param id 理财产品类型ID
     * @return 结果
     */
    public int deleteProductTypeById(Long id);

    List<ProductTypeVO> listProductTypeOrder(Long uid);
}
