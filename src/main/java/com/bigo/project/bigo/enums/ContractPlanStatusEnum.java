package com.bigo.project.bigo.enums;

/**
 * @Description 计划委托状态枚举
 * @Author wenxm
 * @Date 2020/6/20 13:28
 */
public enum ContractPlanStatusEnum {
    /**
     * 委托中
     */
    PLANING(0,"委托中"),
    /**
     * 已完成
     */
    DONE(1,"已完成"),
    /**
     * 已撤销
     */
    REVOKE(2,"已撤销"),
    /**
     * 委托失败
     */
    FAILED(3,"委托失败"),
    /**
     * 正在处理
     */
    DEALING(-1,"委托中"),
    ;
    /**
     * 变更类型
     */
    private Integer type;
    /**
     * 变更类型名称
     */
    private String name;

    ContractPlanStatusEnum(Integer type, String name){
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
        for(ContractPlanStatusEnum symbol : ContractPlanStatusEnum.values()){
            if(symbol.type.equals(type)){
                return symbol.name;
            }
        }
        return null;
    }

}
