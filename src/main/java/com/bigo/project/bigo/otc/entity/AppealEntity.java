package com.bigo.project.bigo.otc.entity;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;

import java.util.Date;

/**
 * @description: 申诉信息
 * @author: wenxm
 * @date: 2020/7/22 14:35
 */
@Data
public class AppealEntity extends BaseEntity {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 申诉人
     */
    private Long uid;
    /**
     * 申诉内容
     */
    private String content;
    /**
     * 状态 0-申诉中 1-通过申诉 2-驳回申诉
     */
    private Integer status;
    /**
     * 审核人ID
     */
    private Long operatorId;
    /**
     * 审核人
     */
    private String operatorName;
    /**
     * 处理时间
     */
    private Date operateTime;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 卖家ID
     */
    private Long sellerId;
    /**
     * 买家ID
     */
    private Long buyerId;
    /**
     * 订单状态
     */
    private Integer orderStatus;


}
