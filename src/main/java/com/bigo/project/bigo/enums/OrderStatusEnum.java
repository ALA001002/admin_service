package com.bigo.project.bigo.enums;

/**
 * @Description OTC订单状态枚举
 * @Author wenxm
 * @Date 2020/6/20 13:28
 */
public enum OrderStatusEnum {
    /**
     * 已下单，未付款
     */
    OUTSTANDING(0,"待付款"),
    /**
     * 已付款，未确认
     */
    UNCONFIRMED(1,"待确认"),
    /**
     * 订单已完成
     */
    COMPLETE(2,"已完成"),
    /**
     * 支付超时
     */
    PAY_EXPIRE(3,"支付超时"),
    /**
     * 申诉中
     */
    APPEALING(4,"申诉中"),
    /**
     * 申诉通过，系统取消
     */
    APPEAL_PASS(97,"申诉通过-已取消"),
    /**
     * 卖家已撤销
     */
    SELLER_REVOKE(98,"已取消"),
    /**
     * 买家已撤销
     */
    BUYER_REVOKE(99,"已取消"),
    ;
    /**
     * 变更类型
     */
    private Integer type;
    /**
     * 变更类型名称
     */
    private String name;

    OrderStatusEnum(Integer type, String name){
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取操作名称
     * @param type
     * @return
     */
    public static String getNameByType(Integer type){
        for(OrderStatusEnum symbol : OrderStatusEnum.values()){
            if(symbol.type.equals(type)){
                return symbol.name;
            }
        }
        return null;
    }

}
