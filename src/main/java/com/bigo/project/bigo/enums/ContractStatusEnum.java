package com.bigo.project.bigo.enums;

/**
 * @Description 合约状态枚举
 * @Author wenxm
 * @Date 2020/6/20 13:28
 */
public enum ContractStatusEnum {
    /**
     * 资金划转
     */
    OPEN(0,"持仓"),
    /**
     * 用户平仓
     */
    PERSONAL_CLOSE(1,"用户平仓"),
    /**
     * 触发止盈止损
     */
    TRIGGER_CLOSE(2,"触发止盈止损"),
    /**
     * 强制平仓
     */
    FORCE_CLOSE(3,"强制平仓"),
    ;
    /**
     * 变更类型
     */
    private Integer type;
    /**
     * 变更类型名称
     */
    private String name;

    ContractStatusEnum(Integer type, String name){
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
        for(ContractStatusEnum symbol : ContractStatusEnum.values()){
            if(symbol.type.equals(type)){
                return symbol.name;
            }
        }
        return null;
    }

}
