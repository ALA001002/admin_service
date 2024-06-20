package com.bigo.project.bigo.enums;

/**
 * @Description 委托类型枚举
 * @Author wenxm
 * @Date 2020/6/18 17:06
 */
public enum TrustTypeEnum {
    /**
     * 市价委托
     */
    MARKET(0,"市价委托"),
    /**
     * 计划委托
     */
    PLAN(1,"计划委托"),
    ;
    private Integer type;

    private String name;

    TrustTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
