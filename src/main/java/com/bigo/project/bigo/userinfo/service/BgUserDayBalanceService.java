package com.bigo.project.bigo.userinfo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.common.utils.RedisUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.web.domain.server.Sys;
import com.bigo.project.bigo.api.CommonInfoApiController;
import com.bigo.project.bigo.api.vo.AccountVO;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BgUserDayBalance;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.entity.UserDayWalletEntity;
import com.bigo.project.bigo.userinfo.mapper.BgUserDayBalanceMapper;
import com.bigo.project.bigo.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/4/16 11:49 PM
 */
@Service
@Slf4j
public class BgUserDayBalanceService {

    @Resource
    BgUserDayBalanceMapper bgUserDayBalanceMapper;

    @Resource
    RedisCache redisCache;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    IBigoUserService iBigoUserService;

    @Resource
    IWalletService walletService;
    BigDecimal usdCalculation(AccountVO account) {
        String symbol = account.getCurrency().toLowerCase() + "usdt";   //交易对
//        if(account.getCurrency().equals(CurrencyEnum.DIEM.getCode())) {
//            symbol = SymbolEnum.CREUSDT.getCode();
//        }
        BigDecimal coinUsdPrice = null;
        if(account.getCurrency().equals(CurrencyEnum.USDT.getCode())) {
            coinUsdPrice = account.getBalance();
            coinUsdPrice = account.getFrozen().add(coinUsdPrice);
        } else {
            BigDecimal symbolPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            // 获取交易对价格
            coinUsdPrice = account.getBalance().multiply(symbolPrice);
            coinUsdPrice = coinUsdPrice.add(account.getFrozen().multiply(symbolPrice));
        }
        return Optional.ofNullable(coinUsdPrice).orElse(BigDecimal.ZERO);
    }
    BigDecimal usdCalculation(UserDayWalletEntity walletEntity) {
        String symbol = walletEntity.getCurrency().toLowerCase() + "usdt";   //交易对
//        if(walletEntity.getCurrency().equals(CurrencyEnum.DIEM.getCode())) {
//            symbol = SymbolEnum.CREUSDT.getCode();
//        }
        BigDecimal coinUsdPrice = null;
        if(walletEntity.getCurrency().equals(CurrencyEnum.USDT.getCode())) {
            coinUsdPrice = walletEntity.getBalance();
        } else {
            BigDecimal symbolPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            // 获取交易对价格
            coinUsdPrice = walletEntity.getBalance().multiply(symbolPrice);
        }
        return Optional.ofNullable(coinUsdPrice).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getBalance(Long uid){
        List<AccountVO> accountList = redisCache.getCacheObject(uid+"_accountInfo");
        BigDecimal balance = BigDecimal.ZERO;
        if(CollectionUtils.isEmpty(accountList)) {
            accountList = walletService.listAccount(uid);
            for(AccountVO account : accountList){
                try {
                    balance = balance.add(usdCalculation(account));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            redisCache.setCacheObject(uid+"_accountInfo", accountList,60, TimeUnit.SECONDS);
        }
        return balance;
    }
    public BigDecimal getBalance(BigoUserEntity user){
        return getBalance(user.getUid());
    }

    public void saveUserBalance(BigoUserEntity user){
        int dayNo = Integer.valueOf(DateUtils.dateTime());
        Long userId = user.getUid();
        BgUserDayBalance bgUserDayBalance = new BgUserDayBalance();
        bgUserDayBalance.setUserId(userId);
        bgUserDayBalance.setDayNo(dayNo);
        List<BgUserDayBalance> bgUserDayBalances = bgUserDayBalanceMapper.findByUserIdAndDayNo(bgUserDayBalance);
        if(!CollectionUtils.isEmpty(bgUserDayBalances)){
            return;
        }else {
            bgUserDayBalance.setCreateTime(new Timestamp(System.currentTimeMillis()));
            bgUserDayBalance.setUserId(userId);
            bgUserDayBalance.setDayNo(dayNo);
            String balanceJson = getBalanceJson(user.getUid());
            bgUserDayBalance.setBalance(balanceJson);
        }
        bgUserDayBalanceMapper.insertBalance(bgUserDayBalance);
    }


    /**
     * 获取昨日盈亏百分比
     */
    public BigDecimal yesterdayProfit(Long uid) {
        try {
            List<UserDayWalletEntity> walletEntityList = redisCache.getCacheObject("day_balance_" + uid);
            if (walletEntityList == null || walletEntityList.size() <= 0) {
                //从数据库取
                String walletJson = "";
                walletJson = bgUserDayBalanceMapper.findByUserIdToDay(uid);
                if(StringUtils.isEmpty(walletJson)) { // 如果数据库也没有，重新生成
                    walletJson = getBalanceJson(uid);
                }
                walletEntityList = JSONArray.parseArray(walletJson, UserDayWalletEntity.class);
            }
            BigDecimal todayRate = BigDecimal.ZERO; // 今日汇率
            BigDecimal yesterdayRate = BigDecimal.ZERO;// 昨日汇率
            for (UserDayWalletEntity walletEntity : walletEntityList) {
                yesterdayRate = yesterdayRate.add(walletEntity.getConvertAmount());
                todayRate = todayRate.add(usdCalculation(walletEntity));
            }
            if (yesterdayRate.equals(BigDecimal.ZERO)) return BigDecimal.ZERO;
            // 计算差价
            BigDecimal spread = BigDecimal.ZERO;
            BigDecimal profit = BigDecimal.ZERO;
            if (yesterdayRate.compareTo(todayRate) > 0) {
                spread = yesterdayRate.subtract(todayRate);
                profit = spread.divide(yesterdayRate, 8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                return profit.negate().setScale(3, BigDecimal.ROUND_HALF_UP);
            } else if (yesterdayRate.compareTo(todayRate) < 0) {
                spread = todayRate.subtract(yesterdayRate);
                profit = spread.divide(yesterdayRate, 8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                return profit.setScale(3, BigDecimal.ROUND_HALF_UP);
            } else {
                return profit;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }


//    @Transactional
//    public void updateToDayBalance(Long uid) {
//        BgUserDayBalance bgUserDayBalance = new BgUserDayBalance();
//        bgUserDayBalance.setUserId(uid);
//        String balanceJson = getBalanceJson(uid);
//        bgUserDayBalance.setBalance(balanceJson);
//        bgUserDayBalanceMapper.updateToDayBalance(bgUserDayBalance);
//        redisCache.deleteObject("day_balance_" + uid);
//    }



    private String getBalanceJson(Long uid) {
        List<AccountVO> accountList = redisCache.getCacheObject(uid+"_accountInfo");
        if(CollectionUtils.isEmpty(accountList)) {
            accountList = walletService.listAccount(uid);
            redisCache.setCacheObject(uid+"_accountInfo", accountList,60, TimeUnit.SECONDS);
        }
        List<UserDayWalletEntity> walletEntityList = new ArrayList<>();
        UserDayWalletEntity walletEntity = null;
        for(AccountVO account : accountList){
            try {
                walletEntity = new UserDayWalletEntity();
                BigDecimal convertAmount = usdCalculation(account);
                walletEntity.setConvertAmount(convertAmount.setScale(8,BigDecimal.ROUND_HALF_UP));
                walletEntity.setBalance(account.getBalance().add(account.getFrozen()).setScale(8,BigDecimal.ROUND_HALF_UP));
                walletEntity.setCurrency(account.getCurrency());
                walletEntity.setUid(uid);
                walletEntityList.add(walletEntity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        JSONArray value = redisCache.getCacheObject("day_balance_"+uid);
        if(StringUtils.isNotEmpty(value))redisTemplate.delete("day_balance_"+uid);
        redisCache.setCacheObject("day_balance_"+uid, walletEntityList, 24, TimeUnit.HOURS);

        JSONArray jsonArray = (JSONArray) JSONArray.toJSON(walletEntityList);
        return jsonArray.toJSONString();
    }

    /**
     * 每天凌晨30s执行
     */
/*    @Scheduled(cron = "30 0 0 * * ?")
    public void insertBgUserBalance(){
        log.info("insertBgUserBalance start");
        List<BigoUserEntity> bigoUserEntities = iBigoUserService.listByEntity(new BigoUserEntity());
        log.info("insertBgUserBalance start={}",bigoUserEntities.size());
        for(BigoUserEntity userEntity:bigoUserEntities){
            saveUserBalance(userEntity);
        }
    }*/

//    @Scheduled(fixedDelay = 5)
//    public void insertBgUserBalance1(){
////        log.info("insertBgUserBalance start");
////        List<BigoUserEntity> bigoUserEntities = iBigoUserService.listByEntity(new BigoUserEntity());
////        log.info("insertBgUserBalance start={}",bigoUserEntities.size());
////        for(BigoUserEntity userEntity:bigoUserEntities){
////            saveUserBalance(userEntity);
////        }
//        redisCache.setCacheObject("vstusdt_price",new BigDecimal(100));
//        redisCache.setCacheObject("bigousdt_price",new BigDecimal(111));
//    }

}
