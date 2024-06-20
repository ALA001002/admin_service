package com.bigo.project.bigo.ico.enums;

import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;

public enum SpotTypeEnum {

    /**
     * banlance 正常余额
     */
    MARKET("MARKET","市价单"),
    /**
     * frozen 冻结余额
     */
    LIMIT("LIMIT","限价单"),
    ;

    /**
     * 类型
     */
    private String type;

    private String name;

    SpotTypeEnum(String type, String name){
        this.type = type;
        this.name = name;
    }

    public String getType() {
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
    public static SpotTypeEnum getTypeByEnum(String type){
        for(SpotTypeEnum typeEnum : SpotTypeEnum.values()){
            if(typeEnum.type.equals(type)){
                return typeEnum;
            }
        }
        return null;
    }

}
