package com.bigo.project.bigo.otc.mapper;

import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.entity.OrderEntity;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/20 17:39
 */
public interface OrderMapper {

    /**
     * 插入
     * @param order
     * @return
     */
    int insert(Order order);

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
     * 获取未处理的超时订单
     * @return
     */
    List<Order> listExpireOrder();

    /**
     * 根据参数获取列表
     * @param entity
     * @return
     */
    List<OrderEntity> listByEntity(OrderEntity entity);

    Order getOrder(Order order);
}
