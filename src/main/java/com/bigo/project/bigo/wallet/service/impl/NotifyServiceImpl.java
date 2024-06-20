package com.bigo.project.bigo.wallet.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.framework.config.RuoYiConfig;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.wallet.dao.TransactionRepository;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.PushData;
import com.bigo.project.bigo.wallet.domain.TronTransaction;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.jpaEntity.Transaction;
import com.bigo.project.bigo.wallet.mapper.TronTransactionMapper;
import com.bigo.project.bigo.wallet.mapper.WithdrawMapper;
import com.bigo.project.bigo.wallet.service.INotifyService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;


@Service
@Slf4j
public class NotifyServiceImpl implements INotifyService {

    @Resource
    IWalletService iWalletService;

    @Resource
    WithdrawMapper withdrawMapper;

    @Resource
    TransactionRepository transactionRepository;

    @Resource
    TronTransactionMapper tronTransactionMapper;

    public String notify(PushData pushData) {
        log.info("notify={}",pushData);
        String method = pushData.getMethod();
        if(!"changeBalance".equals(method)){
            return "NOT MATCH";
        }
        String txId = pushData.getTxId();
        Transaction firstByTxid = transactionRepository.findFirstByTxid(txId);
        if(firstByTxid==null){
            return "NOT EXIST";
        }
        boolean score = Optional.ofNullable(firstByTxid.getScore()).orElse(false);
        if(score){
           return "has score";
        }
        String symbol = pushData.getSymbol();
        boolean test = Optional.ofNullable(pushData.getTest()).orElse(false);
        if("CCT".equals(symbol) && test){
            symbol = "USDT";
        }
        Long uid = pushData.getUid();
        String fromAddress = pushData.getFromAddress();
        String address = pushData.getAddress();
        BigDecimal balance = pushData.getBalance();
        AssetChange assetChange = AssetChange.builder()
                .amount(pushData.getBalance())
                .uid(uid)
                .dim(0).type(AssetLogTypeEnum.CASH_IN)
                .subType(AssetLogSubTypeEnum.CASH_IN_OUTSIDE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .currency(symbol)
                .build();
        iWalletService.changeAsset(assetChange);
        Withdraw inLog = new Withdraw();
        inLog.setCoin("USDT");
        inLog.setUid(uid);
        //内转-入
        inLog.setType(4);
        inLog.setFrom(fromAddress);
        inLog.setToAddress(address);
        inLog.setMoney(balance);
        inLog.setFee(BigDecimal.ZERO);
        inLog.setStatus(1);
        inLog.setCheckStatus(1);
        inLog.setRemark("TRC20充值");
        inLog.setVerifyTime(new Date());
        withdrawMapper.insert(inLog);
        TronTransaction tronTransaction = new TronTransaction();
        tronTransaction.setTxid(txId);
        tronTransaction.setScore(1);
        tronTransactionMapper.updateScore(tronTransaction);
        return "SUCCESS";
    }

    @Override
    public String sendNotify(PushData pushData) {
        String url = RuoYiConfig.getFileUrl()+"/wallet/trc20/notify";
        String result = OkHttpUtil.postJsonParams(url, JSONObject.toJSONString(pushData));
        log.info("人工回调通知Result={}", result);
        return "SUCCESS";
    }
}
