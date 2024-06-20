package com.bigo.project.bigo.product.service.impl;

import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.api.vo.product.ProductTypeVO;
import com.bigo.project.bigo.product.domain.ProductType;
import com.bigo.project.bigo.product.mapper.ProductTypeMapper;
import com.bigo.project.bigo.product.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 理财产品类型Service业务层处理
 * 
 * @author bigo
 * @date 2022-03-22
 */
@Service
public class ProductTypeServiceImpl implements IProductTypeService 
{
    @Autowired
    private ProductTypeMapper productTypeMapper;

    /**
     * 查询理财产品类型
     * 
     * @param id 理财产品类型ID
     * @return 理财产品类型
     */
    @Override
    public ProductType selectProductTypeById(Long id)
    {
        return productTypeMapper.selectProductTypeById(id);
    }

    /**
     * 查询理财产品类型列表
     * 
     * @param productType 理财产品类型
     * @return 理财产品类型
     */
    @Override
    public List<ProductType> selectProductTypeList(ProductType productType)
    {
        return productTypeMapper.selectProductTypeList(productType);
    }

    /**
     * 新增理财产品类型
     * 
     * @param productType 理财产品类型
     * @return 结果
     */
    @Override
    public int insertProductType(ProductType productType)
    {
        productType.setCreateTime(DateUtils.getNowDate());
        return productTypeMapper.insertProductType(productType);
    }

    /**
     * 修改理财产品类型
     * 
     * @param productType 理财产品类型
     * @return 结果
     */
    @Override
    public int updateProductType(ProductType productType)
    {
        return productTypeMapper.updateProductType(productType);
    }

    /**
     * 批量删除理财产品类型
     * 
     * @param ids 需要删除的理财产品类型ID
     * @return 结果
     */
    @Override
    public int deleteProductTypeByIds(Long[] ids)
    {
        return productTypeMapper.deleteProductTypeByIds(ids);
    }

    /**
     * 删除理财产品类型信息
     * 
     * @param id 理财产品类型ID
     * @return 结果
     */
    @Override
    public int deleteProductTypeById(Long id)
    {
        return productTypeMapper.deleteProductTypeById(id);
    }

    @Override
    public List<ProductTypeVO> listProductTypeOrder(Long uid) {
        return productTypeMapper.listProductTypeOrder(uid);
    }
}
