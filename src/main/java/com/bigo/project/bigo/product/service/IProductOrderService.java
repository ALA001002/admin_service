package com.bigo.project.bigo.product.service;

import com.bigo.project.bigo.api.domain.ProductParam;
import com.bigo.project.bigo.product.domain.ProductInfo;
import com.bigo.project.bigo.product.domain.ProductOrder;

import java.util.List;

/**
 * 理财产品订单Service接口
 * 
 * @author bigo
 * @date 2021-01-27
 */
public interface IProductOrderService 
{
    /**
     * 查询理财产品订单
     * 
     * @param id 理财产品订单ID
     * @return 理财产品订单
     */
    public ProductOrder selectProductOrderById(Long id);

    /**
     * 查询理财产品订单列表
     * 
     * @param productOrder 理财产品订单
     * @return 理财产品订单集合
     */
    public List<ProductOrder> selectProductOrderList(ProductOrder productOrder);

    /**
     * 新增理财产品订单
     * 
     * @param productOrder 理财产品订单
     * @return 结果
     */
    public int insertProductOrder(ProductOrder productOrder);

    /**
     * 修改理财产品订单
     * 
     * @param productOrder 理财产品订单
     * @return 结果
     */
    public int updateProductOrder(ProductOrder productOrder);

    /**
     * 批量删除理财产品订单
     * 
     * @param ids 需要删除的理财产品订单ID
     * @return 结果
     */
    public int deleteProductOrderByIds(Long[] ids);

    /**
     * 删除理财产品订单信息
     * 
     * @param id 理财产品订单ID
     * @return 结果
     */
    public int deleteProductOrderById(Long id);

    void buyProducts(ProductParam productParam, ProductInfo productInfo);

    List<ProductOrder> findFrozenOrder(ProductOrder order);

    void release(ProductOrder productOrder);

    void resetReleaseStatus();

    void stopRelease(ProductOrder order);

    void nweRelease(ProductOrder productOrder);

    List<ProductOrder> frozenProductOrder(ProductOrder order);

    void magoRelease(ProductOrder productOrder);

    void releaseOrder();
}
