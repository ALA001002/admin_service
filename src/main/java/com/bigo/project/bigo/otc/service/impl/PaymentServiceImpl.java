package com.bigo.project.bigo.otc.service.impl;

import com.bigo.project.bigo.otc.domain.Payment;
import com.bigo.project.bigo.otc.mapper.PaymentMapper;
import com.bigo.project.bigo.otc.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/20 15:48
 */
@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public int insert(Payment payment) {
        payment.setDeleted(0);
        return paymentMapper.insert(payment);
    }

    @Override
    public int update(Payment payment) {
        //更新收款信息逻辑，先删除原来的收款信息，再新增一条
        paymentMapper.deleteByLogic(payment.getId());
        return this.insert(payment);
    }

    @Override
    public int deleteByLogic(Long id) {
        return paymentMapper.deleteByLogic(id);
    }

    @Override
    public Payment getById(Long id) {
        return paymentMapper.getById(id);
    }

    @Override
    public Payment getByUidAndLegal(Long uid, String legalCurrency) {
        return paymentMapper.getByUidAndLegal(uid, legalCurrency);
    }

    @Override
    public List<Payment> listByUid(Long uid) {
        return paymentMapper.listByUid(uid);
    }
}
