package com.bigo.project.bigo.otc.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.project.bigo.consts.BigoConsts;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.entity.AppealEntity;
import com.bigo.project.bigo.otc.mapper.AppealMapper;
import com.bigo.project.bigo.otc.service.IAppealService;
import com.bigo.project.bigo.otc.service.IOrderService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/22 15:00
 */
@Service
public class AppealServiceImpl implements IAppealService {

    @Autowired
    private AppealMapper appealMapper;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IWalletService walletService;

    @Override
    public int insert(AppealEntity appeal) {
        appeal.setStatus(0);
        return appealMapper.insert(appeal);
    }

    @Override
    public int update(AppealEntity appeal) {
        return appealMapper.update(appeal);
    }

    @Override
    public AppealEntity getById(Long id) {
        return appealMapper.getById(id);
    }

    @Override
    public List<AppealEntity> listByEntity(AppealEntity entity) {
        return appealMapper.listByEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean passAppeal(AppealEntity appeal) {
        //通过申诉，则将订单金额返还给卖家
        Order order = orderService.getById(appeal.getOrderId());
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getType())){
            throw new CustomException("订单状态异常！");
        }
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
        order.setStatus(OrderStatusEnum.APPEAL_PASS.getType());
        orderService.update(order);
        appealMapper.update(appeal);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rejectAppeal(AppealEntity appeal) {
        //驳回申诉，则将订单金额打给买家
        Order order = orderService.getById(appeal.getOrderId());
        if(!order.getStatus().equals(OrderStatusEnum.APPEALING.getType())){
            throw new CustomException("订单状态异常！");
        }
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
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(OrderStatusEnum.COMPLETE.getType());
        orderService.update(updateOrder);
        appealMapper.update(appeal);
        return Boolean.TRUE;
    }
}
