package com.bigo.project.bigo.pay.mapper;

import com.bigo.project.bigo.pay.domain.PayInterfaceType;

import java.util.List;

/**
 * 支付接口类型Mapper接口
 * 
 * @author bigo
 * @date 2021-05-20
 */
public interface PayInterfaceTypeMapper 
{
    /**
     * 查询支付接口类型
     * 
     * @param ifTypeCode 支付接口类型ID
     * @return 支付接口类型
     */
    public PayInterfaceType selectPayInterfaceTypeById(String ifTypeCode);

    /**
     * 查询支付接口类型列表
     * 
     * @param payInterfaceType 支付接口类型
     * @return 支付接口类型集合
     */
    public List<PayInterfaceType> selectPayInterfaceTypeList(PayInterfaceType payInterfaceType);

    /**
     * 新增支付接口类型
     * 
     * @param payInterfaceType 支付接口类型
     * @return 结果
     */
    public int insertPayInterfaceType(PayInterfaceType payInterfaceType);

    /**
     * 修改支付接口类型
     * 
     * @param payInterfaceType 支付接口类型
     * @return 结果
     */
    public int updatePayInterfaceType(PayInterfaceType payInterfaceType);

    /**
     * 删除支付接口类型
     * 
     * @param ifTypeCode 支付接口类型ID
     * @return 结果
     */
    public int deletePayInterfaceTypeById(String ifTypeCode);

    /**
     * 批量删除支付接口类型
     * 
     * @param ifTypeCodes 需要删除的数据ID
     * @return 结果
     */
    public int deletePayInterfaceTypeByIds(String[] ifTypeCodes);
}
