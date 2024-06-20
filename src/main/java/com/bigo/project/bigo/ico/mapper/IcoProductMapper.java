package com.bigo.project.bigo.ico.mapper;

import java.util.List;

import com.bigo.project.bigo.api.vo.ico.IcoProductVO;
import com.bigo.project.bigo.ico.domain.IcoProduct;

/**
 * ico产品Mapper接口
 * 
 * @author xx
 * @date 2023-01-07
 */
public interface IcoProductMapper 
{
    /**
     * 查询ico产品
     * 
     * @param id ico产品ID
     * @return ico产品
     */
    public IcoProduct selectIcoProductById(Long id);

    /**
     * 查询ico产品列表
     * 
     * @param icoProduct ico产品
     * @return ico产品集合
     */
    public List<IcoProduct> selectIcoProductList(IcoProduct icoProduct);

    /**
     * 新增ico产品
     * 
     * @param icoProduct ico产品
     * @return 结果
     */
    public int insertIcoProduct(IcoProduct icoProduct);

    /**
     * 修改ico产品
     * 
     * @param icoProduct ico产品
     * @return 结果
     */
    public int updateIcoProduct(IcoProduct icoProduct);

    /**
     * 删除ico产品
     * 
     * @param id ico产品ID
     * @return 结果
     */
    public int deleteIcoProductById(Long id);

    /**
     * 批量删除ico产品
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteIcoProductByIds(Long[] ids);

    List<IcoProductVO> selectProductListVO(IcoProductVO icoProductVO);

    int reduceInventory(IcoProduct product);

    IcoProduct selectIcoProduct(IcoProduct icoProduct);
}
