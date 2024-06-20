package com.bigo.project.bigo.task;

import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.btc.TransactionItem;
import com.bigo.common.utils.btc.BTCWalletUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.WalletTransactionStatusEnum;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.domain.WalletTransactionStatus;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import com.bigo.project.bigo.wallet.service.IWalletTransactionStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @description: btc区块处理
 * @author: wy
 * @date: 2021/1/7 0:15
 */
@Component("btcTask")
@Slf4j
public class BtcTask {

    @Autowired
    private IWalletTransactionService walletTransactionService;

    @Autowired
    private IWalletTransactionStatusService walletTransactionStatusService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 处理btc充值操作
     */
    public void receive() throws IOException {
        log.info("《《========开始处理BTC 充值轮询==========》》");
        // 获取当前块高度
        Long newBlockCount = BTCWalletUtils.getBlockCount();
        log.info("BTC 当前块高度：{}", newBlockCount);
        // 获取缓存里块高度
        Long oldBlockCount = redisCache.getCacheObject("btcBlockCount") == null ? 0L: redisCache.getCacheObject("btcBlockCount");
        log.info("BTC 缓存中块高度：{}", oldBlockCount);
        if(!newBlockCount.equals(oldBlockCount)) {
            String blockHash = "";
            // 根据区块获取区块hash
            blockHash = BTCWalletUtils.getBlockHash(oldBlockCount);
            // 根据区块Hash获取交易记录
            List<TransactionItem> transactionItemList  = BTCWalletUtils.listSinceBlock(blockHash);
            try {
                String txids = "";
                if(transactionItemList !=null && !transactionItemList.isEmpty() && transactionItemList.size() > 0){
                    // 交易记录非空，查询当前已有的hash值，避免重复操作
                    txids = walletTransactionService.getBtcTxids();
                } else {
                    return;
                }
                walletTransactionService.saveBtcTransaction(txids, transactionItemList);
            } catch(Exception ex) {
                WalletTransactionStatus status = new WalletTransactionStatus();
                status.setBlockCount(newBlockCount);
                status.setCurrency(CurrencyEnum.BTC.getCode());
                status.setHash(blockHash);
                status.setStatus(0);
                status.setCreateTime(new Date());
                walletTransactionStatusService.insertWalletTransactionStatus(status);
                log.error("===========BTC转账处理失败,异常信息："+ ex.getMessage());
            } finally {
                // 添加新的区块高度进缓存
                redisCache.setCacheObject("btcBlockCount", newBlockCount);
            }
        }
        log.info("《《========结束处理BTC 充值轮询==========》》");
    }

}
