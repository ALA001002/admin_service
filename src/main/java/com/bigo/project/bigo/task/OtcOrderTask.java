package com.bigo.project.bigo.task;

import com.bigo.project.bigo.enums.OrderStatusEnum;
import com.bigo.project.bigo.enums.WalletTransactionStatusEnum;
import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.service.IOrderService;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author wenxm
 * @Description: OTC订单定时任务
 * @date 2019/7/27 下午9:50
 */
@Component("orderTask")
@Slf4j
public class OtcOrderTask {

    @Autowired
    private IOrderService orderService;


    /**
     * 处理超时订单
     */
    public void expireOrderTask() {
        //获取为处理的超时订单
        List<Order> orderList = orderService.listExpireOrder();
        if(CollectionUtils.isEmpty(orderList)){
            return;
        }
        for(Order order : orderList){
            try {
                orderService.dealExpireOrder(order);
            }catch (Exception e){
                log.error("处理OTC订单超时失败, 订单ID:{}", order.getId(), e);
            }
        }
    }


}
