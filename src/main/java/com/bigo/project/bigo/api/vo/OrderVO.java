package com.bigo.project.bigo.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @description: otc订单
 * @author: wenxm
 * @date: 2020/7/20 17:17
 */
@Data
public class OrderVO {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 币种
     */
    private String coin;
    /**
     * 数量
     */
    private BigDecimal amount;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 法币
     */
    private String legalCurrency;
    /**
     * 收款方式ID
     */
    private Long paymentId;
    /**
     * 银行
     */
    private String bankName;
    /**
     * 开户支行
     */
    private String bankBranch;
    /**
     * 收款账户
     */
    private String bankAccount;
    /**
     * 收款人
     */
    private String payee;
    /**
     * 卖家ID
     */
    private Long sellerId;
    /**
     * 卖家联系方式
     */
    private String sellerContractInfo;
    /**
     * 买家ID
     */
    private Long buyerId;
    /**
     * 买家联系方式
     */
    private String buyerContractInfo;
    /**
     * 订单状态 0-已下单，未付款 1-已付款，未确认 2-已完成 3-支付超时 4-申诉中  97-已取消(后台操作） 98-卖家已撤销 99-买家已撤销
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 支付时间
     */
    private Date payTime;
    /**
     * 确认时间
     */
    private Date confirmTime;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 撤销时间
     */
    private Date revokeTime;

    /**
     * 总价
     * @return
     */
    public String getTotal(){
        if(this.amount != null && this.price != null){
            return this.amount.multiply(this.price).setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return null;
    }

    public Long getRemainSecond(){
        Date now = new Date();
        if(this.status != null && this.status < 2
                && this.expireTime != null
                && this.expireTime.after(now)){
            return ChronoUnit.SECONDS.between(now.toInstant(),this.expireTime.toInstant());
        }
        return null;
    }

    public String getPrice(){
        if(this.price != null){
            return this.price.setScale(2,RoundingMode.HALF_UP).toPlainString();
        }
        return null;
    }

    public String getAmount(){
        if(this.amount != null){
            return this.amount.setScale(2,RoundingMode.HALF_UP).toPlainString();
        }
        return null;
    }



}
