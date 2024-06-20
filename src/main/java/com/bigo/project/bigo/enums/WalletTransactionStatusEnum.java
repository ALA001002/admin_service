package com.bigo.project.bigo.enums;

/**
 * @Description 钱包交易状态枚举
 * @Author wenxm
 * @Date 2020/6/21 9:58
 */
public enum WalletTransactionStatusEnum {

    /**
     * 待处理
     */
    PENDING(0, "待处理"),
    /**
     * 处理中
     */
    PROCESSING(1,"处理中"),
    /**
     * 成功
     */
    SUCCESS(2,"成功"),
    /**
     * 失败
     */
    FAILED(3,"失败"),
    ;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 状态名称
     */
    private String name;

    WalletTransactionStatusEnum(Integer status, String name){
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static String getNameByStatus(Integer status){
        for(WalletTransactionStatusEnum typeEnum : WalletTransactionStatusEnum.values()){
            if(typeEnum.getStatus().equals(status)){
                return typeEnum.getName();
            }
        }
        return  null;
    }
}
