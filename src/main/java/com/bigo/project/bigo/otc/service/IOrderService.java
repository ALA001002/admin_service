package com.bigo.project.bigo.otc.service;

import com.bigo.project.bigo.api.domain.OrderParam;
import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.entity.OrderEntity;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/20 18:59
 */
public interface IOrderService {

    /**
     * 下单
     * @param param
     * @return
     */
    Long order(OrderParam param);

    /**
     * 更新
     * @param order
     * @return
     */
    int update(Order order);

    /**
     * 根据ID获取订单信息
     * @param id
     * @return
     */
    Order getById(Long id);

    /**
     * 根据参数获取订单列表
     * @param order
     * @return
     */
    List<Order> listByParam(Order order);

    /**
     * 撤销订单
     * @param param
     * @return
     */
    Boolean revokeOrder(OrderParam param);

    /**
     * 确认已支付
     * @param param
     * @return
     */
    Boolean payOrder(OrderParam param);

    /**
     * 确认已收款
     * @param param
     * @return
     */
    Boolean confirmOrder(OrderParam param);

    /**
     * 订单申诉
     * @param param
     * @return
     */
    Boolean appealOrder(OrderParam param);

    /**
     * 获取未处理的超时订单
     * @return
     */
    List<Order> listExpireOrder();

    /**
     * 处理超时订单
     * @param order
     */
    void dealExpireOrder(Order order);

    /**
     * 根据参数获取列表
     * @param entity
     * @return
     */
    List<OrderEntity> listByEntity(OrderEntity entity);

    /**
     * 获取订单
     * @param order
     * @return
     */
    Order getOrder(Order order);
}
