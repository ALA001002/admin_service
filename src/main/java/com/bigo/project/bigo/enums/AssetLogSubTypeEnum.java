package com.bigo.project.bigo.enums;

/**
 * @Description 资产变更子类型枚举
 * @Author wenxm
 * @Date 2020/6/20 13:28
 */
public enum AssetLogSubTypeEnum {
    /**
     * 转入
     */
    IN(11,"转入"),
    /**
     * 转出
     */
    OUT(12,"转出"),
    /**
     * 冻结
     */
    FROZEN(13,"冻结"),
    /**
     * 释放
     */
    RELEASE(14,"释放"),
    /**
     * 内部转帐
     */
    CASH_IN_INSIDE(41,"内部转帐"),
    /**
     * 外部充值
     */
    CASH_IN_OUTSIDE(42,"外部充值"),
    /**
     * 内转
     */
    CASH_OUT_INSIDE(51,"内转"),
    /**
     * 外提
     */
    CASH_OUT_OUTSIDE(52,"外提"),
    /**
     * 一级返佣
     */
    FIRST_BACK(61,"一级返佣"),
    /**
     * 二级返佣
     */
    SECOND_BACK(62,"二级返佣"),
    /**
     * 三级返佣
     */
    THIRD_BACK(63,"三级返佣"),

    FOUR_BACK(64,"四级返佣"),

    FIVE_BACK(65,"五级返佣"),
    /**
     * 以太坊兑换USDT
     */
    ETH_TO_USDT(71,"ETH->USDT"),
    /**
     * USDT兑换以太坊
     */
    USDT_TO_ETH(72,"USDT->ETH"),

    ETH_TO_DIEM(73,"ETH->DIEM"),
    BTC_TO_DIEM(74,"BTC->DIEM"),
    USDT_TO_DIEM(75,"USDT->DIEM"),
    /**
     * 钱包服务转账失败
     */
    WALLET_FAILED(91,"钱包服务转账失败"),
    /**
     * 提币申请被驳回
     */
    REJECT_FAILED(92,"提币申请被驳回"),
    /**
     * OTC卖币
     */
    OTC_SELL(111,"OTC-卖币"),
    /**
     * OTC买币
     */
    OTC_BUY(112,"OTC-买币"),
    /**
     * OTC返还（支付超时，订单撤销）
     */
    OTC_RETURN(113,"OTC-返还"),

    /**
     * 三级返佣
     */
    EXTRA_BACK(64,"额外层级返佣"),

    ;
    /**
     * 变更类型
     */
    private Integer type;
    /**
     * 变更类型名称
     */
    private String name;

    AssetLogSubTypeEnum(Integer type, String name){
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
        for(AssetLogSubTypeEnum symbol : AssetLogSubTypeEnum.values()){
            if(symbol.type.equals(type)){
                return symbol.name;
            }
        }
        return null;
    }

}
