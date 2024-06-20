package com.bigo.project.bigo.enums;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/12/30 16:24
 */
public enum AmountTypeEnum {

    /**
     * banlance 正常余额
     */
    BANLANCE(0,"正常余额"),
    /**
     * frozen 冻结余额
     */
    FROZEN(1,"冻结余额"),
    ;

    /**
     * 变更类型
     */
    private Integer type;
    /**
     * 变更类型名称
     */
    private String name;

    AmountTypeEnum(Integer type, String name){
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
