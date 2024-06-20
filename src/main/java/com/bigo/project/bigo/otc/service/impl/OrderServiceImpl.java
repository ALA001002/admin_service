package com.bigo.project.bigo.otc.service.impl;

import com.bigo.common.constant.Constants;
import com.bigo.common.core.lang.UUID;
import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.DateUtils;
import com.bigo.framework.websocket.server.WebSocketServer;
import com.bigo.project.bigo.api.domain.OrderParam;
import com.bigo.project.bigo.consts.BigoConsts;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.otc.domain.LegalCurrency;
import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.domain.Payment;
import com.bigo.project.bigo.otc.entity.AppealEntity;
import com.bigo.project.bigo.otc.entity.OrderEntity;
import com.bigo.project.bigo.otc.mapper.OrderMapper;
import com.bigo.project.bigo.otc.service.IAppealService;
import com.bigo.project.bigo.otc.service.ILegalCurrencyService;
import com.bigo.project.bigo.otc.service.IOrderService;
import com.bigo.project.bigo.otc.service.IPaymentService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/20 18:59
 */
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private ILegalCurrencyService legalCurrencyService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IAppealService appealService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long order(OrderParam param) {
        Order order = new Order();
        order.setCoin(param.getCoin());
        order.setLegalCurrency(param.getLegal());
        order.setAmount(param.getNum());
        Payment payment = null;
        Date nowTime = new Date();
        LegalCurrency legal = legalCurrencyService.getByCurrency(param.getLegal());
        if(legal == null){
            throw new CustomException("the_legal_currency_is_not_supported");
        }
        if(param.getType() == 2){
            payment = paymentService.getByUidAndLegal(param.getUid(), param.getLegal());
            if(payment == null){
                throw new CustomException("please_config_payment_info");
            }
            order.setSellerId(param.getUid());
            order.setBuyerId(BigoConsts.SYS_USER_ID);
            order.setBankName(payment.getBankName());
            order.setBankBranch(payment.getBankBranch());
            order.setBankAccount(payment.getBankAccount());
            order.setPayee(payment.getPayee());
            order.setPrice(legal.getSellRate());
            //扣除用户的usdt
            AssetChange change = AssetChange.builder().uid(param.getUid())
                    .currency(CurrencyEnum.USDT.getCode())
                    .dim(1)
                    .type(AssetLogTypeEnum.OTC)
                    .subType(AssetLogSubTypeEnum.OTC_SELL)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(param.getNum())
                    .build();
            walletService.changeAsset(change);
        }else if(param.getType() == 1){
            order.setBuyerId(param.getUid());
            order.setSellerId(BigoConsts.SYS_USER_ID);
            order.setBankName(legal.getBankName());
            order.setBankBranch(legal.getBankBranch());
            order.setBankAccount(legal.getBankAccount());
            order.setPayee(legal.getPayee());
            order.setPrice(legal.getBuyRate());
        }
        //计算订单超时时间
        Integer expireMin = CoinUtils.getPayExpireTime();
        order.setExpireTime(DateUtils.addMinutes(nowTime,expireMin));
        order.setOrderNo(UUID.getOrderNo());
        order.setStatus(OrderStatusEnum.OUTSTANDING.getType());
        orderMapper.insert(order);
        return order.getId();
    }

    @Override
    public int update(Order order) {
        int result = orderMapper.update(order);
        order = orderMapper.getById(order.getId());
        if(result == 1){
            //发送订单状态变更通知
            if(!order.getBuyerId().equals(BigoConsts.SYS_USER_ID)){
                WebSocketServer.noticeStatusChange("Order",order.getBuyerId());
            }
            if(!order.getSellerId().equals(BigoConsts.SYS_USER_ID)){
                WebSocketServer.noticeStatusChange("Order",order.getSellerId());
            }
        }
        return result;
    }

    @Override
    public Order getById(Long id) {
        return orderMapper.getById(id);
    }

    @Override
    public List<Order> listByParam(Order order) {
        return orderMapper.listByParam(order);
    }

    @Override
    public Boolean revokeOrder(OrderParam param) {
        Order order = orderMapper.getById(param.getOrderId());
        if(order == null){
            throw new CustomException("order_is_not_exist");
        }
        //只有买家在未付款时可以撤销订单
        if(order.getBuyerId().equals(param.getUid())
                && OrderStatusEnum.OUTSTANDING.getType().equals(order.getStatus())){
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(OrderStatusEnum.BUYER_REVOKE.getType());
            this.update(updateOrder);
        }else{
            throw new CustomException("order_cannot_cancle");
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean payOrder(OrderParam param) {
        Order order = orderMapper.getById(param.getOrderId());
        if(order == null){
            throw new CustomException("order_is_not_exist");
        }
        if(order.getBuyerId().equals(param.getUid())
            && OrderStatusEnum.OUTSTANDING.getType().equals(order.getStatus())){
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(OrderStatusEnum.UNCONFIRMED.getType());
            //计算订单超时时间
            Integer expireMin = CoinUtils.getConfirmExpireTime();
            updateOrder.setExpireTime(DateUtils.addMinutes(new Date(),expireMin));
            this.update(updateOrder);
        }else{
            throw new CustomException("the_operation_cannot_be_performed_at_this_time");
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmOrder(OrderParam param) {
        Order order = orderMapper.getById(param.getOrderId());
        if(order == null){
            throw new CustomException("order_is_not_exist");
        }
        if(order.getSellerId().equals(param.getUid())
                && OrderStatusEnum.UNCONFIRMED.getType().equals(order.getStatus())){
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(OrderStatusEnum.COMPLETE.getType());
            this.update(updateOrder);
            //如果买家不是系统，则增加买家的余额
            if(order.getBuyerId() != BigoConsts.SYS_USER_ID){
                AssetChange change = AssetChange.builder().uid(order.getBuyerId())
                        .currency(CurrencyEnum.USDT.getCode())
                        .dim(0)
                        .type(AssetLogTypeEnum.OTC)
                        .subType(AssetLogSubTypeEnum.OTC_BUY)
                        .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                        .amount(order.getAmount())
                        .build();
                walletService.changeAsset(change);
            }
        }else{
            throw new CustomException("the_operation_cannot_be_performed_at_this_time");
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean appealOrder(OrderParam param) {
        Order order = orderMapper.getById(param.getOrderId());
        if(order == null){
            throw new CustomException("order_is_not_exist");
        }
        if(order.getSellerId().equals(param.getUid())
                && OrderStatusEnum.UNCONFIRMED.getType().equals(order.getStatus())){
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(OrderStatusEnum.APPEALING.getType());
            this.update(updateOrder);
            //生成申诉记录
            AppealEntity appeal = new AppealEntity();
            appeal.setOrderId(order.getId());
            appeal.setUid(param.getUid());
            appeal.setContent(param.getAppealContent());
            appealService.insert(appeal);
        }else{
            throw new CustomException("the_operation_cannot_be_performed_at_this_time");
        }
        return Boolean.TRUE;
    }

    @Override
    public List<Order> listExpireOrder() {
        return orderMapper.listExpireOrder();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealExpireOrder(Order order) {
        if(order.getExpireTime().after(new Date())){
            return;
        }
        if(order.getStatus().equals(OrderStatusEnum.OUTSTANDING.getType())){
            //处理付款超时的订单
            if(!order.getSellerId().equals(BigoConsts.SYS_USER_ID)){
                //如果卖家是用户，则退回订单金额给用户
                //返还用户的usdt
                AssetChange change = AssetChange.builder().uid(order.getSellerId())
                        .currency(CurrencyEnum.USDT.getCode())
                        .dim(0)
                        .type(AssetLogTypeEnum.OTC)
                        .subType(AssetLogSubTypeEnum.OTC_RETURN)
                        .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                        .amount(order.getAmount())
                        .build();
                walletService.changeAsset(change);
            }
            //订单变更为支付超时
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(OrderStatusEnum.PAY_EXPIRE.getType());
            this.update(updateOrder);
        }else if(order.getStatus().equals(OrderStatusEnum.UNCONFIRMED.getType())){
            //未确认的订单超时直接确认
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(OrderStatusEnum.COMPLETE.getType());
            this.update(updateOrder);
            //如果买家不是系统，则增加买家的余额
            if(order.getBuyerId() != BigoConsts.SYS_USER_ID){
                AssetChange change = AssetChange.builder().uid(order.getBuyerId())
                        .currency(CurrencyEnum.USDT.getCode())
                        .dim(0)
                        .type(AssetLogTypeEnum.OTC)
                        .subType(AssetLogSubTypeEnum.OTC_BUY)
                        .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                        .amount(order.getAmount())
                        .build();
                walletService.changeAsset(change);
            }
        }
    }

    @Override
    public List<OrderEntity> listByEntity(OrderEntity entity) {
        return orderMapper.listByEntity(entity);
    }

    @Override
    public Order getOrder(Order order) {
        return orderMapper.getOrder(order);
    }
}
