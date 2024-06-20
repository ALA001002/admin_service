package com.bigo.project.bigo.wallet.service;

import com.bigo.project.bigo.api.dto.WithdrawAddressDTO;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.domain.WithdrawAddress;

import java.util.List;

/**
 * 提现地址Service接口
 * 
 * @author bigo
 * @date 2022-03-27
 */
public interface IWithdrawAddressService 
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
     * 批量删除提现地址
     * 
     * @param ids 需要删除的提现地址ID
     * @return 结果
     */
    public int deleteWithdrawAddressByIds(Long[] ids);

    /**
     * 删除提现地址信息
     * 
     * @param id 提现地址ID
     * @return 结果
     */
    public int deleteWithdrawAddressById(Long id);

    void saveAddress(BigoUser user, Wallet wallet, WithdrawAddressDTO dto);
}
