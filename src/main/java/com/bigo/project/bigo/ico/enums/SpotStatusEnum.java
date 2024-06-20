package com.bigo.project.bigo.ico.enums;

public enum SpotStatusEnum {

    /**
     * 进行中
     */
    NEW("NEW","进行中"),
    /**
     * 已成交
     */
    FILLED("FILLED","已成交"),
    /**
     * 已取消
     */
    CANCELED("CANCELED","已取消")
    ;

    /**
     * 类型
     */
    private String status;

    private String name;

    SpotStatusEnum(String status, String name){
        this.status = status;
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取操作名称
     * @param type
     * @return
     */
    public static SpotStatusEnum getStatusByEnum(String type){
        for(SpotStatusEnum statusEnum : SpotStatusEnum.values()){
            if(statusEnum.status.equals(type)){
                return statusEnum;
            }
        }
        return null;
    }

}
