package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.wallet.domain.WithdrawAddress;

import java.util.List;

/**
 * 提现地址Mapper接口
 * 
 * @author bigo
 * @date 2022-03-27
 */
public interface WithdrawAddressMapper 
{
    /**
     * 查询提现地址
     * 
     * @param id 提现地址ID
     * @return 提现地址
     */
    public WithdrawAddress selectWithdrawAddressById(Long id);

    /**
     * 查询提现地址列表
     * 
     * @param withdrawAddress 提现地址
     * @return 提现地址集合
     */
    public List<WithdrawAddress> selectWithdrawAddressList(WithdrawAddress withdrawAddress);

    /**
     * 新增提现地址
     * 
     * @param withdrawAddress 提现地址
     * @return 结果
     */
    public int insertWithdrawAddress(WithdrawAddress withdrawAddress);

    /**
     * 修改提现地址
     * 
     * @param withdrawAddress 提现地址
     * @return 结果
     */
    public int updateWithdrawAddress(WithdrawAddress withdrawAddress);

    /**
     * 删除提现地址
     * 
     * @param id 提现地址ID
     * @return 结果
     */
    public int deleteWithdrawAddressById(Long id);

    /**
     * 批量删除提现地址
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteWithdrawAddressByIds(Long[] ids);
}
