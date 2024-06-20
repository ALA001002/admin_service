package com.bigo.project.bigo.enums;

/**
 * @Description 钱包类型枚举
 * @Author wenxm
 * @Date 2020/6/21 9:58
 */
public enum WalletTypeEnum {

    /**
     * 资金账户
     */
    CAPITAL_ACCOUNT(0, "资金账户"),
    /**
     * 合约账户
     */
    CONTRACT_ACCOUNT(1,"合约账户"),
    ;

    /**
     * 钱包类型
     */
    private Integer type;
    /**
     * 钱包名称
     */
    private String name;

    WalletTypeEnum(Integer type, String name){
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String getNameByType(Integer type){
        for(WalletTypeEnum typeEnum : WalletTypeEnum.values()){
            if(typeEnum.getType().equals(type)){
                return typeEnum.getName();
            }
        }
        return  null;
    }
}
