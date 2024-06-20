package com.bigo.project.bigo.pay.service;

import java.util.List;

import com.bigo.project.bigo.api.dto.TransDTO;
import com.bigo.project.bigo.pay.domain.*;

/**
 * 代付Service接口
 * 
 * @author bigo
 * @date 2022-05-22
 */
public interface ITransOrderService 
{
    /**
     * 查询代付
     * 
     * @param id 代付ID
     * @return 代付
     */
    public TransOrder selectTransOrderById(Long id);

    /**
     * 查询代付列表
     * 
     * @param transOrder 代付
     * @return 代付集合
     */
    public List<TransOrder> selectTransOrderList(TransOrder transOrder);

    /**
     * 新增代付
     * 
     * @param transOrder 代付
     * @return 结果
     */
    public int insertTransOrder(TransOrder transOrder);

    /**
     * 修改代付
     * 
     * @param transOrder 代付
     * @return 结果
     */
    public int updateTransOrder(TransOrder transOrder);

    /**
     * 批量删除代付
     * 
     * @param ids 需要删除的代付ID
     * @return 结果
     */
    public int deleteTransOrderByIds(Long[] ids);

    /**
     * 删除代付信息
     * 
     * @param id 代付ID
     * @return 结果
     */
    public int deleteTransOrderById(Long id);

    void transPay(TransDTO transDTO, BankInfo bankInfo);

    void agetnPay(TransOrder order, PayPassage payPassage, PayInterfaceType interfaceType);

    TransOrder selectOrderId(String transOrderId);

    int updateStatusSuccess(TransOrder order);

    int updateStatusFail(TransOrder transOrder);

    void rejected(TransOrder order);
}
