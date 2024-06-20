package com.bigo.project.bigo.wallet.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.btc.TransactionItem;
import com.bigo.common.utils.btc.BTCWalletUtils;
import com.bigo.project.bigo.enums.WalletTransactionStatusEnum;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.wallet.mapper.WalletTransactionStatusMapper;
import com.bigo.project.bigo.wallet.domain.WalletTransactionStatus;
import com.bigo.project.bigo.wallet.service.IWalletTransactionStatusService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * 钱包交易状态Service业务层处理
 * 
 * @author bigo
 * @date 2021-01-25
 */
@Service
public class WalletTransactionStatusServiceImpl implements IWalletTransactionStatusService 
{
    @Autowired
    private WalletTransactionStatusMapper walletTransactionStatusMapper;

    @Autowired
    private IWalletTransactionService walletTransactionService;

    /**
     * 查询钱包交易状态
     * 
     * @param id 钱包交易状态ID
     * @return 钱包交易状态
     */
    @Override
    public WalletTransactionStatus selectWalletTransactionStatusById(Long id)
    {
        return walletTransactionStatusMapper.selectWalletTransactionStatusById(id);
    }

    /**
     * 查询钱包交易状态列表
     * 
     * @param walletTransactionStatus 钱包交易状态
     * @return 钱包交易状态
     */
    @Override
    public List<WalletTransactionStatus> selectWalletTransactionStatusList(WalletTransactionStatus walletTransactionStatus)
    {
        return walletTransactionStatusMapper.selectWalletTransactionStatusList(walletTransactionStatus);
    }

    /**
     * 新增钱包交易状态
     * 
     * @param walletTransactionStatus 钱包交易状态
     * @return 结果
     */
    @Override
    public int insertWalletTransactionStatus(WalletTransactionStatus walletTransactionStatus)
    {
        walletTransactionStatus.setCreateTime(DateUtils.getNowDate());
        return walletTransactionStatusMapper.insertWalletTransactionStatus(walletTransactionStatus);
    }

    /**
     * 修改钱包交易状态
     * 
     * @param walletTransactionStatus 钱包交易状态
     * @return 结果
     */
    @Override
    @Transactional
    public int updateWalletTransactionStatus(WalletTransactionStatus walletTransactionStatus)
    {
        return walletTransactionStatusMapper.updateWalletTransactionStatus(walletTransactionStatus);
    }

    /**
     * 批量删除钱包交易状态
     * 
     * @param ids 需要删除的钱包交易状态ID
     * @return 结果
     */
    @Override
    public int deleteWalletTransactionStatusByIds(Long[] ids)
    {
        return walletTransactionStatusMapper.deleteWalletTransactionStatusByIds(ids);
    }

    /**
     * 删除钱包交易状态信息
     * 
     * @param id 钱包交易状态ID
     * @return 结果
     */
    @Override
    public int deleteWalletTransactionStatusById(Long id)
    {
        return walletTransactionStatusMapper.deleteWalletTransactionStatusById(id);
    }

    @Override
    @Transactional
    public void synchronizeBlock(WalletTransactionStatus transactionStatus) {
        // 根据区块获取区块hash
        String blockHash = null;
        try {
            blockHash = BTCWalletUtils.getBlockHash(transactionStatus.getBlockCount());
            // 根据区块Hash获取交易记录
            List<TransactionItem> transactionItemList  = BTCWalletUtils.listSinceBlock(blockHash);
            String txids = "";
            if(transactionItemList !=null && !transactionItemList.isEmpty()){
                // 交易记录非空，查询当前已有的hash值，避免重复操作
                txids = walletTransactionService.getBtcTxids();
            }
            walletTransactionService.saveBtcTransaction(txids, transactionItemList);
            transactionStatus.setStatus(1);
            transactionStatus.setUpdateTime(new Date());
            walletTransactionStatusMapper.updateWalletTransactionStatus(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }
}
