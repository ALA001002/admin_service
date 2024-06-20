package com.bigo.project.bigo.wallet.service.impl;

import com.alibaba.fastjson.JSON;
import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.domain.AssetTransferParam;
import com.bigo.project.bigo.api.domain.CoinExchange;
import com.bigo.project.bigo.api.vo.AccountVO;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.wallet.domain.*;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import com.bigo.project.bigo.wallet.mapper.WalletAddressMapper;
import com.bigo.project.bigo.wallet.mapper.WalletMapper;
import com.bigo.project.bigo.wallet.mapper.WalletTransactionMapper;
import com.bigo.project.bigo.wallet.service.IAssetLogService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/20 10:04
 */
@Slf4j
@Service
public class WalletServiceImpl implements IWalletService {

     protected final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);




    /**
     * BIGO/USDT交易对
     */
    private static String BIGOUSDT = "bigousdt";

    @Autowired
    private RedisCache redisCache;

    @Resource
    private WalletMapper walletMapper;

    @Resource
    private WalletAddressMapper walletAddressMapper;

    @Autowired
    private IAssetLogService assetLogService;

    @Autowired
    private IWithdrawService withdrawService;

    @Resource
    private WalletTransactionMapper walletTransactionMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addWallet(Long uid) {
        List<Wallet> walletList = Lists.newArrayList();
        List<WalletAddress> addresstList = Lists.newArrayList();
        //每个币种一个钱包
        logger.info("=========每个币种一个钱包：==============");
        Wallet wallet = new Wallet();
        wallet.setUid(uid);
        wallet.setType(0);
        wallet.setCurrency(CurrencyEnum.USDT.getCode());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFrozen(BigDecimal.ZERO);
        wallet.setOrder(getOrderByCurrency(CurrencyEnum.USDT.getCode()));
        walletList.add(wallet);
        WalletAddress address = new WalletAddress();
        address.setUid(uid);
        address.setCoin(CurrencyEnum.USDT.getCode());
        addresstList.add(address);
        for (SymbolEnum symbolEnum : SymbolEnum.values()) {
            String coin = symbolEnum.getCoin().toUpperCase();
            wallet = new Wallet();
            wallet.setUid(uid);
            wallet.setType(0);
            wallet.setCurrency(coin);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozen(BigDecimal.ZERO);
            wallet.setOrder(getOrderByCurrency(coin));
            walletList.add(wallet);
            if(coin.equals(CurrencyEnum.ETH.getCode()) || coin.equals(CurrencyEnum.BTC.getCode())) {
                address = new WalletAddress();
                address.setUid(uid);
                address.setCoin(coin);
                if (coin.equals(CurrencyEnum.BTC)) {
                    address.setError("-1");
                }
                addresstList.add(address);
            }
        }
        if(addresstList != null && addresstList.size() >0) walletAddressMapper.batchInsert(addresstList);
        return walletMapper.batchInsert(walletList);
    }

    @Override
    public void complementWallet(BigoUserEntity userEntity) {
        List<Wallet> walletList = Lists.newArrayList();

        for (SymbolEnum symbolEnum : SymbolEnum.values()) {
            String coin = symbolEnum.getCoin().toUpperCase();
            Wallet param = new Wallet(userEntity.getUid(), coin, WalletTypeEnum.CAPITAL_ACCOUNT.getType());
            Wallet wallet = getWallet(param);
            if(wallet != null) continue;
            log.info("uid={}, coin = {}", userEntity.getUid(),coin);
            wallet = new Wallet();
            wallet.setUid(userEntity.getUid());
            wallet.setType(0);
            wallet.setCurrency(coin);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozen(BigDecimal.ZERO);
            wallet.setOrder(getOrderByCurrency(coin));
            walletList.add(wallet);
        }
        if(walletList !=null && walletList.size() >0) walletMapper.batchInsert(walletList);
    }

    @Override
    public List<Map> countUserInfo(List<Long> uidList, Integer authStatus) {
        return walletMapper.countUserInfo(uidList,authStatus);

    }

    @Override
    public void converts(Wallet wallet) {
        walletMapper.converts(wallet);
    }

    @Override
    @Transactional
    public void releaseCurrency() {
        log.info("==================开始释放持有下架币种==================");
        //查询持有币种的用户
        WalletEntity param = new WalletEntity();
        param.setCurrency("OOB");
        param.setType(0);
        List<WalletEntity> entityList = walletMapper.releaseWalletList(param);
        for (WalletEntity releaseEntity : entityList) {
            WalletEntity entity = new WalletEntity();
            entity.setCurrency(CurrencyEnum.USDT.getCode());
            entity.setUid(releaseEntity.getUid());
            log.info("========用户UID:{}==================",releaseEntity.getUid());
            WalletEntity usdtEntity =  walletMapper.releaseWallet(entity);
            BigDecimal usdtBalance = releaseEntity.getBalance().multiply(new BigDecimal(0.002));
            //增加usdt余额
            this.lockChange(usdtBalance, CurrencyEnum.USDT.getCode(),
                    releaseEntity.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.RELEASE);
            //释放持有币种
            this.lockChange(releaseEntity.getBalance(), "OOB",
                    releaseEntity.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.RELEASE);

        }
        log.info("==================结束释放持有下架币种==================");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addWallet(Wallet wallet) {
        List<Wallet> walletList = Lists.newArrayList();
        walletList.add(wallet);
        return walletMapper.batchInsert(walletList);
    }

    @Override
    public List<AccountVO> listAccount(Long uid) {
        Wallet param = new Wallet();
        param.setUid(uid);
        param.setType(WalletTypeEnum.CAPITAL_ACCOUNT.getType());
        return walletMapper.listWallet(param);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean transferAsset(AssetTransferParam transferParam) {
        Wallet param = new Wallet();
        param.setUid(transferParam.getUid());
        param.setCurrency(transferParam.getCurrency());
        param.setType(transferParam.getFromType());
        Wallet fromWallet = walletMapper.getForUpdate(param);
        if(fromWallet == null){
            throw new CustomException("from_account_does_not_exist");
        }
        if(fromWallet.getBalance().compareTo(transferParam.getAmount()) < 0){
            throw new CustomException("account_balance_is_not_enough");
        }
        param.setType(transferParam.getToType());
        Wallet toWallet = walletMapper.getForUpdate(param);
        if(toWallet == null){
            throw new CustomException("to_account_does_not_exist");
        }
        AssetChange change = AssetChange.builder()
                .amount(transferParam.getAmount())
                .build();
        //减少转出钱包余额
        change.setWalletId(fromWallet.getId());
        walletMapper.subBalance(change);
        assetLogService.insertAssetLog(fromWallet,transferParam.getAmount(),1, AssetLogTypeEnum.ASSET_TRANSFER, AssetLogSubTypeEnum.OUT);
        //增加转入钱包余额
        change.setWalletId(toWallet.getId());
        walletMapper.addBalance(change);
        assetLogService.insertAssetLog(toWallet,transferParam.getAmount(),0, AssetLogTypeEnum.ASSET_TRANSFER, AssetLogSubTypeEnum.IN);
        redisCache.deleteObject(transferParam.getUid()+"_accountInfo");
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public void lockChange(BigDecimal amount, String currencyCode, Long userId, int type, int dim, AssetLogTypeEnum assetLogTypeEnum) {
        lockChange(amount, currencyCode, userId, type, dim, assetLogTypeEnum, null);
    }

    @Override
    @Transactional
    public void lockChange(BigDecimal amount, String currencyCode, Long userId, int type, int dim, AssetLogTypeEnum assetLogTypeEnum, AssetLogSubTypeEnum assetLogSubTypeEnum) {
        Wallet paramsWallet = new Wallet();
        paramsWallet.setCurrency(currencyCode);
        paramsWallet.setUid(userId);
        paramsWallet.setType(type);
        Wallet wallet = this.getWallet(paramsWallet);
        AssetChange change = AssetChange.builder().uid(wallet.getUid())
                .currency(wallet.getCurrency())
                .dim(dim)
                .type(assetLogTypeEnum)
                .subType(assetLogSubTypeEnum)
                .walletType(wallet.getType())
                .amount(amount)
                .build();
        change.setWalletId(wallet.getId());
        logger.info("changeAsset={}，wallet={}", JSON.toJSONString(change), JSON.toJSONString(wallet));
        if (wallet == null) {
            throw new CustomException("account_does_not_exist");
        }
        change(change, wallet);
    }



    @Transactional
    public void change(AssetChange change, Wallet wallet) {
        // 金额计算
        BigDecimal updateBalance = BigDecimal.ZERO;
        int result;
        if (change.getAmountType() != null && change.getAmountType() == AmountTypeEnum.FROZEN.getType()) {    // 金额是冻结类型，
            if (change.getDim() == 0) {
                updateBalance = wallet.getFrozen().add(change.getAmount());
                wallet.setChangeAmount(updateBalance);
                result = walletMapper.changeLockFrozen(wallet);
            } else {
                if (wallet.getFrozen().compareTo(change.getAmount()) < 0)
                    throw new CustomException("account_frozen_is_not_enough");
                updateBalance = wallet.getFrozen().subtract(change.getAmount());
                wallet.setChangeAmount(updateBalance);
                result = walletMapper.changeLockFrozen(wallet);
            }
        } else {    // 正常金额类型
            if (change.getDim() == 0) {
                updateBalance = wallet.getBalance().add(change.getAmount());
                wallet.setChangeAmount(updateBalance);
                result = walletMapper.changeLockBalance(wallet);
            } else {
                if (wallet.getBalance().compareTo(change.getAmount()) < 0)
                    throw new CustomException("account_balance_is_not_enough");
                updateBalance = wallet.getBalance().subtract(change.getAmount());
                wallet.setChangeAmount(updateBalance);
                result = walletMapper.changeLockBalance(wallet);
            }
        }
        if (result < 1) {
            logger.info("资金变更-更新余额钱包失败：uid={}, walletId={}, amount={}", change.getUid(), wallet.getId(), change.getAmount());
            throw new CustomException("operation_failed_please_try_again");//操作失败，请重试
        }

        int status = assetLogService.saveAssetLog(wallet, change.getAmount(), change.getDim(), change.getType(), change.getSubType(), null);
        if (status < 1) {
            logger.info("资金变更-添加资金变更记录失败：uid={}, walletId={}, amount={}", change.getUid(), wallet.getId(), change.getAmount());
            throw new CustomException("operation_failed_please_try_again");//操作失败，请重试
        }

//        walletCacheService.add(wallet.getUid());
        redisCache.deleteObject(wallet.getUid() + "_accountInfo");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeAsset(AssetChange change, Long contractId) {
        Wallet param = new Wallet();
        param.setUid(change.getUid());
        param.setCurrency(change.getCurrency());
        param.setType(change.getWalletType());
        Wallet wallet = walletMapper.getForUpdate(param);
        if(wallet == null){
            throw new CustomException("account_does_not_exist");
        }
        change.setWalletId(wallet.getId());
        Boolean result;
        if(change.getAmountType() != null && change.getAmountType() == AmountTypeEnum.FROZEN.getType()){    // 金额是冻结类型，
            if (change.getDim() == 0) {
                result = walletMapper.addFrozen(change) > 0;
            } else {
                if (wallet.getFrozen().compareTo(change.getAmount()) < 0) throw new CustomException("account_frozen_is_not_enough");
                result = walletMapper.subFrozen(change) > 0;
            }
        } else {    // 正常金额类型
            if (change.getDim() == 0) {
                result = walletMapper.addBalance(change) > 0;
            } else {
                if (wallet.getBalance().compareTo(change.getAmount()) < 0) throw new CustomException("account_balance_is_not_enough");
                result = walletMapper.subBalance(change) > 0;
            }
        }
        assetLogService.insertAssetLog(wallet, change.getAmount(),change.getDim(), change.getType(), change.getSubType(), contractId);
        redisCache.deleteObject(wallet.getUid()+"_accountInfo");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeAsset(AssetChange change) {
        return changeAsset(change, null);
    }

    @Override
    public String getWalletAddress(WalletAddress address) {
        return walletAddressMapper.getAddressByCoin(address);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean quickExchange(CoinExchange exchange) {
        String from = exchange.getFrom().toUpperCase();
        String to = exchange.getTo().toUpperCase();
//        if(!to.equals(CurrencyEnum.USDT.getCode())
//                && !to.equals(CurrencyEnum.ETH.getCode())
//                && !to.equals(CurrencyEnum.BTC.getCode())) {
//            throw new CustomException("unsupported_coin");
//        }
        String symbol = null;
        // 计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;
        if(from.equals(CurrencyEnum.USDT.getCode())) {
            symbol = to +""+from;
            symbol = symbol.toLowerCase();
            price = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            totalAmount = exchange.getAmount().divide(price, 8, RoundingMode.HALF_UP);
        }else if(to.equals(CurrencyEnum.USDT.getCode())) {
            symbol = from+""+to;
            symbol = symbol.toLowerCase();
            price = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            totalAmount = exchange.getAmount().multiply(price).setScale(8, RoundingMode.HALF_UP);
        }else {
            throw new CustomException("exchange_currency_not_supported");
        }

            //转出币种余额扣减
        AssetChange outChange = AssetChange.builder().uid(exchange.getUid())
                .currency(exchange.getFrom().toUpperCase())
                .dim(1)
                .type(AssetLogTypeEnum.COIN_EXCHANGE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(exchange.getAmount())
                .build();
        this.changeAsset(outChange);

        //转入币种余额增加
        AssetChange inChange = AssetChange.builder().uid(exchange.getUid())
                .currency(exchange.getTo().toUpperCase())
                .dim(0)
                .type(AssetLogTypeEnum.COIN_EXCHANGE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(totalAmount)
                .build();
        this.changeAsset(inChange);

        return true;
    }

    public static void main(String[] args) {
//        BigDecimal price = new BigDecimal(1.2).setScale(8,BigDecimal.ROUND_HALF_UP);
//        BigDecimal number = new BigDecimal(100.00).setScale(8,BigDecimal.ROUND_HALF_UP);
//        System.out.println(number.divide(price).setScale(8,BigDecimal.ROUND_HALF_UP));
//        System.out.println(number.multiply(price).setScale(8,BigDecimal.ROUND_HALF_UP));
        String to="USDT";
        String c = "USDT,ETH,BTC";
        System.out.println(c.startsWith(to));
    }

    @Override
    public Wallet getWallet(Wallet wallet) {
        return walletMapper.getWallet(wallet);
    }

    @Override
    @Transactional
    public void addBalance(BigoUser user, WalletEntity entity, Wallet wallet) {
        if(entity.getEditType() == AssetLogTypeEnum.INSIDE_RECHARGE.getType() && entity.getEditRelocateType() == 1) { //增加，正常余额
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.INSIDE_RECHARGE)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            this.changeAsset(change);
//            if(user.getStatus() == 0) {
//                this.insertWithdraw(wallet, entity,6);
//            }
        }else if(entity.getEditType() == AssetLogTypeEnum.INTERNAL_DEDUCTION.getType() && entity.getEditRelocateType() == 1) {//扣除，正常余额
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.INTERNAL_DEDUCTION)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            this.changeAsset(change);
        }else if(entity.getEditType() == AssetLogTypeEnum.INSIDE_RECHARGE.getType() && entity.getEditRelocateType() == 2) { //增加，冻结余额
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.INSIDE_RECHARGE)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .amountType(1)
                    .build();
            this.changeAsset(change);
        }else if(entity.getEditType() == AssetLogTypeEnum.INTERNAL_DEDUCTION.getType() && entity.getEditRelocateType() == 2) {//扣除，正常余额
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.INTERNAL_DEDUCTION)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .amountType(1)
                    .build();
            this.changeAsset(change);
        }else if(entity.getEditType() == AssetLogTypeEnum.FROZEN.getType() ) {// 锁仓
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.FROZEN)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            this.changeAsset(change);
            change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.FROZEN)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .amountType(1)
                    .build();
            this.changeAsset(change);
        }else if(entity.getEditType() == AssetLogTypeEnum.RELEASE.getType() ) {// 锁仓
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.RELEASE)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .amountType(1)
                    .build();
            this.changeAsset(change);
             change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.RELEASE)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            this.changeAsset(change);

        }else if(entity.getEditType() == 11) { //模拟实冲
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.CASH_IN)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            this.changeAsset(change);
            this.insertWithdraw(wallet, entity,4);
//            if(user.getStatus() == 0) {
//            }
        }
    }

    public void insertWithdraw(Wallet wallet,WalletEntity entity,int type) {
        Withdraw withdraw = new Withdraw();
        withdraw.setUid(wallet.getUid());
        withdraw.setCoin(wallet.getCurrency());
        withdraw.setMoney(entity.getAmount());
        withdraw.setStatus(1);
        withdraw.setCheckStatus(1);
        withdraw.setVerifyTime(new Date());
        withdraw.setType(type);
        withdraw.setCreateTime(new Date());
        withdraw.setRemark("Manual deposit");
        withdrawService.insert(withdraw);
    }

    @Override
    public List<Wallet> listForzenWallet(Wallet entity) {
        return walletMapper.listForzenWallet(entity);
    }

    @Override
    @Transactional
    public void release(Wallet wallet) {
        AssetChange releaseChange = AssetChange.builder().uid(wallet.getUid())    //增加余额
                .currency(wallet.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(wallet.getFrozen())
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        this.changeAsset(releaseChange);
        AssetChange releaseFrozenChange = AssetChange.builder().uid(wallet.getUid())    // 扣除冻结余额
                .currency(wallet.getCurrency())
                .dim(1)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(wallet.getFrozen())
                .amountType(AmountTypeEnum.FROZEN.getType())
                .build();
        this.changeAsset(releaseFrozenChange);
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean quickExchange(CoinExchange exchange, Integer type) {
        BigDecimal min = CoinUtils.getExchangeMinByCoin(exchange.getFrom().toLowerCase());
        if (exchange.getAmount().compareTo(min) < 0) {
            throw new CustomException("illegal_exchange_quantity");
        }
        // 获取交易对价格
        String symbol = "";
        if (exchange.getFrom().toUpperCase().equals(CurrencyEnum.BTC.getCode()) || exchange.getTo().toUpperCase().equals(CurrencyEnum.BTC.getCode())) {
            symbol = SymbolEnum.BTCUSDT.getCode();
        } else {
            symbol = SymbolEnum.ETHUSDT.getCode();
        }
        BigDecimal price = MarketSituationUtils.getCurrentPriceBySymbol(symbol);

        // 计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (CurrencyEnum.USDT.getCode().equals(exchange.getTo().toUpperCase())) {
            totalAmount = exchange.getAmount().multiply(price).setScale(8, RoundingMode.HALF_UP);
        } else if (CurrencyEnum.ETH.getCode().equals(exchange.getTo().toUpperCase())
                || CurrencyEnum.BTC.getCode().equals(exchange.getTo().toUpperCase())) {
            totalAmount = exchange.getAmount().divide(price, 8, RoundingMode.HALF_UP);
        } else {
            throw new CustomException("exchange_currency_not_supported");
        }
        //闪兑手续费率
//        BigDecimal exchangeRate = CoinUtils.getExchangeRate();
//        BigDecimal fee = totalAmount.multiply(exchangeRate).setScale(8, RoundingMode.HALF_UP);
        // 最终价格 = 总价 - 手续费
//        BigDecimal receiveAmount = totalAmount.subtract(fee);
        BigDecimal receiveAmount = totalAmount;
        //转出币种余额扣减
        AssetChange outChange = AssetChange.builder().uid(exchange.getUid())
                .currency(exchange.getFrom().toUpperCase())
                .dim(1)
                .type(AssetLogTypeEnum.COIN_EXCHANGE)
//                .subType(getSubTypeEnumByCoin(exchange.getTo(), exchange.getFrom()))
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(exchange.getAmount())
                .build();
        this.changeAsset(outChange);
        //转入币种余额增加
        AssetChange inChange = AssetChange.builder().uid(exchange.getUid())
                .currency(exchange.getTo().toUpperCase())
                .dim(0)
                .type(AssetLogTypeEnum.COIN_EXCHANGE)
//                .subType(getSubTypeEnumByCoin(exchange.getTo(), exchange.getFrom()))
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(receiveAmount)
                .build();
        this.changeAsset(inChange);
        return Boolean.TRUE;
    }





    @Override
    public List<WalletEntity> listByEntity(WalletEntity entity) {
        return walletMapper.listByEntity(entity);
    }

    @Override
    public Integer countUserHasMoney(String uids) {
        return walletMapper.countUserHasMoney(uids);
    }

    @Override
    public Wallet getById(Long id) {
        return walletMapper.getById(id);
    }






    private Integer getOrderByCurrency(CurrencyEnum currency){
        Integer order = 0;
        switch (currency) {
            case USDT:
                order = 0;
                break;
            case ETH:
                order = 1;
                break;
//            case DIEM:
//                order = 3;
//                break;
            case BTC:
                order = 4;
                break;
            default:
                order = 99;
        }
        return order;
    }
    private Integer getOrderByCurrency(String code){
        Integer order = 0;
        switch (code) {
            case "USDT":
                order = 0;
                break;
            case "ETH":
                order = 1;
                break;
            case "BTC":
                order = 3;
                break;
            case "GMO":
                order = 4;
                break;
            case "MED":
                order = 5;
                break;
            case "IPCC":
                order = 6;
                break;
            case "BLZ":
                order = 7;
                break;
            case "NEV":
                order = 8;
                break;
            case "SGT":
                order = 9;
                break;
            default:
                order = 99;
        }
        return order;
    }

//    public static TrxResult transfer(TransferData transferData){
////        Map<String,Object> param = new HashMap<>();
////        param.put("price",transferData.getPrice());
////        param.put("address",transferData.getAddress());
////        param.put("withdrawId",transferData.getWithdrawId());
////        param.put("symbol",transferData.getSymbol());
//        String result = OkHttpUtil.postJsonParams(trxService+"/api/transfer",JSON.toJSONString(transferData));
//        log.info("transferToUser={}",result);
//        TrxResult trxResult = JSON.parseObject(result, TrxResult.class);
//        return trxResult;
//    }
}
