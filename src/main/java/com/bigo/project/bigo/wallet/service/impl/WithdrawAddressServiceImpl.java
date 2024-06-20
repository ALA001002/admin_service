package com.bigo.project.bigo.wallet.service.impl;

import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.api.dto.WithdrawAddressDTO;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.domain.WithdrawAddress;
import com.bigo.project.bigo.wallet.mapper.WithdrawAddressMapper;
import com.bigo.project.bigo.wallet.service.IWithdrawAddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 提现地址Service业务层处理
 * 
 * @author bigo
 * @date 2022-03-27
 */
@Service
public class WithdrawAddressServiceImpl implements IWithdrawAddressService 
{
    @Resource
    private WithdrawAddressMapper withdrawAddressMapper;

    /**
     * 查询提现地址
     * 
     * @param id 提现地址ID
     * @return 提现地址
     */
    @Override
    public WithdrawAddress selectWithdrawAddressById(Long id)
    {
        return withdrawAddressMapper.selectWithdrawAddressById(id);
    }

    /**
     * 查询提现地址列表
     * 
     * @param withdrawAddress 提现地址
     * @return 提现地址
     */
    @Override
    public List<WithdrawAddress> selectWithdrawAddressList(WithdrawAddress withdrawAddress)
    {
        return withdrawAddressMapper.selectWithdrawAddressList(withdrawAddress);
    }

    /**
     * 新增提现地址
     * 
     * @param withdrawAddress 提现地址
     * @return 结果
     */
    @Override
    public int insertWithdrawAddress(WithdrawAddress withdrawAddress)
    {
        withdrawAddress.setCreateTime(DateUtils.getNowDate());
        return withdrawAddressMapper.insertWithdrawAddress(withdrawAddress);
    }

    /**
     * 修改提现地址
     * 
     * @param withdrawAddress 提现地址
     * @return 结果
     */
    @Override
    public int updateWithdrawAddress(WithdrawAddress withdrawAddress)
    {
        return withdrawAddressMapper.updateWithdrawAddress(withdrawAddress);
    }

    /**
     * 批量删除提现地址
     * 
     * @param ids 需要删除的提现地址ID
     * @return 结果
     */
    @Override
    public int deleteWithdrawAddressByIds(Long[] ids)
    {
        return withdrawAddressMapper.deleteWithdrawAddressByIds(ids);
    }

    /**
     * 删除提现地址信息
     * 
     * @param id 提现地址ID
     * @return 结果
     */
    @Override
    public int deleteWithdrawAddressById(Long id)
    {
        return withdrawAddressMapper.deleteWithdrawAddressById(id);
    }

    @Override
    public void saveAddress(BigoUser user, Wallet wallet, WithdrawAddressDTO dto) {
        WithdrawAddress address = new WithdrawAddress();
        address.setUid(user.getUid());
        address.setWalletId(wallet.getId());
        address.setCoin(dto.getCoin());
        address.setAddress(dto.getAddress());
        address.setRemark(dto.getRemark());
        withdrawAddressMapper.insertWithdrawAddress(address);
    }
}
