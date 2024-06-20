package com.bigo.project.bigo.product.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.DictUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.project.bigo.api.domain.ProductParam;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.product.domain.ProductInfo;
import com.bigo.project.bigo.product.domain.ProductOrder;
import com.bigo.project.bigo.product.mapper.ProductOrderMapper;
import com.bigo.project.bigo.product.service.IProductOrderService;
import com.bigo.project.bigo.userinfo.service.ILevelVipService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.service.AsyncService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 理财产品订单Service业务层处理
 * 
 * @author bigo
 * @date 2021-01-27
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements IProductOrderService {



    @Resource
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ProductInfoServiceImpl productInfoService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private ILevelVipService levelVipService;

    @Resource
    AsyncService asyncService;
    /**
     * 查询理财产品订单
     * 
     * @param id 理财产品订单ID
     * @return 理财产品订单
     */
    @Override
    public ProductOrder selectProductOrderById(Long id)
    {
        return productOrderMapper.selectProductOrderById(id);
    }

    /**
     * 查询理财产品订单列表
     * 
     * @param productOrder 理财产品订单
     * @return 理财产品订单
     */
    @Override
    public List<ProductOrder> selectProductOrderList(ProductOrder productOrder)
    {
        return productOrderMapper.selectProductOrderList(productOrder);
    }

    /**
     * 新增理财产品订单
     * 
     * @param productOrder 理财产品订单
     * @return 结果
     */
    @Override
    public int insertProductOrder(ProductOrder productOrder)
    {
        productOrder.setCreateTime(DateUtils.getNowDate());
        return productOrderMapper.insertProductOrder(productOrder);
    }

    /**
     * 修改理财产品订单
     * 
     * @param productOrder 理财产品订单
     * @return 结果
     */
    @Override
    public int updateProductOrder(ProductOrder productOrder)
    {
        return productOrderMapper.updateProductOrder(productOrder);
    }

    /**
     * 批量删除理财产品订单
     * 
     * @param ids 需要删除的理财产品订单ID
     * @return 结果
     */
    @Override
    public int deleteProductOrderByIds(Long[] ids)
    {
        return productOrderMapper.deleteProductOrderByIds(ids);
    }

    /**
     * 删除理财产品订单信息
     * 
     * @param id 理财产品订单ID
     * @return 结果
     */
    @Override
    public int deleteProductOrderById(Long id)
    {
        return productOrderMapper.deleteProductOrderById(id);
    }

    /**
     * 购买产品
     * @param productParam
     */
    @Override
    @Transactional
    public void buyProducts(ProductParam productParam, ProductInfo productInfo) {
        ProductInfo inStock = productInfoService.inStockForUpdate(productInfo.getId());
        //减库存
        int updateStatus = productInfoService.updateInStock(inStock);
        if(updateStatus == 0) {
            throw new CustomException("purchase_exception_please_try_again_later");
        }
        // 扣除余额
        AssetChange subBalanceChange = AssetChange.builder().uid(productParam.getUid())
                .currency(productInfo.getCurrency())
                .dim(1)
                .type(AssetLogTypeEnum.BUY_PRODUCTS)
                .subType(AssetLogSubTypeEnum.OUT)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(productParam.getBuyProducts())
                .build();
        walletService.changeAsset(subBalanceChange);

        // 增加冻结金额
       /* AssetChange addFrozenChange = AssetChange.builder().uid(productParam.getUid())
                .currency(productInfo.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.FROZEN)
                .subType(AssetLogSubTypeEnum.FROZEN)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(productParam.getBuyProducts())
                .amountType(AmountTypeEnum.FROZEN.getType())
                .build();
        walletService.changeAsset(addFrozenChange);*/

        // 增加订单记录
        ProductOrder order = new ProductOrder();
        order.setUid(productParam.getUid());
        order.setProductName(productInfo.getProductName());
        order.setPurchaseAmount(productParam.getBuyProducts());
        order.setProfitRate(productInfo.getProfitRate());
        order.setProfitTime(productInfo.getProfitTime());
        order.setCurrency(productInfo.getCurrency());
        order.setProductId(productInfo.getId());
        order.setSettlementType(productInfo.getSettlementType());
        order.setStatus(1); // 1-冻结，2-释放
        order.setTypeId(productInfo.getTypeId());
        if(productInfo.getTypeId() == 1) {
            // 获取开始释放时间
            order.setBeginReleaseTime(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, productInfo.getProfitTime());
            // 获取结束释放时间
            order.setEndReleaseTime(calendar.getTime());
        }else if(productInfo.getTypeId() == 2) {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            String startDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,startCalendar.getTime())+" 00:00:00";

            Date startDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, startDateStr);
            order.setBeginReleaseTime(startDate); //开始时间


            Calendar issueCalendar = Calendar.getInstance();
            issueCalendar.setTime(startDate);
            System.out.println(issueCalendar.getTime());
            issueCalendar.add(Calendar.DATE, productInfo.getProfitTime());
            String issueDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,issueCalendar.getTime())+" 23:59:59";
            order.setEndReleaseTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, issueDateStr)); //发放时间
        }
        order.setLastReleaseTime(new Date());
        order.setCreateTime(new Date());
        productOrderMapper.insertProductOrder(order);
    }


    /**
     * 查找冻结中订单
     * @return
     */
    @Override
    public List<ProductOrder> findFrozenOrder(ProductOrder order) {
        return productOrderMapper.findFrozenOrder(order);
    }

    @Override
    @Transactional
    public void release(ProductOrder productOrder) {
        // 计算每日利息
        BigDecimal profitRate = productOrder.getProfitRate().divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_DOWN);
        BigDecimal interest = productOrder.getPurchaseAmount().multiply(profitRate);
        AssetChange assetChange = AssetChange.builder().uid(productOrder.getUid())
                .currency(productOrder.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.REBATE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(interest)
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(assetChange);

        //释放次数
        Integer releaseCount = productOrder.getReleaseCount() +1;
        //已释放金额
        BigDecimal profitAmount = productOrder.getProfitAmount().add(interest);

        ProductOrder updateOrder = new ProductOrder();
        updateOrder.setId(productOrder.getId());
        updateOrder.setIsTodayRelease(1);
        updateOrder.setReleaseCount(releaseCount);
        updateOrder.setProfitAmount(profitAmount);
        if(releaseCount == productOrder.getProfitTime()) {  // 释放次数已达标
            AssetChange releaseChange = AssetChange.builder().uid(productOrder.getUid())    //增加余额
                    .currency(productOrder.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.RELEASE)
                    .subType(AssetLogSubTypeEnum.RELEASE)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(productOrder.getPurchaseAmount())
                    .amountType(AmountTypeEnum.BANLANCE.getType())
                    .build();
            walletService.changeAsset(releaseChange);
            AssetChange releaseFrozenChange = AssetChange.builder().uid(productOrder.getUid())    // 扣除冻结余额
                    .currency(productOrder.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.RELEASE)
                    .subType(AssetLogSubTypeEnum.RELEASE)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(productOrder.getPurchaseAmount())
                    .amountType(AmountTypeEnum.FROZEN.getType())
                    .build();
            walletService.changeAsset(releaseFrozenChange);
            updateOrder.setStatus(2);
        }
        productOrderMapper.updateProductOrder(updateOrder);
    }

    @Override
    public void resetReleaseStatus() {
        productOrderMapper.resetReleaseStatus();
    }

    /**
     * 中断释放
     * @param productOrder
     */
    @Override
    @Transactional
    public void stopRelease(ProductOrder productOrder) {
        //计算购买日期到目前天数
        BigDecimal interestTotal = BigDecimal.ZERO;
        Long dayNum = daysBetween(new Date(), productOrder.getBeginReleaseTime());
        if(dayNum > 0){
            BigDecimal profitRate = productOrder.getProfitRate().divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_DOWN);
            interestTotal = (productOrder.getPurchaseAmount().multiply(profitRate)).multiply(new BigDecimal(dayNum));
        }
        AssetChange releaseChange = AssetChange.builder().uid(productOrder.getUid())    //增加余额
                .currency(productOrder.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(productOrder.getPurchaseAmount().add(interestTotal))
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(releaseChange);
        /*AssetChange releaseFrozenChange = AssetChange.builder().uid(productOrder.getUid())    // 扣除冻结余额
                .currency(productOrder.getCurrency())
                .dim(1)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(productOrder.getPurchaseAmount())
                .amountType(AmountTypeEnum.FROZEN.getType())
                .build();
        walletService.changeAsset(releaseFrozenChange);*/

        ProductOrder updateOrder = new ProductOrder();
        updateOrder.setId(productOrder.getId());
        updateOrder.setProfitTime(dayNum.intValue());
        updateOrder.setProfitAmount(interestTotal);
        updateOrder.setStatus(2);
        updateOrder.setIsTodayRelease(1);
        updateOrder.setEndReleaseTime(new Date());
        productOrderMapper.updateProductOrder(updateOrder);
    }
    private static long daysBetween(Date one, Date two) {
        long difference =  (one.getTime()-two.getTime())/86400000;
        return Math.abs(difference);
    }

    public static void main(String[] args) {
        String d1 = "2023-04-11 00:00:00";
        Date date1 = DateUtils.parseDate(d1);
        String d2 = "2023-04-13 23:00:00";
        Date date2 = DateUtils.parseDate(d2);
        System.out.println(daysBetween(date2, date1));
    }

    @Override
    @Transactional
    public void nweRelease(ProductOrder productOrder) {


        ProductOrder updateOrder = new ProductOrder();
        updateOrder.setId(productOrder.getId());
        updateOrder.setReleaseCount(productOrder.getProfitTime());
        updateOrder.setLastReleaseTime(new Date());

        //总收益百分比
        BigDecimal profitRate = productOrder.getProfitRate().multiply(new BigDecimal(productOrder.getProfitTime())).divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_DOWN);

        //总收益
        BigDecimal interest = productOrder.getPurchaseAmount().multiply(profitRate);

        int tradeFee = ConfigSettingUtil.getTradeFeeRebate();
        BigDecimal feeAmount = productOrder.getPurchaseAmount().multiply(new BigDecimal(tradeFee).divide(new BigDecimal(100))).setScale(4, BigDecimal.ROUND_HALF_UP);

//        interest = interest.subtract(feeAmount);


        AssetChange assetChange = AssetChange.builder().uid(productOrder.getUid())
                .currency(productOrder.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.REBATE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(interest)
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(assetChange);

        AssetChange releaseChange = AssetChange.builder().uid(productOrder.getUid())    //增加余额
                .currency(productOrder.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(productOrder.getPurchaseAmount())
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(releaseChange);

        updateOrder.setProfitAmount(interest);
        updateOrder.setStatus(2);

        productOrderMapper.updateProductOrder(updateOrder);

        walletService.lockChange(feeAmount, CurrencyEnum.USDT.getCode(),
                productOrder.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.FEE);

        asyncService.levelRebate(productOrder.getUid(), feeAmount);
//        String status = DictUtils.getDictValue("bigo_level_vip_config", "status");
//        if(StringUtils.isNotEmpty(status) && status.equals("true")) {
//        }
    }

    @Transactional
    public void changeBalance(ProductOrder productOrder, BigDecimal amount) {
        AssetChange releaseChange = AssetChange.builder().uid(productOrder.getUid())    //增加余额
                .currency(productOrder.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(amount)
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(releaseChange);
        AssetChange releaseFrozenChange = AssetChange.builder().uid(productOrder.getUid())    // 扣除冻结余额
                .currency(productOrder.getCurrency())
                .dim(1)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(amount)
                .amountType(AmountTypeEnum.FROZEN.getType())
                .build();
        walletService.changeAsset(releaseFrozenChange);
    }

    @Override
    public List<ProductOrder> frozenProductOrder(ProductOrder order) {
        return productOrderMapper.frozenProductOrder(order);
    }

    @Override
    public void magoRelease(ProductOrder productOrder) {
        ProductOrder updateOrder = new ProductOrder();
        updateOrder.setId(productOrder.getId());
        updateOrder.setReleaseCount(productOrder.getProfitTime());
        updateOrder.setLastReleaseTime(new Date());

        BigDecimal profitRate = productOrder.getProfitRate().multiply(new BigDecimal(productOrder.getProfitTime())).divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_DOWN);
        BigDecimal interest = productOrder.getPurchaseAmount().multiply(profitRate);
        AssetChange assetChange = AssetChange.builder().uid(productOrder.getUid())
                .currency(productOrder.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.REBATE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(interest)
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(assetChange);

        AssetChange releaseChange = AssetChange.builder().uid(productOrder.getUid())    //增加余额
                .currency(productOrder.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.RELEASE)
                .subType(AssetLogSubTypeEnum.RELEASE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(productOrder.getPurchaseAmount())
                .amountType(AmountTypeEnum.BANLANCE.getType())
                .build();
        walletService.changeAsset(releaseChange);

        updateOrder.setProfitAmount(interest);
        updateOrder.setStatus(2);

        productOrderMapper.updateProductOrder(updateOrder);

        String status = DictUtils.getDictValue("bigo_level_vip_config", "status");
        if(StringUtils.isNotEmpty(status) && status.equals("true")) {
            asyncService.vipLevelRebate(productOrder.getUid(), interest);
        }
    }

    @Override
    public void releaseOrder() {
        ProductOrder order = new ProductOrder();
        List<ProductOrder> orderList = this.frozenProductOrder(order);
        log.info("=============持币生息待结算订单数量："+orderList.size()+"====================");
        for (ProductOrder productOrder : orderList) {
            try{
                log.info("=============结算持币生息,订单号："+productOrder.getId()+"====================");
                this.nweRelease(productOrder);
            } catch (Exception ex) {
                log.error("产品订单ID:{},处理失败，错误信息：{}", productOrder.getId(),ex.getMessage());
                ex.printStackTrace();
                continue;
            }
        }
        log.info("=============结束结算持币生息====================");
    }

    private Boolean isRelease(ProductOrder productOrder) {
//        log.info("orderId = {}, nowDate = {}, difference = {}",productOrder.getId(), DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", new Date()), DateUtils.difference(productOrder.getLastReleaseTime(), new Date()));
        if(CandlestickEnum.MIN30.getCode().equals(productOrder.getProfitTimeType())
                && DateUtils.difference(productOrder.getLastReleaseTime(), new Date()) < 30) {
            return false;
        }  else if (CandlestickEnum.DAY1.getCode().equals(productOrder.getProfitTimeType())
                && DateUtils.difference(productOrder.getLastReleaseTime(), new Date()) < 1440){
            return false;
        }
        return true;
    }

}
