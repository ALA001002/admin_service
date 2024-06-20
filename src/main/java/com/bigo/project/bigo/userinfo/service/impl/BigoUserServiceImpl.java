package com.bigo.project.bigo.userinfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.*;
import com.bigo.common.utils.ip.IpUtils;
import com.bigo.framework.web.domain.BaseEntity;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentRelationService;
import com.bigo.project.bigo.api.vo.ChildVO;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.entity.ExportBigoUserEntity;
import com.bigo.project.bigo.userinfo.mapper.BigoUserMapper;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.dao.WalletRepository;
import com.bigo.project.bigo.wallet.dao.WithdrawRepository;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/18 9:58
 */
@Service
@Slf4j
public class BigoUserServiceImpl implements IBigoUserService {

    @Autowired
    private BigoUserMapper bigoUserMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IAgentRelationService relationService;

    @Resource
    WalletRepository walletRepository;

    @Resource
    WithdrawRepository withdrawRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(BigoUser user) {
        //注册即为一级用户
        user.setLevel(0L);
        user.setUid(geneUid());
        user.setRegisterIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        Long result = bigoUserMapper.insertUser(user);
        log.info("======处理钱包=========");
        walletService.addWallet(user.getUid());
        log.info("======建立与代理商的关系=========");
        relationService.insert(user);
        return user.getUid();
    }

    @Override
    public int updateUser(BigoUser user) {
        return bigoUserMapper.updateUser(user);
    }

    @Override
    public Boolean isPhoneExist(String phone) {
        return bigoUserMapper.checkPhoneUnique(phone) > 0;
    }

    @Override
    public Boolean isEmailExist(String email) {
        return bigoUserMapper.checkEmailUnique(email) > 0;
    }

    @Override
    public Boolean isInviteCodeExist(Long inviteCode) {
        return bigoUserMapper.checkInviteCodeUnique(inviteCode) > 0;
    }

    @Override
    public BigoUser getUserByUid(Long uid) {
        return bigoUserMapper.getUserByUid(uid);
    }

    @Override
    public BigoUser getUserByPhone(String phone) {
        return bigoUserMapper.getUserByPhone(phone);
    }

    @Override
    public BigoUser getUserByEmail(String email) {
        return bigoUserMapper.getUserByEmail(email);
    }

    @Override
    public List<ChildVO> listMaskChild(List<Long> uidList) {
        List<ChildVO> list = Lists.newArrayList();
        List<BigoUser> childList = bigoUserMapper.listChild(uidList);
        for(BigoUser user : childList){
            ChildVO userVO = new ChildVO();
            if(StringUtils.isNotEmpty(user.getPhone())){
                userVO.setUsername(PrivacyUtils.maskMobile(user.getPhone()));
            }else{
                userVO.setUsername(PrivacyUtils.maskEmail(user.getEmail()));
            }
            userVO.setUid(user.getUid());
            userVO.setInviteTime(user.getRegisterTime());
            list.add(userVO);
        }
        return list;
    }

    @Override
    public List<BigoUserEntity> listByEntity(BigoUserEntity entity) {
        List<BigoUserEntity> bigoUserEntities = new ArrayList<>();
        if(entity.getTeamUid() != null) {
            bigoUserEntities =bigoUserMapper.teamListByEntity(entity);
        }else {
             bigoUserEntities = bigoUserMapper.listByEntity(entity);
        }
        if (!CollectionUtils.isEmpty(bigoUserEntities)) {
            List<Long> userIds = bigoUserEntities.stream().map(t -> t.getUid()).collect(Collectors.toList());
            List<Map> walletInfo = walletRepository.countUserInfo(userIds);
            List<Map> recharge = withdrawRepository.recharge(userIds);
            List<Map> withdraw = withdrawRepository.withdraw(userIds);
            Map<Long, List<Map>> walletMap = walletInfo.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            Map<Long, List<Map>> rechargeMap = recharge.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            Map<Long, List<Map>> withdrawMap = withdraw.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            if (walletMap != null || rechargeMap != null || withdrawMap != null) {
                bigoUserEntities.forEach(item -> {
                    if (walletMap != null) {
                        List<Map> balance = walletMap.get(item.getUid());
                        item.setBalances(mergeMap(balance));
                    }
                    if (recharge != null) {
                        item.setRecharge(mergeMap(rechargeMap.get(item.getUid())));
                    }
                    if (withdrawMap != null) {
                        item.setWithdraw(mergeMap(withdrawMap.get(item.getUid())));
                    }
                });
            }
        }
        return bigoUserEntities;
    }

    @Override
    public List<ExportBigoUserEntity> exportListByEntity(BigoUserEntity entity) {
        List<BigoUserEntity> bigoUserEntities = new ArrayList<>();
        if(entity.getTeamUid() != null) {
            bigoUserEntities =bigoUserMapper.teamListByEntity(entity);
        }else {
            bigoUserEntities = bigoUserMapper.listByEntity(entity);
        }
        if (!CollectionUtils.isEmpty(bigoUserEntities)) {
            List<Long> userIds = bigoUserEntities.stream().map(t -> t.getUid()).collect(Collectors.toList());
            List<Map> walletInfo = walletRepository.countUserInfo(userIds);
            List<Map> recharge = withdrawRepository.recharge(userIds);
            List<Map> withdraw = withdrawRepository.withdraw(userIds);
            Map<Long, List<Map>> walletMap = walletInfo.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            Map<Long, List<Map>> rechargeMap = recharge.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            Map<Long, List<Map>> withdrawMap = withdraw.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            if (walletMap != null || rechargeMap != null || withdrawMap != null) {
                bigoUserEntities.forEach(item -> {
                    if (walletMap != null) {
                        List<Map> balance = walletMap.get(item.getUid());
                        item.setBalances(mergeMap(balance));
                    }
                    if (recharge != null) {
                        item.setRecharge(mergeMap(rechargeMap.get(item.getUid())));
                    }
                    if (withdrawMap != null) {
                        item.setWithdraw(mergeMap(withdrawMap.get(item.getUid())));
                    }
                });
            }
        }
        List<ExportBigoUserEntity> exportBigoUserEntityList = new ArrayList<>();
        if(bigoUserEntities.size() > 0 && bigoUserEntities != null) {
            ExportBigoUserEntity exportBigoUser = null;
            for (BigoUserEntity bigoUserEntity : bigoUserEntities) {
                exportBigoUser = new ExportBigoUserEntity();
                BeanUtils.copyProperties(bigoUserEntity, exportBigoUser);
                BigDecimal balance = (BigDecimal) bigoUserEntity.getBalances().get("USDT");
                exportBigoUser.setBalance(balance.setScale(4,BigDecimal.ROUND_HALF_UP));
                BigDecimal rechargeAmount = BigDecimal.ZERO;
                if(!bigoUserEntity.getRecharge().isEmpty()) rechargeAmount = (BigDecimal) bigoUserEntity.getRecharge().get("USDT");
                exportBigoUser.setRechargeAmount(rechargeAmount);

                BigDecimal withdrawAmount = BigDecimal.ZERO;
                if(!bigoUserEntity.getWithdraw().isEmpty()) withdrawAmount = (BigDecimal) bigoUserEntity.getWithdraw().get("USDT");
                exportBigoUser.setWithdrawAmount(withdrawAmount);
                exportBigoUserEntityList.add(exportBigoUser);

            }
        }
        return exportBigoUserEntityList;
    }

    @Override
    public List<BigoUserEntity> authListByEntity(BigoUserEntity entity) {
        List<BigoUserEntity> bigoUserEntities = bigoUserMapper.authListByEntity(entity);
        return bigoUserEntities;
    }



    @Override
    public List<BigoUserEntity> lowerListEntity(BigoUserEntity entity) {
        List<BigoUserEntity> bigoUserEntities = bigoUserMapper.lowerListEntity(entity);
        if (!CollectionUtils.isEmpty(bigoUserEntities)) {
            List<Long> userIds = bigoUserEntities.stream().map(t -> t.getUid()).collect(Collectors.toList());
            List<Map> walletInfo = walletRepository.countUserInfo(userIds);
            List<Map> recharge = withdrawRepository.recharge(userIds);
            List<Map> withdraw = withdrawRepository.withdraw(userIds);
            Map<Long, List<Map>> walletMap = walletInfo.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            Map<Long, List<Map>> rechargeMap = recharge.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            Map<Long, List<Map>> withdrawMap = withdraw.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            if (walletMap != null || rechargeMap != null || withdrawMap != null) {
                bigoUserEntities.forEach(item -> {
                    if (walletMap != null) {
                        List<Map> balance = walletMap.get(item.getUid());
                        item.setBalances(mergeMap(balance));
                    }
                    if (recharge != null) {
                        item.setRecharge(mergeMap(rechargeMap.get(item.getUid())));
                    }
                    if (withdrawMap != null) {
                        item.setWithdraw(mergeMap(withdrawMap.get(item.getUid())));
                    }
                });
            }
        }
        return bigoUserEntities;
    }



    public static Map mergeMap(List<Map> map){
        Map result = new HashMap();
        if(map==null){
            return result;
        }
        for(Map item:map){
            String coin = (String) item.get("coin");
            result.put(coin,item.get("value"));
        }
        return result;
    }

    @Override
    public List<BigoUser> listNeedLevelUpUser() {
        return bigoUserMapper.listNeedLevelUpUser();
    }

    @Override
    public List<BigoUserEntity> listDockUserByCustomerServiceId(Long customerServiceId) {
        return bigoUserMapper.listDockUserByCustomerServiceId(customerServiceId);
    }

    /**
     * 获取受邀人数
     * @param params
     * @return
     */
    @Override
    public Long getInvitedNumber(Map params) {
        return bigoUserMapper.getInvitedNumber(params);
    }

    @Override
    public List<BigoUserEntity> getChildrenList(BigoUserEntity entity) {
        return bigoUserMapper.getChildrenList(entity);
    }

    @Override
    public List<String> getRechargeUser() {
        return bigoUserMapper.getRechargeUser();
    }

    @Override
    public List<Long> getLowerUids(Long parentUid) {
        return bigoUserMapper.getLowerUids(parentUid);
    }

    @Override
    public void updateSendEmailStatus(String email) {
        bigoUserMapper.updateSendEmailStatus(email);
    }

    @Override
    public List<BigoUser> listParentUids(Long uid) {
        return  bigoUserMapper.listParentUids(uid);
    }

    @Override
    public int editUser(BigoUser user) {
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        }
        if (!StringUtils.isEmpty(user.getPayPassword())) {
            user.setPayPassword(SecurityUtils.encryptPassword(user.getPayPassword()));
        }
        return bigoUserMapper.updateUser(user);
    }

    @Override
    @Transactional
    public void updateUserParent(BigoUser updateUser, BigoUser parentUser) {
        BigoUserEntity entity = new BigoUserEntity();
        entity.setAgentIds(updateUser.getUid().toString());
        // 查询出当前所有下级
        List<BigoUserEntity> getChildrenList = bigoUserMapper.getChildrenList(entity);
        //修改当前下级的所有顶级uid
        Long topUid=null;
        if(parentUser.getTopUid() != null) {
            topUid = parentUser.getTopUid();
        }else {
            topUid = parentUser.getUid();
        }
        if(getChildrenList.size() > 0) {
            List<Long> uidList = new ArrayList<>();
            for (BigoUserEntity bigoUserEntity : getChildrenList) {
                uidList.add(bigoUserEntity.getUid());
            }
            Map map = new HashMap();
            map.put("topUid", topUid);
            map.put("uidList", uidList);
            bigoUserMapper.editUserTopUid(map);
        }
        //修改当前用户的上级id
        updateUser.setTopUid(topUid);
        this.updateUser(updateUser);
    }

    @Override
    @Transactional
    public void CheckAuthStatus(BigoUser updateUser) {
        updateUser.setUpdateTime(new Date());
        updateUser(updateUser);
        if(updateUser.getAuthStatus() != null && updateUser.getAuthStatus() == 2) {
            if(ConfigSettingUtil.getAuthRewardStatus() == 1) {
                // 赠送注册金
                //增加对应币种金额
                BigDecimal rewardNum = ConfigSettingUtil.getAuthRewardNum();
                walletService.lockChange(rewardNum, CurrencyEnum.USDT.getCode(),
                        updateUser.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 0, AssetLogTypeEnum.GIVEAWAY);
            }
        }
    }

    @Override
    public List<BigoUser> getParentUids() {
       return bigoUserMapper.getParentUids();
    }

    /**
     * 获取已认证的下级用户数量
     * @param uid
     * @return
     */
    @Override
    public List<Long> getAuthUserCount(Long uid) {
        return bigoUserMapper.getAuthUserCount(uid);
    }




    /**
     * 生成uid
     * @return
     */
    private Long geneUid(){
        Long uid = RandomNumberUtils.geneRandomUid();
        if(bigoUserMapper.checkInviteCodeUnique(uid) > 0){
            return geneUid();
        }
        return uid;
    }

    public static void main(String[] args) {
        //输出随机生成的UID
        System.out.println(RandomNumberUtils.geneRandomUid());

    }



}
