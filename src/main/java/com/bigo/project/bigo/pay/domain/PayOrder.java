package com.bigo.project.bigo.pay.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 三方支付订单表对象 bg_pay_order
 * 
 * @author bigo
 * @date 2021-05-20
 */
@Data
public class PayOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @Excel(name = "id")
    private Long id;

    /** 支付订单号 */
    @Excel(name = "支付订单号")
    private String payOrderId;

    /** 上游订单号 */
    @Excel(name = "上游订单号")
    private String channelOrderId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long uid;

    /** 支付金额：单位元 */
    @Excel(name = "支付金额：单位元")
    private Long amount;


    /** 支付手续费： */
    @Excel(name = "支付支付手续费")
    private BigDecimal fee;


    /** 货币金额(真实货币)：单位元 */
    @Excel(name = "货币金额(真实货币)：单位元")
    private BigDecimal currencyAmount;

    /** 结算数量： */
    @Excel(name = "结算数量")
    private BigDecimal settValue;

    /** 交易数量： */
    @Excel(name = "交易数量")
    private BigDecimal tradeValue;


    /** 三位货币代码:美元USD */
    @Excel(name = "三位货币代码:美元USD")
    private String currency;

    /** 支付状态：0-订单生成,1-支付中,2-支付成功,3-业务处理完成 */
    @Excel(name = "支付状态：0-订单生成,1-支付中,2-支付成功,3-业务处理完成")
    private Integer status;

    /** 支付通道ID */
    @Excel(name = "支付通道ID")
    private Long payPassageId;

    /** 客户端IP */
    @Excel(name = "客户端IP")
    private String clientIp;

    /** IP地址 */
    @Excel(name = "客户端IP")
    private String ipAddress;

    /** 订单支付成功时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "订单支付成功时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paySuccTime;

    // 扩展字段
    // 用户账号
    private String username;
    // 通道名称
    private String payPassageName;

    @Excel(name = "通道错误信息")
    private String errorMsg;

}
