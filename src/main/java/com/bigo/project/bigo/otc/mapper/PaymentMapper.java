package com.bigo.project.bigo.otc.mapper;

import com.bigo.project.bigo.otc.domain.Payment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 支付方式mapper
 * @author: wenxm
 * @date: 2020/7/20 15:28
 */
public interface PaymentMapper {

    /**
     * 插入
     * @param payment
     * @return
     */
    int insert(Payment payment);

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    int deleteByLogic(Long id);

    /**
     * 根据ID获取支付方式
     * @param id
     * @return
     */
    Payment getById(Long id);

    /**
     * 根据用户id和法币类型获取支付方式
     * @param uid
     * @param legalCurrency
     * @return
     */
    Payment getByUidAndLegal(@Param("uid") Long uid, @Param("legalCurrency") String legalCurrency);

    /**
     * 获取用户所有的收款方式
     * @param uid
     * @return
     */
    List<Payment> listByUid(Long uid);
}
