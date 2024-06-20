package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/22 13:58
 */
@Data
public class PayOrderVO {
    /** 支付订单号 */
    private String payOrderId;

    /** 用户ID */
    private Long uid;

    /** 支付金额：单位元 */
    private Long amount;


    /** 支付状态：0-订单生成,1-支付中,2-支付成功,3-业务处理完成 */
    private Integer status;

    // 支付时间
    private Date paySuccTime;
}
