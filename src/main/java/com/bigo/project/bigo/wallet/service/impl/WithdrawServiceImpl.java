package com.bigo.project.bigo.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.dto.TransDTO;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.mapper.BigoUserMapper;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;
import com.bigo.project.bigo.wallet.mapper.WalletAddressMapper;
import com.bigo.project.bigo.wallet.mapper.WithdrawMapper;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import com.bigo.project.bigo.wallet.view.TransferData;
import com.bigo.project.bigo.wallet.view.TrxResult;
import com.bigo.project.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 提现service实现
 * @author: wenxm
 * @date: 2020/6/27 14:37
 */
@Service
@Slf4j
public class WithdrawServiceImpl implements IWithdrawService {


    @Autowired
    private WithdrawMapper withdrawMapper;

    @Autowired
    private WalletAddressMapper walletAddressMapper;

    @Autowired
    private BigoUserMapper userMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private StringRedisTemplate redisTemplate;



//    @Autowired
//    private IWalletTransactionService transactionService;
//
//    @Autowired
//    private IUserLevelService levelService;
//
//    @Autowired
//    private IBigoUserService userService;
//
//    @Autowired
//    private BgUserDayBalanceService bgUserDayBalanceService;

    @Value("${config.trxService}")
    private String trxService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean withdraw(Withdraw withdraw) {
        Long uid = withdraw.getUid();
//        BigoUser user = userService.getUserByUid(uid);
        //获取每日提币数量
//        BigDecimal dayWithdrawIn = this.getTodayWithdrawQuantity(uid, 1);
//        BigDecimal dayWithdrawOut = this.getTodayWithdrawQuantity(uid, 2);
//        BigDecimal dayWithdraw = dayWithdrawOut.add(dayWithdrawIn).add(withdraw.getMoney());
        /*BigDecimal limitMoney = user.getAuthStatus() == 2 ? level.getLimitAuth() : level.getLimitNoAuth();
        if(dayWithdraw.compareTo(limitMoney) > 0){
            throw new CustomException("the_daily_withdrawal_limit_has_been_reached");
        }*/
        //获取今日提现次数
        Integer withdrawCount = withdrawMapper.getWithdrawCount(uid);
        if(withdrawCount >= ConfigSettingUtil.getWithdrawCount()) {
            throw new CustomException("the_number_of_withdrawals_has_been_capped");
        }
        //获取转出地址
        WalletAddress address = new WalletAddress();
        address.setAddress(withdraw.getToAddress());
        address.setCoin(withdraw.getCoin());
        WalletAddress toAddress = walletAddressMapper.getByAddressAndCoin(address);
        address.setUid(uid);
        String fromAddress = walletAddressMapper.getAddressByCoin(address);
//        BigDecimal fee = CoinUtils.getWithdrawFeeByCurrency(withdraw.getCoin(), withdraw.getMoney(), withdraw.getType());
        BigDecimal fee = withdraw.getMoney().multiply(ConfigSettingUtil.getWithdrawFee().divide(new BigDecimal(100))); // 固定手续费
        withdraw.setFee(fee);
        //先扣减转出账户余额

        BigDecimal subMoney = withdraw.getMoney().subtract(fee); //
//        BigDecimal receiveMoney = withdraw.getMoney();//真实金额

/*        if(withdraw.getType() == 1){
            //内转暂时无需审核
            withdraw.setStatus(3);
            withdraw.setCheckStatus(0);
            //内转
            if(toAddress == null){
                throw new CustomException("to_address_is_not_exist");
            }
            if(toAddress.getUid().equals(uid)){
                throw new CustomException("cannot_transfer_money_to_yourself");
            }
            //增加转入账户余额
*//*            AssetChange toChange = AssetChange.builder().uid(toAddress.getUid())
                    .currency(toAddress.getCoin())
                    .dim(0)
                    .type(AssetLogTypeEnum.CASH_IN)
                    .subType(AssetLogSubTypeEnum.CASH_IN_INSIDE)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(receiveMoney)
                    .build();
            walletService.changeAsset(toChange);
            //插入记录
            Withdraw inLog = new Withdraw();
            inLog.setCoin(withdraw.getCoin());
            inLog.setUid(toAddress.getUid());
            //内转-入
            inLog.setType(3);
            inLog.setFrom(fromAddress);
            inLog.setToAddress(toAddress.getAddress());
            inLog.setMoney(receiveMoney);
            inLog.setFee(BigDecimal.ZERO);
            inLog.setStatus(1);
            inLog.setCheckStatus(1);
            inLog.setRemark(withdraw.getRemark());
            withdrawMapper.insert(inLog);*//*
        }else{
           *//* if(isExistWithdraw(uid)){
                throw new CustomException("there_is_a_withdrawal_under_review");
            }*//*

        }*/
        withdraw.setStatus(3);
        withdraw.setCheckStatus(0);
        withdraw.setMoney(subMoney);
        walletService.lockChange(subMoney, withdraw.getCoin().toUpperCase(), uid,
                WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1,AssetLogTypeEnum.CASH_OUT,AssetLogSubTypeEnum.CASH_OUT_OUTSIDE);
        //扣除手续费
        walletService.lockChange(fee, CurrencyEnum.USDT.getCode(), uid,
                WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1,AssetLogTypeEnum.FEE, AssetLogSubTypeEnum.CASH_OUT_OUTSIDE);

        withdraw.setFrom(fromAddress);
        //插入提币记录
        withdrawMapper.insert(withdraw);
        return Boolean.TRUE;
    }



    @Override
    public int insert(Withdraw withdraw) {
        return withdrawMapper.insert(withdraw);
    }

    @Override
    public List<Withdraw> listWithdraw(Withdraw withdraw) {
        return withdrawMapper.listWithdraw(withdraw);
    }

    @Override
    public Withdraw getByTransactionId(Long transactionId) {
        Withdraw params = new Withdraw();
        params.setTransactionId(transactionId);
        return withdrawMapper.getByParam(params);
    }

    @Override
    public Withdraw getById(Long id) {
        Withdraw params = new Withdraw();
        params.setId(id);
        return withdrawMapper.getByParam(params);
    }

    @Override
    public int update(Withdraw withdraw) {
        return withdrawMapper.update(withdraw);
    }

    @Override
    public List<WithdrawEntity> listByEntity(WithdrawEntity entity) {
        return withdrawMapper.listByEntity(entity);
    }


    @Override
    public List<WithdrawEntity> withdrawListByEntity(WithdrawEntity entity) {
        return withdrawMapper.withdrawListByEntity(entity);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean checkWithdraw(Withdraw withdraw) {
        Withdraw params = new Withdraw();
        params.setId(withdraw.getId());
        params = withdrawMapper.getByParam(params);
        BigoUser user = userMapper.getUserByUid(withdraw.getUid());
        //审核通过
        if(withdraw.getCheckStatus() == 1){
            if(params.getType() ==1){//内转：入
                //获取转出地址
                WalletAddress address = new WalletAddress();
                address.setAddress(params.getToAddress());
                address.setCoin(params.getCoin());
                WalletAddress toAddress = walletAddressMapper.getByAddressAndCoin(address);

                /*AssetChange toChange = AssetChange.builder().uid(toAddress.getUid())
                        .currency(toAddress.getCoin())
                        .dim(0)
                        .type(AssetLogTypeEnum.CASH_IN)
                        .subType(AssetLogSubTypeEnum.CASH_IN_INSIDE)
                        .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                        .amount(params.getMoney())
                        .build();
                walletService.changeAsset(toChange);*/
                walletService.lockChange(params.getMoney(), toAddress.getCoin().toUpperCase(),toAddress.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),
                        0,AssetLogTypeEnum.CASH_IN, AssetLogSubTypeEnum.CASH_IN_INSIDE);
                //插入记录
                Withdraw inLog = new Withdraw();
                inLog.setCoin(withdraw.getCoin());
                inLog.setUid(toAddress.getUid());
                //内转-入
                inLog.setType(3);
                inLog.setFrom(params.getFrom());
                inLog.setToAddress(toAddress.getAddress());
                inLog.setMoney(params.getMoney());
                inLog.setFee(BigDecimal.ZERO);
                inLog.setStatus(1);
                inLog.setCheckStatus(1);
                inLog.setRemark(withdraw.getRemark());
                withdrawMapper.insert(inLog);
            }else {
                withdraw.setStatus(1);
                withdraw.setCheckStatus(1);

                //判断开头字符串是否为T,是否为TRC地址
                if(!params.getToAddress().startsWith("T")){
                    return Boolean.TRUE;
                }

                //判断提现地址是否为系统地址,系统地址不让通过
                WalletAddress withdrawAddr = new WalletAddress();
                withdrawAddr.setAddress(params.getToAddress());
                Integer addressExist = walletAddressMapper.isAddressExist(withdrawAddr);
                if(addressExist != null && addressExist > 0) {
                    return Boolean.TRUE;
                }


                if(params.getType()==2 && "USDT".equals(withdraw.getCoin()) && user.getStatus() == 0){
                    TransferData build = TransferData.builder().
                            withdrawId(withdraw.getId())
                            .price(withdraw.getMoney()).symbol(withdraw.getCoin()).address(params.getToAddress()).build();

                    //防止触发多次
                    boolean transferStatus = redisTemplate.opsForValue().setIfAbsent(withdraw.getId()+"_"+params.getToAddress(),params.getToAddress(), 5, TimeUnit.SECONDS);
                    if(!transferStatus) return Boolean.FALSE;
                    log.info("uid={}发起提现请求，提现地址={},请求地址",params.getUid(), params.getToAddress());
                    String result = OkHttpUtil.postJsonParams(trxService+"/api/transfer",JSON.toJSONString(build));
                    log.info("transferToUser={}",result);
                    TrxResult trxResult = JSON.parseObject(result, TrxResult.class);

                    if(trxResult==null || trxResult.getCode()!=0){
                        log.info("trxResult={},msg={},req={}",trxResult,trxResult!=null?trxResult.getMsg():"",build);
                        throw new CustomException("线上提现失败，TRX服务请求错误，请联系技术人员查看！");
                    }
                }
            }
        }else if(withdraw.getCheckStatus() == 2){
            //驳回请求，返回已扣除的金额
            withdraw.setStatus(2);
            String coin = withdraw.getCoin();
            if("USDT_TRC20".equals(coin)){
                coin = "USDT";
            }
            walletService.lockChange(params.getMoney(), coin.toUpperCase(), withdraw.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),
                    0,AssetLogTypeEnum.CASH_OUT_FAILED, AssetLogSubTypeEnum.REJECT_FAILED);

            walletService.lockChange(params.getFee(), CurrencyEnum.USDT.getCode(), withdraw.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),
                    0,AssetLogTypeEnum.FEE, AssetLogSubTypeEnum.REJECT_FAILED);
        }
        withdrawMapper.update(withdraw);
        return Boolean.TRUE;
    }



    private BigDecimal getTodayWithdrawQuantity(Long uid, Integer type){
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
        String today = sp.format(new Date());
        //定义每天的24h时间范围
        String beginTime = today + " 00:00:00";
        String endTime = today + " 23:59:59";
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("type", type);
        return withdrawMapper.getDayWithdrawQuantity(params);
    }

    private Boolean isExistWithdraw(Long uid){
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
        String today = sp.format(new Date());
        //定义每天的24h时间范围
        String beginTime = today + " 00:00:00";
        String endTime = today + " 23:59:59";
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        return withdrawMapper.isExistWithdraw(params) > 0;
    }

    @Override
    public BigDecimal getWithdraAmount(Long uid, String coin,Integer type,Integer checkStatus, Integer status) {
        WithdrawEntity entity = new WithdrawEntity();
        entity.setType(type);
        entity.setUid(uid);
        entity.setCoin(coin);
        entity.setCheckStatus(checkStatus);
        entity.setStatus(status);
        return withdrawMapper.getWithdraAmount(entity);
    }

    @Override
    public BigDecimal withdrawAuditRecord(WithdrawEntity entity) {
        return withdrawMapper.withdrawAuditRecord(entity);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkRecharge(Withdraw withdraw) {
        //审核通过
        if(withdraw.getCheckStatus() == 1){
            withdraw.setType(5);
            withdraw.setStatus(1);

            BigDecimal rechargeBalance = withdraw.getMoney();
            //判断首充奖励是否开启
            Integer firstRechargeStatus = ConfigSettingUtil.getFirstRechargeStatus();
            BigDecimal firstRechargeComplyAmount = ConfigSettingUtil.getFirstRechargeComplyAmount();
            if(firstRechargeStatus == 1 && withdraw.getMoney().compareTo(firstRechargeComplyAmount) >=0 ) { //开启
                Long uid = withdraw.getUid();
                //判断是否首充
                WithdrawEntity param = new WithdrawEntity();
                param.setUid(uid);
                param.setType(4);
                param.setCoin(withdraw.getCoin());
                param.setStatus(1);
                param.setCheckStatus(1);
                BigDecimal rechargeAmount = withdrawMapper.getWithdraAmount(param);
                if(rechargeAmount.compareTo(BigDecimal.ZERO) == 0) { //首充
                    BigoUser user = userMapper.getUserByUid(uid);
                    //奖励金额
                    BigDecimal firstRechargeRewards =  ConfigSettingUtil.getFirstRechargeRewards();
                    //奖励金额+充值金额
//                    rechargeBalance = rechargeBalance.add(firstRechargeRewards);
                    AssetChange firstRecharge = AssetChange.builder().uid(withdraw.getUid())
                            .currency(withdraw.getCoin())
                            .dim(0)
                            .amount(firstRechargeRewards)
                            .type(AssetLogTypeEnum.CASH_IN_DRAW)
                            .subType(AssetLogSubTypeEnum.FIRST_BACK)
                            .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                            .build();
                    walletService.changeAsset(firstRecharge);
                    // 上级奖励金额
                    BigDecimal firstRechargeSuperRewards = ConfigSettingUtil.getFirstRechargeSuperRewards();
                    if(firstRechargeSuperRewards.compareTo(BigDecimal.ZERO) > 0) {
                        AssetChange assetChange = AssetChange.builder()
                                .amount(firstRechargeSuperRewards)
                                .uid(user.getParentUid())
                                .dim(0).type(AssetLogTypeEnum.CASH_IN_DRAW)
                                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                                .currency(withdraw.getCoin())
                                .build();
                        walletService.changeAsset(assetChange);
                    }
                }
            }

            AssetChange change = AssetChange.builder().uid(withdraw.getUid())
                    .currency(withdraw.getCoin())
                    .dim(0)
                    .type(AssetLogTypeEnum.CASH_IN)
                    .subType(AssetLogSubTypeEnum.CASH_IN_OUTSIDE)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(rechargeBalance)
                    .build();
            walletService.changeAsset(change);
        }else if (withdraw.getCheckStatus() == 2){
            // 驳回
            withdraw.setStatus(2);
        }
        withdrawMapper.update(withdraw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agentPayWithdraw(WithdrawEntity withdraw, SysUser sysUser) {
        Withdraw params = new Withdraw();
        params.setCoin(CurrencyEnum.USDT.getCode());
        params.setMoney(withdraw.getMoney());
        params.setFee(BigDecimal.ZERO);
        params.setToAddress(withdraw.getToAddress());
        params.setStatus(0);
        params.setCheckStatus(0);
        params.setType(1);
        params.setCreateTime(new Date());
        params.setOperatorId(sysUser.getUserId());
        params.setRemark(withdraw.getRemark());
        Integer id = withdrawMapper.insert(params);
       /* TransferData build = TransferData.builder().
                withdrawId(params.getId())
                .price(withdraw.getMoney())
                .symbol(CurrencyEnum.USDT.getCode())
                .address(params.getToAddress())
                .build();

        String result = OkHttpUtil.postJsonParams(trxService+"/api/transfer",JSON.toJSONString(build));
        log.info("transferToUser={}",result);
        TrxResult trxResult = JSON.parseObject(result, TrxResult.class);
        if(trxResult==null || trxResult.getCode()!=0){
            throw new CustomException("人工代付失败");
        }*/

    }

    /**
     * 线下打款
     * @param entity
     * @param sysUser
     */
    @Override
    public void offlinePay(WithdrawEntity entity, SysUser sysUser) {
        Withdraw params = new Withdraw();
        params.setId(entity.getId());
        Withdraw withdraw = withdrawMapper.getByParam(params);
        //审核通过
        withdraw.setStatus(1);
        withdraw.setCheckStatus(1);
        withdraw.setVerifyTime(new Date());
        withdraw.setType(3);
        withdraw.setOperatorId(sysUser.getUserId());
        withdrawMapper.update(withdraw);
    }

    @Override
    @Transactional
    public void manualPayment(Withdraw withdraw) {
        boolean paymentStatus = redisTemplate.opsForValue().setIfAbsent(withdraw.getId()+"_"+withdraw.getToAddress(),withdraw.getToAddress(), 20, TimeUnit.SECONDS);
        if(!paymentStatus) return;
        Withdraw params = new Withdraw();
        params.setId(withdraw.getId());
        params.setStatus(1);
        params.setCheckStatus(1);
        params.setVerifyTime(new Date());
        withdrawMapper.update(params);

        TransferData build = TransferData.builder().
                withdrawId(withdraw.getId())
                .price(withdraw.getMoney())
                .symbol(CurrencyEnum.USDT.getCode())
                .address(withdraw.getToAddress())
                .build();

        String result = OkHttpUtil.postJsonParams(trxService+"/api/transfer",JSON.toJSONString(build));
        log.info("transferToUser={}",result);
        TrxResult trxResult = JSON.parseObject(result, TrxResult.class);
        if(trxResult==null || trxResult.getCode()!=0) {
            throw new CustomException("人工代付失败,提现ID="+withdraw.getId());
        }
    }

    @Override
    public List<Withdraw> getManualPayment(Withdraw params) {
        return withdrawMapper.getManualPayment(params);
    }

    @Override
    public Map<String, BigDecimal> getTotalForm(WithdrawEntity entity) {
        Map<String, BigDecimal> maps = new HashMap<>();
        for (CurrencyEnum value : CurrencyEnum.values()) {
            entity.setCoin(value.getCode());
            entity.setStatus(1);
            entity.setCheckStatus(1);
            maps.put("recharge"+value.getCode(), withdrawMapper.getTotalRecharge(entity));

            maps.put("withdraw"+value.getCode(), withdrawMapper.getTotalWithdraw(entity));
        }
        return maps;
    }

    @Override
    public BigDecimal rechargeAndWithdrawAmount(Withdraw withdraw) {
        return withdrawMapper.rechargeAndWithdrawAmount(withdraw);
    }


}
