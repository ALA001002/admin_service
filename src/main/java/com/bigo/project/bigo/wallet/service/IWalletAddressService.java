package com.bigo.project.bigo.wallet.service;

import com.bigo.project.bigo.wallet.domain.WalletAddress;

import java.util.List;

/**
 * 钱包地址Service接口
 * 
 * @author bigo
 * @date 2021-01-28
 */
public interface IWalletAddressService 
{
    /**
     * 查询钱包地址
     * 
     * @param id 钱包地址ID
     * @return 钱包地址
     */
    public WalletAddress selectWalletAddressById(Long id);

    /**
     * 查询钱包地址列表
     * 
     * @param walletAddress 钱包地址
     * @return 钱包地址集合
     */
    public List<WalletAddress> selectWalletAddressList(WalletAddress walletAddress);
}
