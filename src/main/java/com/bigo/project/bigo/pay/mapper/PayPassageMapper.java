package com.bigo.project.bigo.pay.mapper;

import com.bigo.project.bigo.pay.domain.PayPassage;

import java.util.List;

/**
 * 支付通道Mapper接口
 * 
 * @author bigo
 * @date 2021-05-20
 */
public interface PayPassageMapper
{
    /**
     * 查询支付通道
     * 
     * @param id 支付通道ID
     * @return 支付通道
     */
    public PayPassage selectPayPassageById(Long id);

    /**
     * 查询支付通道列表
     * 
     * @param payPassage 支付通道
     * @return 支付通道集合
     */
    public List<PayPassage> selectPayPassageList(PayPassage payPassage);

    /**
     * 新增支付通道
     * 
     * @param payPassage 支付通道
     * @return 结果
     */
    public int insertPayPassage(PayPassage payPassage);

    /**
     * 修改支付通道
     * 
     * @param payPassage 支付通道
     * @return 结果
     */
    public int updatePayPassage(PayPassage payPassage);

    /**
     * 删除支付通道
     * 
     * @param id 支付通道ID
     * @return 结果
     */
    public int deletePayPassageById(Long id);

    /**
     * 批量删除支付通道
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePayPassageByIds(Long[] ids);
}
