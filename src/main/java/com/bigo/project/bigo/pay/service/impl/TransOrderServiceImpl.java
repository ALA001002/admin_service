package com.bigo.project.bigo.pay.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.exception.BaseException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.SpringUtil;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.dto.TransDTO;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.pay.domain.*;
import com.bigo.project.bigo.pay.entity.PaymentInterface;
import com.bigo.project.bigo.pay.entity.TransInterface;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.pay.mapper.TransOrderMapper;
import com.bigo.project.bigo.pay.service.ITransOrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 代付Service业务层处理
 * 
 * @author bigo
 * @date 2022-05-22
 */
@Slf4j
@Service
public class TransOrderServiceImpl implements ITransOrderService 
{
    @Autowired
    private TransOrderMapper transOrderMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private TransInterface transInterface;

    @Autowired
    private IWithdrawService withdrawService;

    /**
     * 查询代付
     * 
     * @param id 代付ID
     * @return 代付
     */
    @Override
    public TransOrder selectTransOrderById(Long id)
    {
        return transOrderMapper.selectTransOrderById(id);
    }

    /**
     * 查询代付列表
     * 
     * @param transOrder 代付
     * @return 代付
     */
    @Override
    public List<TransOrder> selectTransOrderList(TransOrder transOrder)
    {
        return transOrderMapper.selectTransOrderList(transOrder);
    }

    /**
     * 新增代付
     * 
     * @param transOrder 代付
     * @return 结果
     */
    @Override
    public int insertTransOrder(TransOrder transOrder)
    {
        transOrder.setCreateTime(DateUtils.getNowDate());
        return transOrderMapper.insertTransOrder(transOrder);
    }

    /**
     * 修改代付
     * 
     * @param transOrder 代付
     * @return 结果
     */
    @Override
    public int updateTransOrder(TransOrder transOrder)
    {
        return transOrderMapper.updateTransOrder(transOrder);
    }

    /**
     * 批量删除代付
     * 
     * @param ids 需要删除的代付ID
     * @return 结果
     */
    @Override
    public int deleteTransOrderByIds(Long[] ids)
    {
        return transOrderMapper.deleteTransOrderByIds(ids);
    }

    /**
     * 删除代付信息
     * 
     * @param id 代付ID
     * @return 结果
     */
    @Override
    public int deleteTransOrderById(Long id)
    {
        return transOrderMapper.deleteTransOrderById(id);
    }

    @Override
    @Transactional
    public void transPay(TransDTO transDTO, BankInfo bankInfo) {
        BigDecimal fee = CoinUtils.getWithdrawFeeByCurrency(transDTO.getCoin(), transDTO.getMoney(), 0);

        BigDecimal subMoney = transDTO.getMoney();
        BigDecimal receiveMoney = transDTO.getMoney().subtract(fee);

        TransOrder order = new TransOrder();
        BeanUtils.copyProperties(transDTO, order);
        order.setTransOrderId(getTransOrderId());
        order.setBankName(bankInfo.getBankName());
        order.setBankCode(bankInfo.getBankCode());
        order.setBankNumber(bankInfo.getBankNumber());
        order.setCurrency(bankInfo.getCurrency());
        order.setAmount(receiveMoney);
        order.setFee(fee);
        order.setCurrencyAmount(receiveMoney.multiply(new BigDecimal(20)).setScale(2, BigDecimal.ROUND_HALF_UP));
        // 扣除账户余额
        walletService.lockChange(subMoney, CurrencyEnum.USDT.getCode(), transDTO.getUid(),
                WalletTypeEnum.CAPITAL_ACCOUNT.getType(),1, AssetLogTypeEnum.CASH_OUT, AssetLogSubTypeEnum.CASH_OUT_OUTSIDE);
        //插入记录
        transOrderMapper.insertTransOrder(order);
    }

    public static String getTransOrderId(){
        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("HHmmssSSS");
        String randomString = RandomStringUtils.randomNumeric(8);
        String id = sdformat.format(date);
        return "Trans"+ id + randomString;
    }

    @Override
    @Async
    @Transactional
    public void agetnPay(TransOrder order, PayPassage payPassage, PayInterfaceType interfaceType) {
        try {
            transInterface = (TransInterface) SpringUtil.getBean(interfaceType.getIfTypeCode().toLowerCase() + "TransService");
        } catch (BeansException e) {
            log.error(e.getMessage());
            return;
        }
        JSONObject retObj = transInterface.trans(order);
        String retCode = retObj.getString("retCode");
        if("SUCCESS".equals(retCode)) {
            //更新订单为代付中
            TransOrder updateOrder = new TransOrder();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(1);
            updateOrder.setPayPassageId(payPassage.getId());
            transOrderMapper.updateTransOrder(updateOrder);
        }else {
            log.info("errMsg", retObj.getString("retMsg"));
        }
    }

    @Override
    public TransOrder selectOrderId(String transOrderId) {
        return transOrderMapper.selectOrderId(transOrderId);
    }

    @Override
    @Transactional
    public int updateStatusSuccess(TransOrder order) {
        TransOrder updateTransOrder = new TransOrder();
        updateTransOrder.setTransOrderId(order.getTransOrderId());
        updateTransOrder.setChannelOrderId(order.getChannelOrderId());
        updateTransOrder.setStatus(2);
        updateTransOrder.setChannelOrderId(order.getChannelOrderId());
        return updateSuccessTransactional(updateTransOrder);
    }



    @Transactional
    public int updateSuccessTransactional(TransOrder transOrder) {
        TransOrder Order = this.selectOrderId(transOrder.getTransOrderId());
        //添加充值记录
        Withdraw withdraw = new Withdraw();
        withdraw.setUid(Order.getUid());
        withdraw.setCoin(CurrencyEnum.USDT.getCode());
        withdraw.setMoney(Order.getAmount());
        BigDecimal fee = CoinUtils.getWithdrawFeeByCurrency(CurrencyEnum.USDT.getCode(), Order.getAmount(), 0);
        withdraw.setFee(fee);
        withdraw.setType(7);
        withdraw.setStatus(1);
        withdraw.setCheckStatus(1);
        withdraw.setCreateTime(new Date());
        withdraw.setVerifyTime(new Date());
        withdrawService.insert(withdraw);

        transOrder.setWithdrawId(withdraw.getId());
        transOrder.setTransSuccTime(new Date());
        int count = transOrderMapper.updateStatus(transOrder);
        if(count != 1){
            throw new BaseException("更新支付订单失败,订单ID:{}", transOrder.getTransOrderId());
        }
        /*//更新成功，更新账户余额，增加流水金额
        AssetChange change = AssetChange.builder().uid(transOrder.getUid())
                .currency(CurrencyEnum.USDT.getCode())
                .dim(1)
                .type(AssetLogTypeEnum.CASH_OUT)
                .subType(AssetLogSubTypeEnum.OUT)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(transOrder.)
                .build();
        walletService.changeAsset(change);*/
        return count;
    }

    @Override
    @Transactional
    public int updateStatusFail(TransOrder order) {
        TransOrder updateTransOrder = new TransOrder();
        updateTransOrder.setTransOrderId(order.getTransOrderId());
        updateTransOrder.setChannelOrderId(order.getChannelOrderId());
        updateTransOrder.setStatus(3);
        return updateFailTransactional(updateTransOrder);
    }

    @Override
    @Transactional
    public void rejected(TransOrder order) {
        TransOrder updateOrder = new TransOrder();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(3);
        transOrderMapper.updateTransOrder(updateOrder);
        //代付失败，返还金额
        AssetChange change = AssetChange.builder().uid(order.getUid())
                .currency(CurrencyEnum.USDT.getCode())
                .dim(0)
                .type(AssetLogTypeEnum.RETURN_LOANS)
                .subType(AssetLogSubTypeEnum.IN)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(order.getAmount().add(order.getFee()))
                .build();
        walletService.changeAsset(change);
    }

    @Transactional
    public int updateFailTransactional(TransOrder transOrder) {
        int count = transOrderMapper.updateStatus(transOrder);
        if(count != 1){
            throw new BaseException("更新支付订单失败,订单ID:{}", transOrder.getTransOrderId());
        }
        transOrder = this.selectOrderId(transOrder.getTransOrderId());
        //代付失败，返还金额
        AssetChange change = AssetChange.builder().uid(transOrder.getUid())
                .currency(CurrencyEnum.USDT.getCode())
                .dim(0)
                .type(AssetLogTypeEnum.RETURN_LOANS)
                .subType(AssetLogSubTypeEnum.IN)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(transOrder.getAmount().add(transOrder.getFee()))
                .build();
        walletService.changeAsset(change);
        return count;
    }
}
