package com.bigo.project.bigo.ico.service;

import java.util.List;

import com.bigo.project.bigo.api.dto.IcoProductDTO;
import com.bigo.project.bigo.api.vo.ico.IcoProductVO;
import com.bigo.project.bigo.ico.domain.IcoProduct;
import com.bigo.project.bigo.userinfo.domain.BigoUser;

/**
 * ico产品Service接口
 * 
 * @author xx
 * @date 2023-01-07
 */
public interface IIcoProductService 
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
     * 批量删除ico产品
     * 
     * @param ids 需要删除的ico产品ID
     * @return 结果
     */
    public int deleteIcoProductByIds(Long[] ids);

    /**
     * 删除ico产品信息
     * 
     * @param id ico产品ID
     * @return 结果
     */
    public int deleteIcoProductById(Long id);

    List<IcoProductVO> selectProductListVO(IcoProductVO icoProductVO);

//    void buyIcoProduct(BigoUser user, IcoProductDTO dto);

    void buy(BigoUser user, IcoProductDTO dto);

    void sell(BigoUser user, IcoProductDTO dto);
}
