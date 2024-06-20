package com.bigo.project.bigo.task;

import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.btc.BTCWalletUtils;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.project.bigo.contract.service.IContractPlanService;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.luck.domain.LotteryCode;
import com.bigo.project.bigo.luck.service.ILotteryCodeService;
import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.service.BgUserDayBalanceService;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.domain.*;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import com.bigo.project.bigo.wallet.mapper.WalletAddressMapper;
import com.bigo.project.bigo.wallet.service.IAssetLogService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author
 * @Description: 充提币定时任务
 * @date 2019/7/27 下午9:50
 */
@Component("assetTask")
@Slf4j
public class AssetTask  {

    @Autowired
    private IWalletTransactionService transactionService;

    @Autowired
    private IWalletService walletService;

    @Resource
    private WalletAddressMapper walletAddressMapper;

    @Autowired
    IBigoUserService iBigoUserService;

    @Autowired
    BgUserDayBalanceService bgUserDayBalanceService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private ILotteryCodeService lotteryCodeService;

    @Autowired
    private IKlineService klineService;

    @Autowired
    private IWithdrawService withdrawService;



    /**
     * 处理充币操作
     */
    public void rechargeTask() {
        WalletTransaction param = new WalletTransaction();
        param.setStatus(WalletTransactionStatusEnum.SUCCESS.getStatus());
        param.setHandleStatus(0);
        param.setType(1);
        List<WalletTransaction> transactionList = transactionService.listTransaction(param);
        if(CollectionUtils.isEmpty(transactionList)){
            return;
        }
        for(WalletTransaction transaction : transactionList){
            try {
                transactionService.dealSingleTransaction(transaction);
            }catch (Exception e){
                transaction.setHandleStatus(2);
                transaction.setError(e.getMessage());
                transactionService.updateTransaction(transaction);
                log.error("处理充币失败，记录ID:{}, ", transaction.getId(), e);
            }
        }
    }

    /**
     * 处理提币操作
     */
    public void withdrawTask() {
        WalletTransaction param = new WalletTransaction();
        param.setHandleStatus(0);
        param.setType(2);
        List<WalletTransaction> transactionList = transactionService.listTransaction(param);
        if(CollectionUtils.isEmpty(transactionList)){
            return;
        }
        for(WalletTransaction transaction : transactionList){
            try {
                transactionService.dealSingleWithdraw(transaction);
            }catch (Exception e){
                log.error("处理提币失败，记录ID:{}, ", transaction.getId(), e);
            }
        }
    }

    /**
     * 处理归集操作
     */
    public void collectTask() {
        transactionService.doCollect();
    }

    /**
     * 释放冻结币种数量
     */

//    public void release() {
//        log.info("===========开始释放冻结DIEM币============");
//        // 释放DIEM币
//        Wallet entity = new Wallet();
//        entity.setType(0);
//        entity.setCurrency(CurrencyEnum.DIEM.getCode());
//        List<Wallet> walletList = walletService.listForzenWallet(entity);
//        for (Wallet wallet : walletList) {
//            try {
//                log.info("========用户ID:{} , 冻结DIEM币数量: {} 开始释放=======", wallet.getUid(), wallet.getFrozen());
//                walletService.release(wallet);
//            } catch (Exception ex) {
//                log.error("释放冻结DIEM币失败, 钱包ID:{} , 用户ID: {}", wallet.getId(), wallet.getUid());
//                ex.printStackTrace();
//                continue;
//            }
//            log.info("========用户ID:{} 结束释放=======", wallet.getUid());
//        }
//        log.info("===========结束释放冻结DIEM币============");
//    }

    /**
     * 获取btc地址
     */
    public void getBtcAddress() {
        WalletAddress walletAddress = new WalletAddress();
        walletAddress.setCoin("BTC");
        walletAddress.setError("-1");
        List<WalletAddress> walletAddressList = walletAddressMapper.getAddressCoin(walletAddress);
        for (WalletAddress address : walletAddressList) {
            try {
                String btcAddress = BTCWalletUtils.getNewAddress(address.getUid().toString());
                log.info("开始获取BTC地址，用户ID：{},地址：{}", address.getUid(), btcAddress);
                address.setAddress(btcAddress);
                walletAddressMapper.updateAddress(address);
            } catch (IOException e) { //获取失败重新获取
                log.info("获取btc地址失败的会员id:{}", address.getUid());
                e.printStackTrace();
                continue;
            }
        }
        if(walletAddressList != null && !walletAddressList.isEmpty()) {
            log.info("===============结束更新===============");
        }
    }

    /**
     * 每天凌晨30s执行
     */
//    public void insertBgUserBalance(){
//        log.info("insertBgUserBalance start");
//        List<BigoUserEntity> bigoUserEntities = iBigoUserService.listByEntity(new BigoUserEntity());
//        log.info("insertBgUserBalance start={}",bigoUserEntities.size());
//        for(BigoUserEntity userEntity:bigoUserEntities){
//            bgUserDayBalanceService.saveUserBalance(userEntity);
//        }
//    }

//    @Scheduled(fixedDelay = 10*1000)
    public void addWallet(){
        log.info("addWallet start");
        List<BigoUserEntity> bigoUserEntities = iBigoUserService.listByEntity(new BigoUserEntity());
        log.info("addWallet start={}",bigoUserEntities.size());
        for(BigoUserEntity userEntity : bigoUserEntities){
            walletService.complementWallet(userEntity);
        }
        log.info("addWallet end");
    }

    /**
     * 赠送抽奖码
     */
    public void  giftLotteryCode() {
        List<BigoUserEntity> userList = bigoUserService.listByEntity(new BigoUserEntity());
        log.info("==================开始赠送抽奖码====================");
        for (BigoUserEntity entity : userList) {
            try {
                LotteryCode lotteryCode = new LotteryCode();
                lotteryCode.setUid(entity.getUid());
                lotteryCode.setOverdueTime(DateUtils.getEndTime(new Date(), 7));
                lotteryCodeService.insertLotteryCode(lotteryCode);
                log.info("赠送抽奖码成功，用户账号：{}", entity.getEmail());
            }catch (Exception e) {
                e.printStackTrace();
                log.info("赠送抽奖码失败，用户账号：{}", entity.getEmail());
            }

        }
        log.info("==================结束赠送抽奖码====================");
    }


    public void getSymbolPrice() throws ParseException {
        List<WalletTransaction> transactionList = transactionService.listSymbolPrice();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        for (WalletTransaction walletTransaction : transactionList) {
            log.info("开始获取历史行情==========ID:{}", walletTransaction.getId());
            String symbol = "";
            if(walletTransaction.getCoin().equals(CurrencyEnum.ETH.getCode())) {
                symbol = SymbolEnum.ETHUSDT.getCode();
            }else  if(walletTransaction.getCoin().equals(CurrencyEnum.BTC.getCode())) {
                symbol = SymbolEnum.BTCUSDT.getCode();
            }
            try {
                String dateStr = sdf.format(walletTransaction.getCreateTime());
                Date date = sdf.parse(dateStr);
                Long timestamp = date.getTime() / 1000;

                Kline entity = new Kline();
                entity.setSymbol(symbol);
                entity.setTimestamp(timestamp);
                entity.setPeriod(CandlestickEnum.MIN60.getCode());
                Kline kline = klineService.getKline(entity);
                if(kline == null) continue;
                walletTransaction.setSymbolPrice(kline.getLow().setScale(6, BigDecimal.ROUND_HALF_UP));
                walletTransaction.setConvertedPrice(walletTransaction.getMoney().multiply(kline.getLow()).setScale(2, BigDecimal.ROUND_HALF_UP));
                transactionService.updateHandleStatus(walletTransaction);
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
            log.info("结束获取历时行情==========ID:{}", walletTransaction.getId());
        }
    }

    /**
     * 执行人工代付
     */
 /*   @Scheduled(fixedDelay = 20*1000)
    public void manualPayment(){
        log.info("==========Manual payment Start========");
        Withdraw params = new Withdraw();
        params.setStatus(0);
        params.setCheckStatus(0);
        params.setType(1);
        List<Withdraw> entityList = withdrawService.getManualPayment(params);
        log.info("Manual payment size={}",entityList.size());
        for (Withdraw withdraw : entityList) {
            try {
                withdrawService.manualPayment(withdraw);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        log.info("==========Manual payment End========");
    }*/

    public static void main(String[] args) throws ParseException {
        String str = "2021-06-09 19:29:49";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        Date date = sdf.parse(str);
        String dateStr = sdf.format(date);
        dateStr = dateStr+":00:00";
        System.out.println(dateStr);
        date = sdf.parse(dateStr);
        Long timestamp = date.getTime() / 1000;
        System.out.println(timestamp);
    }

}
