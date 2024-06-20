package com.bigo.project.bigo.wallet.service;

import com.bigo.common.utils.btc.TransactionItem;
import com.bigo.project.bigo.api.domain.AssetTransferParam;
import com.bigo.project.bigo.api.vo.AccountVO;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 钱包交易service
 * @Author wenxm
 * @Date 2020/6/20 10:02
 */
public interface IWalletTransactionService {

    /**
     * 新增
     * @param walletTransaction
     * @return
     */
    int insert(WalletTransaction walletTransaction);

    /**
     * 处理单条充值记录
     * @param transaction
     */
    void dealSingleTransaction(WalletTransaction transaction);

    /**
     * 查询充提币列表
     * @param walletTransaction
     * @return
     */
    List<WalletTransaction> listTransaction(WalletTransaction walletTransaction);



    /**
     * 处理单条充值记录
     * @param transaction
     */
    void dealSingleWithdraw(WalletTransaction transaction);

    /**
     * 更新钱包交易信息
     * @param transaction
     * @return
     */
    int updateTransaction(WalletTransaction transaction);

    /**
     * 执行归集sql
     */
    void doCollect();

    /**
     * 获取btc的txids
     * @return
     */
    String getBtcTxids();


    void saveBtcTransaction(String txids, List<TransactionItem> transactionItemList);

    List<WalletTransaction> subordinateRecordList(WalletTransaction entity);

    List<WalletTransaction> listSymbolPrice();

    void updateHandleStatus(WalletTransaction walletTransaction);

    void manualScoring(WalletAddress address, String fromAddress, String hash, Long blockNum, BigDecimal rechargeNum);
}
