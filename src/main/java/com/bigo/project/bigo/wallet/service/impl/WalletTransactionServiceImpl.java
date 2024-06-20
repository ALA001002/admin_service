package com.bigo.project.bigo.wallet.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.btc.TransactionItem;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.userinfo.service.BgUserDayBalanceService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.mapper.WalletTransactionMapper;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description 钱包交易service
 * @Author wenxm
 * @Date 2020/6/20 10:02
 */
@Service
@Slf4j
public class WalletTransactionServiceImpl implements IWalletTransactionService {

    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IWithdrawService withdrawService;

    @Autowired
    private BgUserDayBalanceService userDayBalanceService;

    @Override
    public int insert(WalletTransaction walletTransaction) {
        return walletTransactionMapper.insert(walletTransaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealSingleTransaction(WalletTransaction transaction){
        AssetChange change = AssetChange.builder().uid(transaction.getUid())
                .currency(transaction.getCoin())
                .dim(0)
                .type(AssetLogTypeEnum.CASH_IN)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(transaction.getMoney())
                .build();
        walletService.changeAsset(change);
        String symbol = "";
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal convertedPrice = BigDecimal.ZERO;
        if(transaction.getCoin().equals(CurrencyEnum.USDT.getCode())){
            transaction.setSymbolPrice(BigDecimal.ZERO);
            transaction.setConvertedPrice(transaction.getMoney());
        }else if (transaction.getCoin().equals(CurrencyEnum.ETH.getCode())) {
            symbol = SymbolEnum.ETHUSDT.getCode();
            price = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            convertedPrice = transaction.getMoney().multiply(price);
            transaction.setSymbolPrice(price);  // ETH/USDT交易对价格
            transaction.setConvertedPrice(convertedPrice);
        } else if (transaction.getCoin().equals(CurrencyEnum.BTC.getCode())) {
            symbol = SymbolEnum.BTCUSDT.getCode();
            price = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            convertedPrice = transaction.getMoney().multiply(price);
            transaction.setSymbolPrice(price);  // BTC/USDT交易对价格
            transaction.setConvertedPrice(convertedPrice);
        }
        walletTransactionMapper.updateHandleStatus(transaction);
        //记录充币记录
        Withdraw withdraw = new Withdraw();
        withdraw.setCoin(transaction.getCoin());
        withdraw.setFrom(transaction.getFrom());
        withdraw.setUid(transaction.getUid());
        withdraw.setMoney(transaction.getMoney());
        withdraw.setStatus(1); //转账成功
        withdraw.setType(4);   //外充-入
        withdraw.setToAddress(transaction.getTo());
        withdraw.setHash(transaction.getHash());
        withdrawService.insert(withdraw);
//        userDayBalanceService.updateToDayBalance(transaction.getUid());
    }

    @Override
    public List<WalletTransaction> listTransaction(WalletTransaction walletTransaction) {
        return walletTransactionMapper.listTransaction(walletTransaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealSingleWithdraw(WalletTransaction transaction) {
        /*Withdraw withdraw = withdrawService.getByTransactionId(transaction.getId());
        if(withdraw == null){
            return;
        }
        if(transaction.getStatus() == 2){
            withdraw.setStatus(1);
        }else if(transaction.getStatus() == 3){
            withdraw.setStatus(2);
            //提币失败，扣除的余额返还给用户
            AssetChange change = AssetChange.builder().uid(withdraw.getUid())
                    .currency(withdraw.getCoin())
                    .dim(0)
                    .type(AssetLogTypeEnum.CASH_OUT_FAILED)
                    .subType(AssetLogSubTypeEnum.WALLET_FAILED)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(withdraw.getMoney())
                    .build();
            walletService.changeAsset(change);
        }else{
            return;
        }
        transaction.setHandleStatus(1);
        withdrawService.update(withdraw);
        //更新状态为已处理
        walletTransactionMapper.updateHandleStatus(transaction.getId());*/
    }

    @Override
    public int updateTransaction(WalletTransaction transaction) {
        return walletTransactionMapper.updateTransaction(transaction);
    }

    @Override
    public void doCollect() {
        walletTransactionMapper.doCollect();
    }

    @Override
    public String getBtcTxids() {
        return walletTransactionMapper.getBtcTxids();
    }

    @Override
    @Transactional
    public void saveBtcTransaction(String txIds, List<TransactionItem> transactionItemList) {
        for (TransactionItem transactionItem : transactionItemList) {
            if (StringUtils.isNotNull(txIds) && txIds.contains(transactionItem.getTxid())) continue; //已经存在的txid跳过'
            log.info("开始处理转账接收地址 TO : {}", transactionItem.getAddress());
            WalletTransaction transaction = new WalletTransaction();
            transaction.setCoin(CurrencyEnum.BTC.getCode());
            if("receive".equals(transactionItem.getCategory())) {
                transaction.setType(1);
            } else {
                transaction.setType(2);
            }
            transaction.setMoney(transactionItem.getAmount());
            transaction.setFee(transactionItem.getFee());
            transaction.setTo(transactionItem.getAddress());
            transaction.setStatus(WalletTransactionStatusEnum.SUCCESS.getStatus());
            transaction.setHash(transactionItem.getTxid());
            transaction.setCreateTime(new Date());
            transaction.setHandleStatus(0);
            this.insert(transaction);
            log.info("结束处理转账接收地址 TO : {}", transactionItem.getAddress());
        }
    }

    @Override
    public List<WalletTransaction> subordinateRecordList(WalletTransaction entity) {
        return walletTransactionMapper.listTransaction(entity);
    }

    @Override
    public List<WalletTransaction> listSymbolPrice() {
        return walletTransactionMapper.listSymbolPrice();
    }

    @Override
    public void updateHandleStatus(WalletTransaction walletTransaction) {
        walletTransactionMapper.updateHandleStatus(walletTransaction);
    }

    @Override
    @Transactional
    public void manualScoring(WalletAddress address, String fromAddress ,String hash, Long blockNum, BigDecimal rechargeNum) {
        String coin = address.getCoin();
        List<WalletTransaction> list = walletTransactionMapper.listByTxId(hash);
        if(list != null && list.size() > 0 ) throw new CustomException("订单记录已存在");
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUid(address.getUid());
        transaction.setCoin(coin);
        transaction.setType(1);
        transaction.setMoney(rechargeNum);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setFrom(fromAddress);
        transaction.setTo(address.getAddress());
        transaction.setStatus(WalletTransactionStatusEnum.SUCCESS.getStatus());
        transaction.setHash(hash);
        transaction.setCreateTime(new Date());
        transaction.setHandleStatus(1);
        walletTransactionMapper.insert(transaction);
        this.dealSingleTransaction(transaction);
    }
}
