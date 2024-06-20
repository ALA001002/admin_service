package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 用户充提币记录mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface WalletTransactionMapper {

    /**
     * 插入记录
     * @param walletTransaction
     * @return
     */
    int insert(WalletTransaction walletTransaction);

    /**
     * 查询充提币列表
     * @param walletTransaction
     * @return
     */
    List<WalletTransaction> listTransaction(WalletTransaction walletTransaction);

    /**
     * 更新处理状态
     * @param transaction
     * @return
     */
    int updateHandleStatus(WalletTransaction transaction);

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

    String getBtcTxids();

    List<WalletTransaction> listSymbolPrice();

    List<WalletTransaction> listByTxId(@Param("hash") String hash);
}
