package com.bigo.project.bigo.task;

import com.bigo.project.bigo.product.domain.ProductOrder;
import com.bigo.project.bigo.product.service.IProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description:    理財產品任務
 * @author: wenxm
 * @date: 2021/1/29 2:54
 */
@Component("productTask")
@Slf4j
public class ProductTask {

    @Autowired
    private IProductOrderService productOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 返息
     */
/*
    public void release() {
        ProductOrder order = new ProductOrder();
        order.setIsTodayRelease(0);
        order.setIsOld(1);
        List<ProductOrder> orderList = productOrderService.findFrozenOrder(order);
        for (ProductOrder productOrder : orderList) {
            try {
                if(productOrder.getReleaseCount() > productOrder.getProfitTime()) { //释放次数超过，则跳过
                    continue;
                }
                Boolean locked = redisTemplate.opsForValue().setIfAbsent(productOrder.getId().toString(), "", 10, TimeUnit.SECONDS);
                if(!locked) continue;
                productOrderService.release(productOrder);
            } catch (Exception ex) {
                log.error("产品订单ID:{},处理失败，错误信息：{}", productOrder.getId());
                ex.printStackTrace();
                continue;
            }finally {
                redisTemplate.delete(productOrder.getId().toString());
            }
        }
    }
*/

    /**
     * 重置每日释放订单状态
     */
    public void resetReleaseStatus() {
        productOrderService.resetReleaseStatus();
    }

//    public void magoRelease() {
//        log.info("=============开始结算持币生息====================");
//        ProductOrder order = new ProductOrder();
//        order.setIsOld(0);
//        List<ProductOrder> orderList = productOrderService.frozenProductOrder(order);
//        log.info("=============持币生息待结算订单数量："+orderList.size()+"====================");
//        for (ProductOrder productOrder : orderList) {
//            try{
//                log.info("=============结算持币生息,订单号："+productOrder.getId()+"====================");
//                productOrderService.magoRelease(productOrder);
//            } catch (Exception ex) {
//                log.error("产品订单ID:{},处理失败，错误信息：{}", productOrder.getId(),ex.getMessage());
//                ex.printStackTrace();
//                continue;
//            }
//        }
//        log.info("=============结束结算持币生息====================");
//    }


    @Scheduled(fixedDelay = 120*1000)
    public void newRelease() {
        log.info("=============开始结算持币生息====================");
        productOrderService.releaseOrder();


    }

}
