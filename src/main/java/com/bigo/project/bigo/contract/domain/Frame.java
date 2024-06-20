package com.bigo.project.bigo.contract.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 插帧实体
 * @author: wenxm
 * @date: 2020/7/5 20:22
 */
@Data
public class Frame extends BaseEntity {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 合约id
     */
    private Long contractId;
    /**
     * 交易对
     */
    private Integer type;
    /**
     * 插帧前真实价格
     */
    private BigDecimal realPrice;
    /**
     * 插帧后的价格
     */
    private BigDecimal framePrice;
    /**
     * 操作人id
     */
    private Long operatorId;
    /**
     * 操作人
     */
    private String operatorName;
    /**
     * 用户名
     */
    private String username;
    /**
     * 订单号
     */
    private String orderNo;


}
