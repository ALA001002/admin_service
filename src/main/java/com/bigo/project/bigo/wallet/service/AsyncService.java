package com.bigo.project.bigo.wallet.service;

import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.domain.LevelConfig;
import com.bigo.project.bigo.userinfo.domain.LevelVip;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.userinfo.service.ILevelVipService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class AsyncService {
    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private ILevelVipService levelVipService;

    /**
     * 层级分销
     * @param uid
     * @param amount
     */
    @Async
    public void levelRebate(Long uid, BigDecimal amount) {
        log.info("==========异步调用分销处理===================");
//        BigoUser user = bigoUserService.getUserByUid(uid);
        BigoUser user = null;
        //从下往上查出关系链
        // 获取所有的上级
        List<BigoUser> parents = bigoUserService.listParentUids(uid);
        if(parents == null || parents.size() <= 0) return;
        for (BigoUser parent : parents) {
            if(parent.getUid().equals(uid)) {
                user = parent;
                break;
            }
        }
        if(user == null || user.getParentUid() == null) return;
        // 分销最高层次
        Integer mostRebateLevel = ConfigSettingUtil.getMostRebateLevel();
        // 分销开始层次,默认1层开始
        int level = 1;
//        log.info("levelRebate={},mostRebateLevel={},parents={}",level,mostRebateLevel,parents.size());
        rebate(amount, user, parents, mostRebateLevel, level, uid);
    }

    @Transactional
    public void rebate(BigDecimal amount, BigoUser user, List<BigoUser> parents, Integer mostRebateLevel, int level, Long orderUid) {
        if(level > mostRebateLevel) return;
        // 获取当前层级分销比例
        BigDecimal levelRebate = ConfigSettingUtil.getLevelRebate(level).divide(new BigDecimal(100));
        // 上级ID
        BigoUser parentUser = null;
        for (BigoUser parent : parents) {
            if(user.getParentUid().equals(parent.getUid())) {
                parentUser = parent;
                break;
            }
        }
        if(parentUser == null) return;
        // 如果上级用户的parentUid和topUid是空的，代表他是代理商，则不进行下一步
        if(parentUser.getParentUid() == null && parentUser.getTopUid() == null) return;
        // 处理分销返利
        AssetLogSubTypeEnum assetLogSubTypeEnum = null;
        if(1 == level) {
            assetLogSubTypeEnum = AssetLogSubTypeEnum.FIRST_BACK;
        }else  if(2 == level) {
            assetLogSubTypeEnum = AssetLogSubTypeEnum.SECOND_BACK;
        }else  if(3 == level) {
            assetLogSubTypeEnum = AssetLogSubTypeEnum.THIRD_BACK;
        }else if(4 == level) {
            assetLogSubTypeEnum = AssetLogSubTypeEnum.FOUR_BACK;
        }else if(5 == level) {
            assetLogSubTypeEnum = AssetLogSubTypeEnum.FIVE_BACK;
        } else {
            assetLogSubTypeEnum = AssetLogSubTypeEnum.EXTRA_BACK;
        }
        // 返利数量
        BigDecimal rebateNum = amount.multiply(levelRebate);
        AssetChange firstChange = AssetChange.builder().uid(parentUser.getUid())
                .currency(CurrencyEnum.USDT.getCode())
                .dim(0)
                .type(AssetLogTypeEnum.RAKE_BACK)
                .subType(assetLogSubTypeEnum)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(rebateNum.setScale(4,BigDecimal.ROUND_HALF_UP))
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(firstChange);
        log.info("下单Uid:{},返佣层级:{},返佣Uid:{}", orderUid, level, parentUser.getUid());
        level ++;
        rebate(amount, parentUser, parents, mostRebateLevel, level, orderUid);
    }

    /**
     * 层级分销
     * @param uid
     * @param amount
     */
    @Async
    public void vipLevelRebate(Long uid, BigDecimal amount) {
        log.info("==========异步调用分销处理===================");
//        BigoUser user = bigoUserService.getUserByUid(uid);
        BigoUser user = null;
        //从下往上查出关系链
        // 获取所有的上级
        List<BigoUser> parents = bigoUserService.listParentUids(uid);
        if(parents == null || parents.size() <= 0) return;
        for (BigoUser parent : parents) {
            if(parent.getUid().equals(uid)) {
                user = parent;
                break;
            }
        }
        if(user == null || user.getParentUid() == null) return;

        List<LevelVip> levelVipList = levelVipService.selectLevelVipList(new LevelVip());
        // 分销最高层次
//        Integer mostRebateLevel = ConfigSettingUtil.getMostRebateLevel();
        // 分销开始层次,默认1层开始
        int level = 1;

        vipRebate(amount, user, parents, 3, level, uid,levelVipList);
    }

    @Transactional
    public void vipRebate(BigDecimal amount, BigoUser user, List<BigoUser> parents, Integer mostRebateLevel, int level, Long orderUid,  List<LevelVip> levelVipList) {
        if(level > mostRebateLevel) return;
        // 获取当前层级分销比例
        // 上级ID
        BigoUser parentUser = null;
        for (BigoUser parent : parents) {
            if(user.getParentUid().equals(parent.getUid())) {
                parentUser = parent;
                break;
            }
        }
        if(parentUser == null) return;
        // 如果上级用户的parentUid和topUid是空的，代表他是代理商，则不进行下一步
        if(parentUser.getParentUid() == null && parentUser.getTopUid() == null) return;
        LevelVip levelVip = null;
        for (LevelVip vip : levelVipList) {
            if(vip.getLevel() == parentUser.getLevel()) {
                levelVip = vip;
                break;
            }
        }
        BigDecimal levelRebate = BigDecimal.ZERO;

        if(levelVip != null) {
            // 处理分销返利
            AssetLogSubTypeEnum assetLogSubTypeEnum = null;
            if (1 == level) {
                assetLogSubTypeEnum = AssetLogSubTypeEnum.FIRST_BACK;
                levelRebate = levelVip.getLevelOne();
            } else if (2 == level) {
                assetLogSubTypeEnum = AssetLogSubTypeEnum.SECOND_BACK;
                levelRebate = levelVip.getLevelTwo();
            } else if (3 == level) {
                assetLogSubTypeEnum = AssetLogSubTypeEnum.THIRD_BACK;
                levelRebate = levelVip.getLevelThree();
            } else {
                assetLogSubTypeEnum = AssetLogSubTypeEnum.EXTRA_BACK;
            }
            // 返利数量
            if(levelRebate.compareTo(BigDecimal.ZERO) > 0) {
                // 返利数量
                BigDecimal rebateNum = amount.multiply(levelRebate.divide(new BigDecimal(100)));
                AssetChange firstChange = AssetChange.builder().uid(parentUser.getUid())
                        .currency(CurrencyEnum.USDT.getCode())
                        .dim(0)
                        .type(AssetLogTypeEnum.RAKE_BACK)
                        .subType(assetLogSubTypeEnum)
                        .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                        .amount(rebateNum.setScale(4, BigDecimal.ROUND_HALF_UP))
                        .amountType(AmountTypeEnum.BANLANCE.getType())
                        .build();
                walletService.changeAsset(firstChange);
                log.info("下单Uid:{},返佣层级:{},返佣Uid:{}", orderUid, level, parentUser.getUid());
            }
        }
        level ++;
        rebate(amount, parentUser, parents, mostRebateLevel, level, orderUid);
    }
}
