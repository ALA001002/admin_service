package com.bigo.project.bigo.pay.entity;

import com.alibaba.fastjson.JSONObject;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.domain.TransOrder;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 17:04
 */
public interface PaymentInterface {
    JSONObject pay(PayOrder payOrder);

}
