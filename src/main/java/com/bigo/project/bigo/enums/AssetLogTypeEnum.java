package com.bigo.project.bigo.enums;

/**
 * @Description 资产变更类型枚举
 * @Author wenxm
 * @Date 2020/6/20 13:28
 */
public enum AssetLogTypeEnum {
    /**
     * 资金划转
     */
    ASSET_TRANSFER(1,"资金划转"),
    /**
     * 合约开仓
     */
    CONTRACT_OPEN(2,"合约开仓"),
    /**
     * 合约平仓
     */
    CONTRACT_CLOSE(3,"合约平仓"),
    /**
     * 充币
     */
    CASH_IN(4,"充币"),
    /**
     * 提币
     */
    CASH_OUT(5,"提币"),
    /**
     * 下级返佣
     */
    RAKE_BACK(6,"下级返佣"),
    /**
     * 币币兑换
     */
    COIN_EXCHANGE(7,"币币兑换"),
    /**
     * 补仓
     */
    REPLENISH_CONTRACT(8,"补仓"),
    /**
     * 提币失败
     */
    CASH_OUT_FAILED(9,"提币失败-返还金额"),
    /**
     * 内部充币
     */
    INSIDE_RECHARGE(10,"内部充币"),
    /**
     * OTC卖币
     */
    OTC(11,"OTC"),
    /**
     * 购买期权（限时合约）
     */
    BUY_TIME_CONTRACT(12,"购买期权"),
    /**
     * 期权结算（限时合约）
     */
    TIME_CONTRACT_SETTLEMENT(13,"期权结算"),
    /**
     * 抽奖奖励
     */
    LUCKY_DRAW(14,"抽奖奖励"),
    /**
     * 释放
     */
    RELEASE(15,"释放"),
    /**
     * 内部扣除
     */
    INTERNAL_DEDUCTION(16,"内部扣除"),
    /**
     * 购买产品
     */
    BUY_PRODUCTS(17,"购买产品"),
    /**
     * 返息
     */
    REBATE(18,"返息"),
    /**
     * 冻结
     */
    FROZEN(19,"锁仓"),
    GIVEAWAY(21,"赠送彩金"),
    LOANS(22,"借贷"),
    RETURN_LOANS(23,"归还"),
    ICO(24,"现货"),
    FEE(25,"手续费"),

    CASH_IN_DRAW(26,"充值奖励"),
    LEVEL_DRAW(27,"等级奖励"),

    ;
    /**
     * 变更类型
     */
    private Integer type;
    /**
     * 变更类型名称
     */
    private String name;

    AssetLogTypeEnum(Integer type, String name){
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
        for(AssetLogTypeEnum symbol : AssetLogTypeEnum.values()){
            if(symbol.type.equals(type)){
                return symbol.name;
            }
        }
        return null;
    }

}
