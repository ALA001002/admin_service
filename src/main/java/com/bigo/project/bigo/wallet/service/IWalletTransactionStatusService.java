package com.bigo.project.bigo.wallet.service;

import java.util.List;
import com.bigo.project.bigo.wallet.domain.WalletTransactionStatus;

/**
 * 钱包交易状态Service接口
 * 
 * @author bigo
 * @date 2021-01-25
 */
public interface IWalletTransactionStatusService 
{
    /**
     * 查询钱包交易状态
     * 
     * @param id 钱包交易状态ID
     * @return 钱包交易状态
     */
    public WalletTransactionStatus selectWalletTransactionStatusById(Long id);

    /**
     * 查询钱包交易状态列表
     * 
     * @param walletTransactionStatus 钱包交易状态
     * @return 钱包交易状态集合
     */
    public List<WalletTransactionStatus> selectWalletTransactionStatusList(WalletTransactionStatus walletTransactionStatus);

    /**
     * 新增钱包交易状态
     * 
     * @param walletTransactionStatus 钱包交易状态
     * @return 结果
     */
    public int insertWalletTransactionStatus(WalletTransactionStatus walletTransactionStatus);

    /**
     * 修改钱包交易状态
     * 
     * @param walletTransactionStatus 钱包交易状态
     * @return 结果
     */
    public int updateWalletTransactionStatus(WalletTransactionStatus walletTransactionStatus);

    /**
     * 批量删除钱包交易状态
     * 
     * @param ids 需要删除的钱包交易状态ID
     * @return 结果
     */
    public int deleteWalletTransactionStatusByIds(Long[] ids);

    /**
     * 删除钱包交易状态信息
     * 
     * @param id 钱包交易状态ID
     * @return 结果
     */
    public int deleteWalletTransactionStatusById(Long id);

    void synchronizeBlock(WalletTransactionStatus transactionStatus);
}
