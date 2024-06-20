package com.bigo.project.bigo.ico.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.project.bigo.api.dto.IcoProductDTO;
import com.bigo.project.bigo.api.vo.ico.IcoProductVO;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.SymbolCoinEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.ico.domain.IcoExchangeHistory;
import com.bigo.project.bigo.ico.domain.IcoProduct;
import com.bigo.project.bigo.ico.domain.IcoProductRecord;
import com.bigo.project.bigo.ico.mapper.IcoProductMapper;
import com.bigo.project.bigo.ico.service.IIcoExchangeHistoryService;
import com.bigo.project.bigo.ico.service.IIcoProductRecordService;
import com.bigo.project.bigo.ico.service.IIcoProductService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.service.AsyncService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ico产品Service业务层处理
 * 
 * @author xx
 * @date 2023-01-07
 */
@Slf4j
@Service
public class IcoProductServiceImpl implements IIcoProductService {
    @Autowired
    private IcoProductMapper icoProductMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IIcoProductRecordService iIcoProductRecordService;

    @Autowired
    private IIcoExchangeHistoryService iIcoExchangeHistoryService;

    @Resource
    AsyncService asyncService;


    /**
     * 查询ico产品
     * 
     * @param id ico产品ID
     * @return ico产品
     */
    @Override
    public IcoProduct selectIcoProductById(Long id) {
        IcoProduct icoProduct = icoProductMapper.selectIcoProductById(id);
        return icoProduct;
    }

    /**
     * 查询ico产品列表
     * 
     * @param icoProduct ico产品
     * @return ico产品
     */
    @Override
    public List<IcoProduct> selectIcoProductList(IcoProduct icoProduct)
    {
        return icoProductMapper.selectIcoProductList(icoProduct);
    }

    /**
     * 新增ico产品
     * 
     * @param icoProduct ico产品
     * @return 结果
     */
    @Override
    public int insertIcoProduct(IcoProduct icoProduct)
    {
        icoProduct.setCreateTime(DateUtils.getNowDate());
        icoProduct.setBuyCurrency(CurrencyEnum.USDT.getCode());
        return icoProductMapper.insertIcoProduct(icoProduct);
    }

    /**
     * 修改ico产品
     * 
     * @param icoProduct ico产品
     * @return 结果
     */
    @Override
    public int updateIcoProduct(IcoProduct icoProduct)
    {
        return icoProductMapper.updateIcoProduct(icoProduct);
    }

    /**
     * 批量删除ico产品
     * 
     * @param ids 需要删除的ico产品ID
     * @return 结果
     */
    @Override
    public int deleteIcoProductByIds(Long[] ids)
    {
        return icoProductMapper.deleteIcoProductByIds(ids);
    }

    /**
     * 删除ico产品信息
     * 
     * @param id ico产品ID
     * @return 结果
     */
    @Override
    public int deleteIcoProductById(Long id)
    {
        return icoProductMapper.deleteIcoProductById(id);
    }

    @Override
    public List<IcoProductVO> selectProductListVO(IcoProductVO icoProductVO) {
        return icoProductMapper.selectProductListVO(icoProductVO);
    }

/*
    @Override
    @Transactional
    public void buyIcoProduct(BigoUser user, IcoProductDTO dto) {
        IcoProduct product = icoProductMapper.selectIcoProductById(dto.getIcoProductId());
        product.setBuyNum(dto.getBuyNum().longValue());
        BigDecimal buyAmount = product.getBuyPrice().multiply(dto.getBuyNum()); // 购买金额
        //减库存
        int status = icoProductMapper.reduceInventory(product);
        if(status < 1) throw new CustomException("please_try_again_later");
        // 扣除余额
        AssetChange subBalanceChange = AssetChange.builder().uid(user.getUid())
                .currency(product.getBuyCurrency())
                .dim(1)
                .type(AssetLogTypeEnum.ICO)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(buyAmount)
                .build();
        walletService.changeAsset(subBalanceChange);
        //查询是否已创建钱包
        Wallet param = new Wallet(user.getUid(), product.getIcoCurrency(), WalletTypeEnum.CAPITAL_ACCOUNT.getType());
        Wallet wallet = walletService.getWallet(param);
        if(wallet == null) {
            //创建新钱包币种
            wallet = new Wallet(user.getUid(), product.getIcoCurrency(), WalletTypeEnum.CAPITAL_ACCOUNT.getType());
            wallet.setUid(user.getUid());
            wallet.setBalance(dto.getBuyNum());
            wallet.setFrozen(BigDecimal.ZERO);
            wallet.setType(WalletTypeEnum.CAPITAL_ACCOUNT.getType());
            wallet.setOrder(0);
            status = walletService.addWallet(wallet);
            if(status < 1) throw new CustomException("please_try_again_later");
        }else {
            //增加ico硬币资金记录
            walletService.lockChange(dto.getBuyNum(),product.getIcoCurrency().toUpperCase(),
                    user.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.ICO);
        }
        IcoProductRecord record = new IcoProductRecord();
        record.setUid(user.getUid());
        record.setAmount(dto.getBuyNum().longValue());
        record.setCurrency(product.getIcoCurrency());
        record.setCreateTime(new Date());
        status = iIcoProductRecordService.insertIcoProductRecord(record);
        if(status < 1) throw new CustomException("please_try_again_later");
    }
*/

    @Override
    @Transactional
    public void buy(BigoUser user, IcoProductDTO dto) {
        String symbol = dto.getSymbol().toLowerCase();
        String coin = SymbolCoinEnum.getCoinByCode(symbol);
        if(coin == null) throw new CustomException("the_operation_cannot_be_performed_at_this_time");
        String currency = coin.toUpperCase();

        BigDecimal buyNum = dto.getBuyNum();// usdt数量

        int status;
        BigDecimal buyPrice = BigDecimal.ZERO;  //购买单价
        BigDecimal buyAmount = BigDecimal.ZERO; //购买金额

//        String symbol =  dto.getTo() +""+ dto.getFrom();
        buyPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
        buyAmount = buyNum.divide(buyPrice, 6 , BigDecimal.ROUND_HALF_UP).setScale(6, BigDecimal.ROUND_HALF_UP); // 购买金额

        /*IcoProduct product = icoProductMapper.selectIcoProduct(new IcoProduct(currency));
        if(product != null) {
            product.setBuyNum(dto.getBuyNum().longValue());
            //减库存
            status = icoProductMapper.reduceInventory(product);
            if(status < 1) throw new CustomException("please_try_again_later");
            IcoProductRecord record = new IcoProductRecord();
            record.setUid(user.getUid());
            record.setAmount(buyAmount.longValue());
            record.setCurrency(product.getIcoCurrency());
            record.setCreateTime(new Date());
            status = iIcoProductRecordService.insertIcoProductRecord(record);
            if(status < 1) throw new CustomException("please_try_again_later");
        }*/

        Wallet param = new Wallet(user.getUid(), currency, WalletTypeEnum.CAPITAL_ACCOUNT.getType());
        Wallet wallet = walletService.getWallet(param);
        if(wallet == null) {
            //创建新钱包币种
            wallet = new Wallet(user.getUid(), currency, WalletTypeEnum.CAPITAL_ACCOUNT.getType());
            wallet.setUid(user.getUid());
            wallet.setBalance(buyAmount);
            wallet.setFrozen(BigDecimal.ZERO);
            wallet.setType(WalletTypeEnum.CAPITAL_ACCOUNT.getType());
            wallet.setOrder(6);
            status = walletService.addWallet(wallet);
            if(status < 1) throw new CustomException("please_try_again_later");
        }else {
            //增加ico硬币资金记录
            walletService.lockChange(buyAmount, currency,
                    user.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.ICO);
        }
        walletService.lockChange(buyNum, CurrencyEnum.USDT.getCode(),
                user.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.ICO);

        IcoExchangeHistory history = new IcoExchangeHistory();
        history.setUid(user.getUid());
        history.setExchangeNum(buyAmount);
        history.setExchangePrice(buyPrice);
        history.setType(0);
        history.setCurrency(currency.toUpperCase());
        history.setCreateTime(new Date());
        status = iIcoExchangeHistoryService.insertIcoExchangeHistory(history);
        if(status < 1) throw new CustomException("please_try_again_later");
    }

    @Override
    @Transactional
    public void sell(BigoUser user, IcoProductDTO dto) {
        String symbol = dto.getSymbol().toLowerCase();
        String coin = SymbolCoinEnum.getCoinByCode(symbol);
        if(coin == null) throw new CustomException("the_operation_cannot_be_performed_at_this_time");
        String currency = coin.toUpperCase();

        BigDecimal sellPrice = BigDecimal.ZERO;  //购买单价
        BigDecimal sellAmount = BigDecimal.ZERO; //购买金额
        sellPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol.toLowerCase());
        sellAmount = dto.getSellNum().multiply(sellPrice).setScale(6, BigDecimal.ROUND_HALF_UP);

        int tradeFee = ConfigSettingUtil.getTradeFeeRebate();
        BigDecimal feeAmount = sellAmount.multiply(new BigDecimal(tradeFee).divide(new BigDecimal(100)));

        Wallet param = new Wallet(user.getUid(), currency, WalletTypeEnum.CAPITAL_ACCOUNT.getType());
        Wallet wallet = walletService.getWallet(param);
        if(wallet == null) {
            throw new CustomException("price_not_valid");
        }
        //减少ico硬币资金记录
        walletService.lockChange(dto.getSellNum(), currency,
                user.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.ICO);

        walletService.lockChange(sellAmount, CurrencyEnum.USDT.getCode(),
                user.getUid(),WalletTypeEnum.CAPITAL_ACCOUNT.getType(),0, AssetLogTypeEnum.ICO);

        IcoExchangeHistory history = new IcoExchangeHistory();
        history.setUid(user.getUid());
        history.setExchangeNum(dto.getSellNum());
        history.setExchangePrice(sellPrice);
        history.setType(1);
        history.setCurrency(currency.toUpperCase());
        history.setCreateTime(new Date());
        int status = iIcoExchangeHistoryService.insertIcoExchangeHistory(history);
        if(status < 1) throw new CustomException("please_try_again_later");

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
