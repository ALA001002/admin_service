package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.wallet.domain.WalletAddress;

import java.util.List;

/**
 * @Description 用户钱包地址mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface WalletAddressMapper {

    /**
     * 批量插入
     * @param list
     * @return
     */
    int batchInsert(List<WalletAddress> list);


    /**
     * 获取钱包地址
     * @param walletAddress
     * @return
     */
    String getAddressByCoin(WalletAddress walletAddress);

    /**
     * 判断钱包是否存在
     * @param walletAddress
     * @return
     */
    Integer isAddressExist(WalletAddress walletAddress);

    /**
     * 根据地址和币种获取钱包地址信息
     * @param walletAddress
     * @return
     */
    WalletAddress getByAddressAndCoin(WalletAddress walletAddress);

    List<WalletAddress> getAddressCoin(WalletAddress walletAddress);

    void updateAddress(WalletAddress address);
    List<WalletAddress> selectWalletAddressList(WalletAddress walletAddress);

    WalletAddress selectWalletAddressById(Long id);

    void saveAddress(WalletAddress temp);
}
