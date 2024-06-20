package com.bigo.project.bigo.pay.entity;

import com.alibaba.fastjson.JSONObject;
import com.bigo.project.bigo.pay.domain.TransOrder;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description:
 */
public interface TransInterface {

    /**
     * 发起转账(代付)
     * @param transOrder
     * @return
     */
    JSONObject trans(TransOrder transOrder) ;

//    /**
//     * 查询结果
//     * @param transOrder
//     * @return
//     */
//    JSONObject query(TransOrder transOrder);
//
//
//    /**
//     * 查询账户余额
//     * @param payParam
//     * @return
//     */
//    JSONObject balance(String payParam);
//
//    /**
//     * 发起转账（批量代付）
//     * @param transOrderList
//     * @return
//     */
//    JSONObject focusTrans(List<TransOrder> transOrderList);
}
