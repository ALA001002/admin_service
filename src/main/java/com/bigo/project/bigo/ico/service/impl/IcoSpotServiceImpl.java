package com.bigo.project.bigo.ico.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.common.utils.RandomNumberUtils;
import com.bigo.project.bigo.api.dto.IcoSpotDTO;
import com.bigo.project.bigo.api.vo.ico.IcoSpotVO;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.ico.enums.SpotStatusEnum;
import com.bigo.project.bigo.ico.enums.SpotTypeEnum;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.service.AsyncService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.ico.mapper.IcoSpotMapper;
import com.bigo.project.bigo.ico.domain.IcoSpot;
import com.bigo.project.bigo.ico.service.IIcoSpotService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 现货交易记录Service业务层处理
 * 
 * @author bigo
 * @date 2023-03-14
 */
@Slf4j
@Service
public class IcoSpotServiceImpl implements IIcoSpotService 
{
    @Autowired
    private IcoSpotMapper icoSpotMapper;

    @Autowired
    private IWalletService walletService;

    @Resource
    AsyncService asyncService;

    /**
     * 查询现货交易记录
     * 
     * @param id 现货交易记录ID
     * @return 现货交易记录
     */
    @Override
    public IcoSpot selectIcoSpotById(Long id)
    {
        return icoSpotMapper.selectIcoSpotById(id);
    }

    @Override
    public IcoSpot selectIcoSpot(IcoSpot icoSpot) {
        return icoSpotMapper.selectIcoSpot(icoSpot);
    }

    /**
     * 撤单
     * @param spot
     */
    @Override
    public void revokeOrder(IcoSpot spot) {
        IcoSpot updateSpot = new IcoSpot();
        updateSpot.setId(spot.getId());
        updateSpot.setStatus(SpotStatusEnum.CANCELED.getStatus());
        updateSpot.setOldStatus(SpotStatusEnum.NEW.getStatus());
        updateSpot.setUpdateTime(new Date());
        icoSpotMapper.revokeOrder(updateSpot);
    }

    @Override
    public List<IcoSpotVO> selectIcoSpotVOList(IcoSpotVO param) {
        return icoSpotMapper.selectIcoSpotVOList(param);
    }

    @Override
    public Long sumTradeUserNum(Map params) {
        return icoSpotMapper.sumTradeUserNum(params);
    }

    @Override
    public List<IcoSpot> selectTradeUser() {
        return icoSpotMapper.selectTradeUser();
    }

    @Override
    public BigDecimal getTradeAmount(IcoSpot param) {
        return icoSpotMapper.getTradeAmount(param);
    }

    /**
     * 查询现货交易记录列表
     * 
     * @param icoSpot 现货交易记录
     * @return 现货交易记录
     */
    @Override
    public List<IcoSpot> selectIcoSpotList(IcoSpot icoSpot)
    {
        return icoSpotMapper.selectIcoSpotList(icoSpot);
    }

    /**
     * 新增现货交易记录
     * 
     * @param icoSpot 现货交易记录
     * @return 结果
     */
    @Override
    public int insertIcoSpot(IcoSpot icoSpot)
    {
        icoSpot.setCreateTime(DateUtils.getNowDate());
        return icoSpotMapper.insertIcoSpot(icoSpot);
    }

    /**
     * 修改现货交易记录
     * 
     * @param icoSpot 现货交易记录
     * @return 结果
     */
    @Override
    public int updateIcoSpot(IcoSpot icoSpot)
    {
        icoSpot.setUpdateTime(DateUtils.getNowDate());
        return icoSpotMapper.updateIcoSpot(icoSpot);
    }

    /**
     * 批量删除现货交易记录
     * 
     * @param ids 需要删除的现货交易记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoSpotByIds(Long[] ids)
    {
        return icoSpotMapper.deleteIcoSpotByIds(ids);
    }

    /**
     * 删除现货交易记录信息
     * 
     * @param id 现货交易记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoSpotById(Long id)
    {
        return icoSpotMapper.deleteIcoSpotById(id);
    }

    @Override
    @Transactional
    public void place(IcoSpotDTO dto, BigoUser user) {
        String orderType = dto.getOrderType();
        SpotTypeEnum typeEnum = SpotTypeEnum.getTypeByEnum(orderType);
        switch (typeEnum) {
            case MARKET:
                marketPlace(typeEnum, dto, user);
                return;
            case LIMIT:
                limitPlace(typeEnum, dto, user);
                return;
            default:
                return;
        }
    }

    /**
     * 限价单
     * @param typeEnum
     * @param dto
     * @param user
     * @return
     */
    @Transactional
    public void limitPlace(SpotTypeEnum typeEnum, IcoSpotDTO dto, BigoUser user) {
        String side = dto.getSide();
        SymbolEnum symbolEnum = SymbolEnum.getCodeByEnum(dto.getSymbol().toLowerCase());
        if(symbolEnum == null) {
            throw new CustomException("failed_to_get_transaction_price");
        }
        BigDecimal symbolPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbolEnum.getCode());
        if("BUY".equals(side)) {
            limitBuy(typeEnum, dto, user,symbolPrice,symbolEnum);
        }else {
            limitSell(typeEnum, dto, user,symbolPrice,symbolEnum);
        }
    }

    /**
     * 限价单-买
     * @param typeEnum
     * @param dto
     * @param user
     * @param symbolPrice
     * @param symbolEnum
     */
    @Transactional
    public void limitBuy(SpotTypeEnum typeEnum, IcoSpotDTO dto, BigoUser user, BigDecimal symbolPrice, SymbolEnum symbolEnum) {
        String orderId = RandomNumberUtils.getRandom("L_buy")+""+RandomNumberUtils.genCaptcha();

        BigDecimal price = dto.getPrice(); //价格
        BigDecimal quantity = dto.getQuantity(); //数量

        IcoSpot spot = new IcoSpot();
        spot.setOrderId(orderId);
        spot.setUid(user.getUid());
        spot.setSide(dto.getSide());
        spot.setSymbol(dto.getSymbol().toUpperCase());
        spot.setOrderType(typeEnum.getType());
        spot.setFee(BigDecimal.ZERO);
        spot.setBaseCoin(symbolEnum.getCoin().toUpperCase());
        spot.setQuoteCoin(CurrencyEnum.USDT.getCode());

        if(price.compareTo(symbolPrice) >=0 ){ //如果购入价大于行情价，立马成交
            // 计算购买数量价格
            BigDecimal buyAmount = quantity.multiply(symbolPrice).setScale(6, BigDecimal.ROUND_HALF_UP); // 购买金额
            // 扣除金额
            walletService.lockChange(buyAmount, CurrencyEnum.USDT.getCode(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.ICO);
            //增加对应币种金额
            walletService.lockChange(quantity, symbolEnum.getCoin().toUpperCase(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.ICO);
            //增加委托记录
            spot.setStatus(SpotStatusEnum.FILLED.getStatus());
            spot.setOrigQty(quantity);
            spot.setExecutedQty(quantity);
            spot.setExecutedPrice(symbolPrice);
            spot.setPrice(price);
            spot.setExecutedQuoteQty(buyAmount);
            spot.setCreateTime(new Date());
            spot.setUpdateTime(new Date());
            spot.setWorkingTime(new Date());
            icoSpotMapper.insertIcoSpot(spot);
        }else {
            //增加委托记录
            spot.setStatus(SpotStatusEnum.NEW.getStatus());
            spot.setOrigQty(quantity);
            spot.setExecutedQty(BigDecimal.ZERO);
            spot.setExecutedPrice(BigDecimal.ZERO);
            spot.setPrice(price);
            spot.setExecutedQuoteQty(BigDecimal.ZERO);
            spot.setCreateTime(new Date());
            icoSpotMapper.insertIcoSpot(spot);
            Wallet wallet = walletService.getWallet(new Wallet(user.getUid(), CurrencyEnum.USDT.getCode(), WalletTypeEnum.CAPITAL_ACCOUNT.getType()));
            if(wallet == null || wallet.getBalance().compareTo(BigDecimal.ZERO) <= 0){
                throw new CustomException("account_frozen_is_not_enough");
            }
            BigDecimal amount = quantity.multiply(price).setScale(6, BigDecimal.ROUND_HALF_UP);
            if(amount.compareTo(wallet.getBalance()) > 0) {
                throw new CustomException("account_frozen_is_not_enough");
            }
        }
    }

    @Override
    @Transactional
    public void commissioningBuy(IcoSpot spot) {
        BigDecimal price = spot.getPrice();
        BigDecimal quantity = spot.getOrigQty(); //数量

        String symbol = spot.getSymbol().toLowerCase();
        BigDecimal symbolPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
//        BigDecimal symbolPrice = new BigDecimal(41063);
        if(symbolPrice.compareTo(price) > 0) return;
        //计算购买价格
        BigDecimal buyAmount = symbolPrice.multiply(quantity).setScale(6, BigDecimal.ROUND_HALF_UP);
        Wallet wallet = walletService.getWallet(new Wallet(spot.getUid(), spot.getQuoteCoin(), WalletTypeEnum.CAPITAL_ACCOUNT.getType()));
        if(wallet == null || wallet.getBalance().compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal subAmount = BigDecimal.ZERO;
        IcoSpot updateSpot = new IcoSpot();
        if(wallet.getBalance().compareTo(buyAmount) >= 0) {//全款买入
            // 扣除金额
            subAmount = buyAmount;
        } else {
            quantity = wallet.getBalance().divide(symbolPrice, 6, BigDecimal.ROUND_HALF_UP).setScale(6, BigDecimal.ROUND_HALF_UP);
            subAmount = wallet.getBalance();
        }
        walletService.lockChange(subAmount, CurrencyEnum.USDT.getCode(),
                spot.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.ICO);
        //增加对应币种金额
        walletService.lockChange(quantity, spot.getBaseCoin().toUpperCase(),
                spot.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.ICO);

        updateSpot.setId(spot.getId());
        updateSpot.setOldStatus(SpotStatusEnum.NEW.getStatus());
        updateSpot.setStatus(SpotStatusEnum.FILLED.getStatus());
        updateSpot.setExecutedQty(quantity);
        updateSpot.setExecutedPrice(symbolPrice);
        updateSpot.setExecutedQuoteQty(subAmount);
        updateSpot.setUpdateTime(new Date());
        updateSpot.setWorkingTime(new Date());
        int status = icoSpotMapper.updateIcoSpot(updateSpot);
        if(status < 1) {
            throw new CustomException("更新委托订单失败：orderId="+spot.getOrderId());
        }
    }


    /**
     * 限价单-卖
     * @param typeEnum
     * @param dto
     * @param user
     * @param symbolPrice
     * @param symbolEnum
     */
    @Transactional
    public void limitSell(SpotTypeEnum typeEnum, IcoSpotDTO dto, BigoUser user, BigDecimal symbolPrice, SymbolEnum symbolEnum) {
        String orderId = RandomNumberUtils.getRandom("L_sell")+""+RandomNumberUtils.genCaptcha();

        BigDecimal price = dto.getPrice(); //价格
        BigDecimal quantity = dto.getQuantity(); //数量

        IcoSpot spot = new IcoSpot();
        spot.setOrderId(orderId);
        spot.setUid(user.getUid());
        spot.setSide(dto.getSide());
        spot.setSymbol(dto.getSymbol().toUpperCase());
        spot.setOrderType(typeEnum.getType());
        spot.setBaseCoin(symbolEnum.getCoin().toUpperCase());
        spot.setQuoteCoin(CurrencyEnum.USDT.getCode());

        if(symbolPrice.compareTo(price) >= 0 ){ //如果行情价大于购入价，立马成交
            // 计算卖出数量价格
            BigDecimal sellAmount = quantity.multiply(symbolPrice).setScale(6, BigDecimal.ROUND_HALF_UP); // 卖出金额

            //增加对应币种金额
            walletService.lockChange(quantity, symbolEnum.getCoin().toUpperCase(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.ICO);
            // 扣除金额
            walletService.lockChange(sellAmount, CurrencyEnum.USDT.getCode(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.ICO);

            int tradeFee = ConfigSettingUtil.getTradeFeeRebate();
            BigDecimal feeAmount = sellAmount.multiply(new BigDecimal(tradeFee).divide(new BigDecimal(100))).setScale(4, BigDecimal.ROUND_HALF_UP);

            //增加委托记录
            spot.setFee(feeAmount);
            spot.setStatus(SpotStatusEnum.FILLED.getStatus());
            spot.setOrigQty(quantity);
            spot.setExecutedQty(quantity);
            spot.setExecutedPrice(symbolPrice);
            spot.setPrice(price);
            spot.setExecutedQuoteQty(sellAmount);
            spot.setCreateTime(new Date());
            spot.setUpdateTime(new Date());
            spot.setWorkingTime(new Date());
            icoSpotMapper.insertIcoSpot(spot);

            int rebateStatus = ConfigSettingUtil.getRebateStatus();
            int mostRebateLevel = ConfigSettingUtil.getMostRebateLevel();
            walletService.lockChange(feeAmount, CurrencyEnum.USDT.getCode(),
                    user.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.FEE);
            if(1 == rebateStatus){ // 开启分销
                if(mostRebateLevel >= 1 && feeAmount !=null && feeAmount.compareTo(BigDecimal.ZERO) > 0) { //最高分销层级要大于1层
                    log.info("开始处理分销逻辑");
                    asyncService.levelRebate(user.getUid(), feeAmount);
                    log.info("结束处理分销逻辑");
                }
            }
        }else {
            //增加委托记录
            spot.setStatus(SpotStatusEnum.NEW.getStatus());
            spot.setOrigQty(quantity);
            spot.setExecutedQty(BigDecimal.ZERO);
            spot.setExecutedPrice(BigDecimal.ZERO);
            spot.setPrice(price);
            spot.setExecutedQuoteQty(BigDecimal.ZERO);
            spot.setFee(BigDecimal.ZERO);
            spot.setCreateTime(new Date());
            icoSpotMapper.insertIcoSpot(spot);

            Wallet wallet = walletService.getWallet(new Wallet(user.getUid(), symbolEnum.getCoin().toUpperCase(), WalletTypeEnum.CAPITAL_ACCOUNT.getType()));
            if(wallet == null || wallet.getBalance().compareTo(BigDecimal.ZERO) <= 0){
                throw new CustomException("account_frozen_is_not_enough");
            }
            if(quantity.compareTo(wallet.getBalance()) > 0) {
                throw new CustomException("account_frozen_is_not_enough");
            }
        }
    }

    @Override
    @Transactional
    public void commissioningSell(IcoSpot spot) {
        BigDecimal price = spot.getPrice();
        BigDecimal quantity = spot.getOrigQty(); //数量

        String symbol = spot.getSymbol().toLowerCase();

        BigDecimal symbolPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
//        BigDecimal symbolPrice = new BigDecimal(43065.990000);
        if(symbolPrice.compareTo(price) < 0) return;
        //计算卖出USDT价格
        BigDecimal sellAmount = symbolPrice.multiply(quantity).setScale(6, BigDecimal.ROUND_HALF_UP);
        Wallet wallet = walletService.getWallet(new Wallet(spot.getUid(), spot.getBaseCoin(), WalletTypeEnum.CAPITAL_ACCOUNT.getType()));
        if(wallet == null || wallet.getBalance().compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal addAmount = BigDecimal.ZERO;
        IcoSpot updateSpot = new IcoSpot();
        if(wallet.getBalance().compareTo(quantity) >= 0) {//全款买入
            // 扣除金额
            addAmount = sellAmount;
        } else {
            addAmount = wallet.getBalance().multiply(symbolPrice).setScale(6, BigDecimal.ROUND_HALF_UP);
            quantity = wallet.getBalance();
        }
        walletService.lockChange(addAmount, CurrencyEnum.USDT.getCode(),
                spot.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.ICO);

        //增加对应币种金额
        walletService.lockChange(quantity, spot.getBaseCoin().toUpperCase(),
                spot.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.ICO);

        int tradeFee = ConfigSettingUtil.getTradeFeeRebate();
        BigDecimal feeAmount = addAmount.multiply(new BigDecimal(tradeFee).divide(new BigDecimal(100))).setScale(4, BigDecimal.ROUND_HALF_UP);


        updateSpot.setId(spot.getId());
        updateSpot.setStatus(SpotStatusEnum.FILLED.getStatus());
        updateSpot.setOldStatus(SpotStatusEnum.NEW.getStatus());
        updateSpot.setExecutedQty(quantity);
        updateSpot.setExecutedPrice(symbolPrice);
        updateSpot.setExecutedQuoteQty(addAmount);
        updateSpot.setUpdateTime(new Date());
        updateSpot.setWorkingTime(new Date());
        updateSpot.setFee(feeAmount);
        int status = icoSpotMapper.updateIcoSpot(updateSpot);
        if(status < 1) {
            throw new CustomException("更新委托订单失败：orderId="+spot.getOrderId());
        }

        int rebateStatus = ConfigSettingUtil.getRebateStatus();
        int mostRebateLevel = ConfigSettingUtil.getMostRebateLevel();
        walletService.lockChange(feeAmount, CurrencyEnum.USDT.getCode(),
                spot.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.FEE);

        if(1 == rebateStatus){ // 开启分销
            if(mostRebateLevel >= 1 && feeAmount !=null && feeAmount.compareTo(BigDecimal.ZERO) > 0) { //最高分销层级要大于1层
                log.info("开始处理分销逻辑");
                asyncService.levelRebate(spot.getUid(), feeAmount);
                log.info("结束处理分销逻辑");
            }
        }
    }




    /**
     * 市价单
     * @param typeEnum
     * @param dto
     * @param user
     * @return
     */
    @Transactional
    public void marketPlace(SpotTypeEnum typeEnum, IcoSpotDTO dto, BigoUser user) {
        String side = dto.getSide();
        SymbolEnum symbolEnum = SymbolEnum.getCodeByEnum(dto.getSymbol().toLowerCase());
        if(symbolEnum == null) {
            throw new CustomException("failed_to_get_transaction_price");
        }
        BigDecimal symbolPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbolEnum.getCode());
        if("BUY".equals(side)) {
            marketBuy(typeEnum, dto, user,symbolPrice,symbolEnum);
        }else {
            marketSell(typeEnum, dto, user,symbolPrice,symbolEnum);
        }
    }

    @Transactional
    public void marketBuy(SpotTypeEnum typeEnum, IcoSpotDTO dto, BigoUser user, BigDecimal symbolPrice, SymbolEnum symbolEnum) {
        String orderId = RandomNumberUtils.getRandom("M_buy")+""+RandomNumberUtils.genCaptcha();

        IcoSpot spot = new IcoSpot();

        BigDecimal quoteOrderQty = dto.getQuoteOrderQty(); //成交额
        BigDecimal quantity = dto.getQuantity(); //数量
        if(quoteOrderQty != null) {
            // 计算购买数量价格
            BigDecimal buyAmount = quoteOrderQty.divide(symbolPrice, 6, BigDecimal.ROUND_HALF_UP).setScale(6, BigDecimal.ROUND_HALF_UP); // 购买数量
            // 扣除金额
            walletService.lockChange(quoteOrderQty, CurrencyEnum.USDT.getCode(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 1, AssetLogTypeEnum.ICO);

            //增加对应币种金额
            walletService.lockChange(buyAmount, symbolEnum.getCoin().toUpperCase(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 0, AssetLogTypeEnum.ICO);

            spot.setOrigQty(buyAmount);
            spot.setExecutedQty(buyAmount);
        }else if(quantity != null) {
            BigDecimal buyAmount = quantity.multiply(symbolPrice).setScale(6, BigDecimal.ROUND_HALF_UP); // 购买金额
            // 扣除金额
            walletService.lockChange(buyAmount, CurrencyEnum.USDT.getCode(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 1, AssetLogTypeEnum.ICO);
            //增加对应币种金额
            walletService.lockChange(quantity, symbolEnum.getCoin().toUpperCase(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 0, AssetLogTypeEnum.ICO);

            spot.setOrigQty(quantity);
            spot.setExecutedQty(quantity);
        }else {
            return;
        }
        //增加委托记录

        spot.setOrderId(orderId);
        spot.setUid(user.getUid());
        spot.setSide(dto.getSide());
        spot.setSymbol(dto.getSymbol().toUpperCase());
        spot.setOrderType(typeEnum.getType());
        spot.setFee(BigDecimal.ZERO);
        spot.setBaseCoin(symbolEnum.getCoin().toUpperCase());
        spot.setQuoteCoin(CurrencyEnum.USDT.getCode());
        spot.setStatus(SpotStatusEnum.FILLED.getStatus());
        spot.setExecutedPrice(symbolPrice);
        spot.setPrice(BigDecimal.ZERO);
        spot.setExecutedQuoteQty(quoteOrderQty);
        spot.setCreateTime(new Date());
        spot.setUpdateTime(new Date());
        spot.setWorkingTime(new Date());
        icoSpotMapper.insertIcoSpot(spot);
    }

    @Transactional
    public void marketSell(SpotTypeEnum typeEnum, IcoSpotDTO dto, BigoUser user, BigDecimal symbolPrice, SymbolEnum symbolEnum) {
        String orderId = RandomNumberUtils.getRandom("M_sell")+""+RandomNumberUtils.genCaptcha();
        BigDecimal quoteOrderQty = dto.getQuoteOrderQty(); //卖出成交额
        BigDecimal quantity = dto.getQuantity(); //数量

        BigDecimal executedQuoteQty = BigDecimal.ZERO; //成交额
        IcoSpot spot = new IcoSpot();
        if(quoteOrderQty !=null) { //卖出成交额
            // 计算卖出数量价格
            BigDecimal sellAmount = quoteOrderQty.divide(symbolPrice,6,BigDecimal.ROUND_HALF_UP).setScale(6, BigDecimal.ROUND_HALF_UP); // 购买数量
            // 扣除金额
            walletService.lockChange(sellAmount, symbolEnum.getCoin().toUpperCase(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 1, AssetLogTypeEnum.ICO);

            //增加对应币种金额
            walletService.lockChange(quoteOrderQty, CurrencyEnum.USDT.getCode(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 0, AssetLogTypeEnum.ICO);

            executedQuoteQty = quoteOrderQty;
            spot.setOrigQty(sellAmount);
            spot.setExecutedQty(sellAmount);

        } else if(quantity != null) {//卖出数量
            // 计算卖出数量价格
            BigDecimal sellAmount = quantity.multiply(symbolPrice).setScale(6, BigDecimal.ROUND_HALF_UP); // 购买数量
            // 扣除金额
            walletService.lockChange(quantity, symbolEnum.getCoin().toUpperCase(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 1, AssetLogTypeEnum.ICO);

            //增加对应币种金额
            walletService.lockChange(sellAmount, CurrencyEnum.USDT.getCode(),
                    user.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(), 0, AssetLogTypeEnum.ICO);

            executedQuoteQty = sellAmount;
            spot.setOrigQty(quantity);
            spot.setExecutedQty(quantity);
        }else {
            return;
        }

        int tradeFee = ConfigSettingUtil.getTradeFeeRebate();
        BigDecimal feeAmount = executedQuoteQty.multiply(new BigDecimal(tradeFee).divide(new BigDecimal(100))).setScale(4, BigDecimal.ROUND_HALF_UP);

        //增加委托记录

        spot.setOrderId(orderId);
        spot.setUid(user.getUid());
        spot.setSide(dto.getSide());
        spot.setSymbol(dto.getSymbol().toUpperCase());
        spot.setOrderType(typeEnum.getType());
        spot.setFee(BigDecimal.ZERO);
        spot.setBaseCoin(symbolEnum.getCoin().toUpperCase());
        spot.setQuoteCoin(CurrencyEnum.USDT.getCode());
        spot.setStatus(SpotStatusEnum.FILLED.getStatus());
        spot.setExecutedPrice(symbolPrice);
        spot.setPrice(BigDecimal.ZERO);
        spot.setExecutedQuoteQty(executedQuoteQty);
        spot.setFee(feeAmount);
        spot.setCreateTime(new Date());
        spot.setUpdateTime(new Date());
        spot.setWorkingTime(new Date());
        icoSpotMapper.insertIcoSpot(spot);

        int rebateStatus = ConfigSettingUtil.getRebateStatus();
        int mostRebateLevel = ConfigSettingUtil.getMostRebateLevel();
        walletService.lockChange(feeAmount, CurrencyEnum.USDT.getCode(),
                user.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.FEE);
        if(1 == rebateStatus){ // 开启分销
            if(mostRebateLevel >= 1 && feeAmount !=null && feeAmount.compareTo(BigDecimal.ZERO) > 0) { //最高分销层级要大于1层
                log.info("开始处理分销逻辑");
                asyncService.levelRebate(user.getUid(), feeAmount);
                log.info("结束处理分销逻辑");
            }
        }
    }



}
