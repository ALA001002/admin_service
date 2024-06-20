package com.bigo.project.bigo.wallet.service.impl;

import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.mapper.WalletAddressMapper;
import com.bigo.project.bigo.wallet.service.IWalletAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 钱包地址Service业务层处理
 * 
 * @author bigo
 * @date 2021-01-28
 */
@Service
public class WalletAddressServiceImpl implements IWalletAddressService
{
    @Autowired
    private WalletAddressMapper walletAddressMapper;

    /**
     * 查询钱包地址
     * 
     * @param id 钱包地址ID
     * @return 钱包地址
     */
    @Override
    public WalletAddress selectWalletAddressById(Long id)
    {
        return walletAddressMapper.selectWalletAddressById(id);
    }

    /**
     * 查询钱包地址列表
     * 
     * @param walletAddress 钱包地址
     * @return 钱包地址
     */
    @Override
    public List<WalletAddress> selectWalletAddressList(WalletAddress walletAddress)
    {
        return walletAddressMapper.selectWalletAddressList(walletAddress);
    }
}
