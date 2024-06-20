package com.bigo.project.bigo.task;

import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.project.bigo.enums.WalletTransactionStatusEnum;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.domain.LevelAward;
import com.bigo.project.bigo.userinfo.domain.LevelConfig;
import com.bigo.project.bigo.userinfo.domain.UserLevel;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.userinfo.service.ILevelAwardService;
import com.bigo.project.bigo.userinfo.service.ILevelConfigService;
import com.bigo.project.bigo.userinfo.service.IUserLevelService;
import com.bigo.project.bigo.wallet.domain.AssetLog;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;
import com.bigo.project.bigo.wallet.service.IAssetLogService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wenxm
 * @Description: 用户定时任务
 * @date 2019/7/27 下午9:50
 */
@Component("userTask")
@Slf4j
public class UserTask {

    @Autowired
    private IBigoUserService userService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IContractService contractService;


    @Autowired
    private ILevelConfigService levelConfigService;

    @Autowired
    private IWithdrawService withdrawService;

    @Autowired
    private IAssetLogService assetLogService;

    @Autowired
    private RedisCache redisCache;



    /**
     * 处理用户等级操作,每天中午15点执行
     */
    public void userLevelTask() {
        List<LevelConfig> levelConfigList = levelConfigService.selectLevelConfigList(new LevelConfig());
        Collections.sort(levelConfigList);
        List<BigoUser> bigoUserList = userService.getParentUids();
        BigoUser bigoUser = null;
        for (BigoUser user : bigoUserList) {
            Long uid = user.getUid();
            Long level = 0L;
            //获取下三级实名认证有效用户
            List<Long> uidList = userService.getAuthUserCount(uid);
            if(uidList == null || uidList.size() == 0) continue;
//            uidList.add(uid);
            // 获取下三级用户充值数量
            Withdraw param = new Withdraw();
            param.setUidList(uidList);
            param.setStatus(1);
            param.setType(4);
            BigDecimal rechargeAmount = withdrawService.rechargeAndWithdrawAmount(param);

            // 获取下三级用户提现数量
            param.setStatus(1);
            param.setType(2);
            BigDecimal withdrawAmount = withdrawService.rechargeAndWithdrawAmount(param);
            BigDecimal teamAsset = rechargeAmount.subtract(withdrawAmount);
            if(teamAsset.compareTo(BigDecimal.ZERO) <=0) teamAsset = BigDecimal.ZERO;

//            BigDecimal teamAsset = getTeamAsset(uidList);//团队资产

            //团队交易佣金
            AssetLogEntity entity =new AssetLogEntity();
            entity.setUid(uid);
            entity.setCoin(CurrencyEnum.USDT.getCode());
            entity.setType(AssetLogTypeEnum.RAKE_BACK.getType());
            List<AssetLogEntity> logEntityList = assetLogService.listAssetLogByEntity(entity);

            BigDecimal teamCommission=BigDecimal.ZERO;
            if(logEntityList != null && logEntityList.size()>0){
                for (AssetLogEntity assetLogEntity : logEntityList) {
                    teamCommission = assetLogEntity.getAmount().add(teamCommission);
                }
            }


            if(uidList.size() > 0) {
                for (LevelConfig levelConfig : levelConfigList) {
                    if (uidList.size() >= levelConfig.getRequireUser()
                            && teamAsset.compareTo(levelConfig.getTeamAsset()) >= 0
                            && teamCommission.compareTo(levelConfig.getTeamCommission()) >=0 ) {
                        level = levelConfig.getLevel();
                        break;
                    }
                }
                //更新用户信息
                if (level >= 0) {
                    bigoUser = new BigoUser();
                    bigoUser.setUid(uid);
                    if(level > user.getTopLevel()){
                        bigoUser.setLevel(level);
                        bigoUser.setTopLevel(level);
                        //新增赠送奖励
                        /*LevelAward award = new LevelAward();
                        award.setUid(uid);
                        award.setLevel(levelConfigSetting.getLevel());
                        award.setAwardAmount(levelConfigSetting.getAwardAmount());
                        award.setRequireUser(count);
                        award.setTeamAsset(teamAsset);
                        award.setGetStatus(0L);
                        award.setCreateTime(new Date());
                        levelAwardService.insertLevelAward(award);*/
                    }
                    userService.updateUser(bigoUser);
                }
            }
        }
    }

    private BigDecimal getContractFeeByUid(Long uid){
        BigDecimal totalFeeUSDT = contractService.getTotalFeeByUid(uid, CurrencyEnum.USDT.getCode());
        BigDecimal totalFeeETH = contractService.getTotalFeeByUid(uid, CurrencyEnum.ETH.getCode());
        if(totalFeeETH.compareTo(BigDecimal.ZERO) > 0){
            BigDecimal curRate = MarketSituationUtils.getCurrentPriceBySymbol(SymbolEnum.ETHUSDT.getCode());
            BigDecimal ethToUSDT = totalFeeETH.multiply(curRate).setScale(8, RoundingMode.HALF_UP);
            totalFeeUSDT = totalFeeUSDT.add(ethToUSDT);
        }
        return totalFeeUSDT;
    }

    private BigDecimal getTeamAsset(List<Long> uidList) {
        List<Map> userInfoList = walletService.countUserInfo(uidList,2);
        Map<Long, List<Map>> walletUserMap = userInfoList.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
        BigDecimal total = BigDecimal.ZERO;
        for (Long uid : uidList) {
            BigDecimal balanceTotal = BigDecimal.ZERO;
            List<Map> walletList = walletUserMap.get(uid);
            if(walletList == null || walletList.size() == 0) continue;
            for (Map map : walletList) {
                BigDecimal amount = (BigDecimal) map.get("balanceValue");
                if(amount.compareTo(BigDecimal.ZERO) <= 0) continue;
                String coin = (String) map.get("coin");
                if(CurrencyEnum.USDT.getCode().equals(coin)) {
                    balanceTotal = balanceTotal.add(amount);
                }else {
                    String symbol = SymbolEnum.getCodeByCoin(coin.toLowerCase());
                    BigDecimal price = redisCache.getCacheObject(symbol + "_price") == null ? BigDecimal.ZERO : redisCache.getCacheObject(symbol + "_price");
                    BigDecimal usdtAmount = amount.multiply(price);
                    balanceTotal = balanceTotal.add(usdtAmount);
                }
            }
            total = total.add(balanceTotal);
        }
        return total;
    }

}
