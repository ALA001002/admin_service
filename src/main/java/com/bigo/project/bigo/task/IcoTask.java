package com.bigo.project.bigo.task;

import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.btc.BTCWalletUtils;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.ico.domain.IcoSpot;
import com.bigo.project.bigo.ico.enums.SpotStatusEnum;
import com.bigo.project.bigo.ico.service.IIcoSpotService;
import com.bigo.project.bigo.luck.domain.LotteryCode;
import com.bigo.project.bigo.luck.service.ILotteryCodeService;
import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.service.BgUserDayBalanceService;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import com.bigo.project.bigo.wallet.mapper.WalletAddressMapper;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author
 * @Description: 充提币定时任务
 * @date 2019/7/27 下午9:50
 */
@Component("icoTask")
@Slf4j
public class IcoTask {

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IWalletTransactionService transactionService;

    @Autowired
    private IBigoUserService iBigoUserService;

    @Autowired
    private IIcoSpotService icoSpotService;

    public void limitBuyOrder() {
        IcoSpot param = new IcoSpot();
        param.setStatus(SpotStatusEnum.NEW.getStatus());
        param.setSide("BUY");
        List<IcoSpot> icoSpotList = icoSpotService.selectIcoSpotList(param);
        for (IcoSpot spot : icoSpotList) {
            try {
                icoSpotService.commissioningBuy(spot);
            }catch (Exception e){
                log.error("处理委托-买订单失败，记录ID:{},错误={}", spot.getOrderId(), e);
            }

        }
    }

    public void limitSellOrder() {
        IcoSpot param = new IcoSpot();
        param.setStatus(SpotStatusEnum.NEW.getStatus());
        param.setSide("SELL");
        List<IcoSpot> icoSpotList = icoSpotService.selectIcoSpotList(param);
        for (IcoSpot spot : icoSpotList) {
            try {
                icoSpotService.commissioningSell(spot);
            }catch (Exception e){
                log.error("处理委托-卖订单失败，记录ID:{},错误={}", spot.getOrderId(), e);
            }

        }
    }

    public void releaseCurrency() {
        walletService.releaseCurrency();
    }



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
